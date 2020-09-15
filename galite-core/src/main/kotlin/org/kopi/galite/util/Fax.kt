package org.kopi.galite.util


import org.kopi.galite.base.Utils
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
import java.net.UnknownHostException
import java.util.StringTokenizer
import java.util.Vector
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

class Fax(port: Int, host: String) {
  fun login(uname: String): Int {
    Utils.log("Fax", "login:$uname")
    print("USER $uname\n")
    clntOut.flush()
    val answer = check(readLine())
    if (answer == NEEDS_PASSWD) {
      print("""
  PASS 0
  
  """.trimIndent())
      clntOut.flush()
    }
    return answer
  }

  // Verbindung schliessen
  fun endCon() {
    print("""
  QUIT
  
  """.trimIndent())
    clntOut.flush()
    check(readLine())
    if (clnt != null) {
      clntIn.close()
      clntOut.close()
      clnt.close()
    }
  }

  fun sendbuffer(`is`: InputStream): String {
    // Diese Funktion sendet das Byte-Array buf an den Server
    val sndsrv: SendServ
    val pstr: String
    val iaddr: ByteArray
    val df = Deflater(9, false)
    val baos = ByteArrayOutputStream()
    val dos = DeflaterOutputStream(baos, df, `is`.available())
    val buffer = ByteArray(1024)
    var read: Int
    while (`is`.read(buffer, 0, 1024).also { read = it } != -1) {
      dos.write(buffer, 0, read)
    }
    dos.close()

    // Erzeugen des SendServ-Threads
    //sndsrv = new SendServ(buf, debug);
    sndsrv = SendServ(baos.toByteArray(), debug)
    iaddr = getInetAddr()
    pstr = makePORT(iaddr, sndsrv.port)
    print("""
  TYPE I
  
  """.trimIndent()) // Binaer
    clntOut.flush()
    check(readLine())
    print("""
  MODE Z
  
  """.trimIndent()) // ZIP
    //print("MODE S" + "\n"); // Stream
    clntOut.flush()
    check(readLine())
    print("PORT $pstr\n")
    System.err.println("PORT $pstr\n")
    clntOut.flush()
    check(readLine())
    print("""
  STOT
  
  """.trimIndent())
    clntOut.flush()
    val line = readLine()
    check(line)

    // Auf Beendigung des Threads warten
    try {
      sndsrv.join()
    } catch (e: InterruptedException) {
    }
    val st = StringTokenizer(line, " ")
    st.nextToken()
    st.nextToken()
    val filename = st.nextToken()
    check(readLine())

    // Zurueckgegeben wird der Dateiname, unter dem der Server
    // den Buffer gespeichert hat
    return filename
  }

  fun getReceived(name: String): ByteArray {
    // Diese Funktion gibt in einem Byte-Array die angeforderte
    // Datei name zurueck. Diese Datei muss sich innerhalb des
    // recvq Verzeichnisses des Servers befinden
    val pstr: String // String für port
    val iaddr: ByteArray // byte-array für Internetadresse
    val recsrv: RecvServ // server-thread-object

    // Thread erzeugen
    recsrv = RecvServ(debug)
    iaddr = getInetAddr()
    pstr = makePORT(iaddr, recsrv.port)
    if (debug) {
      println("Fax.getReceived: $pstr")
    }
    print("""
  TYPE I
  
  """.trimIndent())
    clntOut.flush()
    check(readLine())
    print("""
  MODE S
  
  """.trimIndent())
    //print("MODE Z" + "\n");
    clntOut.flush()
    check(readLine())
    print("""
  CWD recvq
  
  """.trimIndent()) // in das recvq Verzeichnis wechseln
    clntOut.flush()
    check(readLine())
    print("PORT $pstr\n")
    clntOut.flush()
    check(readLine())
    print("RETR $name\n")
    clntOut.flush()
    check(readLine())
    check(readLine())

    // Auf Beendigung des Threads warten
    try {
      recsrv.join()
    } catch (e: InterruptedException) {
    }
    print("""
  CWD
  
  """.trimIndent()) // zurueck nach Serverroot
    clntOut.flush()
    check(readLine())
    return recsrv.data
  }

