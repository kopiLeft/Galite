/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2026 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.optgen

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.OutputStreamWriter

import kotlin.system.exitProcess

/**
 * This class is the entry point for the Option generator.
 */
class Main {

  // --------------------------------------------------------------------
  // ENTRY POINT
  // --------------------------------------------------------------------

  companion object {
    /**
     * Program entry point.
     */
    @JvmStatic
    fun main(args: Array<String>) {
      val success = Main().run(args)

      exitProcess(if (success) 0 else 1)
    }
  }

  /**
   * Runs a compilation session.
   *
   * @param     args    the command line arguments
   */
  fun run(args: Array<String>): Boolean {
    if (!parseArguments(args)) {
      return false
    }
    var errorsFound = false

    options.nonOptions.forEach { sourceFile ->
      sourceFile?.let {
        errorsFound = !processFile(it)
      }
    }

    return !errorsFound
  }

  /*
   * Parse command line arguments.
   *
   * @param     args    the command line arguments
   */
  private fun parseArguments(args: Array<String>): Boolean {
    options = OptgenOptions()

    if (!options.parseCommandLine(args)) {
      return false
    }
    if (options.nonOptions.isEmpty()) {
      System.err.println("error: No input file given")
      options.usage()
      return false
    }
    return true
  }

  /**
   * Process the source file to check for errors.
   *
   * @param     sourceFile      The source file name.
   *
   * @return    a boolean indicating if the method is successfully executed.
   */
  private fun processFile(sourceFile: String): Boolean {
    if (!parseSource(sourceFile)) {
      return false
    }
    if (options.release != null) {
      definition.setVersion(options.release)
    }
    if (!checkIdentifiers()) {
      return false
    }
    if (!checkShortcuts()) {
      return false
    }
    if (!buildInterfaceFile()) {
      return false
    }
    return true
  }

  /**
   * Parse the source file and check for errors
   *
   * @param     sourceFile      The source file name.
   *
   * @return    a boolean indicating if the method is successfully executed.
   */
  private fun parseSource(sourceFile: String): Boolean {
    var errorsFound = false

    try {
      definition = DefinitionFile.read(sourceFile)
    } catch (e: Exception) {
      System.err.println("error: ${e.message}")
      errorsFound = true
    }

    return !errorsFound
  }

  /**
   * Checks for duplicate identifiers.
   */
  private fun checkIdentifiers(): Boolean {
    var errorsFound = false

    try {
      definition.checkIdentifiers()
    } catch (e: Exception) {
      System.err.println("error: ${e.message}")
      errorsFound = true
    }

    return !errorsFound
  }

  /**
   * Checks for duplicate shortcuts.
   */
  private fun checkShortcuts(): Boolean {
    var errorsFound = false

    try {
      definition.checkShortcuts()
    } catch (e: Exception) {
      System.err.println("error: ${e.message}")
      errorsFound = true
    }

    return !errorsFound
  }

  /**
   * Build the generated class file.
   */
  private fun buildInterfaceFile(): Boolean {
    val prefix:                 String  = definition.getPrefix()
    val destinationDirectory:   String  = definition.getPackageName().replace(".", File.separator)
    val outputFile:             File    = File(destinationDirectory + File.separator + prefix + "Options.java")
    var errorsFound:            Boolean = false

    try {
      outputFile.parentFile?.mkdirs()

      val out = PrintWriter(BufferedWriter(OutputStreamWriter(FileOutputStream(outputFile), "UTF-8")))

      definition.printJavaFile(out)

      out.flush()
      out.close()
    } catch (e: IOException) {
      System.err.println("I/O Exception on " + outputFile.path + ": " + e.message)
      errorsFound = true
    }

    return !errorsFound
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private lateinit var options:         OptgenOptions
  private lateinit var definition:      DefinitionFile
}
