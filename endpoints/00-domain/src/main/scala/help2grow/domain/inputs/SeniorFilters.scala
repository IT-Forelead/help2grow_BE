package help2grow.domain.inputs

import help2grow.domain.SkillId

case class SeniorFilters(
    mainSkill: Option[SkillId] = None
  )

object SeniorFilters {
  val Empty: SeniorFilters = SeniorFilters()
}
