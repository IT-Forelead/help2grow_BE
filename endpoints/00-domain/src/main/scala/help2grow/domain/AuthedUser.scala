package help2grow.domain

import java.time.ZonedDateTime

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

import help2grow.Phone
import help2grow.domain.enums.Role

@JsonCodec
sealed trait AuthedUser {
  val id: PersonId
  val firstname: NonEmptyString
  val lastname: NonEmptyString
  val role: Role
  val phone: Phone
}

object AuthedUser {
  @JsonCodec
  case class User(
      id: PersonId,
      createdAt: ZonedDateTime,
      firstname: NonEmptyString,
      lastname: NonEmptyString,
      role: Role,
      phone: Phone,
    ) extends AuthedUser

  @JsonCodec
  case class SeniorUser(
      id: PersonId,
      createdAt: ZonedDateTime,
      firstname: NonEmptyString,
      lastname: NonEmptyString,
      role: Role,
      phone: Phone,
      github: NonEmptyString,
      linkedIn: NonEmptyString,
      mainSkill: SkillId,
      suggestion: NonEmptyString,
    )
}
