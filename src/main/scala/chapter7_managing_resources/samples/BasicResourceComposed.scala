package chapter7_managing_resources

import cats.effect._
import cats.implicits._
import utils.debug._

object BasicResourceComposed extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = 
      // we can also use parTupled for parallel resource composition
      (stringResource, intResource).tupled // equivalent to mapN((s, i) ⇒ (s, i))
      .use { 
          case(s, i) => 
              IO(s"$s is so cool").debug *>
              IO(s"$i is also cool!").debug
      }.as(ExitCode.Success)

  val stringResource: Resource[IO, String] =
    Resource.make(IO("> acquiring stringResource").debug *> IO("String"))(_ =>
      IO("< releasing stringResource").debug.void
    )

  val intResource: Resource[IO, Int] =
    Resource.make(IO("acquiring intResource").debug *> IO(99))(_ =>
      IO("< releasing intResource").debug.void
    )
}
