package help2grow.routes

import cats.Monad
import cats.MonadThrow
import cats.data.OptionT
import cats.implicits.toFlatMapOps
import io.estatico.newtype.ops._
import org.http4s.AuthedRoutes
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.typelevel.log4cats.Logger
import uz.scala.http4s.syntax.all.deriveEntityEncoder
import uz.scala.http4s.syntax.all.http4SyntaxReqOps
import uz.scala.http4s.utils.Routes
import uz.scala.integration.github.GithubClient
import uz.scala.integration.github.requests.CreateLabel

import help2grow.algebras.ProjectsAlgebra
import help2grow.domain.AuthedUser
import help2grow.domain.ProjectId
import help2grow.domain.enums.Role
import help2grow.domain.inputs.LabelInput

final case class IssuesRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    githubClient: GithubClient[F],
    projects: ProjectsAlgebra[F],
  )(implicit
    logger: Logger[F]
  ) extends Routes[F, AuthedUser] {
  override val path = "/issues"

  override val public: HttpRoutes[F] = HttpRoutes.empty[F]

  override val `private`: AuthedRoutes[AuthedUser, F] = AuthedRoutes.of {
    case GET -> Root / UUIDVar(projectId) as _ =>
      OptionT(projects.findById(projectId.coerce[ProjectId])).foldF(
        BadRequest(s"Unknown projectId [$projectId]")
      ) { project =>
        githubClient.getIssues(project.owner, project.repo, project.token).flatMap(Ok(_))
      }

    case ar @ POST -> Root / "label" as user
         if user.role == Role.Senior || user.role == Role.TechAdmin =>
      ar.req.decodeR[LabelInput] { labelInput =>
        OptionT(projects.findById(labelInput.projectId)).foldF(
          BadRequest(s"Unknown projectId [${labelInput.projectId}]")
        ) { project =>
          val createLabel = CreateLabel(
            owner = project.owner,
            repository = project.repo,
            name = labelInput.name,
            color = labelInput.color,
            description = labelInput.description,
          )
          githubClient.createLabel(createLabel, project.token).flatMap(Ok(_))
        }

      }
  }
}
