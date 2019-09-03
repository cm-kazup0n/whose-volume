package infra

import cats.Show
import cats.effect.IO
import cats.implicits._
import cats.tagless._

import scala.language.higherKinds

@finalAlg
@autoFunctorK
trait Console[F[_]] {
  def printLn[A: Show](a: A): F[Unit]
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object Console {
  implicit object ioConsole extends Console[IO] {
    override def printLn[A: Show](a: A): IO[Unit] = IO { println(a.show) }
  }
}
