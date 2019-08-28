package cli

import cats.data.ReaderT
import cats.effect.{ExitCode, IO, IOApp}
import cats.~>
import com.amazonaws.regions.Regions
import infra.aws.{AWSConfig, CTX, EC2Client, TrustedAdvisorClient}
import logic.{FindNoSnapshotVolumes, FindVolumeDetails}
import vo.Result

object Main extends IOApp {

  private val findUnattachedVolumes: FindNoSnapshotVolumes[CTX] =
    new FindNoSnapshotVolumes[CTX](TrustedAdvisorClient[CTX])
  private val findVolumeDetails: FindVolumeDetails[CTX] =
    new FindVolumeDetails[CTX](EC2Client[CTX])

  val ioToCTX: IO ~> CTX = λ[IO ~> CTX](fa => ReaderT(_ => fa))

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      snapshotLess <- findUnattachedVolumes.run
      volumeDetailsMap <- findVolumeDetails
        .run(snapshotLess.flatMap(_.volumeId.toSeq))
        .map(_.map(v => v.volumeId -> v).toMap)
      result = snapshotLess.map(
        c => Result(c, c.volumeId.flatMap(volumeDetailsMap.get))
      )
      exitCode <- ioToCTX apply IO {
        println(result)
        ExitCode.Success
      }
    } yield
      exitCode).run(AWSConfig(Regions.AP_NORTHEAST_1.getName)).handleErrorWith {
      case e: Throwable =>
        Console.err.println(e)
        IO.pure(ExitCode.Error)
    }
}
