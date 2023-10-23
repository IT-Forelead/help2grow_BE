package help2grow.domain.inputs

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

import help2grow.domain.ProjectId

@JsonCodec
case class LabelInput(
    projectId: ProjectId,
    name: NonEmptyString,
    color: NonEmptyString,
    description: Option[NonEmptyString],
  )
