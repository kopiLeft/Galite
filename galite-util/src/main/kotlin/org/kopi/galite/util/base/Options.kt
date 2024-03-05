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
package org.kopi.galite.util.base

import gnu.getopt.Getopt
import gnu.getopt.LongOpt
import kotlin.system.exitProcess

/**
 * This class implements options for an entry point
 *
 * @param        name                the command name to pass to getopt
 */
abstract class Options(private val name: String?) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  /**
   * The array of non-option arguments.
   */
  lateinit var nonOptions: Array<String?>

  /**
   * Parse the argument list
   *
   * @param        argv
   * @param        options
   * @return true if the command line is valid and all options are present
   */
  fun parseArguments(argv: Array<String>, options: Options): Boolean {
    if (!parseCommandLine(argv)) {
      return false
    }

    if (options.nonOptions.isEmpty() || options.nonOptions.size > 1) {
      System.err.println("Invalid number of input options given")
      options.help()
      exitProcess(-1)
    }

    return true
  }

  /**
   * Parses the command line and processes the arguments.
   *
   * @param        argv               the command line arguments
   * @return true if the command line is valid
   */
  fun parseCommandLine(argv: Array<String>): Boolean {
    val parser = Getopt(name, argv, shortOptions, longOptions, true)
    while (true) {
      val code = parser.getopt()

      if (code == -1) {
        break
      }
      if (!processOption(code, parser)) {
        return false
      }
    }

    // store remaining arguments
    nonOptions = arrayOfNulls(argv.size - parser.optind)
    System.arraycopy(argv, parser.optind, nonOptions, 0, nonOptions.size)
    return true
  }

  /**
   * @param        args                the command line arguments
   */
  open fun processOption(code: Int, g: Getopt): Boolean {
    when (code) {
      'h'.toInt() -> {
        help()
        System.exit(0)
      }
      'V'.toInt() -> {
        version()
        System.exit(0)
      }
      else -> return false
    }
    return true
  }

  open val options: Array<String?>
    get() = arrayOf(
            "  --help, -h:           Displays the help information",
            "  --version, -V:        Prints out the version information"
    )

  /**
   * Prints the available options.
   */
  fun printOptions() {
    val options = options

    run {
      var i = options.size
      while (--i >= 0) {
        for (j in 0 until i) {
          if (options[j]!! > options[j + 1]!!) {
            val tmp = options[j]
            options[j] = options[j + 1]
            options[j + 1] = tmp
          }
        }
      }
    }
    println()
    println("Here are the available options : ")
    for (i in options.indices) {
      println(options[i])
    }
  }

  /**
   * shows a help message.
   */
  fun help() {
    usage()
    printOptions()
    System.err.println()
    version()
    System.err.println()
    System.err.println("This program is part of the Kopi Suite.")
    System.err.println("For more info, please see: http://www.kopiright.com/kopi")
  }

  /**
   * Shows the version number.
   */
  protected abstract fun version()

  /**
   * Shows a usage message.
   */
  protected abstract fun usage()
  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------
  /**
   * Processes an integer argument.
   */
  protected fun getInt(g: Getopt, defaultValue: Int): Int {
    return try {
      if (g.optarg != null) g.optarg.toInt() else defaultValue
    } catch (e: Exception) {
      System.err.println("malformed option: " + g.optarg)
      System.exit(0)
      1
    }
  }

  /**
   * Processes a string argument.
   */
  protected fun getString(g: Getopt, defaultValue: String?): String? {
    return if (g.optarg != null) g.optarg else defaultValue
  }

  protected fun addString(array: Array<String?>?, str: String?): Array<String?> {
    return if (array == null) {
      arrayOf(str)
    } else {
      val size = array.size
      val newArray = arrayOfNulls<String>(size + 1)
      for (i in 0 until size) {
        newArray[i] = array[i]
      }
      newArray[size] = str
      newArray
    }
  }

  protected fun addInt(array: IntArray?, value: Int): IntArray {
    return if (array == null) {
      intArrayOf(value)
    } else {
      val size = array.size
      val newArray = IntArray(size + 1)
      for (i in 0 until size) {
        newArray[i] = array[i]
      }
      newArray[size] = value
      newArray
    }
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPTIONS
  // ----------------------------------------------------------------------
  /**
   * Gets short options.
   */
  open val shortOptions: String
    get() = "hV"

  /**
   * Gets long options.
   */
  open val longOptions: Array<LongOpt?>
    get() = arrayOf(
            LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'.toInt()),
            LongOpt("version", LongOpt.NO_ARGUMENT, null, 'V'.toInt())
    )
}
