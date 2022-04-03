package chapter3_parallel_execution.samples

import cats.effect._
import cats.implicits._
import utils.debug.DebugHelper

object DebugExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = seq.as(ExitCode.Success)

  val hello: IO[String] = IO("hello").debug
  val world: IO[String] = IO("world").debug

  // always runs on the same thread
  val seq: IO[String] = (hello, world).mapN((h, w) => s"$h $w").debug
}
