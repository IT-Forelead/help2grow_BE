package help2grow.repos.sql

import skunk._
import skunk.implicits._

import help2grow.domain.PersonId
import help2grow.repos.sql.dto.UserSkill

private[repos] object UserSkillsSql {
  private val codec = (UsersSql.id *: SkillsSql.id).to[UserSkill]

  val selectUserSkills: Query[PersonId, UserSkill] =
    sql"""SELECT * user_skills WHERE user_id = ${UsersSql.id}""".query(codec)

  val insert: Command[UserSkill] =
    sql"""INSERT INTO user_skills VALUES ($codec)""".command
}
