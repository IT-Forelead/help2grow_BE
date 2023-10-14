package help2grow.auth

import help2grow.domain.JwtAccessTokenKey
import help2grow.domain.TokenExpiration

case class AuthConfig(
    tokenKey: JwtAccessTokenKey,
    accessTokenExpiration: TokenExpiration,
    refreshTokenExpiration: TokenExpiration,
  )
