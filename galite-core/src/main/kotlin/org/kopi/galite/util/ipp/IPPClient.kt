package org.kopi.galite.util.ipp

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList
import java.util.LinkedList


class IPPClient(val hostname: String, val port: Short,val printer: String,val user: String) {

  fun print(file: InputStream, nbCopies: Int, attributes: Array<String>?) {
    val mediaAttributes = arrayListOf<String>()
    val optionalAttributes = arrayListOf<String>()
    if (attributes != null) {
      for (i in attributes.indices) {
        if (attributes[i].indexOf('=') != -1) {
          optionalAttributes.add(attributes[i])
        } else {
          mediaAttributes.add(attributes[i])
        }
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
                      mediaAttributes: List<*>?,
                      optionalAttributes: List<*>?) {
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
    if (mediaAttributes != null && !mediaAttributes.isEmpty()) {
      val atts = mediaAttributes.iterator()
      att = IPPAttribute(IPPConstants.TAG_JOB,
              IPPConstants.TAG_KEYWORD,
              "media")
      while (atts.hasNext()) {
        att.addValue(StringValue(atts.next() as String))
      }
      req.addAttribute(att)
    }
    if (optionalAttributes != null) {
      val atts = optionalAttributes.iterator()
      while (atts.hasNext()) {
        val optionalAttribute = atts.next() as String
        val attributeName = optionalAttribute.substring(0, optionalAttribute.indexOf("="))
        val attributeValue = optionalAttribute.substring(optionalAttribute.indexOf("=") + 1, optionalAttribute.length)
        att = IPPAttribute(IPPConstants.TAG_JOB,
                IPPConstants.TAG_NAME,
                attributeName)
        att.addValue(StringValue(attributeValue))
        req.addAttribute(att)
      }
    }
    while (file.read().also { read = it } != -1) {
      baos.write(read)
    }
    req.setData(baos.toByteArray())

    // workaround LETTERHEAD lackner 13.01.2004
    // see description above
    for (i in 0 until nbCopies) {
      var resp: IPP
      var httpConnection = IPPHttpConnection(
              URL("http://$hostname:$port/printers/$printer"))
      httpConnection.sendRequest(req)
      resp = httpConnection.receiveResponse()
      resp.simpleDump()
    }
  }

  fun printPrinterAttributes() {
    val resp: IPP = printerAttributes
    resp.dump()
  }

  val mediaTypes: List<*>
    get() {
      val media= arrayListOf<String>()
      val properties: IPP = printerAttributes
      val attributes: Iterator<*> = properties.getAttributes()
      while (attributes.hasNext()) {
        val attribute: IPPAttribute = attributes.next() as IPPAttribute
        if (attribute.getName().equals("media-supported")) {
          val values = attribute.getValues()
          while (values!!.hasNext()) {
            val value: IPPValue = values.next() as IPPValue
            if (value is StringValue) {
              media.add((value as StringValue).value)
            }
          }
        }
      }
      return media
    }//att.addValue(new StringValue("utf-8"));

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private val printerAttributes: IPP
    private get() {
      val req = IPP()
      val httpConnection= IPPHttpConnection(
              URL("http://$hostname:$port/printers/$printer"))
      req.setRequest(1, IPPConstants.OPS_GET_PRINTER_ATTRIBUTES)
      var att= IPPAttribute(IPPConstants.TAG_OPERATION,
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
