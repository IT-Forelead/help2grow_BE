package help2grow.domain.inputs

import io.circe.generic.JsonCodec

import help2grow.domain.enums.Role

@JsonCodec
case class UserFilters(
    role: Option[Role] = None
  )

object UserFilters {
  val Empty: UserFilters = UserFilters()
}
