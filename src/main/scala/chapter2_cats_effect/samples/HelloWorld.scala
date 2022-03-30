package chapter2_cats_effect.samples

import cats.effect._

object HelloWorld extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = helloWorld.as(ExitCode.Success)

  val helloWorld: IO[Unit] =
    IO(println("Hello world!"))
}
