package uz.scala.mailer.data

import cats.data.NonEmptyList
import uz.scala.mailer.data.types.Subject

import help2grow.EmailAddress

/** Represents the e-mail message itself.
  *
  * @param from
  *   e-mail sender address
  * @param subject
  *   e-mail subject text
  * @param content
  *   e-mail content,
  * @param to
  *   set of e-mail receiver addresses
  * @param cc
  *   set of e-mail ''carbon copy'' receiver addresses
  * @param bcc
  *   set of e-mail ''blind carbon copy'' receiver addresses
  * @param replyTo
  *   addresses used to reply this message
  */
case class Email(
    from: EmailAddress,
    subject: Subject,
    content: Content,
    to: NonEmptyList[EmailAddress],
    cc: List[EmailAddress] = Nil,
    bcc: List[EmailAddress] = Nil,
    replyTo: List[EmailAddress] = Nil,
    headers: List[Header] = Nil,
  )
