package help2grow.repos.sql

import skunk._
import skunk.implicits._

import help2grow.domain.Skill
import help2grow.domain.SkillId

private[repos] object SkillsSql extends Sql[SkillId] {
  val codec = (id *: nes).to[Skill]

  val selectAll: Query[Void, Skill] =
    sql"""SELECT * from skills""".query(codec)

  val insert: Command[Skill] =
    sql"""INSERT INTO skills VALUES ($codec)""".command
}
