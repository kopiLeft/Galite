/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util

import java.io.IOException
import java.net.ConnectException
import java.util.ArrayList
import java.util.StringTokenizer

import gnu.hylafax.HylaFAXClient
import gnu.hylafax.HylaFAXClientProtocol
import gnu.inet.ftp.ServerResponseException

import org.kopi.galite.base.Utils

object HylaFAXUtils {

  // ----------------------------------------------------------------------
  // CONVENIENCE METHODS TO SEND FAX, GET QUEUE STATUS, ...
  // ----------------------------------------------------------------------

  /*
   * ----------------------------------------------------------------------
   * READ THE SEND QUEUE
   * RETURNS A ArrayList OF STRINGS
   * ----------------------------------------------------------------------
   */
  fun readSendQueue(host: String, port: Int, user: String, password: String): ArrayList<FaxStatus>
    = readQueue(host, port, user, password, "sendq")

  /*
   * ----------------------------------------------------------------------
   * READ THE DONE QUEUE
   * RETURNS A ArrayList OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  fun readDoneQueue(host: String, port: Int, user: String, password: String): ArrayList<FaxStatus>
    = readQueue(host, port, user, password, "doneq")

  /*
   * ----------------------------------------------------------------------
   * READ THE RECEIVE QUEUE
   * RETURNS A ArrayList OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  fun readRecQueue(host: String, port: Int, user: String, password: String): ArrayList<FaxStatus>
    = readQueue(host, port, user, password, "recvq")

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */
  fun readServerStatus(host: String, port: Int, user: String, password: String): ArrayList<String> {
    val status: ArrayList<String>

    try {
      status = getQueue(host, port, user, password, "status")
      Utils.log("Fax", "READ STATE : host $host / user $user")
    } catch (e: ConnectException) {
      throw FaxException("NO FAX SERVER")
    } catch (e: IOException) {
      throw FaxException("Trying read server state: " + e.message, e)
    } catch (e: ServerResponseException) {
      throw FaxException("Trying read server state: " + e.message, e)
    }
    return status
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */

  /**
   * Convenience method
   */
  fun killJob(host: String,
              port: Int,
              user: String,
              password: String,
              job: Long) {
    try {
      val faxClient = HylaFAXClient()

      faxClient.open(host)
      if (faxClient.user(user)) {
        // need password
        faxClient.pass(password)
      }
      faxClient.jkill(job)
      Utils.log("Fax", "Kill 1: $job")
      faxClient.quit()
    } catch (ioe: IOException) {
      throw FaxException(ioe)
    } catch (sre: ServerResponseException) {
      throw FaxException(sre)
    }
  }

  /**
   * Convenience method
   */
  fun deleteJob(host: String,
                port: Int,
                user: String,
                password: String,
                job: Long) {
    try {
      val faxClient = HylaFAXClient()

      faxClient.open(host)
      if (faxClient.user(user)) {
        // need password
        faxClient.pass(password)
      }
      faxClient.jdele(job)
      Utils.log("Fax", "Delete 1: $job")
      faxClient.quit()
    } catch (ioe: IOException) {
      throw FaxException(ioe)
    } catch (sre: ServerResponseException) {
      throw FaxException(sre)
    }
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE QUEUES --- ALL QUEUES ARE HANDLED BY THAT METHOD
   * ----------------------------------------------------------------------
   */
  @Suppress("UNCHECKED_CAST")
  private fun getQueue(host: String, port: Int, user: String, password: String, qname: String): ArrayList<String> {
    val faxClient = HylaFAXClient()

    faxClient.open(host)
    if (faxClient.user(user)) {
      // need password
      faxClient.pass(password)
    }
    faxClient.tzone(HylaFAXClientProtocol.TZONE_LOCAL)
    faxClient.rcvfmt("%f| %t| %s| %p| %h| %e")
    faxClient.jobfmt("%j| %J| %o| %e| %a| %P| %D| %.25s")
    faxClient.mdmfmt("Modem %m (%n): %s")
    val entries = ArrayList(faxClient.getList(qname)) as ArrayList<String>
    faxClient.quit()
    return entries
  }

  /*
   * ----------------------------------------------------------------------
   * READS ANY QUEUE
   * RETURNS A ArrayList OF STRINGS
   * ----------------------------------------------------------------------
   */
  private fun readQueue(host: String, port: Int, user: String, password: String, qname: String): ArrayList<FaxStatus> {
    val queue = ArrayList<FaxStatus>()

    try {
      val result = getQueue(host, port, user, password, qname)

      Utils.log("Fax", "READ $qname : host $host / user $user")
     result.forEach{ element ->
        try {
          val process = StringTokenizer(element, "|")

          if (qname != "recvq") {
            queue.add(FaxStatus(process.nextToken().trim(),  // ID
                                process.nextToken().trim(),  // TAG
                                process.nextToken().trim(),  // USER
                                process.nextToken().trim(),  // DIALNO
                                process.nextToken().trim(),  // STATE OF CODE
                                process.nextToken().trim(),  // PAGES
                                process.nextToken().trim(),  // DIALS
                                process.nextToken().trim())) // STATE OF TEXT
          } else {
            queue.add(FaxStatus(process.nextToken().trim(),  // FILENAME %f
                                process.nextToken().trim(),  // TIME IN %t
                                process.nextToken().trim(),  // SENDER %s
                                process.nextToken().trim(),  // PAGES %p
                                process.nextToken().trim(),  // DURATION %h
                                process.nextToken().trim())) // ERROR TEXT %e
          }
        } catch (e: Exception) {
          throw FaxException(e.message!!, e)
        }
      }
    } catch (e: ConnectException) {
      Utils.log("Fax", "NO FAX SERVER")
      throw FaxException("NO FAX SERVER")
    } catch (e: IOException) {
      throw FaxException(e)
    } catch (e: ServerResponseException) {
      throw FaxException(e)
    }
    return queue
  }

  var HFX_DEFAULT_PORT = 4559
  var HFX_DEFAULT_USER = "GALITE"
}

