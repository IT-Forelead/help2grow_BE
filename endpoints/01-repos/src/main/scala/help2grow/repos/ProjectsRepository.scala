package help2grow.repos

import cats.data.NonEmptyList
import cats.effect.Async
import cats.effect.Resource
import skunk._
import uz.scala.skunk.syntax.all.skunkSyntaxCommandOps
import uz.scala.skunk.syntax.all.skunkSyntaxQueryVoidOps

import help2grow.domain.Project
import help2grow.repos.sql.ProjectSkillsSql
import help2grow.repos.sql.ProjectsSql
import help2grow.repos.sql.dto.ProjectSkill
trait ProjectsRepository[F[_]] {
  def get: F[List[Project]]
  def create(project: Project): F[Unit]
  def createSkills(skills: NonEmptyList[ProjectSkill]): F[Unit]
}

object ProjectsRepository {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): ProjectsRepository[F] = new ProjectsRepository[F] {
    override def get: F[List[Project]] =
      ProjectsSql.select.all
    override def create(project: Project): F[Unit] =
      ProjectsSql.insert.execute(project)
    override def createSkills(skills: NonEmptyList[ProjectSkill]): F[Unit] = {
      val skillsList = skills.toList
      ProjectSkillsSql.insertBatch(skillsList).execute(skillsList)
    }
  }
}
