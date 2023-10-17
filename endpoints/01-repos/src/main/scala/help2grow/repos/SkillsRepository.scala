package help2grow.repos

import cats.effect.Async
import cats.effect.Resource
import skunk._
import uz.scala.skunk.syntax.all.skunkSyntaxCommandOps
import uz.scala.skunk.syntax.all.skunkSyntaxQueryVoidOps

import help2grow.domain.Skill
import help2grow.repos.sql.SkillsSql
trait SkillsRepository[F[_]] {
  def get: F[List[Skill]]
  def create(skill: Skill): F[Unit]
}

object SkillsRepository {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): SkillsRepository[F] = new SkillsRepository[F] {
    override def get: F[List[Skill]] =
      SkillsSql.selectAll.all
    override def create(skill: Skill): F[Unit] =
      SkillsSql.insert.execute(skill)
  }
}
