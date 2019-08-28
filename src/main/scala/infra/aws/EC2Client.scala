package infra.aws

import cats.data.ReaderT
import cats.effect.IO
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.{
  DescribeVolumesRequest,
  DescribeVolumesResult
}

import scala.language.higherKinds

trait EC2Client[F[_]] {

  def describeVolumes(volumeIds: Seq[String]): F[DescribeVolumesResult]

}

object EC2Client {
  def apply[F[_]](implicit F: EC2Client[F]): EC2Client[F] = F

  implicit def onAWSClient: EC2Client[CTX] =
    (volumeIds: Seq[String]) =>
      ReaderT { aws =>
        IO {
          AmazonEC2Client
            .builder()
            .withRegion(aws.region)
            .build()
            .describeVolumes(
              new DescribeVolumesRequest().withVolumeIds(volumeIds: _*)
            )
        }
      }
}
