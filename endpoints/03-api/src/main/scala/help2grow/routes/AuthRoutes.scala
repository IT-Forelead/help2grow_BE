package help2grow.routes

import cats.Monad
import cats.MonadThrow
import cats.implicits.catsSyntaxApply
import cats.implicits.toFlatMapOps
import org.http4s.AuthedRoutes
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.typelevel.log4cats.Logger
import uz.scala.http4s.syntax.all.deriveEntityEncoder
import uz.scala.http4s.syntax.all.http4SyntaxReqOps
import uz.scala.http4s.utils.Routes

import help2grow.auth.impl.Auth
import help2grow.domain.AuthedUser
import help2grow.domain.auth.Credentials

final case class AuthRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    auth: Auth[F, AuthedUser]
  )(implicit
    logger: Logger[F]
  ) extends Routes[F, AuthedUser] {
  override val path = "/auth"

  override val public: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "login" =>
        req.decodeR[Credentials] { credentials =>
          auth.login(credentials).flatMap(Ok(_))
        }
      case req @ GET -> Root / "refresh" =>
        auth.refresh(req).flatMap(Ok(_))
    }

  override val `private`: AuthedRoutes[AuthedUser, F] = AuthedRoutes.of {
    case ar @ GET -> Root / "logout" as user =>
      auth.destroySession(ar.req, user.email) *> NoContent()
  }
}
