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

package org.kopi.galite.util.ipp

class RangeValue : IPPValue {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  constructor(lower: Int, upper: Int) {
    this.lower = lower
    this.upper = upper
  }

  constructor(inputStream: IPPInputStream) {
    inputStream.readShort() //value-length
    lower = inputStream.readInteger()
    upper = inputStream.readInteger()
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  override fun getSize(): Int = 2 + 8 // value-length + value

  override fun write(os: IPPOutputStream) {
    os.writeShort(8)
    os.writeInteger(lower)
    os.writeInteger(upper)
  }

  override fun dump() {
    println("\tlower : $lower\tupper : $upper")
  }

  override fun toString(): String {
    return "<$lower, $upper>"
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private var lower: Int
  private var upper: Int
}
