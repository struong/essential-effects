package chapter10_job_scheduler

import cats.effect._
import cats.implicits._
import java.util.UUID
import cats.effect.concurrent.Deferred

sealed trait Job

object Job {
  case class Scheduled(id: Id, task: IO[_]) extends Job {
    def start(implicit cs: ContextShift[IO]): IO[Running] =
      for {
        exitCase <- Deferred[IO, ExitCase[Throwable]]
        fiber <- task.void.attempt
          // guaranteeCase combinator = the given funciton is guaranteed to be ran
          .guaranteeCase(exitCase.complete)
          .start
      } yield Running(id, fiber, exitCase)
  }

  case class Running(
      id: Id,
      fiber: Fiber[IO, Either[Throwable, Unit]],
      exitCase: Deferred[IO, ExitCase[Throwable]]
  ) extends Job {

    // block via the get method until the ExitCase value has been produced
    // then wrap it a Completed
    val await: IO[Completed] = exitCase.get.map(Completed(id, _))
  }

  case class Completed(id: Id, exitCase: ExitCase[Throwable]) extends Job

  // value class
  case class Id(value: UUID) extends AnyVal

  def create[A](task: IO[A]): IO[Scheduled] =
    IO(Id(UUID.randomUUID())).map(Scheduled(_, task))
}
