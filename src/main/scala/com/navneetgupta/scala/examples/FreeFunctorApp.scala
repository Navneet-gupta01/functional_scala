package com.navneetgupta.scala.examples

object FreeFunctorApp {
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  object Functor {
    def apply[F[_]](implicit F: Functor[F]): Functor[F] = F
  }
}


