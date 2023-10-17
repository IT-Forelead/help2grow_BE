package help2grow

import help2grow.algebras.SkillsAlgebra
import help2grow.algebras.UsersAlgebra
import help2grow.auth.impl.Auth
import help2grow.domain.AuthedUser

case class Algebras[F[_]](
    auth: Auth[F, AuthedUser],
    users: UsersAlgebra[F],
    skills: SkillsAlgebra[F],
  )
