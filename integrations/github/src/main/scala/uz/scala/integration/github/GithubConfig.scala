package uz.scala.integration.github

import java.net.URI

import eu.timepit.refined.types.string.NonEmptyString

case class GithubConfig(
    enabled: Boolean,
    apiUri: URI,
    token: NonEmptyString,
  )
