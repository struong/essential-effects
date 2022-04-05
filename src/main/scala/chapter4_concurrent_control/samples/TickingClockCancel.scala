package chapter4_concurrent_control.samples

import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._

object TickingClockCancel extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    (tickingClock, ohNoes).parTupled.as(ExitCode.Success)

  val ohNoes =
    IO.sleep(2.seconds) *> IO.raiseError(new RuntimeException("oh noes!"))

  val tickingClock: IO[Unit] =
    for {
      _ <- IO(println(System.currentTimeMillis()))
      _ <- IO(Thread.sleep(1000))
      _ <- tickingClock
    } yield ()

}
