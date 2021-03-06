package com.navneetgupta.cats.effects

import cats.Applicative
import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._

object ExternalInteractionWithIO extends IOApp {
  val program: IO[Unit] =
    for {
      _ <- IO(println("Enter Your Name: "))
      name <- IO(scala.io.StdIn.readLine)
      _ <- IO(println(s"Hello Dear $name"))
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- program
    } yield ExitCode.Success

  // Problem :: How to test `program` written is behaving properly or not. Until we run it we cannnot test it.
  // Soultion Approach Tagless Final

}

object Common {

  trait Console[F[_]] {
    def putStrLn(str: String): F[Unit]

    def readLn(): F[String]
  }

  object Console {
    def apply[F[_]](implicit F: Console[F]): Console[F] = F
  }

  def putStrLn[F[_] : Console](line: String): F[Unit] = Console[F].putStrLn(line)

  def readLn[F[_] : Console](): F[String] = Console[F].readLn()

}

//
object TaglessSupportedEx {

  import Common._
  import cats.Monad

  def program[F[_] : Common.Console : Monad]: F[Unit] =
    for {
      _ <- putStrLn("Enter Your Name: ")
      name <- readLn
      _ <- putStrLn(s"Hello Dear $name")
    } yield ()
}

object StdConsoleApp extends IOApp {

  implicit val ConsoleIO = new Common.Console[IO] {
    def putStrLn(line: String): IO[Unit] = IO(println(line))

    def readLn(): IO[String] = IO(scala.io.StdIn.readLine)
  }

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- TaglessSupportedEx.program[IO]
    } yield ExitCode.Success
}

object TaglessSupportEx2 extends IOApp {

  import cats.Monad

  def program[F[_] : Monad](implicit C: Common.Console[F]): F[Unit] =
    for {
      _ <- C.putStrLn("Enter Your Name: ")
      name <- C.readLn
      _ <- C.putStrLn(s"Hello Dear $name")
    } yield ()

  class StdConsole[F[_] : Sync] extends Common.Console[F] {
    override def putStrLn(str: String): F[Unit] = Sync[F].delay(println(str))

    override def readLn(): F[String] = Sync[F].delay(scala.io.StdIn.readLine)
  }

  implicit val ConsoleIO = new StdConsole[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- program[IO]
    } yield ExitCode.Success

}

//object TaglessRemoteConsoleEx extends IOApp {
//  import Common._
//  import scala.concurrent.Future
//
//  implicit val ec = ExecutionContext.global
//
//
//  def program[F[_]: Monad](implicit C: Console[F]): F[Unit] =
//    for {
//      _ <- C.putStrLn("Enter Your Name: ")
//      name <- C.readLn
//      _ <- C.putStrLn(s"Hello Dear $name")
//    } yield ()
//
//
//  def remoteWrite(str: String): Future[Unit] = Future {println(str)}
//  def remoteRead(): Future[String] = Future {scala.io.StdIn.readLine}
//  class RemoteConsole[F[_]: Async] extends Console[F] {
//
//    private def fromFuture[A](fa: F[Future[A]]): F[A] = {
//      fa.flatMap {future =>
//        Async[F].async {cb =>
//          future.onComplete {
//            case Success(x) => cb(Right(x))
//            case Failure(e) => cb(Left(e))
//          }
//        }
//      }
//    }
//    override def putStrLn(str: String): F[Unit] = fromFuture(Sync[F].delay(remoteWrite(str)))
//
//    override def readLn(): F[String] = fromFuture(Sync[F].delay(remoteRead))
//  }
//
//
//
//  implicit val ConsoleIO =  new RemoteConsole[IO]
//  override def run(args: List[String]): IO[ExitCode] =
//    for {
//      _ <- program[IO]
//    } yield ExitCode.Success
//
//}


// To test pure logic
object TestTaglessProgramEx extends App {

  import cats.Monad

  def program[F[_] : Monad](implicit C: Common.Console[F]): F[Unit] =
    for {
      _ <- C.putStrLn("Enter Your Name: ")
      name <- C.readLn
      _ <- C.putStrLn(s"Hello Dear $name")
    } yield ()

  class TestConsole[F[_] : Applicative](state: Ref[F, List[String]])
    extends Common.Console[F] {
    override def putStrLn(str: String): F[Unit] = state.update(_ :+ str)

    override def readLn(): F[String] = "test".pure[F]
  }

  //  implicit def ConsoleIO(state: Ref[IO, List[String]]) =  new TestConsole[IO](state)

  val spec = for {
    state <- Ref.of[IO, List[String]](List.empty[String])
    implicit0(c: Common.Console[IO]) = new TestConsole[IO](state)
    _ <- program[IO]
    st <- state.get
  } yield st

  println(spec.unsafeToFuture())
}
