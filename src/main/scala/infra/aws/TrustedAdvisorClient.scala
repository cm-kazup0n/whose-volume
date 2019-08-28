package infra.aws

import cats.data.ReaderT
import cats.effect.IO
import com.amazonaws.services.support.model.{
  DescribeTrustedAdvisorCheckResultRequest,
  DescribeTrustedAdvisorCheckResultResult,
  DescribeTrustedAdvisorChecksRequest,
  DescribeTrustedAdvisorChecksResult
}
import com.amazonaws.services.support.{AWSSupport, AWSSupportClient}

import scala.language.higherKinds

trait TrustedAdvisorClient[F[_]] {
  def describeTrustedAdvisorChecks: F[DescribeTrustedAdvisorChecksResult]

  def describeTrustedAdvisorCheck(
      checkId: String
  ): F[DescribeTrustedAdvisorCheckResultResult]
}

object TrustedAdvisorClient {

  def apply[F[_]](
      implicit F: TrustedAdvisorClient[F]
  ): TrustedAdvisorClient[F] = F

  implicit def onAWSClient: TrustedAdvisorClient[CTX] =
    new TrustedAdvisorClient[CTX] {

      private val request =
        new DescribeTrustedAdvisorChecksRequest().withLanguage("ja")
      private val region = "us-east-1"

      override def describeTrustedAdvisorChecks
          : CTX[DescribeTrustedAdvisorChecksResult] = ReaderT {
        aws: AWSConfig =>
          IO {
            client(aws).describeTrustedAdvisorChecks(request)
          }
      }

      override def describeTrustedAdvisorCheck(
          checkId: String
      ): CTX[DescribeTrustedAdvisorCheckResultResult] = ReaderT {
        aws: AWSConfig =>
          IO {
            client(aws).describeTrustedAdvisorCheckResult(
              new DescribeTrustedAdvisorCheckResultRequest()
                .withCheckId(checkId)
            )
          }
      }

      private def client(aws: AWSConfig): AWSSupport =
        AWSSupportClient.builder().withRegion(region).build()

    }
}
