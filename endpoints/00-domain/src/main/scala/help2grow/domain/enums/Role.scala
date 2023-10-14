package help2grow.domain.enums

import enumeratum.EnumEntry.Snakecase
import enumeratum._

sealed trait Role extends Snakecase
object Role extends Enum[Role] with CirceEnum[Role] {
  case object TechAdmin extends Role
  case object Junior extends Role
  case object Senior extends Role
  override def values: IndexedSeq[Role] = findValues
}
