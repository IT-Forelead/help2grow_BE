package help2grow.repos.sql

import skunk._
import skunk.implicits._

import help2grow.domain.Label
import help2grow.domain.LabelId

private[repos] object LabelsSql extends Sql[LabelId] {
  private val codec = (id *: nes *: nes *: nes.opt).to[Label]

  val insert: Command[Label] =
    sql"""INSERT INTO labels VALUES ($codec)""".command

  val select: Query[Void, Label] =
    sql"""SELECT * from labels""".query(codec)
}
