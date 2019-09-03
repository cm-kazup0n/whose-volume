package object vo {

  final case class NoSnapshotVolume(
      region: Option[String],
      volumeId: Option[String],
      resourceId: String
  )

  final case class VolumeAttachment(volumeId: String, instanceIds: Seq[String])

  final case class NoSnapshotVolumeWithAttachment(
      noSnapshotVolume: NoSnapshotVolume,
      volume: Option[VolumeAttachment]
  )
}
