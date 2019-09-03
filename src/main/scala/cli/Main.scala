package cli

import cats.data.ReaderT
import cats.effect._
import cats._
import com.amazonaws.regions.Regions
import infra.aws.{AWSConfig, CTX}
import logic._
import infra.Console.autoDerive._
import wvlet.airframe.log
import wvlet.log.{LogFormatter, Logger}

object Main extends IOApp {

  private implicit val fk: IO ~> CTX = λ[IO ~> CTX](io => ReaderT(_ => io))
  private val config: AWSConfig = AWSConfig(Regions.AP_NORTHEAST_1.getName)

  log.init
  Logger.setDefaultFormatter(LogFormatter.AppLogFormatter)
  Logger.scheduleLogLevelScan

  override def run(args: List[String]): IO[ExitCode] = program.run(config)

  def program: CTX[ExitCode] =
    for {
      a <- new FindNoSnapshotVolumes[CTX].run
      b <- new FindVolumeDetails[CTX].run(a.flatMap(_.volumeId.toSeq))
      c <- new MergeNoSnapshotAndAttaches[CTX].run(a, b)
      _ <- new PrintResult[CTX].run(c)
    } yield ExitCode.Success

}
