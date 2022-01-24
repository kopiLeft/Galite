/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.galite.visual.util.mailer

import java.util.Date
import java.util.Properties
import java.util.StringTokenizer

import javax.activation.DataHandler
import javax.mail.Address
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

/**
 * Class allowing to send mails using convenience method.
 *
 * You can send a mail with or without attachments.
 *
 * If no subject was specified, the string "NO SUBJECT" is used instead.
 *
 */
class Mailer {
  companion object {

    /**
     * Convenience method.
     *
     * @param        mailHost         the SMTP server (name or IP)
     * @param        recipient        the e-mail address (name@domain)
     * @param        bccRecipient     the e-mail address (name@domain)
     * @param        subject          the subject of the mail
     * @param        body             the body of the mail
     * @param        sender           the sender address (name@domain)
     */
    fun sendMail(
      mailHost: String?,
      recipient: String?,
      ccRecipient: String?,
      bccRecipient: String?,
      subject: String?,
      body: String?,
      sender: String
    ) {
      sendMail(mailHost, recipient, ccRecipient, bccRecipient, subject, body, sender, null)
    }

    /**
     * Convenience method.
     *
     * @param        host               the SMTP server (name or IP)
     * @param        recipient          the e-mail address (name@domain)
     * @param        ccRecipient        the e-mail address (name@domain)
     * @param        bccRecipient       the e-mail address (name@domain)
     * @param        subject            the subject of the mail
     * @param        body               the body of the mail
     * @param        sender             the sender address (name@domain)
     * @param        attachments        files to send in attachment
     */
    fun sendMail(
      host: String?,
      recipient: String?,
      ccRecipient: String?,
      bccRecipient: String?,
      subject: String?,
      body: String?,
      sender: String,
      attachments: MutableList<Attachment>?
    ) {
      val runnable = Runnable {
        try {
          val mailer = Mailer()
          mailer.setMailHost(host)
          mailer.sendMessage(
            sender,
            recipient,
            ccRecipient,
            bccRecipient,
            subject,
            body,
            attachments
          )
        } catch (e: SMTPException) {
          e.printStackTrace()
          System.err.println("*** SMTP Exception: " + e.message)
        }
      }
      Thread(runnable).start()
    }
  }

  // HOSTS
  private var mailHost: String? = null

  /**
   * Sends a message with only one attachment.
   *
   * @param        sender                the sender address (name@domain)
   * @param        recipient             a comma separated list of e-mail addresses (name@domain)
   * @param        ccRecipient           a comma separated list of e-mail addresses (name@domain)
   * @param        bccRecipient          a comma separated list of e-mail addresses (name@domain)
   * @param        subject               the subject of the mail
   * @param        message               the body of the mail
   * @param        attachment            a file to send in attachment
   */
  fun sendMessage(
    sender: String,
    recipient: String?,
    ccRecipient: String?,
    bccRecipient: String?,
    subject: String? = null,
    message: String?,
    attachment: Attachment
  ) {
    val attachments = listOf(attachment)
    sendMessage(sender, recipient, ccRecipient, bccRecipient, subject, message, attachments)
  }

  /**
   * Sends a message.
   *
   * @param        sender                the sender address (name@domain)
   * @param        recipient             a comma separated list of e-mail addresses (name@domain)
   * @param        ccRecipient           a comma separated list of e-mail addresses (name@domain)
   * @param        bccRecipient          a comma separated list of e-mail addresses (name@domain)
   * @param        subject               the subject of the mail
   * @param        message               the body of the mail
   * @param        attachments           files to send in attachment
   */
  fun sendMessage(
    sender: String,
    recipient: String?,
    ccRecipient: String?,
    bccRecipient: String?,
    subject: String?,
    message: String?,
    attachments: List<Attachment>? = listOf()
  ) {
    val recipients = split(recipient)
    val ccRecipients = split(ccRecipient)
    val bccRecipients = split(bccRecipient)

    sendMessage(sender, recipients, ccRecipients, bccRecipients, subject, message, attachments)
  }

  /**
   * Sends a message.
   *
   * @param        sender                the sender address (name@domain)
   * @param        recipients            a list of e-mail addresses (name@domain)
   * @param        ccRecipients          a list of e-mail addresses (name@domain)
   * @param        bccRecipients         a list of e-mail addresses (name@domain)
   * @param        subject               the subject of the mail
   * @param        message               the body of the mail
   * @param        attachments           files to send in attachment
   */
  fun sendMessage(
    sender: String,
    recipients: List<String>?,
    ccRecipients: List<String>?,
    bccRecipients: List<String>?,
    subject: String?,
    message: String?,
    attachments: List<Attachment>?
  ) {
    try {
      val props = Properties()

      props["mail.smtp.host"] = mailHost

      val session: Session = Session.getDefaultInstance(props)
      val msg: Message = MimeMessage(session)

      msg.setFrom(InternetAddress(sender))
      if (recipients != null && recipients.isNotEmpty()) {
        msg.addRecipients(Message.RecipientType.TO, getAddresses(recipients))
      }
      if (ccRecipients != null && ccRecipients.isNotEmpty()) {
        msg.addRecipients(Message.RecipientType.CC, getAddresses(ccRecipients))
      }
      if (bccRecipients != null && bccRecipients.isNotEmpty()) {
        msg.addRecipients(Message.RecipientType.BCC, getAddresses(bccRecipients))
      }

      msg.subject = subject ?: "NO SUBJECT"

      if (attachments == null || attachments.isEmpty()) {
        msg.setText(message)
      } else {
        val content = MimeMultipart()
        val text = MimeBodyPart()
        text.setText(message)
        content.addBodyPart(text)
        val iterator = attachments.listIterator()
        while (iterator.hasNext()) {
          val attachment: Attachment = iterator.next()
          val bodyPart = MimeBodyPart()
          bodyPart.dataHandler = DataHandler(attachment)
          bodyPart.setHeader("Content-Transfer-Encoding", "base64")
          bodyPart.fileName = attachment.name
          content.addBodyPart(bodyPart)
        }
        msg.setContent(content)
      }
      msg.sentDate = Date()
      Transport.send(msg)
    } catch (e: MessagingException) {
      throw SMTPException(e)
    }
  }

  private fun getAddresses(recipients: List<*>): Array<Address> {
    val iterator = recipients.listIterator()
    val addresses = mutableListOf<InternetAddress>()
    while (iterator.hasNext()) {
      val tmp = iterator.next() as String
      if (tmp.isNotEmpty()) {
        addresses.add(InternetAddress(tmp))
      }
    }
    return addresses.toTypedArray()
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  fun setMailHost(mailHost: String?) {
    this.mailHost = mailHost
  }

  /**
   * Each token of the line is an element of the List.
   */
  private fun split(line: String?): MutableList<String> {
    val result: MutableList<String>
    if (line == null) {
      result = mutableListOf()
    } else {
      val tok = StringTokenizer(line, ",")
      result = mutableListOf()
      while (tok.hasMoreTokens()) {
        result.add(tok.nextToken())
      }
    }
    return result
  }
}
