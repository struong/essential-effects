package chapter3_parallel_execution.samples

import cats.effect._
import cats.implicits._
import utils.debug._

object ParMapN extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = par.as(ExitCode.Success)

  val hello: IO[String] = IO("Hello").debug
  val world: IO[String] = IO("World").debug

  // Execution is now running in parallel and is non-deterministic
  val par: IO[String] = (hello, world).parMapN((h, w) => s"$h $w").debug
}
