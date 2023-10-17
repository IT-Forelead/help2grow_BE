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

import help2grow.algebras.SkillsAlgebra
import help2grow.domain.AuthedUser
import help2grow.domain.inputs.SkillInput

final case class SkillsRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    skills: SkillsAlgebra[F]
  )(implicit
    logger: Logger[F]
  ) extends Routes[F, AuthedUser] {
  override val path = "/skill"

  override val public: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        skills.get.flatMap(Ok(_))
    }

  override val `private`: AuthedRoutes[AuthedUser, F] = AuthedRoutes.of {
    case ar @ POST -> Root as _ =>
      ar.req.decodeR[SkillInput] { skillInput =>
        skills.createSkill(skillInput.name).flatMap(Created(_))
      }
  }
}
