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

class LangValue : IPPValue {

 constructor(charset: String, value: String) {
    this.charset = charset
    this.value = value
  }

  constructor(inputStream: IPPInputStream) {
    inputStream.readShort() //value-length
    var n = inputStream.readShort().toInt()
    charset = inputStream.readString(n)
    n = inputStream.readShort().toInt()
    value = inputStream.readString(n)
  }

  override fun getSize(): Int = 6 + charset.length + value.length

  override fun write(os: IPPOutputStream) {
    os.writeShort(4 + value.length + charset.length)
    os.writeShort(charset.length)
    os.writeString(charset)
    os.writeShort(value.length)
    os.writeString(value)
  }

  override fun dump() {println("\tcharset : $charset\tvalue : $value") }

  override fun toString(): String = value

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private var charset: String
  private var value: String
}
