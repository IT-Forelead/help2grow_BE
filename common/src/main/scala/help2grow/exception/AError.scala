package help2grow.exception

import java.util.UUID
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax.EncoderOps

@ConfiguredJsonCodec
sealed trait AError extends Throwable {
  def cause: String
  override def getMessage: String = s"${AError.Prefix}${(this: AError).asJson}"
}

object AError {
  val Prefix: String = "AError: "
  implicit val config: Configuration = Configuration.default.withDiscriminator("Kind")
  final case class Internal(cause: String) extends AError

  sealed trait UserError extends AError

  object UserError {
    final case class PhoneInUse(cause: String) extends UserError
  }
  sealed trait AuthError extends AError
  object AuthError {
    final case class NoSuchUser(cause: String) extends AuthError
    final case class InvalidToken(cause: String) extends AuthError
    final case class PasswordDoesNotMatch(cause: String) extends AuthError
    final case class EmailDoesNotConfirmed(cause: String) extends AuthError
    final case class LinkCodeExpired(cause: String) extends AuthError
  }
}
