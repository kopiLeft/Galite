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

package org.kopi.galite.util.optionGenerator.definition

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.util.optionGenerator.utils.OptgenError
import org.kopi.galite.util.optionGenerator.utils.OptgenMessages
import java.io.PrintWriter
import java.util.*

class OptionDefinition(private val longname: String,
                       private val shortname: String,
                       private val type: String,
                       private val isMultiple: Boolean,
                       private val defaultValue: String,
                       private val argument: String?,
                       private val help: String?) {

  @Throws(OptgenError::class)
  fun checkIdentifiers(identifiers: Hashtable<String, String>, sourceFile: String) {
    val stored = identifiers[longname]
    if (stored != null) {
      throw OptgenError(OptgenMessages.DUPLICATE_DEFINITION, longname, sourceFile, stored)
    }
    identifiers[longname] = sourceFile
  }

  @Throws(OptgenError::class)
  fun checkShortcuts(shortcuts: Hashtable<String, String>, sourceFile: String) {
    val stored = shortcuts[shortname]
    if (stored != null) {
      throw OptgenError(OptgenMessages.DUPLICATE_SHORTCUT, shortname, sourceFile, stored)
    }
    shortcuts[shortname] = sourceFile
  }

  /**
   * Prints the case statement for the parseArgument method
   *
   * @param    out        the output stream
   */
  fun printParseArgument(out: PrintWriter) {
    out.print("    case \'")
    out.print(shortname)
    out.println("\':")
    out.print("      ")
    out.print(longname)
    out.print(" = ")
    if (argument == null) {
      if (isMultiple) {
        throw InconsistencyException("multiple arguments support for type $type is not yet implemented.")
      }
      out.print("!$defaultValue")
      out.print(";")
    } else {
      val methodName: String
      var arg = this.argument
      if (type == "int") {
        methodName = "getInt"
        if (arg == null || arg == "") {
          arg = "0"
        }
      } else {
        methodName = "getString"
        arg = "\"" + arg + "\""
      }
      if (isMultiple) {
        if (type == "int") {
          out.print("addInt($longname, $methodName(g, $arg))")
        } else if (type == "String") {
          out.print("addString($longname, $methodName(g, $arg))")
        } else {
          throw InconsistencyException("multiple arguments support for type $type is not yet implemented.")
        }
      } else {
        out.print("$methodName(g, $arg)")
      }
      out.print(";")
    }
    out.println(" return true;")
  }

  /**
   * Prints the field declaration
   *
   * @param    out        the output stream
   */
  fun printFields(out: PrintWriter) {
    out.print("  public ")
    out.print(if (!isMultiple) type else "$type[]")
    out.print(" ")
    out.print(longname)
    out.print(" = ")
    if (type != "String" || defaultValue == "null") {
      if (defaultValue == "null") {
        out.print(defaultValue)
      } else {
        out.print(if (!isMultiple) defaultValue else "{ $defaultValue }")
      }
    } else {
      out.print(if (!isMultiple) "\"" + defaultValue + "\"" else "{ \"$defaultValue\" }")
    }
    out.println(";")
  }

  /**
   * Prints the usage message
   *
   * @param    out        the output stream
   */
  fun printUsage(out: PrintWriter) {
    val prefix = StringBuffer("\"  --")

    prefix.append(longname)
    prefix.append(", -")
    prefix.append(shortname)
    if (argument != null) {
      prefix.append("<$type>")
    }
    prefix.append(": ")
    for (i in prefix.length..24) {
      prefix.append(" ")
    }
    out.print(prefix.toString() + (help?.replace("\"".toRegex(), "\\\\\"") ?: ""))
    if (defaultValue != "null") {
      out.print(" [")
      out.print(defaultValue)
      out.print("]")
    }
    out.print("\"")
  }

  /**
   * Prints the LongOpt instantiation
   *
   * @param    out        the output stream
   */
  fun printLongOpts(out: PrintWriter) {
    out.print("    new LongOpt(\"")
    out.print(longname)
    out.print("\", ")
    if (argument == null) {
      out.print("LongOpt.NO_ARGUMENT")
    } else if (argument == "") {
      out.print("LongOpt.REQUIRED_ARGUMENT")
    } else {
      out.print("LongOpt.OPTIONAL_ARGUMENT")
    }
    out.print(", null, \'")
    out.print(shortname)
    out.print("\')")
  }

  /**
   * Prints the short option
   *
   * @param    out        the output stream
   */
  fun printShortOption(out: PrintWriter) {
    out.print(shortname)
    if (argument != null) {
      if (argument == "") {
        out.print(":")
      } else {
        out.print("::")
      }
    }
  }

  companion object {
    private fun trail(s: String?): String? {
      return when {
        s == null -> null
        s.length < 2 -> s
        else -> s.substring(1, s.length - 1)
      }
    }
  }
}
