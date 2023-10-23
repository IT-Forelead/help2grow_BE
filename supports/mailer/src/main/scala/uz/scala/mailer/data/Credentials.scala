package uz.scala.mailer.data

import uz.scala.mailer.data.types.Password

import help2grow.EmailAddress

case class Credentials(user: EmailAddress, password: Password)
