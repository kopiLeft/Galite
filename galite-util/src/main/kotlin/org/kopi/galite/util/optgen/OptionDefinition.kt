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

import java.io.PrintWriter

import org.kopi.galite.util.base.InconsistencyException

class OptionDefinition(private val longname: String,
                       private val shortname: String,
                       private val type: String,
                       private val isMultiple: Boolean,
                       private val defaultValue: String,
                       private val argument: String?,
                       private val help: String?)
{
  /**
   * Check for duplicate identifiers
   *
   * @param     identifiers     a table of all token identifiers
   * @param     sourceFile      the file where the token is defined
   */
  fun checkIdentifiers(identifiers: HashMap<String, String>, sourceFile: String) {
    val stored = identifiers[longname]

    if (stored != null) {
      throw InconsistencyException("""Option "$longname" redefined in "$sourceFile": previous definition in "$stored"""")
    }
    identifiers[longname] = sourceFile
  }

  /**
   * Check for duplicate shortcuts
   *
   * @param     shortcuts       a table of all token identifiers
   * @param     sourceFile      the file where the token is defined
   */
  fun checkShortcuts(shortcuts: HashMap<String, String>, sourceFile: String) {
    val stored = shortcuts[shortname]

    if (stored != null) {
      throw InconsistencyException("""Shortcut "$shortname" redefined in "$sourceFile": previous definition in "$stored"""")
    }
    shortcuts[shortname] = sourceFile
  }

  /**
   * Prints the case statement for the parseArgument method
   *
   * @param    out        the output stream
   */
  fun printJavaParseArgument(out: PrintWriter) {
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
      var arg = argument
      if (type == "int") {
        methodName = "getInt"
        if (arg.isEmpty()) {
          arg = "0"
        }
      } else {
        methodName = "getString"
        arg = "\"" + arg + "\""
      }
      if (isMultiple) {
        when (type) {
          "int"    -> out.print("addInt($longname, $methodName(g, $arg))")
          "String" -> out.print("addString($longname, $methodName(g, $arg))")

          else     -> throw InconsistencyException("multiple arguments support for type $type is not yet implemented.")
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
  fun printJavaFields(out: PrintWriter) {
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
  fun printJavaUsage(out: PrintWriter) {
    val prefix = "\"  --$longname, -$shortname${argument?.let { "<$type>" }.orEmpty()}: ".padEnd(33, ' ')

    out.print(prefix + (help?.replace("\"".toRegex(), "\\\\\"") ?: ""))
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
  fun printJavaLongOpts(out: PrintWriter) {
    out.print("    new LongOpt(\"")
    out.print(longname)
    out.print("\", ")
    when (argument) {
      null -> out.print("LongOpt.NO_ARGUMENT")
      ""   -> out.print("LongOpt.REQUIRED_ARGUMENT")
      else -> out.print("LongOpt.OPTIONAL_ARGUMENT")
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
    argument?.let { if (it.isEmpty()) out.print(":") else out.print("::") }
  }
}
