package chapter6_integrating_asynchrony

import cats.effect._
import utils.debug._
import java.util.concurrent.CompletableFuture
import scala.jdk.FunctionConverters._
import cats.effect.syntax.effect

object AsynCompletable extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    effect.debug.as(ExitCode.Success)

  val effect: IO[String] =
    fromCF(IO(cf()))

  def fromCF[A](cfa: IO[CompletableFuture[A]]): IO[A] =
    cfa.flatMap { fa =>
      IO.async { cb =>
        val handler: (A, Throwable) => Unit = {
          case (a, null) => cb(Right(a))
          case (null, t) => cb(Left(t))
          case (a, t) => sys.error("not possible?")
        }
      
        fa.handle(handler.asJavaBiFunction)

        ()
      }
    }

  def cf(): CompletableFuture[String] =
    CompletableFuture.supplyAsync(() => "woo!")
}
