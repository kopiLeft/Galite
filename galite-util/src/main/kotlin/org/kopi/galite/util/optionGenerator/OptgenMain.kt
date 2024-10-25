/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.optionGenerator

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.OutputStreamWriter
import org.kopi.compiler.base.CompilerMessages.Companion.NO_INPUT_FILE
import org.kopi.galite.util.optionGenerator.definition.DefinitionFile
import org.kopi.galite.util.optionGenerator.options.OptgenOptions
import org.kopi.galite.util.optionGenerator.utils.OptgenError

/**
 * This class is the entry point for the Message generator.
 */
class OptgenMain
{
  /**
   * Runs a compilation session.
   *
   * @param    args        the command line arguments
   */
  fun run(args: Array<String>): Boolean {
    if (!parseArguments(args)) {
      return false
    }
    var errorsFound = false

    for (i in 0 until options.nonOptions.size) {
      errorsFound = !processFile(options.nonOptions.get(i)!!)
    }

    return !errorsFound
  }

  /*
   * Parse command line arguments.
   */
  private fun parseArguments(args: Array<String>): Boolean {
    options = OptgenOptions()
    if (!options.parseCommandLine(args)) {
      return false
    }
    if (options.nonOptions.size === 0) {
      System.err.println(NO_INPUT_FILE)
      options.usage()
      return false
    }
    return true
  }

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

  private fun parseSource(sourceFile: String): Boolean {
    var errorsFound = false

    try {
      definition = DefinitionFile.read(sourceFile)
    } catch (e: OptgenError) {
      System.err.println(e.getFormattedMessage())
      errorsFound = true
    }

    return !errorsFound
  }

  private fun checkIdentifiers(): Boolean {
    var errorsFound = false

    try {
      definition.checkIdentifiers()
    } catch (e: OptgenError) {
      System.err.println(e.getFormattedMessage())
      errorsFound = true
    }

    return !errorsFound
  }

  /**
   *
   */
  private fun checkShortcuts(): Boolean {
    var errorsFound = false

    try {
      definition.checkShortcuts()
    } catch (e: OptgenError) {
      System.err.println(e.getFormattedMessage())
      errorsFound = true
    }

    return !errorsFound
  }

  /**
   *
   */
  private fun buildInterfaceFile(): Boolean {
    val prefix: String = definition.prefix
    val outputFile = File(prefix + "Options.java")
    var errorsFound = false

    try {
      val out= PrintWriter(BufferedWriter(OutputStreamWriter(FileOutputStream(outputFile), "UTF-8")))

      definition.printFile(out)

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
  private lateinit var options: OptgenOptions
  private lateinit var definition: DefinitionFile

  companion object {

    /**
     * Program entry point.
     */
    @JvmStatic
    fun main(args: Array<String>) {
      val success = OptgenMain().run(args)

      System.exit(if (success) 0 else 1)
    }
  }
}