package uz.scala.integration.github.responses

import enumeratum.EnumEntry.Lowercase
import enumeratum._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._

@JsonCodec
case class Issue(
    url: NonEmptyString,
    title: NonEmptyString,
    number: Int,
    labels: List[Issue.Label],
    state: Issue.State,
    body: Option[NonEmptyString],
  )
object Issue {
  @JsonCodec
  case class Label(
      id: Long,
      color: String,
      name: String,
      description: Option[String],
    )

  sealed trait State extends Lowercase
  object State extends Enum[State] with CirceEnum[State] {
    case object Open extends State
    case object Closed extends State
    case object All extends State
    override def values: IndexedSeq[State] = findValues
  }
}
