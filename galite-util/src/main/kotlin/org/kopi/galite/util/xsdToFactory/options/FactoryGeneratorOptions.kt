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

package org.kopi.galite.util.xsdToFactory.options

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import org.kopi.galite.util.base.Options

class FactoryGeneratorOptions(var name: String? = null,
                              var fpackage: String? = null,
                              var source:String? = null,
                              var directory: String? = null,
                              var classpath: String? = null,
                              var getAbstract: Boolean? = false,
                              var lsdFactory: Boolean? = false): Options("generator") {

  override fun processOption(code: Int, g: Getopt): Boolean {
    when (code) {
      'n'.code -> {
        name = getString(g, "")
        return true
      }
      'p'.code -> {
        fpackage = getString(g, "")
        return true
      }
      's'.code -> {
        source = getString(g, "")
        return true
      }
      'd'.code -> {
        directory = getString(g, "")
        return true
      }
      'c'.code -> {
        classpath = getString(g, "")
        return true
      }
      'a'.code -> {
        getAbstract = true
        return true
      }
      'l'.code -> {
        lsdFactory = true
        return true
      }
      else -> return super.processOption(code, g)
    }
  }

  override val options: Array<String?>
    get() {
      val parent: Array<String?> = super.options
      val total = arrayOfNulls<String>(parent.size + 7)
      System.arraycopy(parent, 0, total, 0, parent.size)
      total[parent.size + 0] = "  --name, -n<String>: The name of the factory class to be generated"
      total[parent.size + 1] = "  --fpackage, -p<String>: The package of the factory class to be generated"
      total[parent.size + 2] = "  --source, -s<String>: Target binary directory for .xsb files."
      total[parent.size + 3] = "  --directory, -d<String>: Target directory for generated Factory files."
      total[parent.size + 4] = "  --classpath, -c<String>: Classpath specifying classes to include during compilation. pathA;pathB;pathC — Class search path of directories and JAR files."
      total[parent.size + 5] = "  --getAbstract, -a<boolean>: Generate methods for abstract types"
      total[parent.size + 6] = "  --lsdFactory, -l<boolean>: Generate methods for LSD Project"

      return total
    }

  override val shortOptions: String
    get() = "n:p:s:d:c:b:a:l" + super.shortOptions

  override fun version() {
    println("Version 1.0 released 10 Mai 2024")
  }

  public override fun usage() {
    System.err.println("usage: FactoryGenerator [option]*")
  }

  override val longOptions: Array<LongOpt?>
    get() {
      val parent: Array<LongOpt?> = super.longOptions
      val total = arrayOfNulls<LongOpt>(parent.size + LONGOPTS.size)

      System.arraycopy(parent, 0, total, 0, parent.size)
      System.arraycopy(LONGOPTS, 0, total, parent.size, LONGOPTS.size)

      return total
    }

  companion object {
    private val LONGOPTS = arrayOf(
      LongOpt("name", LongOpt.NO_ARGUMENT, null, 'n'.code),
      LongOpt("fpackage", LongOpt.NO_ARGUMENT, null, 'p'.code),
      LongOpt("source", LongOpt.REQUIRED_ARGUMENT, null, 's'.code),
      LongOpt("directory", LongOpt.REQUIRED_ARGUMENT, null, 'd'.code),
      LongOpt("classpath", LongOpt.REQUIRED_ARGUMENT, null, 'c'.code),
      LongOpt("getAbstract", LongOpt.NO_ARGUMENT, null, 'a'.code),
      LongOpt("lsdFactory", LongOpt.NO_ARGUMENT, null, 'l'.code)
    )
  }
}