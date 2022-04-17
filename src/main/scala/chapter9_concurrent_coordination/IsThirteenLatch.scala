package chapter9_concurrent_coordination

import cats.effect._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._

object IsThirteenLatch extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    latch <- CountdownLatch(13)
    _ <- (beeper(latch), tickingClock(latch)).parTupled
  } yield ExitCode.Success

  def beeper(latch: CountdownLatch): IO[Unit] =
    for {
      _ <- latch.await
      _ <- IO("BEEP!").debug
    } yield ()

  def tickingClock(latch: CountdownLatch): IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis()).debug
      _ <- latch.decrement
      _ <- tickingClock(latch)
    } yield ()
}
