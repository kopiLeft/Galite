/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.util.ipp

class BooleanValue : IPPValue {

  constructor(value: Boolean) {
    this.value = value
  }

  constructor(inputStream: IPPInputStream) {
    inputStream.readShort() // value-length
    value = inputStream.readByte().toInt() != 0 // value
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  override fun getSize(): Int = 2 + 1 // value-length + value

  override fun write(os: IPPOutputStream) {
    os.writeShort(1)
    os.writeByte(if (value) 1 else 0)
  }

  override fun dump() {
    println("\tboolean : $value")
  }

  override fun toString(): String = value.toString() + ""

  private var value: Boolean
}
