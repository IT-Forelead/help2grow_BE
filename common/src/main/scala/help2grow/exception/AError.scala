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

  sealed trait ChatError extends AError
  object ChatError {
    final case class NotFound(chatId: UUID) extends ChatError {
      override def cause: String = s"Chat not found by id [$chatId]"
    }
  }
  sealed trait AuthError extends AError
  object AuthError {
    final case class NoSuchUser(cause: String) extends AuthError
    final case class InvalidToken(cause: String) extends AuthError
    final case class AuthenticationException(cause: String) extends AuthError
    final case class AuthorizationException(cause: String) extends AuthError
    final case class PasswordDoesNotMatch(cause: String) extends AuthError
  }
}
