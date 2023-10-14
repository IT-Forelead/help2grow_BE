package help2grow.http

import cats.effect.Async
import help2grow.Algebras
import help2grow.domain.AuthedUser
import org.http4s.server
import uz.scala.http4s.HttpServerConfig
case class Environment[F[_]: Async](
    config: HttpServerConfig,
    middleware: server.AuthMiddleware[F, AuthedUser],
    algebras: Algebras[F],
  )
