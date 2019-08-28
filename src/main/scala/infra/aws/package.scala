package infra

import cats.data.ReaderT
import cats.effect.IO

package object aws {
  type CTX[A] = ReaderT[IO, AWSConfig, A]
}
