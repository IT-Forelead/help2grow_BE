package help2grow.repos.sql

import skunk._
import skunk.implicits._

import help2grow.domain.ProjectId
import help2grow.domain.Skill
import help2grow.repos.sql.dto.ProjectSkill

private[repos] object ProjectSkillsSql {
  private val codec = (ProjectsSql.id *: SkillsSql.id).to[ProjectSkill]

  val selectUserSkills: Query[ProjectId, Skill] =
    sql"""SELECT s.* from project_skills ps
         INNER JOIN skills s on s.id = us.skill_id
         WHERE ps.project_id = ${ProjectsSql.id}
         """.query(SkillsSql.codec)

  def insertBatch(skills: List[ProjectSkill]): Command[skills.type] =
    sql"""INSERT INTO project_skills VALUES ${codec.values.list(skills)}""".command
}
