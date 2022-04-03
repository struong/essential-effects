package chapter3_parallel_execution.samples

import cats.effect._
import cats.implicits._
import utils.debug._

// Most common case of (par)traverse is when you have a collection of work to be done and a function which handles one unit of work.
// Then you get a collection of results combined into one effect
object ParTraverse extends IOApp {
  val numTasks = 100
  val tasks: List[Int] = List.range(0, numTasks)

  def task(id: Int): IO[Int] = IO(id).debug

  // List(1, 2, 3, 4).parTraverse(f) is the same as (f(1), f(2), f(3), f(4)).parMapN(…)
  override def run(args: List[String]): IO[ExitCode] =
    tasks
      .parTraverse(task)
      .debug
      .as(ExitCode.Success)
}
