package help2grow.algebras

import cats.Monad
import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import eu.timepit.refined.types.string.NonEmptyString

import help2grow.domain.Skill
import help2grow.domain.SkillId
import help2grow.effects.Calendar
import help2grow.effects.GenUUID
import help2grow.repos.SkillsRepository
import help2grow.utils.ID

trait SkillsAlgebra[F[_]] {
  def createSkill(name: NonEmptyString): F[SkillId]
  def get: F[List[Skill]]
}

object SkillsAlgebra {
  def make[F[_]: Monad: GenUUID: Calendar](
      skillsRepository: SkillsRepository[F]
    ): SkillsAlgebra[F] = new SkillsAlgebra[F] {
    override def createSkill(
        name: NonEmptyString
      ): F[SkillId] =
      for {
        id <- ID.make[F, SkillId]
        skill = Skill(
          id = id,
          name = name,
        )
        _ <- skillsRepository.create(skill)
      } yield id

    override def get: F[List[Skill]] =
      skillsRepository.get
  }
}
