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

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ConnectException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.StringTokenizer
import java.util.Vector
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

import org.kopi.galite.base.Utils

@Deprecated("replaced by the class HylaFAXUtils")
class Fax(var port: Int, var host: String) {

  fun login(uname: String): Int {
    Utils.log("Fax", "login:$uname")

    print("USER $uname\n")
    clntOut.flush()
    val answer = check(readLine())
    if (answer == NEEDS_PASSWD) {
      print("PASS " + 0 + "\n")
      clntOut.flush()
    }
    return answer
  }

  // Close connection
  fun endCon() {
    print("QUIT" + "\n")
    clntOut.flush()

    check(readLine())
    if (clnt != null) {
      clntIn.close()
      clntOut.close()
      clnt.close()
    }
  }

  fun sendbuffer(inputStream: InputStream): String {
    // This function sends the byte array buf to the server
    val sendsrv: SendServ
    val pstr: String

    val df = Deflater(9, false)
    val baos = ByteArrayOutputStream()
    val dos = DeflaterOutputStream(baos, df, inputStream.available())

    val buffer = ByteArray(1024)
    var read: Int

    while (inputStream.read(buffer, 0, 1024).also { read = it } != -1) {
      dos.write(buffer, 0, read)
    }
    dos.close()

    // Creation of the SendServ thread
    // sndsrv = new SendServ(buf, debug);
    sendsrv = SendServ(baos.toByteArray(), debug)

    val iaddr = getInetAddr()
    pstr = makePORT(iaddr, sendsrv.port)

    print("TYPE I" + "\n") // Binary
    clntOut.flush()
    check(readLine())

    print("MODE Z" + "\n") // ZIP
    //print("MODE S" + "\n"); // Stream
    clntOut.flush()
    check(readLine())

    print("PORT $pstr\n")
    System.err.println("PORT $pstr\n")
    clntOut.flush()
    check(readLine())

    print("STOT" + "\n")
    clntOut.flush()
    val line = readLine()
    check(line)

    // Wait for the thread to finish
    try {
      sendsrv.join()
    } catch (e: InterruptedException) {
    }

    val st = StringTokenizer(line, " ")
    st.nextToken()
    st.nextToken()
    val filename = st.nextToken()
    check(readLine())

    // The file name under which the server is returned is returned
    // has saved the buffer
    return filename
  }

  fun getReceived(name: String): ByteArray {
    // This function returns the requested file name in a byte array.
    // This file must be located in the server's recvq directory
    val pstr: String // String for port

    // Thread creation
    val recsrv = RecvServ(debug) // server-thread-object

    val iaddr = getInetAddr() // byte-array for Internet address
    pstr = makePORT(iaddr, recsrv.port)

    if (debug) {
      println("Fax.getReceived: $pstr")
    }

    print("TYPE I" + "\n")
    clntOut.flush()
    check(readLine())

    print("MODE S" + "\n")
    //print("MODE Z" + "\n");
    clntOut.flush()
    check(readLine())

    print("CWD recvq" + "\n") // Change to the recvq directory
    clntOut.flush()
    check(readLine())

    print("PORT $pstr\n")
    clntOut.flush()
    check(readLine())

    print("RETR $name\n")
    clntOut.flush()
    check(readLine())
    check(readLine())

    // Wait for the thread to finish
    try {
      recsrv.join()
    } catch (e: InterruptedException) {
    }

    print("CWD" + "\n") // back to server root
    clntOut.flush()
    check(readLine())

    return recsrv.data
  }

  /**
   * This function returns the content of the specified with what
   * Returns the directory as a string
   */
  fun infoS(what: String): String {
    val pstr: String // String for port

    // Thread creation
    val recsrv = RecvServ(debug) // server-thread-object

    val iaddr = getInetAddr() // byte-array for Internet address
    pstr = makePORT(iaddr, recsrv.port)

    print("PORT $pstr\n")
    clntOut.flush()
    check(readLine())

    print("LIST $what\n")
    clntOut.flush()

    // Wait for the thread to finish
    try {
      recsrv.join()
    } catch (e: InterruptedException) {
    }

    if (typeOfThread != NONE) {
      throw PROTOException(errText, LOST_CONNECTION)
    }

    if (check(readLine()) == ABOUT_TO_OPEN_DATACON) {
      check(readLine())
    } else {
      throw PROTOException("Fax.infoS: No Data from Fax server", 1)
    }

    var cnt = 0 // Count the number of characters
    while (cnt < recsrv.data.size) {
      if (recsrv.data[cnt].toInt() == 0) {
        break
      }
      cnt++
    }

    // hibyte - the top 8 bits of each 16-bit Unicode character
    return String(recsrv.data, 0, cnt)
  }

