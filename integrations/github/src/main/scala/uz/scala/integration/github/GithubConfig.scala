package uz.scala.integration.github

import java.net.URI

case class GithubConfig(
    enabled: Boolean,
    apiUri: URI,
  )
