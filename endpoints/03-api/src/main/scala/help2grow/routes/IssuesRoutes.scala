package help2grow.routes

import cats.Monad
import cats.MonadThrow
import cats.implicits.toFlatMapOps
import org.http4s.AuthedRoutes
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.typelevel.log4cats.Logger
import uz.scala.http4s.syntax.all.deriveEntityEncoder
import uz.scala.http4s.utils.Routes
import uz.scala.integration.github.GithubClient
import uz.scala.syntax.refined.commonSyntaxAutoRefineV

import help2grow.domain.AuthedUser

final case class IssuesRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    githubClient: GithubClient[F]
  )(implicit
    logger: Logger[F]
  ) extends Routes[F, AuthedUser] {
  override val path = "/issues"

  override val public: HttpRoutes[F] = HttpRoutes.empty[F]

  override val `private`: AuthedRoutes[AuthedUser, F] = AuthedRoutes.of {
    case GET -> Root as _ =>
      githubClient.getIssues("IT-Forelead", "help2grow_BE").flatMap(Ok(_))
  }
}
