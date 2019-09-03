package logic

import cats.Monad
import cats.implicits._
import infra.aws.{TrustedAdvisorClient, TrustedAdvisorLanguage}
import vo.NoSnapshotVolume

import scala.collection.JavaConverters._
import scala.language.higherKinds

class FindNoSnapshotVolumes[F[_]: Monad](
    implicit trustedAdvisorClient: TrustedAdvisorClient[F]
) {

  def run: F[Seq[NoSnapshotVolume]] =
    for {
      checks <- trustedAdvisorClient
        .describeTrustedAdvisorChecks(TrustedAdvisorLanguage.EN)
        .map(
          _.getChecks.asScala
        )
      volumes <- checks.find(
        chk =>
          chk.getCategory === "fault_tolerance" && chk.getName === "Amazon EBS Snapshots"
      ) match {
        case Some(chk) => checkToVolumes(chk.getId())
        case None      => Monad[F].pure(Seq.empty[NoSnapshotVolume])
      }
    } yield volumes

  private def checkToVolumes(checkId: String): F[Seq[NoSnapshotVolume]] =
    trustedAdvisorClient.describeTrustedAdvisorCheck(checkId).map {
      checkResult =>
        checkResult.getResult.getFlaggedResources.asScala.map {
          flaggedResource =>
            flaggedResource.getMetadata.asScala match {
              case Seq(region, volId, _*) =>
                NoSnapshotVolume(
                  region.some,
                  volId.some,
                  flaggedResource.getResourceId
                )
              case _ =>
                NoSnapshotVolume(
                  flaggedResource.getRegion.some,
                  none,
                  flaggedResource.getResourceId
                )
            }
        }
    }
}
