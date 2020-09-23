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

import java.io.InputStream
import java.io.BufferedInputStream
import java.io.OutputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.net.URL

class IPPHttpConnection(private val url: URL) {
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  fun sendRequest(request: IPP) {
    val httpRequest = IPPHttp(url.path, request)
    httpRequest.write(IPPOutputStream(os))
    os.flush()
  }

  fun receiveResponse(): IPP {
    val httpRequest = IPPHttp(IPPInputStream(inputStream))
    return httpRequest.iPP
  }

  val connection: Socket = Socket(url.host, url.port)
  val os: OutputStream
  val inputStream: InputStream

  init {
    os = BufferedOutputStream(DataOutputStream(connection.getOutputStream()))
    inputStream = BufferedInputStream(DataInputStream(connection.getInputStream()))
  }
}
