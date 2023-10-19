package uz.scala.integration.github.requests

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.JsonCodec
import io.circe.refined._
import sttp.model.Method
import uz.scala.integration.github.responses.Issue
import uz.scalal.sttp.SttpRequest

@JsonCodec
case class GetIssues(
    owner: NonEmptyString,
    repository: NonEmptyString,
  )

object GetIssues {
  implicit val sttpRequest: SttpRequest[GetIssues, List[Issue]] =
    new SttpRequest[GetIssues, List[Issue]] {
      val method: Method = Method.GET
      override def path: Path = r => s"/repos/${r.owner}/${r.repository}/issues"
      override def headers: Headers = _ =>
        Map(
          "Accept" -> "application/vnd.github+json",
          "X-GitHub-Api-Version" -> "2022-11-28",
        )
      override def body: Body = noBody
    }
}
