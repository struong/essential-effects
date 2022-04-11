package chapter5_shifting_contexts.samples

import cats.effect._
import cats.implicits._
import utils.debug._

// Blocker is the I/O unbounded thread pool, essentially a wrapper around EC
object Blocking extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
      // user Blocker.apply[IO] to create a Resource[IO, Blocker] that manages the thread pool for blocking computations
    Blocker[IO].use { blocker => 
      withBlocker(blocker).as(ExitCode.Success)
    }

  def withBlocker(blocker: Blocker): IO[Unit] =
    for {
      _ <- IO("on default").debug
      // in CE3 this is just _ <- IO.blocking("on blocker").debug
      _ <- blocker.blockOn(IO("on blocker").debug) // execute our effect on the blocking context
      _ <- IO("where am I?").debug // subsequent effects execute on the original context
    } yield ()
}
