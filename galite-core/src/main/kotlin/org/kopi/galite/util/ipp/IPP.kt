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

class IPP() {

  constructor(ippInputStream: IPPInputStream) : this() {
    var endAttributes = false
    var groupTag = IPPConstants.TAG_ZERO.toByte()
    var read: Byte

    while (!endAttributes) {
      read = ippInputStream.peekByte()
      when {
        read.toInt() == IPPConstants.TAG_END -> {
          ippInputStream.readByte()
          endAttributes = true
        }
        read < IPPConstants.TAG_UNSUPPORTED_VALUE -> {
          // it is a new group tag
          groupTag = ippInputStream.readByte()
        }
        else -> {
          // new attribute
          (attributes).add(IPPAttribute(ippInputStream, groupTag.toInt()))
        }
      }
    }

    data = ippInputStream.readArray()
  }


  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  fun isAnError(): Boolean = header.isAnError()

  fun getStatus(): String? = header.getStatus()

  fun getAttributes(): Iterator<*> = attributes.iterator()

  fun addAttribute(attribute: IPPAttribute) {
    attributes.add(attribute)
  }

  fun setRequest(requestID: Int, operationID: Short) {
    header.requestID = requestID
    header.operationID = operationID
  }

  // 1 for the TAG_END
  fun getSize(): Int {
    var size = header.getSize()
    val atts = attributes.iterator()
    var lastGroup = -1

    while (atts.hasNext()) {
      val attribute = atts.next() as IPPAttribute

      size += attribute.getSize(lastGroup)
      lastGroup = attribute.group
    }

    return size + 1 + data.size // 1 for the TAG_END
  }

  fun write(os: IPPOutputStream) {
    var lastGroup = -1

    header.write(os)
    attributes.forEach {
      it.write(os, lastGroup)
      lastGroup = it.group
    }

    os.writeByte(IPPConstants.TAG_END)
    os.writeArray(data)
  }

  fun dump() {
    header.dump()
    attributes.forEach {
      it.dump()
    }
  }

  fun simpleDump() {
        attributes.forEach {
      it.simpleDump()
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  var header: IPPHeader = IPPHeader()
    private set

  private var attributes: MutableList<IPPAttribute> = mutableListOf()

  var data: ByteArray = ByteArray(0)

  companion object {
    const val DEBUG = false
  }
}
