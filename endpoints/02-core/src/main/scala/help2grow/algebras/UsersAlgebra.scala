package help2grow.algebras

import scala.concurrent.duration.DurationInt

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.data.OptionT
import cats.effect.std.Random
import cats.implicits.catsSyntaxApplicativeErrorId
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import cats.implicits.toTraverseOps
import tsec.passwordhashers.PasswordHasher
import tsec.passwordhashers.jca.SCrypt
import uz.scala.mailer.Mailer
import uz.scala.mailer.data.Content
import uz.scala.mailer.data.Email
import uz.scala.mailer.data.Text
import uz.scala.mailer.data.types.Subject
import uz.scala.redis.RedisClient
import uz.scala.syntax.all.circeSyntaxDecoderOps
import uz.scala.syntax.refined.commonSyntaxAutoRefineV

import help2grow.AppDomain
import help2grow.domain.AuthedUser.SeniorUser
import help2grow.domain.AuthedUser.User
import help2grow.domain.PersonId
import help2grow.domain.Skill
import help2grow.domain.auth.AccessCredentials
import help2grow.domain.enums.Role
import help2grow.domain.inputs.JuniorInput
import help2grow.domain.inputs.SeniorFilters
import help2grow.domain.inputs.SeniorInput
import help2grow.domain.inputs.UserFilters
import help2grow.effects.Calendar
import help2grow.effects.GenUUID
import help2grow.exception.AError.AuthError.LinkCodeExpired
import help2grow.randomStr
import help2grow.repos.SeniorsRepository
import help2grow.repos.UsersRepository
import help2grow.repos.sql.dto.SeniorData
import help2grow.repos.sql.dto.UserSkill
import help2grow.utils.ID

trait UsersAlgebra[F[_]] {
  def createSenior(seniorInput: SeniorInput): F[PersonId]
  def createJunior(juniorInput: JuniorInput): F[PersonId]
  def getSeniors(seniorFilters: SeniorFilters): F[List[SeniorUser]]
  def getJuniors(filter: UserFilters): F[List[User]]
  def getSkills(userId: PersonId): F[List[Skill]]
  def confirmEmail(token: String): F[Unit]
}

object UsersAlgebra {
  def make[F[_]: MonadThrow: GenUUID: Calendar: Random](
      usersRepository: UsersRepository[F],
      seniorsRepository: SeniorsRepository[F],
    )(implicit
      P: PasswordHasher[F, SCrypt],
      mailer: Mailer[F],
      redis: RedisClient[F],
    ): UsersAlgebra[F] = new UsersAlgebra[F] {
    override def createSenior(
        seniorInput: SeniorInput
      ): F[PersonId] =
      for {
        id <- ID.make[F, PersonId]
        now <- Calendar[F].currentZonedDateTime
        user = User(
          id = id,
          createdAt = now,
          firstname = seniorInput.firstname,
          lastname = seniorInput.lastname,
          role = Role.Senior,
          email = seniorInput.email,
        )

        hash <- SCrypt.hashpw[F](seniorInput.password.value)

        accessCredentials = AccessCredentials(user, hash)
        _ <- usersRepository.create(accessCredentials)
        seniorData = SeniorData(
          userId = id,
          suggestion = seniorInput.suggestion,
          mainSkill = seniorInput.mainSkill,
          linkedIn = seniorInput.linkedIn,
          github = seniorInput.github,
        )
        _ <- NonEmptyList
          .fromList(
            seniorInput.skills.map(skillId => UserSkill(id, skillId))
          )
          .traverse { skills =>
            seniorsRepository.createSkills(skills)
          }
        _ <- seniorsRepository.create(seniorData)
        linkCode = randomStr[F](8)
        _ <- redis.put(s"confirm_$linkCode", id.value, 15.minutes)
        email = Email(
          from = "no-reply@it-forelead.uz",
          subject = Subject("Подтвердите свой адрес электронной почты"),
          content = Content(text =
            Text(
              s"""Добро пожаловать\n\n
                 Подтвердите свой адрес электронной почты,
                 откройте эту ссылку в браузере $AppDomain/auth/confirmemail?t=$linkCode
                 С Уважением Команда IT-Forelead"""
            ).some
          ),
          to = NonEmptyList.one(user.email),
        )
        _ <- mailer.send(email)
      } yield id

    override def createJunior(
        juniorInput: JuniorInput
      ): F[PersonId] =
      for {
        id <- ID.make[F, PersonId]
        now <- Calendar[F].currentZonedDateTime
        user = User(
          id = id,
          createdAt = now,
          firstname = juniorInput.firstname,
          lastname = juniorInput.lastname,
          role = Role.Junior,
          email = juniorInput.email,
        )

        hash <- SCrypt.hashpw[F](juniorInput.password.value)

        accessCredentials = AccessCredentials(user, hash)
        _ <- usersRepository.create(accessCredentials)
        _ <- NonEmptyList
          .fromList(
            juniorInput.skills.map(skillId => UserSkill(id, skillId))
          )
          .traverse { skills =>
            seniorsRepository.createSkills(skills)
          }
        linkCode = randomStr[F](8)
        _ <- redis.put(s"confirm_$linkCode", id.value, 15.minutes)
        email = Email(
          from = "no-reply@it-forelead.uz",
          subject = Subject("Подтвердите свой адрес электронной почты"),
          content = Content(text =
            Text(
              s"""Добро пожаловать\n\n
                 Подтвердите свой адрес электронной почты,
                 откройте эту ссылку в браузере $AppDomain/auth/confirmemail?t=$linkCode
                 С Уважением Команда IT-Forelead"""
            ).some
          ),
          to = NonEmptyList.one(user.email),
        )
        _ <- mailer.send(email)
      } yield id

    override def getSeniors(seniorFilters: SeniorFilters): F[List[SeniorUser]] =
      seniorsRepository.get(seniorFilters)

    override def getJuniors(filters: UserFilters): F[List[User]] =
      usersRepository.get(filters)

    override def getSkills(userId: PersonId): F[List[Skill]] =
      seniorsRepository.getSkills(userId)

    override def confirmEmail(token: String): F[Unit] =
      OptionT(redis.get(s"confirm_$token"))
        .semiflatMap(_.decodeAsF[F, PersonId])
        .foldF(
          LinkCodeExpired("Link code is expired").raiseError[F, Unit]
        ) { personId =>
          Calendar[F].currentZonedDateTime.flatMap { now =>
            usersRepository.update(personId)(
              _.copy(
                acceptedAt = now.some
              )
            )
          }
        }
  }
}
