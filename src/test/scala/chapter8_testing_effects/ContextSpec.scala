package chapter8_testing_effects.samples

import cats.effect._
import utils.debug._
import munit.FunSuite
import java.util.concurrent.TimeoutException

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.Failure

class ContextSpec extends munit.FunSuite {
  test("effect scheduling") { 
    val context = laws.util.TestContext()
    
    implicit val cs: ContextShift[IO] = context.ioContextShift
    implicit val timer: Timer[IO] = context.timer

    val timeoutError: TimeoutException = new TimeoutException
    val timeout = IO.sleep(10.seconds) *> IO.raiseError[Int](timeoutError)

    val f: Future[Int] = timeout.unsafeToFuture()

    // Not yet
    context.tick(5.seconds)
    assertEquals(f.value, None)

    // Good to go
    context.tick(5.seconds)
    assertEquals(f.value, Some(Failure(timeoutError)))
  }
}
