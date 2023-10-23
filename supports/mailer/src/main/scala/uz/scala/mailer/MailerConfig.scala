package uz.scala.mailer

import eu.timepit.refined.types.all.SystemPortNumber
import uz.scala.mailer.data.types.Host
import uz.scala.mailer.data.types.Password

import help2grow.EmailAddress

case class MailerConfig(
    enabled: Boolean,
    host: Host,
    port: SystemPortNumber,
    username: EmailAddress,
    password: Password,
    fromAddress: EmailAddress,
    recipients: List[EmailAddress],
  )
