package whose_volume.logic

import cats.Applicative
import whose_volume.vo.{
  NoSnapshotVolume,
  NoSnapshotVolumeWithAttachment,
  VolumeAttachment
}

import scala.language.higherKinds

class MergeNoSnapshotAndAttaches[F[_]: Applicative] {

  def run(
      noSnapshotVolumes: Seq[NoSnapshotVolume],
      attachments: Seq[VolumeAttachment]
  ): F[Seq[NoSnapshotVolumeWithAttachment]] = {
    val volumeDetailsMap = attachments.map(v => v.volumeId -> v).toMap
    Applicative[F].pure(
      noSnapshotVolumes.map(
        c =>
          NoSnapshotVolumeWithAttachment(
            c,
            c.volumeId.flatMap(volumeDetailsMap.get)
        )
      )
    )
  }

}
