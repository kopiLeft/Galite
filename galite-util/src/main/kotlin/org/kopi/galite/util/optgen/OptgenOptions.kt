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

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import org.kopi.galite.util.base.Options

class OptgenOptions @JvmOverloads constructor(name: String? = "Optgen") : Options(name) {
  var release: String? = null

  override fun processOption(code: Int, g: Getopt): Boolean {
    when (code) {
      'r'.code -> {
        release = getString(g, "")
        return true
      }
      else -> return super.processOption(code, g)
    }
  }

  override val options: Array<String?>
    get() {
      val parent: Array<String?> = super.options
      val total = arrayOfNulls<String>(parent.size + 1)
      System.arraycopy(parent, 0, total, 0, parent.size)
      total[parent.size + 0] = "  --release, -r<String>: Sets the release version of the program"

      return total
    }

  override val shortOptions: String
    get() = "r:" + super.shortOptions

  override fun version() {
    println("Version 2.1B released 17. July 2002")
  }

  public override fun usage() {
    System.err.println("usage: org.kopi.galite.util.optionGenerator.optgenMain [option]* [--help] <file>+")
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
      LongOpt("release", LongOpt.REQUIRED_ARGUMENT, null, 'r'.code)
    )
  }
}
