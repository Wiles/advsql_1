/**
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 21, 2012
 * DESCRIPTION:
 * 	Email services
 */

package org.sh.plc.server.services

import play.api._
import play.api.Play._

import com.typesafe.plugin._

object EmailContentType extends Enumeration {
  type EmailContentType = Value
  val Html, Text = Value
}

import EmailContentType.EmailContentType

case class Email(
  from: String,
  to: List[String],
  subject: String,
  body: String,
  
  cc: List[String] = List(),
  bcc: List[String] = List(),
  contentType: EmailContentType = EmailContentType.Html)

trait EmailService {
  def send(email: Email): Unit
}

private class DefaultEmailService extends EmailService {
  def send(email: Email): Unit = {
    val mail = use[MailerPlugin].email
    
	mail.addFrom(email.from)
    mail.setSubject(email.subject)
    
    for (to <- email.to) {
    	mail.addRecipient(to)
    }
    
    // TODO: Set CC and BCC
    
    if (email.contentType == EmailContentType.Html) {
    	mail.sendHtml(email.body)
    } else {
    	mail.send(email.body)
    }
  }
}

trait EmailServiceComponent {
  val emailService: EmailService = new DefaultEmailService()
}