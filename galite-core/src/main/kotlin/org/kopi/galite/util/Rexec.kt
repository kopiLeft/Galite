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
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.io.OutputStream
import java.net.Socket
import java.net.UnknownHostException
import kotlin.system.exitProcess

/**
 * Remote execution client
 *
 * @param host represents the first characteristic of the client's remote execution
 * @param port represents the second characteristic of the client's remote execution
 */
class Rexec(private val host: String, private val port: Int = STANDARD_EXEC_PORT) {
  /**
   *
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
   *
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
      val output = socket?.getOutputStream()
      output?.write("0".toByteArray()) // no socket for stderr
      output?.write(0)
      output?.write(user?.toByteArray()) // !!! at most 16 chars
      output?.write(0)
      output?.write(pass?.toByteArray()) // !!! at most 16 chars
      output?.write(0)
      output?.write(command.toByteArray())
      output?.write(0)
      socket?.getInputStream()?.read() == 0
    } catch (e: IOException) {
      e.printStackTrace()
      false // !!! raise an exception
    }
  }

  /**
   *
   */
  fun close() {
    try {
      getOutputStream()?.flush()
      Thread.sleep(250) // !!!!
      getOutputStream()?.close()
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
   *
   */
  fun getInputStream(): InputStream? = socket?.getInputStream()

  /**
   *
   */
  private fun getOutputStream(): OutputStream? = socket?.getOutputStream()


  private var socket: Socket? = null
  private var user: String? = null
  private var pass: String? = null

  companion object {
    // ----------------------------------------------------------------------
    // TEST java org.kopi.vkopi.lib.util.Rexec host user passwd cmd
    // ----------------------------------------------------------------------
    /* fun main(args: Array<String>) {
       System.err.println("USAGE:Rexec server user pass command")
       val rexec = Rexec(args[0])
       if (!rexec.open(args[1], args[2], args[3])) {
         System.err.println("Error")
         exitProcess(1)
       }
       val data = LineNumberReader(InputStreamReader(rexec.getInputStream()))
       while (true) {
         val line = data.readLine() ?: break
         println("--> $line")
       }
       rexec.close()
     }*/

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    const val STANDARD_EXEC_PORT = 512 // exec/tcp
  }
}
