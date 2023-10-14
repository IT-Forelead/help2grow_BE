package help2grow.repos

import cats.effect.Async
import cats.effect.Resource
import skunk._
import uz.scala.skunk.syntax.all.skunkSyntaxCommandOps
import uz.scala.skunk.syntax.all.skunkSyntaxQueryOps
import help2grow.domain.AuthedUser.SeniorUser
import help2grow.domain.{PersonId, Skill}
import help2grow.domain.inputs.SeniorFilters
import help2grow.repos.sql.{SeniorsSql, UserSkillsSql}
import help2grow.repos.sql.dto.SeniorData
trait SeniorsRepository[F[_]] {
  def get(seniorFilters: SeniorFilters): F[List[SeniorUser]]
  def create(seniorData: SeniorData): F[Unit]
  def getSkills(userId: PersonId): F[List[Skill]]
}

object SeniorsRepository {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): SeniorsRepository[F] = new SeniorsRepository[F] {
    override def get(seniorFilters: SeniorFilters): F[List[SeniorUser]] = {
      val query = SeniorsSql.get(seniorFilters)
      query.fragment.query(SeniorsSql.codec).queryList(query.argument)
    }
    override def create(seniorData: SeniorData): F[Unit] =
      SeniorsSql.insert.execute(seniorData)

    override def getSkills(
        userId: PersonId
      ): F[List[Skill]] =
      UserSkillsSql.selectUserSkills.queryList(userId)
  }
}
