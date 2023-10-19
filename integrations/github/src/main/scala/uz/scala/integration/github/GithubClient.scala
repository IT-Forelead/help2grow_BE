package uz.scala.integration.github

import cats.Applicative
import cats.effect.Sync
import eu.timepit.refined.types.string.NonEmptyString
import uz.scala.integration.github.requests.GetIssues
import uz.scala.integration.github.responses.Issue
import uz.scalal.sttp.SttpBackends
import uz.scalal.sttp.SttpClient
import uz.scalal.sttp.SttpClientAuth

trait GithubClient[F[_]] {
  def getIssues(owner: NonEmptyString, repo: NonEmptyString): F[List[Issue]]
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
  }

  private class NoOpTelegramClient[F[_]: Applicative] extends GithubClient[F] {
    override def getIssues(owner: NonEmptyString, repo: NonEmptyString): F[List[Issue]] =
      Applicative[F].pure(List.empty)
  }
}
