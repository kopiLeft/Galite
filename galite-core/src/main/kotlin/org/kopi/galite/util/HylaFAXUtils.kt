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
import java.util.Vector
import java.util.StringTokenizer

import org.kopi.galite.base.Utils

import gnu.hylafax.HylaFAXClient
import gnu.hylafax.HylaFAXClientProtocol
import gnu.inet.ftp.ServerResponseException


object HylaFAXUtils {

  // ----------------------------------------------------------------------
  // CONVENIENCE METHODS TO SEND FAX, GET QUEUE STATUS, ...
  // ----------------------------------------------------------------------

  /*
   * ----------------------------------------------------------------------
   * READ THE SEND QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
  fun readSendQueue(host: String, port: Int, user: String, password: String): Vector<FaxStatus> {
    return readQueue(host, port, user, password, "sendq")
  }

  /*
   * ----------------------------------------------------------------------
   * READ THE DONE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  fun readDoneQueue(host: String, port: Int, user: String, password: String): Vector<FaxStatus> {
    return readQueue(host, port, user, password, "doneq")
  }

  /*
   * ----------------------------------------------------------------------
   * READ THE RECEIVE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  fun readRecQueue(host: String, port: Int, user: String, password: String): Vector<FaxStatus> {
    return readQueue(host, port, user, password, "recvq")
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */
  fun readServerStatus(host: String, port: Int, user: String, password: String): Vector<String> {
    val status: Vector<String>
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
  private fun getQueue(host: String, port: Int, user: String, password: String, qname: String): Vector<String> {
    val entries: Vector<String>
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
    entries = faxClient.getList(qname) as Vector<String>
    faxClient.quit()
    return entries
  }

  /*
   * ----------------------------------------------------------------------
   * READS ANY QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
  private fun readQueue(host: String, port: Int, user: String, password: String, qname: String): Vector<FaxStatus> {
    val queue = Vector<FaxStatus>()
    try {
      val result = getQueue(host, port, user, password, qname)
      Utils.log("Fax", "READ $qname : host $host / user $user")
      for (i in result.indices) {
        try {
          val str = result.elementAt(i)
          val process = StringTokenizer(str, "|")
          if (qname != "recvq") {
            queue.addElement(FaxStatus(process.nextToken().trim { it <= ' ' },  // ID
                    process.nextToken().trim { it <= ' ' },  // TAG
                    process.nextToken().trim { it <= ' ' },  // USER
                    process.nextToken().trim { it <= ' ' },  // DIALNO
                    process.nextToken().trim { it <= ' ' },  // STATE OF CODE
                    process.nextToken().trim { it <= ' ' },  // PAGES
                    process.nextToken().trim { it <= ' ' },  // DIALS
                    process.nextToken().trim { it <= ' ' })) // STATE OF TEXT
          } else {
            queue.addElement(FaxStatus(process.nextToken().trim { it <= ' ' },  // FILENAME %f
                    process.nextToken().trim { it <= ' ' },  // TIME IN %t
                    process.nextToken().trim { it <= ' ' },  // SENDER %s
                    process.nextToken().trim { it <= ' ' },  // PAGES %p
                    process.nextToken().trim { it <= ' ' },  // DURATION %h
                    process.nextToken().trim { it <= ' ' })) // ERROR TEXT %e
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

