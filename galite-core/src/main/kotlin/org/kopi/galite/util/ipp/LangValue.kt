/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: LangValue.java 34961 2016-11-04 17:20:49Z hacheni $
 */
package org.kopi.galite.util.ipp

import java.io.IOException

class LangValue : IPPValue {
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  constructor(charset: String?, value: String) {
    this.charset = charset
    this.value = value
  }

  constructor(`is`: IPPInputStream) {
    var n: Int
    `is`.readShort() //value-length
    n = `is`.readShort().toInt()
    charset = `is`.readString(n)
    n = `is`.readShort().toInt()
    value = `is`.readString(n)!!
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  override val size: Int
    get() = 6 + charset!!.length + value.length

  override fun write(os: IPPOutputStream) {
    os.writeShort(4 + value.length + charset!!.length)
    os.writeShort(charset!!.length)
    os.writeString(charset!!)
    os.writeShort(value.length)
    os.writeString(value)
  }

  override fun dump() {
    println("\tcharset : $charset\tvalue : $value")
  }

  override fun toString(): String {
    return value
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var charset: String?
  private var value: String
}