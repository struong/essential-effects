package chapter4_concurrent_control.samples

import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._

object Timeout extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      done <- IO.race(task, timeout)
    //   _ <- done match {
    //     case Left(value)  => IO("    task won").debug
    //     case Right(value) => IO("timeout won").debug
    //   }
    _ <- task.timeoutTo(500.millis, timeout) // other ways of timing out
    } yield ExitCode.Success

  val task: IO[Unit] = annotatedSleep("    task", 1000.milliseconds)
  val timeout: IO[Unit] = annotatedSleep("timeout", 500.milliseconds)

  def annotatedSleep(name: String, duration: FiniteDuration): IO[Unit] =
    (
      IO(s"$name: starting").debug *>
        IO.sleep(duration) *>
        IO(s"$name: done").debug
    ).onCancel(IO(s"$name: cancelled").debug.void).void
}
