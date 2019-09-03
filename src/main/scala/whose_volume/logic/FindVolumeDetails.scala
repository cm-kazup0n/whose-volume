package whose_volume.logic

import cats.Monad
import cats.implicits._
import whose_volume.infra.aws.EC2Client
import whose_volume.vo.VolumeAttachment

import scala.collection.JavaConverters._
import scala.language.higherKinds

class FindVolumeDetails[F[_]: Monad](implicit ec2Client: EC2Client[F]) {

  def run(volumeIds: Seq[String]): F[Seq[VolumeAttachment]] =
    for {
      volumes <- ec2Client.describeVolumes(volumeIds).map { results =>
        results
          .flatMap(_.getVolumes.asScala)
          .map(
            v =>
              VolumeAttachment(
                v.getVolumeId,
                v.getAttachments.asScala.map(_.getInstanceId)
            )
          )
      }
    } yield volumes

}
