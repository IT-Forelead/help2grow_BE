package help2grow.repos.sql

import java.time.ZonedDateTime

import cats.implicits.catsSyntaxOptionId
import shapeless.HNil
import skunk._
import skunk.implicits._
import uz.scala.skunk.syntax.all.skunkSyntaxFragmentOps

import help2grow.EmailAddress
import help2grow.domain.AuthedUser.User
import help2grow.domain.PersonId
import help2grow.domain.auth.AccessCredentials
import help2grow.domain.inputs.UserFilters

private[repos] object UsersSql extends Sql[PersonId] {
  val codec =
    (id *: zonedDateTime *: nes *: nes *: role *: email *: zonedDateTime.opt *: zonedDateTime.opt)
      .to[User]
  private val accessCredentialsDecoder: Decoder[AccessCredentials[User]] =
    (codec *: passwordHash).map {
      case user *: hash *: HNil =>
        AccessCredentials(
          data = user,
          password = hash,
        )
    }

  val findByLogin: Query[EmailAddress, AccessCredentials[User]] =
    sql"""SELECT id, created_at, firstname, lastname, role, email, updated_at, accepted_at, password FROM users
          WHERE email = $email LIMIT 1""".query(accessCredentialsDecoder)

  private def searchFilter(filters: UserFilters): List[Option[AppliedFragment]] =
    List(
      filters.role.map(sql"role = $role"),
      filters.id.map(sql"id = $id"),
    )

  def get(filters: UserFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""
         SELECT id, created_at, firstname, lastname, role, email, updated_at, accepted_at FROM users
       """
    baseQuery(Void).whereAndOpt(searchFilter(filters))
  }

  val insert: Command[AccessCredentials[User]] =
    sql"""INSERT INTO users VALUES ($id, $zonedDateTime, $nes, $nes, $role, $email, ${zonedDateTime.opt}, ${zonedDateTime.opt}, $passwordHash)"""
      .command
      .contramap { (u: AccessCredentials[User]) =>
        u.data.id *: u.data.createdAt *: u.data.firstname *: u.data.lastname *: u.data.role *:
          u.data.email *: u.data.updatedAt *: u.data.acceptedAt *: u.password *: EmptyTuple
      }

  val update: Command[User] =
    sql"""UPDATE users SET
          updated_at = ${zonedDateTime.opt},
          accepted_at = ${zonedDateTime.opt}
          WHERE id = $id
      """
      .command
      .contramap {
        case u: User =>
          ZonedDateTime.now().some *: u.acceptedAt *: u.id *: EmptyTuple
      }
}
