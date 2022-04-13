package chapter7_managing_resources

import cats.effect._
import utils.debug._
import java.io.RandomAccessFile

object TestFileBufferReader extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    FileBufferReader
      .makeResource("build.sbt")
      .use { f =>
        for {
          buffer <- f.readBuffer(0).debug
          _ <- IO(buffer._1.toSeq.mkString).debug
        } yield ExitCode.Success
      }
}

// We are going to manage the RandomAccessFile wrapped in a FileBufferReader
class FileBufferReader private (in: RandomAccessFile) {

  // An effect to read a buffer from an offset
  def readBuffer(offset: Long): IO[(Array[Byte], Int)] =
    IO {
      in.seek(offset)

      val buf = new Array[Byte](FileBufferReader.bufferSize)
      val len = in.read(buf)

      (buf, len)
    }

  // Since we want the Resource to manage our hidden state, we make close private
  private def close: IO[Unit] = IO(in.close())
}

object FileBufferReader {
  val bufferSize = 4096

  // Make a Resource by creating the FileBufferReader in an IO effect, ensuring that we close
  // the state when the Resource is released
  def makeResource(fileName: String): Resource[IO, FileBufferReader] =
    Resource.make {
      IO(new FileBufferReader(new RandomAccessFile(fileName, "r")))
    } { res =>
      res.close
    }
}
