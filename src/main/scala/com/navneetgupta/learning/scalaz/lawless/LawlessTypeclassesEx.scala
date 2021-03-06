package com.navneetgupta.learning.scalaz.lawless

import scalaz._, Scalaz._

object LawlessTypeclassesEx extends App {

  //Length
  /**
    * trait Length[F[_]] { self =>
    *   def length[A](fa: F[A]): Int
    * }
    * */

  // Index

  // noraml scala
//  println(List(1,2,3)(3)) // java.lang.IndexOutOfBoundsException: 3
  println(List(1,2,3) index 3) // None
  println(List(1,2,3) index 2) // Some(3)
  println(List(1,2,3) index 1) // Some(2)
  println(List(1,2,3) index 0) // Some(1)


  // This way, we can both perform IO and update state in the same game loop.
  //
  //It turns out that for theoretical reasons, we can't just take any two monads M1 and M2 and combine them into another monad M3. It's not possible!
  //
  //However, there are a number of ways to combine monadic effects, ranging from Free monads to monad zippers and views to monad coproducts (and lots mo
}
