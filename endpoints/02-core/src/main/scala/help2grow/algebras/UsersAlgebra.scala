package help2grow.algebras

import cats.Monad
import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import tsec.passwordhashers.PasswordHasher
import tsec.passwordhashers.jca.SCrypt

import help2grow.domain.AuthedUser.SeniorUser
import help2grow.domain.AuthedUser.User
import help2grow.domain.PersonId
import help2grow.domain.Skill
import help2grow.domain.auth.AccessCredentials
import help2grow.domain.enums.Role
import help2grow.domain.inputs.SeniorFilters
import help2grow.domain.inputs.SeniorInput
import help2grow.effects.Calendar
import help2grow.effects.GenUUID
import help2grow.repos.SeniorsRepository
import help2grow.repos.UsersRepository
import help2grow.repos.sql.dto.SeniorData
import help2grow.utils.ID

trait UsersAlgebra[F[_]] {
  def createSenior(seniorInput: SeniorInput): F[PersonId]
  def getSeniors(seniorFilters: SeniorFilters): F[List[SeniorUser]]
  def getSkills(userId: PersonId): F[List[Skill]]
}

object UsersAlgebra {
  def make[F[_]: Monad: GenUUID: Calendar](
      usersRepository: UsersRepository[F],
      seniorsRepository: SeniorsRepository[F],
    )(implicit
      P: PasswordHasher[F, SCrypt]
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
          phone = seniorInput.phone,
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
        _ <- seniorsRepository.create(seniorData)
      } yield id

    override def getSeniors(
        seniorFilters: SeniorFilters
      ): F[List[SeniorUser]] =
      seniorsRepository.get(seniorFilters)

    override def getSkills(userId: PersonId): F[List[Skill]] =
      seniorsRepository.getSkills(userId)
  }
}
