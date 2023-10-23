package help2grow.repos

import cats.data.OptionT
import cats.effect.Async
import cats.effect.Resource
import cats.implicits.catsSyntaxApplicativeErrorId
import cats.implicits.catsSyntaxMonadError
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toFunctorOps
import skunk._
import uz.scala.skunk.syntax.all.skunkSyntaxCommandOps
import uz.scala.skunk.syntax.all.skunkSyntaxQueryOps

import help2grow.EmailAddress
import help2grow.domain.AuthedUser.User
import help2grow.domain.PersonId
import help2grow.domain.auth.AccessCredentials
import help2grow.domain.inputs.UserFilters
import help2grow.exception.AError
import help2grow.exception.AError.UserError
import help2grow.repos.sql.UsersSql
trait UsersRepository[F[_]] {
  def get(filter: UserFilters): F[List[User]]
  def find(email: EmailAddress): F[Option[AccessCredentials[User]]]
  def create(userAndHash: AccessCredentials[User]): F[Unit]
  def update(personId: PersonId)(update: User => User): F[Unit]
}

object UsersRepository {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): UsersRepository[F] = new UsersRepository[F] {
    override def get(filter: UserFilters): F[List[User]] = {
      val query = UsersSql.get(filter)
      query.fragment.query(UsersSql.codec).queryList(query.argument)
    }
    override def find(email: EmailAddress): F[Option[AccessCredentials[User]]] =
      UsersSql.findByLogin.queryOption(email)

    override def create(userAndHash: AccessCredentials[User]): F[Unit] =
      UsersSql.insert.execute(userAndHash).adaptError {
        case SqlState.UniqueViolation(_) =>
          UserError.PhoneInUse("The email address already exists")
      }
    override def update(personId: PersonId)(update: User => User): F[Unit] =
      OptionT(get(UserFilters(id = personId.some)).map(_.headOption))
        .cataF(
          AError.Internal(s"User not found by id [${personId.value}]").raiseError[F, Unit],
          user => UsersSql.update.execute(update(user)),
        )
  }
}
