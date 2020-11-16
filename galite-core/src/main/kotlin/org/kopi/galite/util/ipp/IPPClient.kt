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

package org.kopi.galite.util.ipp

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

class IPPClient(private val hostname: String,
                private val port: Short,
                private val printer: String,
                private val user: String) {

  fun print(file: InputStream, nbCopies: Int, attributes: Array<String>?) {
    val mediaAttributes: MutableList<String>? = mutableListOf()
    val optionalAttributes: MutableList<String>? = mutableListOf()

    attributes?.forEach {
      if (it.indexOf('=') != -1) {
        optionalAttributes!!.add(it)
      } else {
        mediaAttributes!!.add(it)
      }
    }

    print(file, nbCopies, mediaAttributes, optionalAttributes)
  }

  /**
   *
   * optionalAttributes are attributes supported by the printer
   * the format is "attribute=value".
   * mediaAttributes are single value corresponding to a media.
   */
  protected fun print(file: InputStream,
                      nbCopies: Int,
                      mediaAttributes: MutableList<String>?,
                      optionalAttributes: MutableList<String>?) {
    var att: IPPAttribute
    val req = IPP()
    val baos = ByteArrayOutputStream()
    var read: Int

    req.setRequest(1, IPPConstants.OPS_PRINT_JOB)

    att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_CHARSET,
            "attributes-charset")
    att.addValue(StringValue("utf-8"))
    req.addAttribute(att)
    att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_LANGUAGE,
            "attributes-natural-language")
    att.addValue(StringValue("en"))
    req.addAttribute(att)

    att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_URI,
            "printer-uri")
    att.addValue(StringValue("ipp://" + hostname + ":" + port +
            "/printers/" + printer))
    req.addAttribute(att)

    att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_NAME,
            "requesting-user-name")
    att.addValue(StringValue(user))
    req.addAttribute(att)

    att = IPPAttribute(IPPConstants.TAG_JOB,
            IPPConstants.TAG_INTEGER,
            "copies")
    // workaround LETTERHEAD lackner 13.01.2004
    // send each kopi as an own job
    // for e.g. a letterhead on every copy (milavec)
    // -> 1 instead of nbCopies
    att.addValue(IntegerValue(1))
    req.addAttribute(att)
    // end workaround

    if (mediaAttributes != null && mediaAttributes.isNotEmpty()) {
      att = IPPAttribute(IPPConstants.TAG_JOB,
              IPPConstants.TAG_KEYWORD,
              "media")

      mediaAttributes.forEach {
        att.addValue(StringValue(it))
      }
      req.addAttribute(att)
    }

    optionalAttributes?.forEach {
      val optionalAttribute = it
      val attributeName = optionalAttribute.substring(0, optionalAttribute.indexOf("="))
      val attributeValue = optionalAttribute.substring(optionalAttribute.indexOf("=") + 1, optionalAttribute.length)
      att = IPPAttribute(IPPConstants.TAG_JOB,
              IPPConstants.TAG_NAME,
              attributeName)
      att.addValue(StringValue(attributeValue))
      req.addAttribute(att)
    }

    while (file.read().also { read = it } != -1) {
      baos.write(read)
    }

    req.data = baos.toByteArray()

    // workaround LETTERHEAD lackner 13.01.2004
    // see description above
    for (i in 0 until nbCopies) {
      var resp: IPP
      val httpConnection = IPPHttpConnection(
              URL("http://$hostname:$port/printers/$printer"))

      httpConnection.sendRequest(req)

      resp = httpConnection.receiveResponse()
      resp.simpleDump()
    }
  }

  fun printPrinterAttributes() {
    val resp = getPrinterAttributes()
    resp.dump()
  }

  fun getMediaTypes(): List<*> {
    val media = mutableListOf<String>()
    val properties = getPrinterAttributes()
    val attributes = properties.getAttributes()

    while (attributes.hasNext()) {
      val attribute = attributes.next() as IPPAttribute

      if (attribute.name == "media-supported") {
        val values = attribute.getValues()

        while (values.hasNext()) {
          val value = values.next() as IPPValue

          if (value is StringValue) {
            media.add(value.value)
          }
        }
      }
    }

    return media
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private fun getPrinterAttributes(): IPP {
    val req = IPP()
    val httpConnection = IPPHttpConnection(
            URL("http://$hostname:$port/printers/$printer"))
    req.setRequest(1, IPPConstants.OPS_GET_PRINTER_ATTRIBUTES)

    var att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_CHARSET,
            "attributes-charset")
    att.addValue(StringValue("iso-8859-1"))
    //att.addValue(new StringValue("utf-8"));
    req.addAttribute(att)

    att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_LANGUAGE,
            "attributes-natural-language")
    att.addValue(StringValue("en"))
    req.addAttribute(att)

    att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_URI,
            "printer-uri")
    att.addValue(StringValue("ipp://" + hostname + ":" + port +
            "/printers/" + printer))
    req.addAttribute(att)

    att = IPPAttribute(IPPConstants.TAG_OPERATION,
            IPPConstants.TAG_NAME,
            "printer-name")
    att.addValue(StringValue(printer))
    req.addAttribute(att)

    httpConnection.sendRequest(req)

    return httpConnection.receiveResponse()
  }
}
