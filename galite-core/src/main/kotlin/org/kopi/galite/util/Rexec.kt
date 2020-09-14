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
import java.io.OutputStream
import java.net.Socket
import java.net.UnknownHostException

/**
 * Remote execution client
 *
 * @param host specifies the host name
 * @param port identifies the port number.
 */
class Rexec(private val host: String, private val port: Int = STANDARD_EXEC_PORT) {
  /**
   *  This function is used to establish the connection on a socket
   */
  fun open(user: String, pass: String, command: String): Boolean {
    setUser(user, pass)
    return run(command)
  }

  /**
   * Sets the user
   */
  fun setUser(user: String, pass: String) {
    this.user = user
    this.pass = pass
  }

  /**
   * This function is used to run a certain command
   * establishing a connection on a socket
   */
  fun run(command: String): Boolean {
    socket = try {
      Socket(host, port)
    } catch (e: UnknownHostException) {
      e.printStackTrace()
      return false // !!! raise an exception
    } catch (e: IOException) {
      e.printStackTrace()
      return false // !!! raise an exception
    }

    return try {
      val output = socket!!.getOutputStream()
      output.write("0".toByteArray()) // no socket for stderr
      output.write(0)
      output.write(user!!.toByteArray()) // !!! at most 16 chars
      output.write(0)
      output.write(pass!!.toByteArray()) // !!! at most 16 chars
      output.write(0)
      output.write(command.toByteArray())
      output.write(0)
      socket!!.getInputStream()?.read() == 0
    } catch (e: IOException) {
      e.printStackTrace()
      false // !!! raise an exception
    }
  }

  /**
   * This function is used to close the connection on the socket
   */
  fun close() {
    try {
      getOutputStream().flush()
      Thread.sleep(250) // !!!!
      getOutputStream().close()
    } catch (e: Exception) {
      // Already close
      e.printStackTrace()
    }
    try {
      socket?.close()
    } catch (e: IOException) {
      e.printStackTrace()
      // !!! raise an exception
    } finally {
      socket = null
    }
  }

  /**
   * returns an input stream for the given socket
   */
  fun getInputStream(): InputStream= socket!!.getInputStream()

  /**
   * returns an output stream for the given socket
   */
  private fun getOutputStream(): OutputStream = socket!!.getOutputStream()


  private var socket: Socket? = null
  private var user: String? = null
  private var pass: String? = null

  companion object {
    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    private const val STANDARD_EXEC_PORT = 512 // exec/tcp
  }
}
