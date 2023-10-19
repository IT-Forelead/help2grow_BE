package help2grow.http

import cats.effect.Async
import org.http4s.server
import uz.scala.http4s.HttpServerConfig
import uz.scala.integration.github.GithubClient

import help2grow.Algebras
import help2grow.domain.AuthedUser
case class Environment[F[_]: Async](
    config: HttpServerConfig,
    middleware: server.AuthMiddleware[F, AuthedUser],
    algebras: Algebras[F],
    githubClient: GithubClient[F],
  )