  fun command(what: String): String {
    /* Sends a command to the fax server and
     * returns the answer or generates
     * an exception
     */

    val response = StringBuffer()
    var line: String

    print(what + "\n")
    clntOut.flush()

    val erg = check(readLine())

    // SYSTEM_STATUS and HELP_MESSAGE are longer than one line
    if (erg == SYSTEM_STATUS || erg == HELP_MESSAGE) {
      while (true) {
        line = readLine()
        if (check(line) == erg) {
          break
        }
        response.append(line + "\n")
      }
    }

    return response.toString()
  }

  private fun setNewJob(number: String, user: String, id: String) {
    // number check:
    val number = checkNumber(number)

    val user = DEFAULT_USER
    Utils.log("Fax", "NEW JOB:$id / user: $user")

    // Set job parameters
    command("JNEW")
    command("JPARM FROMUSER \"$user\"")
    command("JPARM LASTTIME 145959")
    command("JPARM MAXDIALS 3")
    command("JPARM MAXTRIES 3")
    command("JPARM SCHEDPRI 127")
    command("JPARM DIALSTRING \"$number\"")
    command("JPARM NOTIFYADDR \"$user\"")
    command("JPARM JOBINFO \"$id\"")
    command("JPARM VRES 196")
    command("JPARM PAGEWIDTH " + 209)
    command("JPARM PAGELENGTH " + 296)
    command("JPARM NOTIFY \"NONE\"") // 1:mail when done
    command("JPARM PAGECHOP \"default\"")
    command("JPARM CHOPTHRESHOLD 3")
  }

  private fun checkNumber(number: String): String {
    var newNumber = ""

    number.forEach {
      if (it in '0'..'9') {
        newNumber += it
      }
    }

    return newNumber
  }

  private fun makePORT(iaddr: ByteArray, port: Int): String {
    // the lower byte
    val a = (port and 0xff).toByte()
    // the upper byte
    val b = (port and 0xff00 shr 8).toByte()

    // Assembling the string
    return ((0xff and iaddr[0].toInt()).toString() + "," + (0xff and iaddr[1].toInt()) + "," +
            (0xff and iaddr[2].toInt()) + "," + (0xff and iaddr[3].toInt()) + "," +
            (0xff and b.toInt()) + "," + (0xff and a.toInt()))
  }

  private fun print(s: String) {
    if (verboseMode) {
      System.err.print("->$s")
    }
    clntOut.print(s)
  }

  private fun readLine(): String {
    val readLine: String = clntIn.readLine()
    if (verboseMode) {
      System.err.println(readLine)
    }

    return readLine
  }

  private fun check(str: String?): Int {
    /*
     * checks the replies from the fax server and reacts
     * as follows:
     *
     * - returns the number of the reply code, if everything
     *   is ok
     * - creates an exception object of the type PROTOException
     *   if a fatal error occurred
     * - returns 0 if str is not a reply code
     */

    val message = StringBuffer()
    val rtc: Int

    /* When the log server is shut down while connected
     * existed, a null string is returned */
    if (str == null) {
      throw PROTOException("Fax.check: empty Reply String!!!",
                           EMPTY_REPLY_STRING)
    }
    val delim: String = if (str[3] == '-') "-" else " "

    val st = StringTokenizer(str, delim)


    //If str is a normal string, return 0
    rtc = try {
      st.nextToken().toInt()
    } catch (e: NumberFormatException) {
      0
    }

    for (i in st.countTokens() downTo 1) {
      message.append(st.nextToken() + " ")
    }

    // The following reply codes generate an exception object
    return when (rtc) {
      SERVICE_NOT_AVAILABLE,
      NO_DATA_CONNECTION,
      CONNECTION_CLOSED,
      FILE_ACTION_NOT_TAKEN,
      ACTION_ABORTED_ERROR,
      ACTION_NOT_TAKEN_SPACE,
      SYNTAX_ERROR_COMMAND,
      SYNTAX_ERROR_PARAMETER,
      COMMAND_NOT_IMPLEMENTED,
      BAD_COMMAND_SEQUENCE,
      OPERATION_NOT_PERMITTET,
      NOT_LOGGED_IN,
      NEED_ACC_FOR_STORING,
      ACTION_NOT_TAKEN,
      ACTION_ABORTED_PAGETYPE,
      FILE_ACTION_ABORTED,
      FAILED_TO_KILL_JOB,
      ACTION_NOT_TAKEN_NAME
      -> throw PROTOException(message.toString(), rtc)
      else -> rtc
    }
  }

