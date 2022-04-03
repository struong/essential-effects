package chapter3_parallel_execution.samples

import cats.effect._
import cats.implicits._

object IOComposition extends App {
  val hello = IO(println(s"[${Thread.currentThread().getName} Hello]"))
  val world = IO(println(s"[${Thread.currentThread().getName} World]"))

  val hw1: IO[Unit] =
    for {
      _ <- hello
      _ <- world
    } yield ()

  val hw2 = (hello, world).mapN((_, _) => ())
  hw1.unsafeRunSync()
  hw2.unsafeRunSync()
}
