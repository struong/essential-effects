package chapter9_concurrent_coordination

import cats.effect._
import cats.effect.concurrent._
import cats.implicits._

sealed trait State
final case class Outstanding(n: Long, whenDone: Deferred[IO, Unit])
    extends State
final case class Done() extends State

trait CountdownLatch {
  def await: IO[Unit]
  def decrement: IO[Unit]
}

object CountdownLatch {
  def apply(n: Long)(implicit cs: ContextShift[IO]): IO[CountdownLatch] =
    for {
      whenDone <- Deferred[IO, Unit] // block and unblock await() callers
      state <- Ref[IO].of[State](Outstanding(n, whenDone))
    } yield new CountdownLatch {

      // await never changes the state
      override def await: IO[Unit] = state.get.flatMap {
        case Outstanding(n, whenDone) =>
          whenDone.get // blocking effect that unblocks when deferred is complete
        case Done() => IO.unit
      }

      // decrement always changes the state
      override def decrement: IO[Unit] =
        state
          .modify {
            case Outstanding(1, whenDone) => Done() -> whenDone.complete(())
            case Outstanding(n, whenDone) =>
              Outstanding(n - 1, whenDone) -> IO.unit
            case Done() => Done() -> IO.unit
          }
          .flatten
          .uncancelable
    }
}

object LatchExample extends IOApp {
  import utils.debug._
  override def run(args: List[String]): IO[ExitCode] = for {
    latch <- CountdownLatch(1)
    _ <- (actionWithPrerequisite(latch), runPrerequisite(latch)).parTupled
  } yield ExitCode.Success

  def runPrerequisite(latch: CountdownLatch) =
    for {
      result <- IO("prerequisite").debug
      _ <- latch.decrement
    } yield result

  def actionWithPrerequisite(latch: CountdownLatch) =
    for {
      _ <- IO("watiting for prerequisite").debug
      _ <- latch.await
      result <- IO("action").debug
    } yield result
}
