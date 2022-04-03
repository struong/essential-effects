package chapter3_parallel_execution.samples

import cats.effect._
import cats.implicits._
import utils.debug._

// (par)Sequence turns a nested structure inside out
object ParSequence extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = 
      tasks
      .parSequence // F[G[A]] => G[F[A]] 
      .debug
      .as(ExitCode.Success)

  val numTasks: Int = 100
  val tasks: List[IO[Int]] = List.tabulate(numTasks)(task) // F[G[A]]

  def task(id: Int): IO[Int] = IO(id).debug
}
