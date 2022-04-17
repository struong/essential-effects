package chapter9_concurrent_coordination

import cats.effect._
import cats.implicits._
import cats.effect.concurrent.Ref
import utils.debug._

object RefUpdateImpure extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = 
    for { 
      ref <- Ref[IO].of(0)
      _ <- List(1, 2, 3).parTraverse(task(_, ref))
    } yield ExitCode.Success

  def task(id: Int, ref: Ref[IO, Int]): IO[Unit] =
    ref
      .modify(previous =>
        id -> IO(s"$previous->$id").debug
      )
      .flatten // flatten because modify returns an IO and we wrap another IO
      .replicateA(3) // repeats an effect n times
      .void
}
