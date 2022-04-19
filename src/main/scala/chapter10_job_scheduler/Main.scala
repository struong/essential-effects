package chapter10_job_scheduler

import cats.effect._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    def createTask(i: Int): IO[Unit] =
      IO.sleep((200 + i * 10).millisecond) *>
        IO(s"Task $i").debug.void

    for {
      resource <- JobScheduler.resource(2)
      _ <- resource.use { scheduler =>
        (1 to 50)
          .map(createTask)
          .toList
          .traverse(scheduler.schedule(_)) *>
          IO.sleep(1.minute)
      }
      _ <- IO("Finished!").debug
    } yield ExitCode.Success
  }
}
