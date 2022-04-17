package chapter9_concurrent_coordination

import cats.effect._
import cats.effect.concurrent._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._
import cats.effect.concurrent.Deferred

object IsThirteen extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = 
    for {
      ticks <- Ref[IO].of(0L)
      is13 <- Deferred[IO, Unit] // holds a Unit value once the condition is met
      _ <- (beepWhen13(is13), tickingClock(ticks, is13)).parTupled // two effects are comm through is13 value
    } yield ExitCode.Success

  def beepWhen13(is13: Deferred[IO, Unit]): IO[Unit] =
    for {
      _ <- is13.get // block until the current effect (is13) has a value
      _ <- IO("BEEP!").debug
    } yield ()


  def tickingClock(ticks: Ref[IO, Long], is13: Deferred[IO, Unit]): IO[Unit] = 
    for { 
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis).debug
      count <- ticks.updateAndGet(_ + 1)
      _ <- if(count >= 13) is13.complete(()) else IO.unit
      _ <- tickingClock(ticks, is13)
    } yield () 
}
