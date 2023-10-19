package uz.scala.integration.github.requests

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._
import sttp.model.Method
import uz.scala.integration.github.responses.Issue
import uz.scalal.sttp.SttpRequest
@JsonCodec
case class CreateLabel(
    owner: NonEmptyString,
    repository: NonEmptyString,
    name: NonEmptyString,
    color: NonEmptyString,
    description: Option[NonEmptyString] = None,
  )

object CreateLabel {
  @JsonCodec
  case class ReqBody(
      name: NonEmptyString,
      color: NonEmptyString,
      description: Option[NonEmptyString] = None,
    )
  implicit val sttpRequest: SttpRequest[CreateLabel, Issue.Label] =
    new SttpRequest[CreateLabel, Issue.Label] {
      val method: Method = Method.POST
      override def path: Path = r => s"/repos/${r.owner}/${r.repository}/labels"
      override def headers: Headers = _ =>
        Map(
          "Accept" -> "application/vnd.github+json",
          "X-GitHub-Api-Version" -> "2022-11-28",
        )
      override def body: Body = jsonBodyFrom(req => ReqBody(req.name, req.color, req.description))
    }
}
