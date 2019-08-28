package vo

final case class Result(
    noSnapshotVolume: NoSnapshotVolume,
    volume: Option[Volume]
)
