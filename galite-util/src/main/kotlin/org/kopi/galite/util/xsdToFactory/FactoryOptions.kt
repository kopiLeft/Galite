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

package org.kopi.galite.util.xsdToFactory

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import org.kopi.galite.util.base.Options

class FactoryOptions(var name: String? = null,
                     var fpackage: String? = null,
                     var source:String? = null,
                     var directory: String? = null) : Options("factory") {

  override fun processOption(code: Int, getopt: Getopt): Boolean {
    when (code) {
      110         -> name = getString(getopt, "")
      112         -> fpackage = getString(getopt, "")
      115         -> source = getString(getopt, "")
      100         -> directory = getString(getopt, "")
      else        -> return super.processOption(code, getopt)
    }
    return true
  }

  override val options: Array<String?>
    get() {
      val primitiveOptions = super.options
      val options = arrayOfNulls<String>(primitiveOptions.size + LONGOPTS.size)

      System.arraycopy(primitiveOptions, 0, options, 0, primitiveOptions.size)
      options[primitiveOptions.size + 1] = "  --name, -n<String>: Name of the generated factory."
      options[primitiveOptions.size + 2] = "  --fpackage, -p<String>: The package of the factory class to be generated."
      options[primitiveOptions.size + 3] = "  --source, -s<String>: Source directory for .xsd files."
      options[primitiveOptions.size + 4] = "  --directory, -d<String>: Target directory for generated Factory files."

      return options
    }

  override val shortOptions: String
    get() = "n:p:s:d:" + super.shortOptions

  override fun version() {
    println("Version 1.0")
  }

  override val longOptions: Array<LongOpt?>
    get() {
      val primitiveOptions = super.longOptions
      val options = arrayOfNulls<LongOpt>(primitiveOptions.size + 9)

      System.arraycopy(primitiveOptions, 0, options, 0, primitiveOptions.size)
      System.arraycopy(LONGOPTS, 0, options, primitiveOptions.size, LONGOPTS.size)

      return options
    }

  override fun usage() {
    println("Usage: FactoryGenerator [-n factoryName] [-p package] [-s source] [-d directory] files...")
  }

  /**
   * Parses the command line arguments.
   */
  fun parseCommand(paramArrayOfString: Array<String>): Boolean {
    if (parseCommandLine(paramArrayOfString)) {
      if (fpackage == null) {
        System.err.println("No package mentioned for the factory.")
        usage()
        printOptions()

        return false
      }
      if (name == null) {
        System.err.println("No name mentioned for the factory.")
        usage()
        printOptions()

        return false
      }
      if (source == null) {
        System.err.println("No directory source mentioned for the .xsd files.")
        usage()
        printOptions()

        return false
      } else if (nonOptions.filter { it?.endsWith(".xsd") == true }.isEmpty()) {
        System.err.println("No .xsd files mentioned.")
        usage()
        printOptions()

        return false
      }
    }

    return true
  }

  val LONGOPTS = arrayOf(LongOpt("name", LongOpt.REQUIRED_ARGUMENT, null, 110),
                         LongOpt("fpackage", LongOpt.REQUIRED_ARGUMENT, null, 112),
                         LongOpt("source", LongOpt.REQUIRED_ARGUMENT, null, 115),
                         LongOpt("directory", LongOpt.OPTIONAL_ARGUMENT, null, 100))
}