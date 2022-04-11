package chapter5_shifting_contexts.samples

import cats.effect._
import cats.implicits._
import utils.debug._

object Parallelism extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO(s"(number of CPUs: $numCpus").debug
      _ <- tasks.debug // our underlying thread pool has at most numCpus threads
    } yield ExitCode.Success

  val numCpus =
    Runtime.getRuntime().availableProcessors() // We want more than this
  val tasks = List
    .range(0, numCpus * 2)
    .parTraverse(task) // run a large number of tasks in parallel

  def task(i: Int): IO[Int] = IO(i).debug
}
