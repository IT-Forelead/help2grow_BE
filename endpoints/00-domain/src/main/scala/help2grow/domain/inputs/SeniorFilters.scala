package help2grow.domain.inputs

import io.circe.generic.JsonCodec

import help2grow.domain.SkillId

@JsonCodec
case class SeniorFilters(
    mainSkill: Option[SkillId] = None
  )

object SeniorFilters {
  val Empty: SeniorFilters = SeniorFilters()
}
