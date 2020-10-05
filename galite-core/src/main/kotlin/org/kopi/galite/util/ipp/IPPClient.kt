package org.kopi.galite.util.ipp

import java.io.InputStream

class IPPClient(val hostname: String, val port: Short, val printer: String, val user: String) {

  fun getMediaTypes(): List<*> {
    TODO()
  }
  fun print(file: InputStream,
            nbCopies: Int,
            mediaAttributes: List<*>,
            optionalAttributes: List<*>) {
    TODO()
  }
  fun print(file: InputStream, nbCopies: Int, attributes: Array<String>?) {
    TODO()
  }
}