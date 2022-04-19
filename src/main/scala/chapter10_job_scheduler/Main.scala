package chapter10_job_scheduler

import cats.effect._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._

object Main extends IOApp {

  def createTask(i: Int): IO[Unit] =
    IO.sleep((200 + i * 100).millisecond) *>
      IO(s"Task $i").debug.void

  val numTasks = 100
  val tasks: List[Int] = List.range(0, numTasks)

  override def run(args: List[String]): IO[ExitCode] = {
    tasks
      .traverse(createTask) // could use parTraverse?
      .as(ExitCode.Success)
  }
}
