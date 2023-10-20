package help2grow.domain

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

@JsonCodec
case class Project(
    id: ProjectId,
    repo: NonEmptyString,
    owner: NonEmptyString,
    description: Option[NonEmptyString],
  )
