package chapter4_concurrent_control.samples

import cats.effect._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._

object JoinAfterStart extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      fiber <- task.start
      _ <- IO("pre-join").debug
      _ <- fiber.join.debug
      _ <- IO("post-join").debug
    } yield ExitCode.Success

  /*
    As a reminder, the *> extension method is equivalent to using mapN with
    two effects but only the second effect’s value is produced; for example,
    first *> second is equivalent to (first, second).mapN((_, b) ⇒ b).
   */
  val task = IO.sleep(2.seconds) *> IO("task").debug
}
