package logic

import cats.Monad
import cats.implicits._
import infra.aws.EC2Client
import vo.Volume

import scala.collection.JavaConverters._
import scala.language.higherKinds

class FindVolumeDetails[F[_]: Monad](ec2Client: EC2Client[F]) {

  def run(volumeIds: Seq[String]): F[Seq[Volume]] =
    for {
      volumes <- ec2Client.describeVolumes(volumeIds).map { result =>
        result.getVolumes.asScala.map(
          v =>
            Volume(v.getVolumeId, v.getAttachments.asScala.map(_.getInstanceId))
        )
      }
    } yield volumes

}
