package help2grow.domain

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

@JsonCodec
case class Label(
    id: LabelId,
    name: NonEmptyString,
    color: NonEmptyString,
    description: Option[NonEmptyString],
  )
