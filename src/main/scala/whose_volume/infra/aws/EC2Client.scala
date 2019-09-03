package whose_volume.infra.aws

import cats.data.{OptionT, ReaderT}
import cats.effect.{ContextShift, IO}
import cats.implicits._
import cats.tagless.finalAlg
import com.amazonaws.services.ec2.model.{
  AmazonEC2Exception,
  DescribeVolumesRequest,
  DescribeVolumesResult
}
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2Client}
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

@finalAlg
trait EC2Client[F[_]] {
  def describeVolumes(volumeIds: Seq[String]): F[Seq[DescribeVolumesResult]]
}

object EC2Client {
  implicit object onAWSClient extends EC2Client[CTX] with LogSupport {

    type M[A] = OptionT[IO, A]

    override def describeVolumes(
        volumeIds: Seq[String]
    ): CTX[Seq[DescribeVolumesResult]] = ReaderT { config =>
      implicit val client: AmazonEC2 =
        AmazonEC2Client.builder().withRegion(config.region).build()
      implicit val cs: ContextShift[IO] =
        IO.contextShift(ExecutionContext.Implicits.global)
      volumeIds.toList.traverseFilter(describeVolume(_).value)
    }

    def describeVolume(
        id: String
    )(
        implicit client: AmazonEC2,
        cs: ContextShift[IO]
    ): M[DescribeVolumesResult] =
      OptionT {
        IO.shift *> IO {
          debug(s"start getting volume detail for ${id}")
          val r = client
            .describeVolumes(new DescribeVolumesRequest().withVolumeIds(id))
            .some
          debug(s"getting volume detail for ${id} completed")
          r
        }.recoverWith {
          case e: AmazonEC2Exception
              if e.getErrorCode === "InvalidVolume.NotFound" =>
            warn(s"volume not found ${id}")
            IO.pure(none)
        }
      }
  }

}
