package whose_volume.cli

import cats.data.ReaderT
import cats.effect._
import cats._
import com.amazonaws.regions.Regions
import whose_volume.infra.aws.{AWSConfig, CTX}
import whose_volume.logic._
import whose_volume.infra.Console.autoDerive._
import wvlet.airframe.log
import wvlet.log.{LogFormatter, LogSupport, Logger}

object Main extends IOApp with LogSupport {

  private implicit val fk: IO ~> CTX = λ[IO ~> CTX](io => ReaderT(_ => io))
  private val config: AWSConfig = AWSConfig(Regions.AP_NORTHEAST_1.getName)

  log.init
  Logger.setDefaultFormatter(LogFormatter.AppLogFormatter)
  Logger.scheduleLogLevelScan

  override def run(args: List[String]): IO[ExitCode] = program.run(config)

  def program: CTX[ExitCode] =
    for {
      _ <- fk(IO(info("start getting trusted advisors result")))
      a <- new FindNoSnapshotVolumes[CTX].run
      _ <- fk(IO(info("start getting volume details")))
      b <- new FindVolumeDetails[CTX].run(a.flatMap(_.volumeId.toSeq))
      c <- new MergeNoSnapshotAndAttaches[CTX].run(a, b)
      _ <- new PrintResult[CTX].run(c)
    } yield ExitCode.Success

}
