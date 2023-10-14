package help2grow.repos.sql

import eu.timepit.refined.types.string.NonEmptyString

import help2grow.domain.PersonId
import help2grow.domain.SkillId

object dto {
  case class SeniorData(
      userId: PersonId,
      github: NonEmptyString,
      linkedIn: NonEmptyString,
      mainSkill: SkillId,
      suggestion: NonEmptyString,
    )

  case class UserSkill(userId: PersonId, skillId: SkillId)
}
