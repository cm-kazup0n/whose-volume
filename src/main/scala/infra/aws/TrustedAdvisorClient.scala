package infra.aws

import cats.data.ReaderT
import cats.effect.IO
import cats.tagless.finalAlg
import com.amazonaws.services.support.model.{
  DescribeTrustedAdvisorCheckResultRequest,
  DescribeTrustedAdvisorCheckResultResult,
  DescribeTrustedAdvisorChecksRequest,
  DescribeTrustedAdvisorChecksResult
}
import com.amazonaws.services.support.{AWSSupport, AWSSupportClient}

import scala.language.higherKinds

@finalAlg
trait TrustedAdvisorClient[F[_]] {
  def describeTrustedAdvisorChecks(
      lang: TrustedAdvisorLanguage
  ): F[DescribeTrustedAdvisorChecksResult]

  def describeTrustedAdvisorCheck(
      checkId: String
  ): F[DescribeTrustedAdvisorCheckResultResult]
}

object TrustedAdvisorClient {

  implicit val onAWSClient: TrustedAdvisorClient[CTX] =
    new TrustedAdvisorClient[CTX] {

      private val region = "us-east-1"

      override def describeTrustedAdvisorChecks(
          lang: TrustedAdvisorLanguage
      ): CTX[DescribeTrustedAdvisorChecksResult] = ReaderT { aws: AWSConfig =>
        IO {
          val request =
            new DescribeTrustedAdvisorChecksRequest().withLanguage(lang.iso639)
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
