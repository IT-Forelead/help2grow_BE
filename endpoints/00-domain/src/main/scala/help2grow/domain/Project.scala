package help2grow.domain

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Encoder
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto.deriveEncoder
import io.circe.refined._

@JsonCodec(decodeOnly = true)
case class Project(
    id: ProjectId,
    repo: NonEmptyString,
    owner: NonEmptyString,
    token: NonEmptyString,
    description: Option[NonEmptyString],
  )

object Project {
  implicit val encoder: Encoder[Project] = deriveEncoder[Project].mapJsonObject(_.remove("token"))
}
