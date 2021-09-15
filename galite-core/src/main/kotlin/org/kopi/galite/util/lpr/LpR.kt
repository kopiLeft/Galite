/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.lpr

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

import kotlin.system.exitProcess

open class LpR {

  @JvmOverloads
  constructor(host: String,
              port: Int,
              proxyHost: String? = null,
              queue: String? = null,
              user: String? = null) {
    options = LpROptions()
    client = LpdClient(options)
    client.printHost = host
    client.setRemotePort(port)
    setQueue(queue!!)
    setUser(user)
    if (proxyHost != null) {
      client.setProxyHost(proxyHost)
    }
  }

  /**
   * Only main can construct Main
   * @exception    IOException    such as file not found
   * @exception    org.kopi.galite.util.lpr.LpdException    if some problems occurs during
   * connection to lpd
   */
  private constructor(args: Array<String>) {
    if (!parseArguments(args)) {
      exitProcess(1)
    }
    client = LpdClient(options)
    val infiles: Array<String?> = options.nonOptions

    try {
      if (infiles.isEmpty()) {
        print(System.`in`, "standard input")
      } else {
        for (i in infiles.indices) {
          print(File(infiles[i]), infiles[i])
        }
      }
    } finally {
      close()
    }
  }

  private fun parseArguments(args: Array<String>): Boolean {
    options = LpROptions()
    return options.parseCommandLine(args)
  }

  // --------------------------------------------------------------------
  // PRINT METHODS
  // --------------------------------------------------------------------

  /**
   * Prints the specified file
   * @exception    org.kopi.galite.util.lpr.LpdException    problem during communication with lpd
   * @exception    java.io.IOException    io problem
   */
  fun print(file: File?, document: String?) {
    val inputStream = FileInputStream(file)
    print(inputStream, document)
    if (options.remove) {
      // !!! should remove file
    }
  }

  /**
   * Prints the specified file
   * @exception    org.kopi.galite.util.lpr.LpdException    problem during communication with lpd
   * @exception    java.io.IOException    io problem
   */
  fun print(inputStream: InputStream, document: String?) {
    val data: ByteArray = readFully(inputStream)

    print(data, document)
  }

  /**
   * Prints the specified file
   * @exception    org.kopi.galite.util.lpr.LpdException   problem during communication with lpd
   * @exception    java.io.IOException    io problem
   */
  fun print(data: ByteArray?, document: String?) {
    if (!client.isConnected) {
      client.open()
    }
    options.job = jobID
    initControl()
    for (i in 0 until options.copies) {
      addControl(options.filetype[0], "dfA" + options.job + client.printHost)
    }

    // unlink file after printing
    addControl('U', "dfA" + options.job + client.printHost)
    document ?: options.title?.let { addControl('N', it) }
    options.queue?.let {
      client.sendPrinterJob(it,
                            !options.dataFirst,
                            control.toString(),
                            options.job!!,
                            data)
    }
    if (options.windows) {
      client.close()
    }
  }

  /**
   * PrintWaitingJobs
   */
  fun printWaitingJob() {
    client.startWaitingJob()
  }

  fun close() {
    client.close()
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Sets the user
   */
  fun setUser(user: String?) {
    client.user = user
  }

  /**
   * Sets the file type
   */
  fun setFileType(filetype: Char) {
    options.filetype = "" + filetype
  }

  /**
   * the number of copy to print
   */
  var numberOfCopies: Int
    get() = options.copies
    set(nb) {
      options.copies = nb
    }

  /**
   * Sets if the burst page is printed
   */
  fun setPrintBurst(printBurst: Boolean) {
    options.burst = printBurst
  }

  /**
   * Sets if a mail is send after printing
   */
  fun setMailAfter(mailAfter: Boolean) {
    options.mail = mailAfter
  }

  /**
   * Sets if the file is removed after printing
   */
  fun setRemoveAfter(removeAfter: Boolean) {
    options.remove = removeAfter
  }

  /**
   * Sets if the lpd (daemon is hosted on a windows host
   */
  fun setWindowsLpd(windowsLpd: Boolean) {
    options.windows = windowsLpd
  }

  /**
   * Sets the print class
   */
  fun setPrintClass(printClass: String?) {
    //options.printClass = printClass;
  }

  /**
   * Sets the queue
   */
  fun setQueue(queue: String) {
    options.queue = queue
  }

  /**
   * Sets the job
   */
  fun setJob(job: String) {
    options.job = job
  }

  /**
   * Sets the title
   */
  fun setTitle(title: String) {
    options.title = title
  }

  /**
   * Sets the tmp directory
   */
  fun setTmpDir(tmpDir: String) {
    options.tmpdir = tmpDir
  }

  /**
   * Sets the control first flag
   */
  fun setControlFirst(controlFirst: Boolean) {
    options.dataFirst = !controlFirst
  }

  /**
   * Sets the width
   */
  fun setWidth(width: Int) {
    options.width = width
  }

  /**
   * Sets the indentation
   */
  fun setIndentation(indentation: Int) {
    options.indent = indentation
  }

  // --------------------------------------------------------------------
  // UTILITIES METHODS
  // --------------------------------------------------------------------

  protected open fun readFully(inputStream: InputStream): ByteArray {
    val size = inputStream.available()
    val data = ByteArray(size)
    var count = 0

    while (count < size) {
      count += inputStream.read(data, count, size - count)
    }
    inputStream.close()
    return data
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private fun initControl() {
    control = StringBuffer()
    addControl('H', client.printHost)
    addControl('P', client.user!!)
    if (options.indent != -1) {
      addControl('I', "" + options.indent)
    }
    addControl('T', options.title!!)
    if (options.mail) {
      addControl('M', client.user!!)
    }
    if (options.burst) {
      if (options.job == null) "XXX" else options.job?.let { addControl('J', it) }
      if (options.printClass == null) client.localHost else options.printClass?.let { addControl('C', it) }
      addControl('L', client.user!!)
    }
    if (options.filetype != null
            && (options.filetype[0] == 'c' || options.filetype[0] == 'l' || options.filetype[0] == 'p')) {
      addControl('W', "" + options.width)
    }
  }

  /**
   * Adds a line to the control file
   */
  private fun addControl(tag: Char, value: Any) {
    control!!.append(tag)
    control!!.append(value)
    control!!.append('\n')
  }

  private val jobID: String
    private get() {
      Companion.jobID += 1
      return when {
        Companion.jobID < 10 -> {
          "00" + Companion.jobID
        }
        Companion.jobID < 100 -> {
          "0" + Companion.jobID
        }
        else -> {
          "" + Companion.jobID
        }
      }
    }

  private var control: StringBuffer? = null
  private lateinit var options: LpROptions
  private var client: LpdClient

  companion object {

    // --------------------------------------------------------------------
    // ENTRY POINT
    // --------------------------------------------------------------------

    /**
     * Program entry point
     * @exception    org.kopi.galite.util.lpr.LpdException    problem during communication with lpd
     * @exception    java.io.IOException    io problem
     */
    @JvmStatic
    fun main(args: Array<String>) {
      LpR(args)
    }

    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------
    private var jobID = 0
  }
}
