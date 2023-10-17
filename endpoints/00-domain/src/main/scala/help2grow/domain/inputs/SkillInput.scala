package help2grow.domain.inputs

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._
@JsonCodec
case class SkillInput(name: NonEmptyString)
