package help2grow.domain.inputs

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

import help2grow.EmailAddress
import help2grow.domain.SkillId

@JsonCodec
case class JuniorInput(
    email: EmailAddress,
    password: NonEmptyString,
    firstname: NonEmptyString,
    lastname: NonEmptyString,
    skills: List[SkillId],
  )
