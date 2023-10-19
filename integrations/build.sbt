name := "integrations"

lazy val integration_github = project.in(file("github"))


aggregateProjects(
  integration_github,
)
