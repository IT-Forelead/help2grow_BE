package help2grow.domain
package inputs

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

import help2grow.Phone
import help2grow.domain.SkillId

@JsonCodec
case class SeniorInput(
    phone: Phone,
    password: NonEmptyString,
    firstname: NonEmptyString,
    lastname: NonEmptyString,
    github: NonEmptyString,
    linkedIn: NonEmptyString,
    skill: List[SkillId],
    mainSkill: SkillId,
    suggestion: NonEmptyString,
  )
