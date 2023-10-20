package help2grow.repos

import cats.effect.Async
import cats.effect.Resource
import skunk._
import uz.scala.skunk.syntax.all.skunkSyntaxCommandOps
import uz.scala.skunk.syntax.all.skunkSyntaxQueryVoidOps

import help2grow.domain.Label
import help2grow.repos.sql.LabelsSql
trait LabelsRepository[F[_]] {
  def get: F[List[Label]]
  def create(label: Label): F[Unit]
}

object LabelsRepository {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): LabelsRepository[F] = new LabelsRepository[F] {
    override def get: F[List[Label]] =
      LabelsSql.select.all
    override def create(label: Label): F[Unit] =
      LabelsSql.insert.execute(label)
  }
}
