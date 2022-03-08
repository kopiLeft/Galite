/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.util.lpr

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException

import kotlin.collections.ArrayList

/**
 * This class encapsulates the communication with a LPD host
 */
internal class LpdClient(var options: LpdOptions) {

  private var connection: Socket? = null

  /**
   * Gets the input stream
   */
  var inputStream: DataInputStream? = null
    private set

  /**
   * Gets the output stream
   */
  var outputStream: DataOutputStream? = null
    private set

  // --------------------------------------------------------------------
  // CONNECTION
  // --------------------------------------------------------------------

  /**
   * Connects to the LPD server
   */
  fun open() {
    if (options.localHost == "localhost") {
      try {
        options.localHost = InetAddress.getLocalHost().hostName
      } catch (e: UnknownHostException) {
        System.err.println("can't resolve local host name")
        options.localHost = "localhost"
      }
    }
    if (options.printHost == null) {
      options.printHost = options.localHost
    }
    if (options.user == null) {
      options.user = System.getProperty("user.name")
    }
    if (options.remotePort == -1) {
      options.remotePort = if (options.proxyHost != null) PROXY_PORT else REMOTE_PORT
    }
    val remoteHost = InetAddress.getByName(if (options.proxyHost != null) options.proxyHost else options.printHost)

    if (options.bindSourcePort) {
      var port = SOURCE_PORT_LOW
      while (connection == null && port <= SOURCE_PORT_HIGH) {
        try {
          connection = Socket(remoteHost,
                              options.remotePort,
                              InetAddress.getLocalHost(),
                              port)
          options.sourcePort = port
        } catch (e: IOException) {
          // !!! try next one, but check error first
          if (port == SOURCE_PORT_HIGH) {
            throw e
          }
        }
        port++
      }
    } else {
      connection = Socket(options.printHost, options.remotePort)
      options.sourcePort = connection!!.localPort
    }
    connection!!.soTimeout = options.timeout
    inputStream = DataInputStream(connection!!.getInputStream())
    outputStream = DataOutputStream(connection!!.getOutputStream())

    if (options.proxyHost != null) {
      outputStream!!.writeBytes("${options.printHost}\n")
    }
  }

  /**
   * Disconnects from the LPD server
   */
  fun close() {
    try {
      inputStream!!.close()
      outputStream!!.close()
      connection!!.close()
    } catch (e: Exception) {
      e.printStackTrace()
      println("Error while closing connection port")
      println(e.message)
    }
    connection = null
  }

  /**
   * Returns true if the connection to the LPD server is established.
   */
  val isConnected: Boolean
    get() = connection != null

  /**
   * the local hostname.
   */
  var localHost: String?
    get() = options.localHost
    set(localHost) {
      options.localHost = localHost
    }

  /**
   * Sets the proxy hostname.
   */
  fun setProxyHost(proxyHost: String) {
    options.proxyHost = proxyHost
  }

  /**
   * the print hostname.
   */
  var printHost: String?
    get() = options.printHost
    set(printHost) {
      options.printHost = printHost
    }

  /**
   * Sets the remote port.
   */
  fun setRemotePort(remotePort: Int) {
    options.remotePort = remotePort
  }

  /**
   * Set if source port should be bound to value in RFC 1179.
   */
  fun setBindSourcePort(flag: Boolean) {
    options.bindSourcePort = flag
  }

  /**
   * the user name.
   */
  var user: String?
    get() = options.user!!
    set(user) {
      options.user = user
    }

  /**
   * Sets the timeout.
   */
  fun setTimeout(timeout: Int) {
    options.timeout = timeout
  }

  // --------------------------------------------------------------------
  // LPD REQUEST
  // --------------------------------------------------------------------

  /**
   * Prints all job
   */
  fun startWaitingJob() {
    val out = outputStream
    out!!.write(1)
    out.writeBytes("\n")
    out.flush()
  }

  /**
   * Request the queue state
   */
  fun getWaitingJob(queue: String,
                    longOutput: Boolean,
                    userOrJobID: String?): ArrayList<*> {
    val out = outputStream

    out!!.write(if (longOutput) 4 else 3)
    out.writeBytes(queue)
    if (userOrJobID != null) {
      out.writeBytes(" $userOrJobID")
    }
    out.writeBytes("\n")
    val inputStream = inputStream
    val lnr = LineNumberReader(InputStreamReader(inputStream))
    var line: String
    val state: ArrayList<String> = ArrayList()

    while (lnr.readLine().also { line = it } != null && line.length > 1) {
      state.add(line)
    }
    return state
  }

  /**
   * Remove jobs
   */
  fun removeJob(queue: String, args: Array<String>?) {
    val out = outputStream
    out!!.write(5)
    out.writeBytes("$queue $user")
    args?.forEach {
      out.writeBytes(" $it")
    }
    out.writeBytes("\n")
  }

  fun sendPrinterJob(queue: String,
                     controlFirst: Boolean,
                     control: String,
                     jobID: String,
                     data: ByteArray?) {
    sendPrinterJob(queue,
                   controlFirst,
                   control,
                   jobID,
                   ByteArrayInputStream(data))
  }

  fun sendPrinterJob(queue: String,
                     controlFirst: Boolean,
                     control: String,
                     jobID: String,
                     stream: InputStream) {
    val out = outputStream

    out!!.write(2)
    out.writeBytes("$queue".trimIndent())
    checkAcknowledge("Error while start printing on queue $queue")
    if (controlFirst) {
      sendControl(jobID, control)
      sendData(jobID, stream)
    } else {
      sendData(jobID, stream)
      sendControl(jobID, control)
    }
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private fun sendControl(jobID: String,
                          control: String) {
    val out = outputStream

    out!!.write(2)
    out.writeBytes("" + control.length)
    out.writeBytes(" ")
    out.writeBytes("cfA$jobID$printHost\n")
    checkAcknowledge("Error while start sending control file")
    out.writeBytes(control)
    out.writeByte(0)
    checkAcknowledge("Error while sending control file")
  }

  private fun sendData(jobID: String, inputStream: InputStream) {
    val size: Int = inputStream.available()
    val out: DataOutputStream? = outputStream

    out!!.write(3)
    out.writeBytes(size.toString())
    out.writeBytes(" ")
    out.writeBytes("dfA$jobID$printHost\n")
    checkAcknowledge("Error while start sending data file")
    for (i in 0 until size) {
      out.write(inputStream.read())
    }
    out.writeByte(0)
    out.flush()
    inputStream.close()
    checkAcknowledge("Error while sending data file")
  }

  private fun checkAcknowledge(errorMessage: String) {
    val returnCode = inputStream!!.readByte().toInt()

    if (returnCode != 0) {
      throw LpdException(returnCode, errorMessage)
    }
  }

  companion object {

    // Constants are defined in RFC 1179
    private const val SOURCE_PORT_LOW = 721
    private const val SOURCE_PORT_HIGH = 731
    private const val REMOTE_PORT = 515
    private const val PROXY_PORT = 7290
  }
}
