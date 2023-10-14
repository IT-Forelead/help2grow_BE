package help2grow.routes

import cats.Monad
import cats.MonadThrow
import cats.implicits.toFlatMapOps
import org.http4s.AuthedRoutes
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.typelevel.log4cats.Logger
import uz.scala.http4s.syntax.all.deriveEntityEncoder
import uz.scala.http4s.syntax.all.http4SyntaxReqOps
import uz.scala.http4s.utils.Routes

import help2grow.algebras.UsersAlgebra
import help2grow.domain.AuthedUser
import help2grow.domain.inputs.SeniorInput

final case class UserRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    users: UsersAlgebra[F]
  )(implicit
    logger: Logger[F]
  ) extends Routes[F, AuthedUser] {
  override val path = "/user"

  override val public: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "senior" =>
        req.decodeR[SeniorInput] { seniorData =>
          users.createSenior(seniorData).flatMap(Created(_))
        }
    }

  override val `private`: AuthedRoutes[AuthedUser, F] = AuthedRoutes.empty[AuthedUser, F]
}
