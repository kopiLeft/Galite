package org.kopi.galite.util

import java.io.InputStream
import org.kopi.galite.util.lpr.LpR
import org.kopi.galite.util.lpr.LpdException
class LpRPrinter  : Printer {
  /**
   * Creates a printer that send file to an lpd server
   */
  fun LpRPrinter(name: String?,
                 serverHost: String?,
                 port: Int,
                 proxyHost: String?,
                 queue: String?,
                 user: String?) {
    this.name = name
    this.serverHost = serverHost
    this.port = port
    this.proxyHost = proxyHost
    this.queue = queue
    this.user = user

    //    setNumberOfCopies(1);
    selectTray(1) // Standard tray (see common/MAKEDB/dbSchema)
    setPaperFormat(null)
  }

  override fun getPrinterName(): String? {
    return name
  }

  // ----------------------------------------------------------------------
  // PRINT OPTIONS
  // ----------------------------------------------------------------------

//   /**
//    * Sets the number of copy to print
//    */
//   public void setNumberOfCopies(int number) {
//     this.numberOfCopies = number;
//   }

//   /**
//    * Gets the number of copy to print
//    */
//   public int getNumberOfCopies() {
//     return numberOfCopies;
//   }

  // ----------------------------------------------------------------------
  // PRINT OPTIONS
  // ----------------------------------------------------------------------
  //   /**
  //    * Sets the number of copy to print
  //    */
  //   public void setNumberOfCopies(int number) {
  //     this.numberOfCopies = number;
  //   }
  //   /**
  //    * Gets the number of copy to print
  //    */
  //   public int getNumberOfCopies() {
  //     return numberOfCopies;
  //   }
  /**
   * Sets the tray to use
   */
  override fun selectTray(tray: Int) {
    this.tray = tray
  }

  /**
   * Sets the paper format
   */
  override fun setPaperFormat(paperFormat: String?) {
    this.paperFormat = paperFormat
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  fun print(data: PrintJob): String {
    val lpr = LprImpl(data)
    return lpr.print()
  }

  private class LprImpl internal constructor(data: PrintJob) : LpR(serverHost, port, proxyHost, queue, user) {
    fun print(): String {
      try {
        if (data.getTitle() != null) {
          setTitle(data.getTitle())
        }
        print(data.getInputStream(), null)
        close()
      } catch (e: LpdException) {
        throw PrintException(e.getMessage(), PrintException.EXC_UNKNOWN)
      }
      return "not yet implemented"
    }

    protected fun readFully(input : InputStream): ByteArray  {
      val size = input.available()
      val data = ByteArray(size)
      var count: Int
      count = 0
      while (count < size) {
        count += input.read(data, count, size - count)
      }
      input.close()
      return data
    }

    private val data: PrintJob

    init {
      setPrintBurst(false)
      this.data = data
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var name: String? = null

  private var serverHost: String? = null
  private var port = 0
  private var proxyHost: String? = null
  private var queue: String? = null
  private var user: String? = null


  private var tray = 0
  private var paperFormat: String? = null

}