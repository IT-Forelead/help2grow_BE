package help2grow.repos.sql

import skunk._
import skunk.implicits._

import help2grow.domain.Project
import help2grow.domain.ProjectId

private[repos] object ProjectsSql extends Sql[ProjectId] {
  private val codec = (id *: nes *: nes *: nes.opt).to[Project]

  val insert: Command[Project] =
    sql"""INSERT INTO projects VALUES ($codec)""".command

  val select: Query[Void, Project] =
    sql"""SELECT * from projects""".query(codec)
}
