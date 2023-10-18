package help2grow.algebras

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import cats.implicits.toTraverseOps
import tsec.passwordhashers.PasswordHasher
import tsec.passwordhashers.jca.SCrypt

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
      } yield id

    override def getSeniors(seniorFilters: SeniorFilters): F[List[SeniorUser]] =
      seniorsRepository.get(seniorFilters)

    override def getJuniors(filters: UserFilters): F[List[User]] =
      usersRepository.get(filters)

    override def getSkills(userId: PersonId): F[List[Skill]] =
      seniorsRepository.getSkills(userId)
  }
}
