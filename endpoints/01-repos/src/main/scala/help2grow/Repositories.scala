package help2grow

import cats.effect.Async
import cats.effect.Resource
import skunk.Session

import help2grow.repos._

case class Repositories[F[_]](
    users: UsersRepository[F],
    skills: SkillsRepository[F],
    seniors: SeniorsRepository[F],
    projects: ProjectsRepository[F],
    labels: LabelsRepository[F],
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      users = UsersRepository.make[F],
      skills = SkillsRepository.make[F],
      seniors = SeniorsRepository.make[F],
      projects = ProjectsRepository.make[F],
      labels = LabelsRepository.make[F],
    )
}
