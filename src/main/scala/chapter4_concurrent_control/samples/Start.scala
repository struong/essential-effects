package chapter4_concurrent_control.samples

import cats.effect._
import cats.implicits._
import utils.debug._

object Start extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = 
      for { 
          _ <- task.start // we start to fork the execution from the current effect. if we start an effect, the current execution is "forked"
          _ <- IO("task was started").debug // immediatley after starting the task, we print to console
      } yield ExitCode.Success

  val task: IO[String] = IO("task").debug
}
