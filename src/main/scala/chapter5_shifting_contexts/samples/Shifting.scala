package chapter5_shifting_contexts.samples

import cats.effect._
import cats.implicits._
import utils.debug._

object Shifting extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = 
      for { 
          _ <- IO("one").debug
          _ <- IO.shift // ensures next effect is run on a different thread
          _ <- IO("two").debug
          _ <- IO.shift
          _ <- IO("three").debug
      } yield ExitCode.Success
}
