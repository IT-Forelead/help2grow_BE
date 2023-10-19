package uz.scalal.sttp

import cats.data.Reader
import cats.kernel.Semigroup
import sttp.client3._

trait SttpClientAuth { self =>
  def reader[T, R]: SttpClientAuth.AuthRequestT[T, R]
  def show: String

  def apply[T, R](req: Request[T, R]): Request[T, R] = reader.run(req)

  def compose(auth: SttpClientAuth): SttpClientAuth =
    new SttpClientAuth {
      def reader[T, R]: SttpClientAuth.AuthRequestT[T, R] =
        auth.reader[T, R].compose(self.reader[T, R])

      def show: String = s"${self.show} | ${auth.show}"
    }
}

object SttpClientAuth {
  type AuthRequestT[T, R] = Reader[Request[T, R], Request[T, R]]
  def bearer(token: String): SttpClientAuth = new SttpClientAuth {
    def reader[T, R]: AuthRequestT[T, R] = Reader(_.auth.bearer(token))
    def show: String = s"bearer(${mask(token)})"
  }

  implicit def semigroup: Semigroup[SttpClientAuth] =
    (x: SttpClientAuth, y: SttpClientAuth) => y.compose(x)

  private def mask(s: String): String =
    s.patch(4, "*" * (s.length - 4), s.length)
}