package chapter5_shifting_contexts.samples

import cats.effect._
import cats.implicits._
import utils.debug._
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

object ShiftingMultiple extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (ec("1"), ec("2")) match {
      case (ec1, ec2) =>
        for {
          _ <- IO("one").debug // by default will use cats EC
          _ <- IO.shift(ec1) // shift on to pool-1-thread-1
          _ <- IO("two").debug
          _ <- IO.shift(ec2) // shift on to pool-2-thread-1
          _ <- IO("three").debug
        } yield ExitCode.Success
    }

  // create a new single-threaded EC
  def ec(name: String): ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor { r =>
      val t = new Thread(r, s"pool-$name-thread-1")
      t.setDaemon(true) // ensure the JVM shuts down correctly
      t
    })
}
