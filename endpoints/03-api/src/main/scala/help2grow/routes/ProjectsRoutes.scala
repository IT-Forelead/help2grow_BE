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

import help2grow.algebras.ProjectsAlgebra
import help2grow.domain.AuthedUser
import help2grow.domain.enums.Role
import help2grow.domain.inputs.ProjectInput

final case class ProjectsRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    projects: ProjectsAlgebra[F]
  )(implicit
    logger: Logger[F]
  ) extends Routes[F, AuthedUser] {
  override val path = "/projects"

  override val public: HttpRoutes[F] = HttpRoutes.empty[F]

  override val `private`: AuthedRoutes[AuthedUser, F] = AuthedRoutes.of {
    case GET -> Root as _ =>
      projects.get.flatMap(Ok(_))

    case ar @ POST -> Root as user if user.role == Role.Senior || user.role == Role.TechAdmin =>
      ar.req.decodeR[ProjectInput] { createProject =>
        projects.create(createProject).flatMap(Ok(_))
      }
  }
}
