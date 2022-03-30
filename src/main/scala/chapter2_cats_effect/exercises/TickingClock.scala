package chapter2_cats_effect.exercises

import cats.effect.{ExitCode, IO, IOApp}

object TickingClock extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = tickingClock.as(ExitCode.Success)

  val tickingClock: IO[Unit] =
    for {
      _ <- IO(println(System.currentTimeMillis()))
      _ <- IO(Thread.sleep(1000))
      _ <- tickingClock
    } yield ()
}
