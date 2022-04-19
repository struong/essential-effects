package chapter10_job_scheduler

import cats.effect._
import cats.effect.concurrent._
import cats.implicits._

trait Zzz {
  // semantically block until wakeUp is invoked
  def sleep: IO[Unit]
  // wake up any sleepers, no effect if already awake
  def wakeUp: IO[Unit]
}

object Zzz {

  sealed trait State
  case class Asleep(d: Deferred[IO, Unit]) extends State
  case class Awake(d: Deferred[IO, Unit]) extends State

  def apply(implicit cs: ContextShift[IO]): IO[Zzz] =
    for {
      awaken <- Deferred[IO, Unit]
      state <- Ref[IO].of[State](Asleep(awaken))
    } yield new Zzz {

      override def sleep: IO[Unit] = state
        .modify {
          case Asleep(d) => Asleep(d) -> IO.unit
          case Awake(d)  => Asleep(d) -> d.get
        }
        .flatten
        .uncancelable

      override def wakeUp: IO[Unit] = state
        .modify {
          case Asleep(d) => Awake(d) -> awaken.complete(())
          case Awake(d)  => Awake(d) -> IO.unit
        }
        .flatten
        .uncancelable
    }
}