  /**
   * Diese Funktion gibt den Inhalt des mit what angegebenen
   * Verzeichnisses als String zurueck.
   */
  fun infoS(what: String): String {
    val pstr: String // String für port
    val iaddr: ByteArray // byte-array für Internetadresse
    val recsrv: RecvServ // server-thread-object

    // Thread erzeugen
    recsrv = RecvServ(debug)
    iaddr = getInetAddr()
    pstr = makePORT(iaddr, recsrv.port)
    print("PORT $pstr\n")
    clntOut.flush()
    check(readLine())
    print("LIST $what\n")
    clntOut.flush()

    // Auf Beendigung des Threads warten
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
      throw PROTOException("Fax.infoS: No Data from Faxserver", 1)
    }
    var cnt: Int // Anzahl der Zeichen zaehlen
    cnt = 0
    while (cnt < recsrv.data.size) {
      if (recsrv.data[cnt].toInt() == 0) {
        break
      }
      cnt++
    }

    //hibyte - the top 8 bits of each 16-bit Unicode character
    return String(recsrv.data, 0, cnt)
  }

  fun command(what: String): String {
    /*
     * Sendet ein Kommando an den Faxserver und
     * gibt die Antwort zurueck oder erzeugt ein
     * Ausnahmeobjekt
     */
    val response = StringBuffer()
    var line: String
    print("""
  $what
  
  """.trimIndent())
    clntOut.flush()
    val erg = check(readLine())

    // SYSTEM_STATUS und HELP_MESSAGE sind laenger als
    // eine Zeile
    if (erg == SYSTEM_STATUS || erg == HELP_MESSAGE) {
      while (true) {
        line = readLine()
        if (check(line) == erg) {
          break
        }
        response.append("""
  $line
  
  """.trimIndent())
      }
    }
    return response.toString()
  }

  private fun setNewJob(number: String, user: String, id: String) {
    // number check:
    var number = number
    var user = user
    number = checkNumber(number)
    user = DEFAULT_USER
    Utils.log("Fax", "NEW JOB:$id / user: $user")

    // Jobparameter einstellen
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
    command("JPARM NOTIFY \"NONE\"") //1:mail when done
    command("JPARM PAGECHOP \"default\"")
    command("JPARM CHOPTHRESHOLD 3")
  }

  private fun checkNumber(number: String): String {
    var newNumber = ""
    for (i in 0 until number.length) {
      if (number[i] >= '0' && number[i] <= '9') {
        newNumber += number[i]
      }
    }
    return newNumber
  }

  private fun makePORT(iaddr: ByteArray, port: Int): String {
    // unteres byte
    val a = (port and 0xff).toByte()
    // oberes byte
    val b = (port and 0xff00 shr 8).toByte()

    // Zusammensetzen des Strings
    return ((0xff and iaddr[0].toInt()).toString() + "," + (0xff and iaddr[1].toInt()) + "," +
            (0xff and iaddr[2].toInt()) + "," + (0xff and iaddr[3].toInt()) + "," +
            (0xff and b.toInt()) + "," + (0xff and a.toInt())).toString()
  }

  private fun print(s: String) {
    if (verboseMode) {
      System.err.print("->$s")
    }
    clntOut.print(s)
  }

  private fun readLine(): String {
    val readLine = clntIn.readLine()
    if (verboseMode) {
      System.err.println(readLine)
    }
    return readLine
  }

  private fun check(str: String?): Int {
    /*
     * check prueft die Antworten des Faxservers und reagiert
     * folgendermassen:
     *
     * - gibt die Nummer des Reply-Codes zurueck, wenn alles
     *   in Ordnung ist
     * - erzeugt ein Ausnahmeobjekt vom Typ PROTOException
     *   wenn ein fataler Fehler auftrat
     * - gibt 0 zurück, wenn str kein Reply-Code ist
     */
    val delim: String
    val message = StringBuffer()
    val rtc: Int

    /* Wenn der Protokollserver beendet wird, waerend eine Verbindung
     * bestand, wird ein null-String geliefert */if (str == null) {
      throw PROTOException("Fax.check: empty Reply String!!!",
              EMPTY_REPLY_STRING)
    }
    if (str[3] == '-') {
      delim = "-"
    } else {
      delim = " "
    }
    val st = StringTokenizer(str, delim)

    // Wenn str ein normaler String ist, gib 0 zurueck
    rtc = try {
      st.nextToken().toInt()
    } catch (e: NumberFormatException) {
      0
    }
    for (i in st.countTokens() downTo 1) {
      message.append(st.nextToken() + " ")
    }
    return when (rtc) {
      SERVICE_NOT_AVAILABLE, NO_DATA_CONNECTION, CONNECTION_CLOSED, FILE_ACTION_NOT_TAKEN, ACTION_ABORTED_ERROR, ACTION_NOT_TAKEN_SPACE, SYNTAX_ERROR_COMMAND, SYNTAX_ERROR_PARAMETER, COMMAND_NOT_IMPLEMENTED, BAD_COMMAND_SEQUENCE, OPERATION_NOT_PERMITTET, NOT_LOGGED_IN, NEED_ACC_FOR_STORING, ACTION_NOT_TAKEN, ACTION_ABORTED_PAGETYPE, FILE_ACTION_ABORTED, FAILED_TO_KILL_JOB, ACTION_NOT_TAKEN_NAME -> throw PROTOException(message.toString(), rtc)
      else -> rtc
    }
  }

  // Localhost soll auch Localhost bleiben...
  private fun getInetAddr():ByteArray {
    val iaddr:ByteArray
    if (host.equals("localhost"))
    {
      // Localhost soll auch Localhost bleiben...
      iaddr = byteArrayOf(127, 0, 0, 1)
    }
    else
    {
      iaddr = InetAddress.getLocalHost().getAddress()
    }
    return iaddr
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val debug = false
  private val clnt: Socket
  private val clntIn: BufferedReader
  private val clntOut: PrintWriter
  private val host: String
  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------
  /**
   * Definition einer eigenen Ausnahme Klasse
   * fuer die Protokollabhaengigen Fehler
   */
   class PROTOException(s: String, val number : Int) : FaxException(s) {

    override val message: String = super.message.toString() + " Replay Code: " + number
  }

  /**
   * Mother class the send and receive thread workers.
   */
  private abstract class BasicServ protected constructor(// ----------------------------------------------------------------------
          // DATA MEMBERS
          // ----------------------------------------------------------------------
          private val debug1: Boolean) : Thread() {
    protected fun debug(message: String?) {
      if (debug1) {
        println(message)
      }
    }

    val port: Int
    protected val srv: ServerSocket?

    companion object {
      // ----------------------------------------------------------------------
      // DATA CONSTANTS
      // ----------------------------------------------------------------------
      private const val TIMEOUT = 20 // in seconds
    }

    init {
      srv = ServerSocket(0, Companion.TIMEOUT)
      // get next free port
      port = srv.getLocalPort()
      debug("BasicServ: port=$port")
      start()
    }
  }

  /**
   * Die Klasse RecServ ist abgeleitet von der Klasse BasicServ.
   * Sie empfaengt Daten vom Protokoll Server
   */
  private class RecvServ(debug: Boolean) : BasicServ(debug) {
    // thread body
    override fun run() {
      val `in`: DataInputStream
      val buf = ByteArray(1024)
      try {
        debug("RecvServ.run: Baue Verbindung auf")
        val srv_clnt = srv!!.accept()
        debug("RecvServ.run: Erzeuge InputStream")
        `in` = DataInputStream(srv_clnt.getInputStream())
        debug("RecvServ.run: Warte auf Daten")

        // ByteArrayOutputStream verhaelt sich wie ein Stream
        // und laesst sich wunderbar in ein Byte-Array zurueckwandeln
        val out = ByteArrayOutputStream()
        var anz: Int
        while (`in`.read(buf).also { anz = it } > 0) {
          out.write(buf, 0, anz)
        }
        data = out.toByteArray()
        srv?.close()
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
   * Die Klasse SendServ ist abgeleitet von der Klasse BasicServ.
   * Sie sendet Daten zum Protokoll Server
   */
  private class SendServ(// ----------------------------------------------------------------------
          // DATA MEMBERS
          // ----------------------------------------------------------------------
          private val buf: ByteArray, debug: Boolean) : BasicServ(debug) {
    // thread body
    override fun run() {
      val out: DataOutputStream
      try {
        debug("SendServ.run: Baue Verbindung auf")
        val srv_clnt = srv!!.accept()
        debug("SendServ.run: Erzeuge OutputStream")
        out = DataOutputStream(srv_clnt.getOutputStream())
        out.write(buf, 0, buf.size)
        out.flush()
        srv_clnt.close()
        debug("SendServ.run: Gesendete Bytes=" + out.size())
        srv?.close()
      } catch (e: IOException) {
        fail("Thread error", e, SEND)
      }
      debug("SendServ.run: Thread beendet!")
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
            input : InputStream,
            user: String,
            nummer: String,
            jobId: String) {
      fax(HFAX_PORT, host, input, user, nummer, jobId)
    }

    /**
     * Sends a fax
     */
    fun fax(port: Int,
            host: String,
            `is`: InputStream,
            user: String,
            nummer: String,
            jobId: String) {
      val fax = Fax(port, host)
      val filename: String
      fax.login(user)
      fax.command("IDLE 900")
      fax.command("TZONE LOCAL")
      filename = fax.sendbuffer(`is`)
      fax.setNewJob(nummer, user, jobId)
      fax.command("JPARM DOCUMENT $filename")
      fax.command("JSUBM")
      fax.endCon()
    }

    /*
   * ----------------------------------------------------------------------
   * READ THE SEND QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
    fun readSendQueue(host: String, user: String): Vector<FaxStatus> {
      return readQueue(host, user, "sendq")
    }

    /*
   * ----------------------------------------------------------------------
   * READ THE DONE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
    fun readDoneQueue(host: String, user: String): Vector<FaxStatus> {
      return readQueue(host, user, "doneq")
    }

    /*
   * ----------------------------------------------------------------------
   * READ THE RECEIVE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
    fun readRecQueue(host: String, user: String): Vector<FaxStatus> {
      return readQueue(host, user, "recvq")
    }

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
    fun readSendtime(jobId: String): String? {
      return null
    }

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
      val ret: String // Hole Statusinformationen
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
    // IMPLEMENTATION
    // ----------------------------------------------------------------------
    /*
   * ----------------------------------------------------------------------
   * READS ANY QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
    private fun readQueue(host: String, user: String, qname: String): Vector<FaxStatus> {
      val queue: Vector<FaxStatus> = Vector<FaxStatus>()
      try {
        val ret = getQueue(HFAX_PORT, host, user, qname)
        val token = StringTokenizer(ret, "\n")
        Utils.log("Fax", "READ $qname : host $host / user $user")
        while (token.hasMoreElements()) {
          try {
            val str = token.nextElement().toString()
            val prozess = StringTokenizer(str, "|")
            if (qname != "recvq") {
              queue.addElement(FaxStatus(prozess.nextToken().trim { it <= ' ' },  // ID
                      prozess.nextToken().trim { it <= ' ' },  // TAG
                      prozess.nextToken().trim { it <= ' ' },  // USER
                      prozess.nextToken().trim { it <= ' ' },  // DIALNO
                      prozess.nextToken().trim { it <= ' ' },  // STATE (CODE)
                      prozess.nextToken().trim { it <= ' ' },  // PAGES
                      prozess.nextToken().trim { it <= ' ' },  // DIALS
                      prozess.nextToken().trim { it <= ' ' })) // STATE (TEXT)
            } else {
              queue.addElement(FaxStatus(prozess.nextToken().trim { it <= ' ' },  // FILENAME %f
                      prozess.nextToken().trim { it <= ' ' },  // TIME IN %t
                      prozess.nextToken().trim { it <= ' ' },  // SENDER %s
                      prozess.nextToken().trim { it <= ' ' },  // PAGES %p
                      prozess.nextToken().trim { it <= ' ' },  // DURATION %h
                      prozess.nextToken().trim { it <= ' ' },"","")) // ERRORTEXT %e
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

    protected fun fail(msg: String, e: Exception, which: Int) {
      // Wird vom RecServ aufgerufen, wenn im Thread ein Fehler auftritt
      System.err.println("$msg: $e")
      typeOfThread = which
      errText = "$msg: $e"
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

    // Eigene Reply-Codes
    private const val LOST_CONNECTION = -1
    private const val EMPTY_REPLY_STRING = -2
    private const val HFAX_PORT = 4559
    private const val HFAX_HOST = "localhost"
    private const val DEFAULT_USER = "KOPI" // !!! laurent : why is there a DEFAULT_USER ?
    protected const val NONE = 0
    protected const val RECEIVE = 1
    protected const val SEND = 2
    private var typeOfThread = NONE
    private const val verboseMode = false
    private var errText = ""

        @JvmStatic
    fun main(argv: Array<String>) {
      val vec: Vector<FaxStatus> = readDoneQueue("vie.kopiright.com", "KOPI")
      println(vec.size)
    }
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  init {
    var port = port
    var host = host
    if (port == 0) {
      port = HFAX_PORT
    }
    if (host == null) {
      host = HFAX_HOST
    }
    this.host = host

    // Socket erzeugen
    clnt = Socket(host, port)

    // I/O Streams erzeugen
    clntIn = BufferedReader(InputStreamReader(clnt.getInputStream()))
    clntOut = PrintWriter(clnt.getOutputStream())
    check(readLine())
  }
}
