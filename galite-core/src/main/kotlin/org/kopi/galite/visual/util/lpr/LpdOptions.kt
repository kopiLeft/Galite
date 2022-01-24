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

package org.kopi.galite.visual.util.lpr

import org.kopi.galite.visual.util.base.Options

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

open class LpdOptions @JvmOverloads constructor(name: String? = "Lpd") : Options(name) {

  var localHost = "localhost"
  var printHost = "localhost"
  var proxyHost: String? = null
  var remotePort = -1
  var sourcePort = -1
  var timeout = 60000
  var bindSourcePort = false
  var user: String? = null
  var queue: String? = null

  override fun processOption(code: Int, g: Getopt): Boolean {
    return when (code) {
      'L'.toInt() -> {
        localHost = getString(g, "")
        true
      }
      'H'.toInt() -> {
        printHost = getString(g, "")
        true
      }
      'X'.toInt() -> {
        proxyHost = getString(g, "")
        true
      }
      'd'.toInt() -> {
        remotePort = getInt(g, 0)
        true
      }
      's'.toInt() -> {
        sourcePort = getInt(g, 0)
        true
      }
      't'.toInt() -> {
        timeout = getInt(g, 0)
        true
      }
      'B'.toInt() -> {
        bindSourcePort = !false
        true
      }
      'u'.toInt() -> {
        user = getString(g, "")
        true
      }
      'P'.toInt() -> {
        queue = getString(g, "")
        true
      }
      else -> super.processOption(code, g)
    }
  }

  override val options: Array<String?>
    get() {
      val parent: Array<String?> = super.options
      val total = arrayOfNulls<String>(parent.size + 9)

      System.arraycopy(parent, 0, total, 0, parent.size)
      total[parent.size + 0] = "  --localHost, -L<String>: Sets the local host [localhost]"
      total[parent.size + 1] = "  --printHost, -H<String>: Sets the print hosts [localhost]"
      total[parent.size + 2] = "  --proxyHost, -X<String>: Sets the proxy host to use (UNIX)"
      total[parent.size + 3] = "  --remotePort, -d<int>: Sets the remote port [-1]"
      total[parent.size + 4] = "  --sourcePort, -s<int>: Sets the source port [-1]"
      total[parent.size + 5] = "  --timeout, -t<int>:   Sets the timeout value in millisc [60000]"
      total[parent.size + 6] = "  --bindSourcePort, -B: Binds the source port [false]"
      total[parent.size + 7] = "  --user, -u<String>:   Sets the user who invoked the command"
      total[parent.size + 8] = "  --queue, -P<String>:  Sets the queue to work on"
      return total
    }
  override val shortOptions: String
    get() = "L:H:X:d:s:t:Bu:P:" + super.shortOptions

  override fun version() {
    println("Version 2.3B released 17 September 2007")
  }

  override fun usage() {
    System.err.println("usage: org.kopi.galite.visual.lpd.Dummy")
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
            LongOpt("localHost", LongOpt.REQUIRED_ARGUMENT, null, 'L'.toInt()),
            LongOpt("printHost", LongOpt.REQUIRED_ARGUMENT, null, 'H'.toInt()),
            LongOpt("proxyHost", LongOpt.REQUIRED_ARGUMENT, null, 'X'.toInt()),
            LongOpt("remotePort", LongOpt.REQUIRED_ARGUMENT, null, 'd'.toInt()),
            LongOpt("sourcePort", LongOpt.REQUIRED_ARGUMENT, null, 's'.toInt()),
            LongOpt("timeout", LongOpt.REQUIRED_ARGUMENT, null, 't'.toInt()),
            LongOpt("bindSourcePort", LongOpt.NO_ARGUMENT, null, 'B'.toInt()),
            LongOpt("user", LongOpt.REQUIRED_ARGUMENT, null, 'u'.toInt()),
            LongOpt("queue", LongOpt.REQUIRED_ARGUMENT, null, 'P'.toInt())
    )
  }
}
