package chapter7_managing_resources.samples

import cats.effect._
import cats.implicits._
import utils.debug._
import scala.concurrent.duration._

object ResourceBackgroundTask extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- backgroundTask.use { _ =>
        IO("other work while background task is running").debug *>
          IO.sleep(200.millis) *>
          IO("other work done").debug
      }
      _ <- IO("all done").debug
    } yield ExitCode.Success

  val backgroundTask: Resource[IO, Unit] = {
    val loop =
      // foreverM is the same as step.flatMap(_ => loop)
      (IO("looping...").debug *> IO.sleep(100.millis)).foreverM

    // Cats Effect gives us a background,
    // the Resoruce below could be rewritten as
    // loop.background
    Resource
      .make(
        IO("> forking backgroundTask").debug *> loop.start
      )( // acquire effect forks a fiber
        IO(
          "< cancelling backgroundTask"
        ).debug.void *> _.cancel // the release cancels it
      )
      .void // We do not give the user of the Resource access to the fiber but we could.

  }
}
