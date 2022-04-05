package chapter4_concurrent_control.samples


import cats.effect._
import cats.effect.implicits._ // for onCancel
import cats.implicits._
import utils.debug._

object Cancel extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = 
      for { 
          fiber <- task.onCancel(IO("I was cancelled").debug.void)
          .start
          _ <- IO("pre-cancel").debug
          _ <- fiber.cancel
          _ <- IO("cancelled").debug
      } yield ExitCode.Success

  val task: IO[String] = IO("task").debug *> IO.never // Can never complete, but can be cancelled
  
}
