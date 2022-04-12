package chapter6_integrating_asynchrony

import cats.effect._
import utils.debug._

object Never extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = never
  .guarantee(IO("I guess never is now").debug.void)
  .as(ExitCode.Success)

  val never: IO[Nothing] = IO.async (cb => ())
}