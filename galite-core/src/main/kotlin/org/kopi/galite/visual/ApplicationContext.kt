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

package org.kopi.galite.visual

import java.io.CharArrayWriter
import java.io.StringWriter
import java.io.PrintWriter
import java.io.InputStreamReader
import java.io.FileWriter
import java.io.IOException
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Locale
import java.util.Date

import org.kopi.galite.base.UComponent
import org.kopi.galite.db.DBContext
import org.kopi.galite.base.Utils
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.mailer.Mailer

/**
 * `ApplicationContext` is an application context that contains the
 * running [Application] instance in the context thread. The `ApplicationContext`
 * handles all shared applications components.
 */
abstract class ApplicationContext {

  //-----------------------------------------------------------
  // ABSTRACT METHODS
  //-----------------------------------------------------------

  /**
   * Returns the **current** [Application] instance.
   * @return The **current** [Application] instance.
   */
  abstract fun getApplication(): Application

  /**
   * Returns the **current** [PreviewRunner] instance.
   * @return The **current** [PreviewRunner] instance.
   */
  abstract fun getPreviewRunner(): PreviewRunner

  /**
   * Returns `true` if we are in a web application context.
   * @return `true` if we are in a web application context.
   */
  abstract fun isWebApplicationContext(): Boolean

  companion object {

    //-----------------------------------------------------------
    // UTILS
    //-----------------------------------------------------------

    /**
     * Returns the default configuration of the Application
     */
    fun getDefaults(): ApplicationConfiguration = applicationContext.getApplication().getApplicationConfiguration()

    /**
     * Returns the [Application] menu.
     * @return The [Application] menu.
     */
    fun getMenu(): VMenuTree = applicationContext.getApplication().getMenu()

    /**
     * Returns the [LocalizationManager] instance.
     * @return The [LocalizationManager] instance.
     */
    fun getLocalizationManager(): LocalizationManager = applicationContext.getApplication().getLocalizationManager()

    /**
     * Returns the default application [Locale].
     * @return The default application [Locale].
     */
    fun getDefaultLocale(): Locale = applicationContext.getApplication().getDefaultLocale()

    /**
     * Returns the application [Registry].
     * @return the application [Registry].
     */
    fun getRegistry(): Registry = applicationContext.getApplication().getRegistry()

    /**
     * Returns the application [DBContext].
     * @return The application [DBContext].
     */
    fun getDBContext(): DBContext = applicationContext.getApplication().getDBContext()

    /**
     * Returns `true` if the [Application] should only generate help.
     * @return `true` if the [Application] should only generate help.
     */
    fun isGeneratingHelp(): Boolean = applicationContext.getApplication().isGeneratingHelp()

    /**
     * Displays an error message outside a model context. This can happen when launching a module.
     * @param parent The parent component.
     * @param message The message to be displayed.
     */
    fun displayError(parent: UComponent, message: String) {
      applicationContext.getApplication().displayError(parent, message)
    }

    // ---------------------------------------------------------------------
    // SEND A BUG REPORT
    // ---------------------------------------------------------------------

    /**
     * Reports a trouble at execution time.
     *
     * @param     module          the module where the trouble was detected
     * @param     reason          the exception that triggered the bug report
     */
    fun reportTrouble(module: String,
                      place: String,
                      data: String,
                      reason: Throwable) {

      if (applicationContext.getApplication().isNoBugReport()) {
        println("notice: reporting trouble is disabled, no mail will be sent.")
        System.err.println(reason.message)
        reason.printStackTrace(System.err)
      } else {
        var revision: String? = null
        var releaseDate: String? = null
        val versionArray: Array<String> = Utils.getVersion()
        versionArray.forEach {
          if (it.startsWith("Revision: ")) {
            revision = it.substring(10)
          } else if (it.startsWith("Last Changed Date: ")) {
            releaseDate = it.substring(19)
          }
        }
        if (getDefaults() == null) {
          System.err.println("ERROR: No application configuration available")
          return
        }
        val applicationName: String = try {
          getDefaults().getApplicationName()
        } catch (e: PropertyException) {
          "application name not defined"
        }
        val version: String = try {
          getDefaults().getVersion()
        } catch (e: PropertyException) {
          "version not defined"
        }
        val smtpServer: String? = try {
          getDefaults().getSMTPServer()
        } catch (e: PropertyException) {
          null
        }
        val logFile: String? = try {
          getDefaults().getLogFile()
        } catch (e: PropertyException) {
          null
        }
        val sendMail: Boolean = try {
          getDefaults().mailErrors()
        } catch (e: PropertyException) {
          false
        }
        val writeLog: Boolean = try {
          getDefaults().logErrors()
        } catch (e: PropertyException) {
          false
        }
        if (smtpServer != null && sendMail) {
          val recipient: String = try {
            getDefaults().getDebugMailRecipient()
          } catch (e: PropertyException) {
            TODO()
          }
          val cc: String? = try {
            getDefaults().getStringFor("debugging.mail.cc")
          } catch (e: PropertyException) {
            null
          }
          val bcc: String? = try {
            getDefaults().getStringFor("debugging.mail.bcc")
          } catch (e: PropertyException) {
            null
          }
          val sender: String? = try {
            getDefaults().getStringFor("debugging.mail.sender")
          } catch (e: PropertyException) {
            TODO()
          }
          val buffer = StringWriter()
          val writer = PrintWriter(buffer)
          // failureID is added to the subject of the mail.
          // similar error mail should have the same id which makes the
          // easier to find duplicated messages.
          val failureID: String
          writer.println("Application Name:    $applicationName")
          writer.println("SVN Version:         " + (revision ?: "no revision available."))
          writer.println("Version:             $version")
          writer.println("Release Date:        " + (releaseDate ?: "not available."))
          writer.println("Module:              $module")
          writer.println("Started at:          " + applicationContext.getApplication().getStartupTime())
          writer.println()
          writer.println("Architecture:        " + System.getProperty("os.arch", ""))
          writer.print("Operating System:    " + System.getProperty("os.name", "") + " ")
          writer.println(System.getProperty("os.version", ""))
          writeNetworkInterfaces(writer)
          writer.println("Local Time:          " + Date().toString() + ":")
          writer.println("Default Locale:      " + Locale.getDefault())
          writer.println("Default Encoding:    " + InputStreamReader(System.`in`).encoding)
          writer.println()
          if (applicationContext.isWebApplicationContext()) {
            writer.println("User-IP:             " + applicationContext.getApplication().getUserIP())
          }
          try {
            writer.println("User-Name:           " + applicationContext.getApplication().getUserName())
          } catch (e: Exception) {
            writer.println("User-Name:           <not available>")
          }
          writer.println("System-User/Name:    " + System.getProperty("user.name", ""))
          writer.println("System-User/Home:    " + System.getProperty("user.home", ""))
          writer.println("System-User/Dir:     " + System.getProperty("user.dir", ""))
          writer.println()
          writer.println("Java Version:        " + System.getProperty("java.version", ""))
          writer.println("Java Vendor:         " + System.getProperty("java.vendor", ""))
          writer.println("Java Home:           " + System.getProperty("java.home", ""))
          writer.println("Java VM Version:     " + System.getProperty("java.vm.version", ""))
          writer.println("Java VM Vendor:      " + System.getProperty("java.vm.vendor", ""))
          writer.println("Java VM Name:        " + System.getProperty("java.vm.name", ""))
          writer.println("Java Class Version:  " + System.getProperty("java.class.version", ""))
          writer.println("Java Class Path:     " + System.getProperty("java.class.path", ""))
          writer.println("Java Libr. Path:     " + System.getProperty("java.library.path", ""))
          writer.println("Java Tmp. Directory: " + System.getProperty("java.io.tmpdir", ""))
          writer.println("Java Compiler:       " + System.getProperty("java.compiler", ""))
          writer.println("Java Ext. Direct.:   " + System.getProperty("java.ext.dirs", ""))
          writer.println()
          writer.println("Memory Usage:        total = " + Runtime.getRuntime().totalMemory()
                  + "  free = " + Runtime.getRuntime().freeMemory()
                  + "  used = " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))
          writer.println()
          writer.println("Catched at:          $place")
          writer.println("Message:             " + reason.message)
          writer.println("Exception:           ")
          reason.printStackTrace(writer)
          failureID = try {
            val write: CharArrayWriter = CharArrayWriter()
            reason.printStackTrace(PrintWriter(write))
            " " + write.toString().hashCode()
          } catch (e: Exception) {
            " " + e.message
          }
          writer.println()
          writer.println("Information:         $data")
          writer.flush()
          Mailer.sendMail(smtpServer,
                  recipient,
                  cc,
                  bcc,
                  "[ERROR] $applicationName$failureID",
                  buffer.toString(),
                  sender!!)
        }
        if (logFile != null && writeLog) {
          try {
            val writer: PrintWriter = PrintWriter(FileWriter(logFile, true))
            writer.println()
            writer.println()
            try {
              writer.println(applicationContext.getApplication().getUserName().toString() + ":" + Date())
            } catch (e: Exception) {
              writer.println("<user no available>" + ":" + Date())
            }
            writer.println(reason.message)
            reason.printStackTrace(writer)
            if (writer.checkError()) {
              throw IOException("error while writing")
            }
            writer.close()
          } catch (e: IOException) {
            System.err.println("Can't write to file:$logFile")
            System.err.println(": " + e.message)
          }
        }
        System.err.println(reason.message)
        reason.printStackTrace(System.err)
      }
    }

    /**
     * Write the network interfaces.
     * @param writer The Writer object.
     */
    private fun writeNetworkInterfaces(writer: PrintWriter) {
      try {
        // find out which ip-addresses this host has
        val netInterfaces = NetworkInterface.getNetworkInterfaces()
        while (netInterfaces.hasMoreElements()) {
          val ni = netInterfaces.nextElement()
          writer.println("Network:            " + ni.displayName)
          val addresses = ni.inetAddresses
          while (addresses.hasMoreElements()) {
            val address = addresses.nextElement()
            writer.println("                     "
                    + address.hostAddress + " "
                    + address.canonicalHostName)
          }
        }
      } catch (e: SocketException) {
      }
    }

    //-----------------------------------------------------------
    // DATA MEMBERS
    //-----------------------------------------------------------

    /**
     * The `ApplicationContext` instance.
     */
    lateinit var applicationContext: ApplicationContext
    var compt = 0
  }
}
