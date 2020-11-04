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
                     user: String,
                     attachments: List<*>)
      : AbstractPrinter("FaxPrinter $number"), Printer {

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  override fun print(printdata: PrintJob): String {
    // get down to business, send the FAX already

    // List with names of temporary files on the server side
    val documents = ArrayList<Any>()
    // fax client
    val faxClient = HylaFAXClient()

    try {
      faxClient.debug = false // no debug messages
      faxClient.open(faxHost) // name of host
      faxClient.user(user) // hyla fax user
      // necessary for pdf documents to keep the correct file size
      faxClient.type(FtpClientProtocol.TYPE_IMAGE)
      faxClient.noop()
      faxClient.tzone(HylaFAXClientProtocol.TZONE_LOCAL)

      // add fax document
      documents.add(faxClient.putTemporary(printdata.inputStream))

      // put attachments to server
      if (attachments != null) {
        val attachmentIterator = attachments.iterator()

        while (attachmentIterator.hasNext()) {
          val dataSource = attachmentIterator.next() as InputStream

          // put data to the hylafax server
          documents.add(faxClient.putTemporary(dataSource))
        }
      }
      // all file to send are at the server
      // create a job to send them
      val job = faxClient.createJob()

      // set job properties
      job.fromUser = user
      job.notifyAddress = user
      job.killtime = "000259"
      job.maximumDials = 3
      job.maximumTries = 3
      job.priority = Job.PRIORITY_NORMAL
      job.dialstring = number
      job.verticalResolution = Job.RESOLUTION_MEDIUM
      job.pageDimension = Pagesize.A4
      job.notifyType = HylaFAXClientProtocol.NOTIFY_NONE
      job.chopThreshold = 3

      // add documents to the job
      val docIterator: Iterator<*> = documents.iterator()

      while (docIterator.hasNext()) {
        job.addDocument(docIterator.next() as String?)
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

  private val user: String
  private val attachments: List<*>

  /**
   * Constructs a fax printer
   */
  init {
    number = number
    this.user = user
    this.attachments = attachments
  }
}
