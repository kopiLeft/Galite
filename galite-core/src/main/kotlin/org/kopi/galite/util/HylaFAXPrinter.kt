package org.kopi.galite.util

import org.kopi.galite.util.base.InconsistencyException
import java.awt.Dimension
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Iterator
import java.util.List

import gnu.hylafax.HylaFAXClient
import gnu.hylafax.HylaFAXClientProtocol
import gnu.hylafax.Job
import gnu.inet.ftp.FtpClientProtocol
import gnu.inet.ftp.ServerResponseException


/**
 * Fax printer
 */
class HylaFAXPrinter
/**
 * Constructs a fax printer
 */(// ----------------------------------------------------------------------
        // DATA MEMBERS
        // ----------------------------------------------------------------------
        private val faxHost: String,
        /**
         * Gets the phone number
         */
        val number: String,
        private val user: String,
        private val attachments: List<*>?) : AbstractPrinter("FaxPrinter $number"), CachePrinter {
  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  /**
   * Print a file and return the output of the command
   */
  fun print(printdata: PrintJob): String {
    // get down to business, send the FAX already

    // List with names of temporary files on the server side
    val documents = ArrayList<Any>()
    // fax client
    val faxClient = HylaFAXClient()
    try {
      faxClient.setDebug(false) // no debug messages
      faxClient.open(faxHost) // name of host
      faxClient.user(user) // hyla fax user
      // necessary for pdf documents to keep the correct file size
      faxClient.type(FtpClientProtocol.TYPE_IMAGE)
      faxClient.noop()
      faxClient.tzone(HylaFAXClientProtocol.TZONE_LOCAL)

      // add fax document
      documents.add(faxClient.putTemporary(printdata.getInputStream()))

      // put attachments to server
      if (attachments != null) {
        val attachmentInterator = attachments.iterator()
        while (attachmentInterator.hasNext()) {
          val dataSource = attachmentInterator.next() as InputStream

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
      job.pageDimension = Job.Pagesize.get("a4") as Dimension
      job.notifyType = HylaFAXClientProtocol.NOTIFY_NONE
      job.chopThreshold = 3

      // add documents to the job
      val docIterator = documents.iterator()
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

}
