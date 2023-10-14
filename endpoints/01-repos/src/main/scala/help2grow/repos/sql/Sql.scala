package help2grow.repos.sql

import help2grow.effects.IsUUID
import skunk.Codec

abstract class Sql[T: IsUUID] {
  val id: Codec[T] = identification[T]
}
