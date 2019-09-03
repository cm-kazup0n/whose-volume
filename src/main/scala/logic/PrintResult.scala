package logic

import cats.implicits._
import cats.{Monad, Show}
import infra.Console
import vo.{NoSnapshotVolume, NoSnapshotVolumeWithAttachment, VolumeAttachment}

import scala.language.higherKinds

class PrintResult[F[_]: Monad](implicit console: Console[F]) {

  implicit val show: Show[NoSnapshotVolumeWithAttachment] = Show.show {
    case NoSnapshotVolumeWithAttachment(
        NoSnapshotVolume(_, Some(vol), _),
        Some(VolumeAttachment(_, instances))
        ) =>
      s"$vol\t${instances.mkString("\t")}"
    case NoSnapshotVolumeWithAttachment(NoSnapshotVolume(_, Some(vol), _), _) =>
      vol
    case _ => ""
  }

  def run(
      noSnapshotVolumeWithAttachments: Seq[NoSnapshotVolumeWithAttachment]
  ): F[Unit] =
    for {
      _ <- console.printLn("vol\tinstance ids")
      _ <- noSnapshotVolumeWithAttachments.toList.traverse_[F, Unit](
        v => console.printLn(v.show)
      )
    } yield ()

}
