import Dependencies.*

name := "mailer"
libraryDependencies ++=
  Seq(
    javax.mailer,
    org.typelevel.log4cats,
    org.github.retry,
  )

dependsOn(LocalProject("common"))
