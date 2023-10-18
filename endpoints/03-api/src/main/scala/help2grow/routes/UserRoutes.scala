package help2grow.routes

import cats.Monad
import cats.MonadThrow
import cats.implicits.catsSyntaxOptionId
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
import help2grow.domain.PersonId
import help2grow.domain.enums.Role
import help2grow.domain.inputs.JuniorInput
import help2grow.domain.inputs.SeniorFilters
import help2grow.domain.inputs.SeniorInput
import help2grow.domain.inputs.UserFilters

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
      case req @ POST -> Root / "junior" =>
        req.decodeR[JuniorInput] { juniorInput =>
          users.createJunior(juniorInput).flatMap(Created(_))
        }
    }

  override val `private`: AuthedRoutes[AuthedUser, F] = AuthedRoutes.of {
    case ar @ POST -> Root / "seniors" as _ =>
      ar.req.decodeR[SeniorFilters] { filters =>
        users.getSeniors(filters).flatMap(Ok(_))
      }
    case ar @ POST -> Root / "juniors" as _ =>
      ar.req.decodeR[UserFilters] { filters =>
        users.getJuniors(filters.copy(role = Role.Junior.some)).flatMap(Ok(_))
      }
    case GET -> Root / "skills" / UUIDVar(uuid) as _ =>
      users.getSkills(PersonId(uuid)).flatMap(Ok(_))
  }
}
