package infra

import cats.data.ReaderT
import cats.effect.IO

package object aws {
  final case class AWSConfig(region: String)
  type CTX[A] = ReaderT[IO, AWSConfig, A]

  sealed abstract class TrustedAdvisorLanguage(val iso639: String)
  object TrustedAdvisorLanguage {
    final object EN extends TrustedAdvisorLanguage("en")
  }
}
