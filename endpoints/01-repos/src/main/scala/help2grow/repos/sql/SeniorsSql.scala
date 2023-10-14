package help2grow.repos.sql

import skunk._
import skunk.implicits._
import uz.scala.skunk.syntax.all.skunkSyntaxFragmentOps

import help2grow.domain.AuthedUser.SeniorUser
import help2grow.domain.PersonId
import help2grow.domain.inputs.SeniorFilters
import help2grow.repos.sql.dto.SeniorData

private[repos] object SeniorsSql extends Sql[PersonId] {
  private val codecSeniorData = (id *: nes *: nes *: SkillsSql.id *: nes).to[SeniorData]
  val codec =
    (id *: zonedDateTime *: nes *: nes *: role *: phone *: nes *: nes *: SkillsSql.id *: nes)
      .to[SeniorUser]

  val insert: Command[SeniorData] =
    sql"""INSERT INTO seniors VALUES ($codecSeniorData)""".command

  private def searchFilter(filters: SeniorFilters): List[Option[AppliedFragment]] =
    List(
      filters.mainSkill.map(sql"s.main_skill = ${SkillsSql.id}")
    )

  def get(filters: SeniorFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""
         SELECT
           u.id, u.created_at, u.firstname, u.lastname, u.role, u.phone, s.github, s.linked_in, s.main_skill, s.suggestion
         FROM seniors s
         INNER JOIN users u on u.id = s.user_id
       """
    baseQuery(Void).whereAndOpt(searchFilter(filters))
  }
}
