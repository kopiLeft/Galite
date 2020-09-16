/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: IPPHttpConnection.java 34997 2016-12-01 09:51:43Z hacheni $
 */
package org.kopi.galite.util.ipp


import java.io.*
import java.net.Socket
import java.net.URL

class IPPHttpConnection(// --------------------------------------------------------------------
        // DATA MEMBERS
        // --------------------------------------------------------------------
        private val url: URL) {
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  fun sendRequest(request: IPP) {
    val httpRequest = IPPHttp(url.path, request)
    httpRequest.write(IPPOutputStream(os))
    os.flush()
  }

  fun receiveResponse(): IPP {
    val httpRequest = IPPHttp(IPPInputStream(`is`))
    return httpRequest.iPP
  }

  private val connection: Socket
  private val os: OutputStream
  private val `is`: InputStream

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  init {
    if (IPP.DEBUG) {
      println("printURL : $url")
    }
    connection = Socket(url.host, url.port)
    os = BufferedOutputStream(DataOutputStream(connection.getOutputStream()))
    `is` = BufferedInputStream(DataInputStream(connection.getInputStream()))
  }
}