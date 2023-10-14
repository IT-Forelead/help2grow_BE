package help2grow

import cats.effect.Async
import cats.effect.Resource
import help2grow.repos.UsersRepository
import skunk.Session

case class Repositories[F[_]](
    users: UsersRepository[F]
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      users = UsersRepository.make[F]
    )
}
