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
 * $Id: RangeValue.java 34961 2016-11-04 17:20:49Z hacheni $
 */
package org.kopi.galite.util.ipp

import java.io.IOException

class RangeValue : IPPValue {
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  constructor(lower: Int, upper: Int) {
    this.lower = lower
    this.upper = upper
  }

  constructor(`is`: IPPInputStream) {
    `is`.readShort() //value-length
    lower = `is`.readInteger()
    upper = `is`.readInteger()
  }// value-length + value

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  override val size: Int
    get() = 2 + 8 // value-length + value

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