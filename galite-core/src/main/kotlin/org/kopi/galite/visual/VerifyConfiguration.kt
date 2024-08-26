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

package org.kopi.galite.visual

import java.io.PrintWriter
import java.io.StringWriter
import java.net.InetAddress
import java.net.UnknownHostException

import org.kopi.galite.util.mailer.Mailer

class VerifyConfiguration private constructor() {

  fun verifyConfiguration(smtpServer: String?, failureRecipient: String, applicationName: String) {
    val buffer = StringWriter()
    val writer = PrintWriter(buffer)
    var configurationError = false
    var hostname: String

    val failureSender = try {
      ApplicationConfiguration.getConfiguration()!!.getStringFor("debugging.mail.sender") ?: throw PropertyException()
    } catch (e: PropertyException) {
      throw Exception("Couldn't find the debugging mail sender while sending an error report mail", e)
    }

    // get Hostname
    try {
      val inetAdress = InetAddress.getLocalHost()
      hostname = inetAdress.canonicalHostName
      writer.println(formatMessage("Getting hostname ", false))
    } catch (e: UnknownHostException) {
      hostname = "unknown"
      writer.println(formatMessage("Getting hostname ", true))
      e.printStackTrace(writer)
      writer.println()
      configurationError = true
    }

    // check that -ea is on
    var isAssertOn = false
    assert(true.also { isAssertOn = it })
    writer.println(formatMessage("java called with option -ea", !isAssertOn))
    configurationError = configurationError || !isAssertOn

    // check tmp-directory (exists, writable)
    // !!! todo

    // check print-server (exits)
    // !!! todo

    // check fax-server (exits)
    // !!! todo

    // check mail-server
    // NOT useful (how to mail this error?)

    // check gs-server (works)
    // !!! todo

    // check aspell-server (works)
    // !!! todo
    if (configurationError) {
      Mailer.sendMail(smtpServer,
                      failureRecipient,  // recipient
                      null,  // cc
                      null,  // bcc
                      "[KOPI CONFIGURATION] "
                              + applicationName
                              + " "
                              + System.getProperty("user.name", "")
                              + "@"
                              + hostname,
                      buffer.toString(),
                      failureSender)
    }
  }

  companion object {
    private fun formatMessage(message: String, fail: Boolean): String {
      val result = if (message.length >= 70) {
        message.substring(0, 70)
      } else {
        message + STR_BASIC.substring(0, 70 - message.length)
      }
      return if (fail) {
        result + STR_FAILED
      } else {
        result + STR_OK
      }
    }

    val verifyConfiguration = VerifyConfiguration()
    private const val STR_BASIC = "   ........................................................................"
    private const val STR_OK = " [OK]"
    private const val STR_FAILED = " [FAILED]"
  }
}
