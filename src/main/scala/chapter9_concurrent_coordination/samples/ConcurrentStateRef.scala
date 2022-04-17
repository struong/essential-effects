package chapter9_concurrent_coordination.samples

import cats.implicits._
import cats.effect._
import utils.debug._

import scala.concurrent.duration._
import cats.effect.concurrent.Ref

object ConcurrentStateRef extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = 
    for {
      ticks <- Ref[IO].of(0L) // wraps our Long (init with 0) in a Java AtomicReference
      _ <- (tickingClock(ticks), printTicks(ticks)).parTupled
    } yield ExitCode.Success

  def tickingClock(ticks: Ref[IO, Long]): IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO("System time: " + System.currentTimeMillis()).debug
      _ <- ticks.update(_ + 1)
      _ <- tickingClock(ticks)
    } yield ()

  def printTicks(ticks: Ref[IO, Long]): IO[Unit] =
    for {
      _ <- IO.sleep(5.seconds)
      n <- ticks.get
      _ <- IO(s"Ticks: $n").debug.void
      _ <- printTicks(ticks)
    } yield ()
}
