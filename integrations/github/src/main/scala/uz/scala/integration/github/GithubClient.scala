package uz.scala.integration.github

import scala.util.Random

import cats.Applicative
import cats.effect.Sync
import cats.implicits.catsSyntaxOptionId
import eu.timepit.refined.types.string.NonEmptyString
import uz.scala.integration.github.requests.CreateLabel
import uz.scala.integration.github.requests.GetIssues
import uz.scala.integration.github.responses.Issue
import uz.scalal.sttp.SttpBackends
import uz.scalal.sttp.SttpClient
import uz.scalal.sttp.SttpClientAuth

trait GithubClient[F[_]] {
  def getIssues(owner: NonEmptyString, repo: NonEmptyString): F[List[Issue]]
  def createLabel(labelReq: CreateLabel): F[Issue.Label]
}

object GithubClient {
  def make[F[_]: Sync: SttpBackends.Simple](config: GithubConfig): GithubClient[F] =
    if (config.enabled)
      new Impl[F](config)
    else
      new NoOpTelegramClient[F]

  private class Impl[F[_]: Sync: SttpBackends.Simple](config: GithubConfig)
      extends GithubClient[F] {
    private lazy val client: SttpClient.CirceJson[F] =
      SttpClient.circeJson(
        config.apiUri,
        SttpClientAuth.bearer(config.token.value),
      )
    override def getIssues(owner: NonEmptyString, repo: NonEmptyString): F[List[Issue]] =
      client.request(GetIssues(owner, repo))

    override def createLabel(labelReq: CreateLabel): F[Issue.Label] =
      client.request(labelReq)
  }

  private class NoOpTelegramClient[F[_]: Applicative] extends GithubClient[F] {
    override def getIssues(owner: NonEmptyString, repo: NonEmptyString): F[List[Issue]] =
      Applicative[F].pure(List.empty)
    override def createLabel(labelReq: CreateLabel): F[Issue.Label] =
      Applicative[F].pure(
        Issue.Label(Random.between(1L, Long.MaxValue), "f29513", "BUG", "This is mock label".some)
      )
  }
}
