package help2grow

import cats.effect.Async
import cats.effect.Resource
import skunk.Session

import help2grow.repos.SeniorsRepository
import help2grow.repos.UsersRepository

case class Repositories[F[_]](
    users: UsersRepository[F],
    seniors: SeniorsRepository[F],
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      users = UsersRepository.make[F],
      seniors = SeniorsRepository.make[F],
    )
}
