package chapter10_job_scheduler

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._

trait Reactor {
  def whenAwake(
      onStart: Job.Id => IO[Unit],
      onComplete: (Job.Id, ExitCase[Throwable]) => IO[Unit]
  ): IO[Unit]
}

object Reactor {

  def apply(
      stateRef: Ref[IO, JobScheduler.State]
  )(implicit cs: ContextShift[IO]): Reactor =
    new Reactor {
      override def whenAwake(
          onStart: Job.Id => IO[Unit],
          onComplete: (Job.Id, ExitCase[Throwable]) => IO[Unit]
      ): IO[Unit] = {
        def startJob(scheduled: Job.Scheduled): IO[Job.Running] =
          for {
            running <- scheduled.start
            _ <- stateRef.update(_.running(running))
            _ <- registerOnComplete(running)
            // notify any listeners a job has started
            _ <- onStart(running.id).attempt
          } yield running

        def startNextJob: IO[Option[Job.Running]] =
          for {
            job <- stateRef.modify(_.dequeue)
            running <- job.traverse(startJob)
          } yield running

        // forks an effect and awaits completion
        def registerOnComplete(job: Job.Running) =
          job.await
            .flatMap(jobCompleted)
            .start

        // update the state and notify listeners
        def jobCompleted(job: Job.Completed): IO[Unit] = stateRef
          .update(_.onComplete(job))
          .flatTap(_ => onComplete(job.id, job.exitCase).attempt)

        startNextJob.iterateUntil(_.isEmpty).void
      }
    }
}
