package help2grow.domain.auth

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

import help2grow.EmailAddress

@JsonCodec
case class Credentials(email: EmailAddress, password: NonEmptyString)
