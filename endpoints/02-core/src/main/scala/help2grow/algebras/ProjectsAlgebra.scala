package help2grow.algebras

import cats.Monad
import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps

import help2grow.domain.Project
import help2grow.domain.ProjectId
import help2grow.domain.inputs.ProjectInput
import help2grow.effects.Calendar
import help2grow.effects.GenUUID
import help2grow.repos.ProjectsRepository
import help2grow.utils.ID

trait ProjectsAlgebra[F[_]] {
  def create(projectInput: ProjectInput): F[ProjectId]
  def get: F[List[Project]]
  def findById(projectId: ProjectId): F[Option[Project]]
}

object ProjectsAlgebra {
  def make[F[_]: Monad: GenUUID: Calendar](
      projectsRepository: ProjectsRepository[F]
    ): ProjectsAlgebra[F] = new ProjectsAlgebra[F] {
    override def create(projectInput: ProjectInput): F[ProjectId] =
      for {
        id <- ID.make[F, ProjectId]
        project = Project(
          id = id,
          repo = projectInput.repo,
          owner = projectInput.owner,
          token = projectInput.token,
          description = projectInput.description,
        )
        _ <- projectsRepository.create(project)
      } yield id
    override def get: F[List[Project]] =
      projectsRepository.get
    override def findById(projectId: ProjectId): F[Option[Project]] =
      projectsRepository.findById(projectId)
  }
}
