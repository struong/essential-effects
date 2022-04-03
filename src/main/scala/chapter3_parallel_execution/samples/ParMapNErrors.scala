package chapter3_parallel_execution.samples

import cats.effect._
import cats.implicits._
import utils.debug._

import scala.concurrent.duration._

object ParMapNErrors extends IOApp{
  // *>  is fa.flatMap(_ => fb) e.g. "process the original computation, and replace the result with whatever is given in the second argument"
  override def run(args: List[String]): IO[ExitCode] =
    e1.attempt.debug *>
      IO("---").debug *>
      e2.attempt.debug *>
      IO("---").debug *>
      e3.attempt.debug *>
      IO.pure(ExitCode.Success)

  val ok: IO[String] = IO("hi").debug
  val ko1: IO[String] = {
    IO.sleep(1.second).as("ko1").debug *> // to prove that the left most argument is not always ran first, delay ko1 so we see output of ok before k1 triggers the exception
    IO.raiseError[String](new RuntimeException("oh!")).debug
  }
  val ko2: IO[String] = IO.raiseError[String](new RuntimeException("noes!")).debug

  // parTupled is the same as a parMapN that does nothing - (l, r) => (l, r)
  val e1: IO[Unit] = (ok, ko1).parMapN((l, r) => (l, r)).void // void is the same as (_, _) => () or map(_ => ())
  val e2: IO[Unit] = (ko1, ok).parTupled.void
  val e3: IO[Unit] = (ko1, ko2).parMapN((_, _) => ())
}
