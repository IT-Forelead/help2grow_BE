package help2grow.setup

import cats.Monad
import cats.data.OptionT
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import cats.effect.std.Dispatcher
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import eu.timepit.refined.pureconfig._
import org.http4s.server
import org.typelevel.log4cats.Logger
import pureconfig.generic.auto.exportReader
import uz.scala.flyway.Migrations
import uz.scala.redis.RedisClient
import uz.scala.skunk.SkunkSession

import help2grow.Algebras
import help2grow.Phone
import help2grow.Repositories
import help2grow.algebras.UsersAlgebra
import help2grow.auth.impl.Auth
import help2grow.auth.impl.LiveMiddleware
import help2grow.domain.AuthedUser
import help2grow.domain.auth.AccessCredentials
import help2grow.http.{ Environment => ServerEnvironment }
import help2grow.utils.ConfigLoader

case class Environment[F[_]: Async: Logger: Dispatcher](
    config: Config,
    repositories: Repositories[F],
    auth: Auth[F, AuthedUser],
    middleware: server.AuthMiddleware[F, AuthedUser],
  ) {
  private val Repositories(users, seniors) = repositories
  private val usersAlgebra = UsersAlgebra.make[F](users, seniors)
  private val algebras: Algebras[F] = Algebras[F](auth, usersAlgebra)

  lazy val toServer: ServerEnvironment[F] =
    ServerEnvironment(
      config = config.http,
      middleware = middleware,
      algebras = algebras,
    )
}
object Environment {
  private def findUser[F[_]: Monad](
      repositories: Repositories[F]
    ): Phone => F[Option[AccessCredentials[AuthedUser]]] = phone =>
    OptionT(repositories.users.find(phone))
      .map(identity[AccessCredentials[AuthedUser]])
      .value

  def make[F[_]: Async: Console: Logger]: Resource[F, Environment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F, Config])
      _ <- Resource.eval(Migrations.run[F](config.migrations))
      repositories <- SkunkSession.make[F](config.database).map { implicit session =>
        Repositories.make[F]
      }
      redis <- Redis[F].utf8(config.redis.uri.toString).map(RedisClient[F](_, config.redis.prefix))

      implicit0(dispatcher: Dispatcher[F]) <- Dispatcher.parallel[F]
      middleware = LiveMiddleware.make[F](config.auth, redis)
      auth = Auth.make[F](config.auth, findUser(repositories), redis)
    } yield Environment[F](config, repositories, auth, middleware)
}
