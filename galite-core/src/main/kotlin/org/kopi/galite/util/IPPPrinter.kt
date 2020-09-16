package org.kopi.galite.util

import org.kopi.galite.util.ipp.IPPClient
import org.kopi.util.ipp.IPPClient
import java.io.IOException


/**
 * IPP printer
 */
class IPPPrinter
/**
 * Construct an IPP Printer
 *
 * @param host the IPP server host
 * @param port the IPP server port
 * @param printer the name of the IPP printer
 * @param user the name of the printer user
 * @param attributesForMedia a list of String[2] with the correpondance
 * between media and IPP attributes for this printer.
 */(name: String?,
    private val host: String,
    private val port: Int,
    private val printer: String,
        // ----------------------------------------------------------------------
        // DATA MEMBERS
        // ----------------------------------------------------------------------
    private val user: String,
    private val attributesForMedia: List<*>) : AbstractPrinter(name!!), CachePrinter {
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  val mediaTypes: List<*>
    get() {
      val client = IPPClient(host, port.toShort(), printer, user)
      return client.getMediaTypes()
    }

  /**
   * Set a given media for the printer.
   * Choose de attributes associated with this attribute for this printer.
   *
   * @return true iff the attribute is supported by this printer.
   */
  private fun getAttributes(media: String?): Array<String>? {
    return if (media == null) {
      null
    } else {
      val it = attributesForMedia.iterator()
      while (it.hasNext()) {
        val att = it.next() as Array<String?>
        if (att.size == 2 && att[0] == media) {
          return if (att[1] == null) null else att[1]!!.split(" ").toTypedArray()
        }
      }
      null
    }
  }
  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  /**
   * Print a file and return the output of the command
   */
    fun print(printData: PrintJob): String {
    val ippClient = IPPClient(host, port.toShort(), printer, user)
    ippClient.print(printData.getInputStream(),
            printData.getNumberOfCopies(),
            if (printData.getMedia() == null) null else getAttributes(printData.getMedia()))
    return "IPP Print"
  }

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
}
