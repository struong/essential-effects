package chapter10_job_scheduler

import cats.effect._
import cats.implicits._
import cats.data.Chain
import cats.effect.concurrent.Ref

trait JobScheduler {

  // Don't care what type of value the task produces, so we "forget" its type
  def schedule(task: IO[_]): IO[Job.Id]
}

object JobScheduler {
  case class State(
      maxRunning: Int,
      // Queue of scheduled jobs
      scheduled: Chain[Job.Scheduled] = Chain.empty,
      // Running jobs are indexed by their id, for easy lookups.
      running: Map[Job.Id, Job.Running] = Map.empty,
      // Accumulate completed jobs
      completed: Chain[Job.Completed] = Chain.empty
  ) {
    def enqueue(job: Job.Scheduled): State =
      copy(scheduled = scheduled :+ job)

    def dequeue: (State, Option[Job.Scheduled]) =
      if (running.size >= maxRunning)
        this -> None // ArrowAssoc -> just creates a tuple (this, None)
      else
        scheduled.uncons
          .map { case (head, tail) =>
            copy(scheduled = tail) -> Some(head)
          }
          .getOrElse(this -> None)

    def running(job: Job.Running): State =
      copy(running = running + (job.id -> job))

    def onComplete(job: Job.Completed): State =
      copy(
        running = running.removed(job.id),
        completed = completed :+ job
      )
  }

  def scheduler(schedulerState: Ref[IO, State], zzz: Zzz): JobScheduler =
    new JobScheduler {
      override def schedule(task: IO[_]): IO[Job.Id] =
        for {
          job <- Job.create(task)
          _ <- schedulerState.update(_.enqueue(job))
          _ <- zzz.wakeUp // when a job is scheduled, wake up
        } yield job.id
    }

  def resource(
      maxRunning: Int
  )(implicit cs: ContextShift[IO]): IO[Resource[IO, JobScheduler]] =
    for {
      state <- Ref[IO].of(JobScheduler.State(maxRunning))
      zzz <- Zzz.apply
      jobScheduler = scheduler(state, zzz)
      reactor = Reactor(state)
      onStart = (id: Job.Id) => IO.unit
      onComplete = (id: Job.Id, exitCase: ExitCase[Throwable]) => zzz.wakeUp
      loop =
        (zzz.sleep *> reactor.whenAwake(onStart, onComplete)).foreverM
    } yield loop.background.as(jobScheduler)
}
