package vo

final case class NoSnapshotVolume(
    region: Option[String],
    volumeId: Option[String],
    resourceId: String
)
