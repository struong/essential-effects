package chapter9_concurrent_coordination.samples

import cats.implicits._
import cats.effect._
import utils.debug._

import scala.concurrent.duration._

object ConcurrentStateVar extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- (tickingClock, printTicks).parTupled
    } yield ExitCode.Success

  // non atomic state, leading to the lost update problem
  var ticks: Long = 0L

  val tickingClock: IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO("System time: " + System.currentTimeMillis()).debug
      _ = (ticks = ticks + 1)
      _ <- tickingClock
    } yield ()

  val printTicks: IO[Unit] =
    for {
      _ <- IO.sleep(5.seconds)
      _ <- IO(s"Ticks: $ticks").debug.void
      _ <- printTicks
    } yield ()
}
