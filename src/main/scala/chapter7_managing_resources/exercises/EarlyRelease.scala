package chapter7_managing_resources.exercises

import cats.effect._
import utils.debug._
import scala.io.Source
import cats.effect

object EarlyRelease extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    dbConnectionResource
      .use { conn =>
        conn.query("SELECT * FROM users WHERE id = 12").debug
      }
      .as(ExitCode.Success)

  val dbConnectionResource =
    for {
      config <- configResource
      conn <- DbConnection.make(config.connectUrl)
    } yield conn

  lazy val configResource: Resource[IO, Config] =
    Resource.liftF(sourceResource.use { source =>
      Config.fromSource(source)
    })

  lazy val sourceResource: Resource[IO, Source] =
    Resource.make(
      IO(s"> opening Source to config").debug *> IO(Source.fromString(config))
    )(source => IO("< closing source to config").debug *> IO(source.close))

  val config = "exampleConnectionURL"
}

case class Config(connectUrl: String)

object Config {
  def fromSource(source: Source): IO[Config] =
    for {
      config <- IO(Config(source.getLines().next()))
      _ <- IO(s"read $config").debug
    } yield config
}

trait DbConnection {
  def query(sql: String): IO[String]
}

object DbConnection {
  def make(connectUrl: String): Resource[IO, DbConnection] =
    Resource.make(
      IO(s"> opening Connection to $connectUrl").debug *> IO(
        new DbConnection {
          def query(sql: String): IO[String] =
            IO(s"""(results for SQL "$sql")""")
        }
      )
    )(_ => IO(s"< closing Connection to $connectUrl").debug.void)

}