  // Localhost should also remain localhost ...
  private fun getInetAddr(): ByteArray = if (host.equals("localhost", ignoreCase = true)) {
    // Localhost should also remain localhost ...
    byteArrayOf(127, 0, 0, 1)
  } else {
    InetAddress.getLocalHost().address
  }

  protected fun fail(msg: String, e: Exception, which: Int) {
    // Called by the RecServ when an error occurs in the thread
    System.err.println("$msg: $e")

    typeOfThread = which
    errText = "$msg: $e"
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private val debug = false
  private val clnt: Socket?
  private val clntIn: BufferedReader
  private val clntOut: PrintWriter

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------

  /**
   * Definition of your own exception class
   * for the protocol-dependent errors
   * @param number  This variable stores the reply code
   */
  inner class PROTOException(s: String, val number: Int) : FaxException(s) {

    override val message: String
      get() = super.message.toString() + " Replay Code: " + number

  }

  /**
   * Mother class that sends and receives thread workers.
   */
  private abstract class BasicServ protected constructor(private val debug1: Boolean) : Thread() {

    protected fun debug(message: String?) {
      if (debug1) {
        println(message)
      }
    }

    val port: Int
    protected val srv: ServerSocket = ServerSocket(0, TIMEOUT)


    init {
      // get next free port
      port = srv.localPort
      debug("BasicServ: port=$port")
      start()
    }

    companion object {
      private const val TIMEOUT = 20 // in seconds
    }
  }

  /**
   * The RecServ class is derived from the BasicServ class.
   * It is receiving data from the protocol server
   */
  private inner class RecvServ(debug: Boolean) : BasicServ(debug) {

    // thread body
    override fun run() {
      val buf = ByteArray(1024)

      try {
        debug("RecvServ.run: Build connection")
        val srv_clnt = srv.accept()

        debug("RecvServ.run: Generate InputStream")
        val dataInputStream = DataInputStream(srv_clnt.getInputStream())

        debug("RecvServ.run: Wait for data")

        // ByteArrayOutputStream behaves like a stream
        // and can be easily converted back into a byte array
        val out = ByteArrayOutputStream()
        var anz: Int

        while (dataInputStream.read(buf).also { anz = it } > 0) {
          out.write(buf, 0, anz)
        }

        data = out.toByteArray()

        srv.close()
      } catch (e: IOException) {
        fail("RecvServ", e, RECEIVE)
      }

      debug("RecvServ.run: Thread beendet!")
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    lateinit var data: ByteArray
  }

  /*
   * The SendServ class is derived from the BasicServ class.
   * It sends data to the protocol server
   */
  private inner class SendServ(private val buf: ByteArray, debug: Boolean) : BasicServ(debug) {

    // thread body
    override fun run() {
      val out: DataOutputStream

      try {
        debug("SendServ.run: Build connection")
        val srv_clnt = srv.accept()

        debug("SendServ.run: Generate OutputStream")
        out = DataOutputStream(srv_clnt.getOutputStream())

        out.write(buf, 0, buf.size)
        out.flush()

        srv_clnt.close()

        debug("SendServ.run: Bytes sent= " + out.size())
        srv.close()
      } catch (e: IOException) {
        fail("Thread error", e, SEND)
      }

      debug("SendServ.run: Thread completed!")
    }

  }

  companion object {

    // ----------------------------------------------------------------------
    // CONVENIENCE METHODS TO SEND FAX, GET QUEUE STATUS, ...
    // ----------------------------------------------------------------------

    /**
     * Sends a fax
     */
    fun fax(host: String,
            inputStream: InputStream,
            user: String,
            number: String,
            jobId: String) {
      fax(HFAX_PORT, host, inputStream, user, number, jobId)
    }

    /**
     * Sends a fax
     */
    fun fax(port: Int,
            host: String,
            inputStream: InputStream,
            user: String,
            number: String,
            jobId: String) {
      val fax = Fax(port, host)
      val filename: String

      fax.login(user)
      fax.command("IDLE 900")
      fax.command("TZONE LOCAL")
      filename = fax.sendbuffer(inputStream)
      fax.setNewJob(number, user, jobId)
      fax.command("JPARM DOCUMENT $filename")
      fax.command("JSUBM")
      fax.endCon()
    }

    // ----------------------------------------------------------------------
    // IMPLEMENTATION
    // ----------------------------------------------------------------------

    /*
     * ----------------------------------------------------------------------
     * READS ANY QUEUE
     * RETURNS A VECTOR OF STRINGS
     * ----------------------------------------------------------------------
     */
    private fun readQueue(host: String, user: String, qname: String): Vector<FaxStatus> {
      val queue = Vector<FaxStatus>()

      try {
        val ret = getQueue(HFAX_PORT, host, user, qname)
        val token = StringTokenizer(ret, "\n")

        Utils.log("Fax", "READ $qname : host $host / user $user")

        while (token.hasMoreElements()) {
          try {
            val str = token.nextElement().toString()
            val process = StringTokenizer(str, "|")

            if (qname != "recvq") {
              queue.addElement(FaxStatus(process.nextToken().trim(),  // ID
                                         process.nextToken().trim(),  // TAG
                                         process.nextToken().trim(),  // USER
                                         process.nextToken().trim(),  // DIALNO
                                         process.nextToken().trim(),  // STATE (CODE)
                                         process.nextToken().trim(),  // PAGES
                                         process.nextToken().trim(),  // DIALS
                                         process.nextToken().trim())) // STATE (TEXT)
            } else {
              queue.addElement(FaxStatus(process.nextToken().trim(),  // FILENAME %f
                                         process.nextToken().trim(),  // TIME IN %t
                                         process.nextToken().trim(),  // SENDER %s
                                         process.nextToken().trim(),  // PAGES %p
                                         process.nextToken().trim(),  // DURATION %h
                                         process.nextToken().trim())) // ERRORTEXT %e
            }
          } catch (e: Exception) {
            throw FaxException(e.message!!, e)
          }
        }
      } catch (e: ConnectException) {
        Utils.log("Fax", "NO FAX SERVER")
        throw FaxException("NO FAX SERVER")
      } catch (e: Exception) {
        throw FaxException(e.message!!, e)
      }

      return queue
    }

    /*
     * ----------------------------------------------------------------------
     * READ THE SEND QUEUE
     * RETURNS A VECTOR OF STRINGS
     * ----------------------------------------------------------------------
     */
    fun readSendQueue(host: String, user: String): Vector<FaxStatus> = readQueue(host, user, "sendq")

    /*
   * ----------------------------------------------------------------------
   * READ THE DONE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
    fun readDoneQueue(host: String, user: String): Vector<FaxStatus> = readQueue(host, user, "doneq")

    /*
   * ----------------------------------------------------------------------
   * READ THE RECEIVE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
    fun readRecQueue(host: String, user: String): Vector<FaxStatus> = readQueue(host, user, "recvq")

    /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */
    fun readServerState(host: String, user: String): Vector<String> {
      val queue = Vector<String>()
      try {
        val ret = getQueue(HFAX_PORT, host, user, "status")
        val token = StringTokenizer(ret, "\n")

        Utils.log("Fax", "READ STATE : host $host / user $user")

        while (token.hasMoreElements()) {
          queue.addElement(token.nextElement().toString())
        }
      } catch (e: ConnectException) {
        throw FaxException("NO FAX SERVER")
      } catch (e: Exception) {
        throw FaxException("Trying read server state: " + e.message, e)
      }

      return queue
    }

    /*
     * ----------------------------------------------------------------------
     * HANDLE THE SERVER AND MODEM STATE
     * ----------------------------------------------------------------------
     */
    fun readSendtime(jobId: String?): String? = null

    /**
     * Convenience method
     */
    fun killJob(host: String, user: String, job: String) {
      killJob(HFAX_PORT, host, user, job)
    }

    /**
     * Convenience method
     */
    fun killJob(port: Int,
                host: String,
                user: String,
                job: String) {
      val fax = Fax(port, host)

      fax.login(DEFAULT_USER) // !!! laurent 20020626 : why DEFAULT_USER and not user ?
      fax.command("JKILL $job")
      Utils.log("Fax", "Kill 1: $job")
      fax.endCon()
    }

    /**
     * Convenience method
     */
    fun clearJob(host: String,
                 user: String,
                 job: String) {
      clearJob(HFAX_PORT, host, user, job)
    }

    /**
     * Convenience method
     */
    fun clearJob(port: Int,
                 host: String,
                 user: String,
                 job: String) {
      val fax = Fax(port, host)

      fax.login(DEFAULT_USER) // !!! laurent 20020626 : why DEFAULT_USER and not user ?
      fax.command("JDELE $job")
      Utils.log("Fax", "Delete 1: $job")
      fax.endCon()
    }

    /*
     * ----------------------------------------------------------------------
     * HANDLE THE QUEUES --- ALL QUEUES ARE HANDLED BY THAT METHOD
     * ----------------------------------------------------------------------
     */
    private fun getQueue(port: Int, host: String, user: String, qname: String): String {
      val fax = Fax(port, host)
      val ret: String // Get status information

      fax.login(user)
      fax.command("IDLE 900")
      fax.command("TZONE LOCAL")
      fax.command("JOBFMT \" %j| %J| %o| %e| %a| %P| %D| %s\"")
      fax.command("RCVFMT \" %f| %t| %s| %p| %h| %e\"")
      fax.command("MDMFMT \"Modem %m (%n): %s\"")
      ret = fax.infoS(qname)
      fax.endCon()

      return ret
    }

    // ----------------------------------------------------------------------
    // DATA CONSTANTS
    // ----------------------------------------------------------------------

    private const val ABOUT_TO_OPEN_DATACON = 150
    private const val SYSTEM_STATUS = 211
    private const val HELP_MESSAGE = 214
    private const val NEEDS_PASSWD = 331
    private const val SERVICE_NOT_AVAILABLE = 421
    private const val NO_DATA_CONNECTION = 425
    private const val CONNECTION_CLOSED = 426
    private const val FILE_ACTION_NOT_TAKEN = 450
    private const val ACTION_ABORTED_ERROR = 451
    private const val ACTION_NOT_TAKEN_SPACE = 452
    private const val FAILED_TO_KILL_JOB = 460
    private const val SYNTAX_ERROR_COMMAND = 500
    private const val SYNTAX_ERROR_PARAMETER = 501
    private const val COMMAND_NOT_IMPLEMENTED = 502
    private const val BAD_COMMAND_SEQUENCE = 503
    private const val OPERATION_NOT_PERMITTET = 504
    private const val NOT_LOGGED_IN = 530
    private const val NEED_ACC_FOR_STORING = 532
    private const val ACTION_NOT_TAKEN = 550
    private const val ACTION_ABORTED_PAGETYPE = 551
    private const val FILE_ACTION_ABORTED = 552
    private const val ACTION_NOT_TAKEN_NAME = 553

    // Own reply codes
    private const val LOST_CONNECTION = -1
    private const val EMPTY_REPLY_STRING = -2

    private const val HFAX_PORT = 4559
    private const val HFAX_HOST = "localhost"

    private const val DEFAULT_USER = "GALITE" // !!! laurent : why is there a DEFAULT_USER ?

    protected const val NONE = 0
    protected const val RECEIVE = 1
    protected const val SEND = 2

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private var typeOfThread = NONE
    private const val verboseMode = false
    private var errText = ""

  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  init {

    if (port == 0) {
      port = HFAX_PORT
    }

    if (host == null) {
      host = HFAX_HOST
    }
    this.host = host

    // Create socket
    clnt = Socket(host, port)

    // I/O Streams creation
    clntIn = BufferedReader(InputStreamReader(clnt.getInputStream()))
    clntOut = PrintWriter(clnt.getOutputStream())

    check(readLine())
  }
}
