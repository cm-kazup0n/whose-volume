package logic

import cats.Monad
import cats.implicits._
import infra.aws.TrustedAdvisorClient
import vo.NoSnapshotVolume

import scala.collection.JavaConverters._
import scala.language.higherKinds

class FindNoSnapshotVolumes[F[_]: Monad](
    trustedAdvisorClient: TrustedAdvisorClient[F]
) {

  def run: F[Seq[NoSnapshotVolume]] =
    for {
      checks <- trustedAdvisorClient.describeTrustedAdvisorChecks.map(
        _.getChecks.asScala
      )
      volumes <- checks.find(
        chk =>
          chk.getCategory === "fault_tolerance" && chk.getName === "Amazon EBS スナップショット"
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
              case Seq(region, volId) =>
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
