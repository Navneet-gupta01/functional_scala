package com.navneetgupta.scalaz.zio

import java.io.IOException

import scalaz.zio._
import scalaz.zio.console._

object Zio1 extends App {
  // A value of type IO[E, A] describes an effect that may fail with an E, run forever, or produce a single A


  def run(args: List[String]) =
    myAppLogic.fold(_ => 1, _ => 0)


  def myAppLogic= for {
    _ <- putStrLn("What's Your Good Name dear?")
    name <- getStrLn
    _ <- putStrLn(s"Nice to meet you Mr. ${name}")
  } yield ()
}
