/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
import java.io.InputStream

import org.kopi.galite.util.base.InconsistencyException

import gnu.hylafax.HylaFAXClient
import gnu.hylafax.HylaFAXClientProtocol
import gnu.hylafax.Job
import gnu.hylafax.Pagesize
import gnu.inet.ftp.FtpClientProtocol
import gnu.inet.ftp.ServerResponseException

/**
 * Fax printer
 */
class HylaFAXPrinter(private val faxHost: String,
                     var number: String,
                     val user: String,
                     val attachments: List<*>?)
  : AbstractPrinter("FaxPrinter $number"), Printer {

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  override fun print(data: PrintJob): String {
    // get down to business, send the FAX already

    // List with names of temporary files on the server side
    val documents = ArrayList<Any>()
    // fax client
    val faxClient = HylaFAXClient()

    try {
      with(faxClient) {
        debug = false // no debug messages
        open(faxHost) // name of host
        user(user) // hyla fax user
        // necessary for pdf documents to keep the correct file size
        type(FtpClientProtocol.TYPE_IMAGE)
        noop()
        tzone(HylaFAXClientProtocol.TZONE_LOCAL)
      }

      // add fax document
      documents.add(faxClient.putTemporary(data.inputStream))

      // put attachments to server
      attachments?.let {
        it.forEach { element ->
          val dataSource = element as InputStream
          // put data to the hylafax server
          documents.add(faxClient.putTemporary(dataSource))
        }
      }

      // all file to send are at the server
      // create a job to send them
      val job = faxClient.createJob()

      // set job properties
      with(job) {
        fromUser = user
        notifyAddress = user
        killtime = "000259"
        maximumDials = 3
        maximumTries = 3
        priority = Job.PRIORITY_NORMAL
        dialstring = number
        verticalResolution = Job.RESOLUTION_MEDIUM
        pageDimension = Pagesize.A4
        notifyType = HylaFAXClientProtocol.NOTIFY_NONE
        chopThreshold = 3
      }

      // add documents to the job
      documents.forEach {
        job.addDocument((it as String?))
      }

      faxClient.submit(job) // submit the job to the scheduler
    } catch (e: ServerResponseException) {
      throw InconsistencyException("Can't send fax job", e)
    } catch (e: IOException) {
      throw InconsistencyException("Can't send fax job", e)
    } finally {
      // disconnect from the server
      try {
        faxClient.quit()
      } catch (e: IOException) {
        throw InconsistencyException("Can't disconnect from server", e)
      } catch (e: ServerResponseException) {
        throw InconsistencyException("Can't disconnect from server", e)
      }
    }
    return "NYI"
  }
}
