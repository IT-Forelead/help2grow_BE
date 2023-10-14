package help2grow

import help2grow.auth.impl.Auth
import help2grow.domain.AuthedUser

case class Algebras[F[_]](
    auth: Auth[F, AuthedUser]
  )
