package help2grow.repos.sql

import skunk._
import skunk.implicits._

import help2grow.domain.PersonId
import help2grow.domain.Skill
import help2grow.repos.sql.dto.UserSkill

private[repos] object UserSkillsSql {
  private val codec = (UsersSql.id *: SkillsSql.id).to[UserSkill]

  val selectUserSkills: Query[PersonId, Skill] =
    sql"""SELECT s.* from user_skills us
         INNER JOIN skills s on s.id = us.skill_id
         WHERE us.user_id = ${UsersSql.id}
         """.query(SkillsSql.codec)

  def insertBatch(skills: List[UserSkill]): Command[skills.type] =
    sql"""INSERT INTO user_skills VALUES ${codec.values.list(skills)}""".command
}
