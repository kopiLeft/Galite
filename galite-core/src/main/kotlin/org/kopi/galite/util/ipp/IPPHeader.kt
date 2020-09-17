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

class IPPHeader {
  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  constructor() {}
  constructor(`is`: IPPInputStream) {
    majorVersion = `is`.readByte()
    minorVersion = `is`.readByte()
    operationID = `is`.readShort()
    requestID = `is`.readInteger()
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  fun setVersion(major: Byte, minor: Byte) {
    majorVersion = major
    minorVersion = minor
  }

  fun write(os: IPPOutputStream) {
    os.writeByte(majorVersion.toInt())
    os.writeByte(minorVersion.toInt())
    os.writeShort(operationID.toInt())
    os.writeInteger(requestID)
  }

  val size: Int
    get() = 8

  fun dump() {
    println("Major version : $majorVersion")
    println("Minor version : $minorVersion")
    println("Operation ID : $operationID")
    println("Request ID : $requestID")
  }

  fun setOperationID(operationID: Short) {
    this.operationID = operationID
  }

  fun getOperationID(): Short {
    return operationID
  }

  fun setRequestID(requestID: Int) {
    this.requestID = requestID
  }

  fun getRequestID(): Int {
    return requestID
  }


  val isAnError: Boolean
    get() = operationID >= 0x400

  val status: String?
    get() {
      val units: Int
      if (operationID < 0x400) {
        units = operationID.toInt()
        if (units < IPPConstants.ERR_SUCCESSFUL.size) {
          return IPPConstants.ERR_SUCCESSFUL[units]
        }
      } else if (operationID < 0x500) {
        units = operationID - 0x400
        if (units < IPPConstants.ERR_CLIENT_ERROR.size) {
          return IPPConstants.ERR_CLIENT_ERROR[units]
        }
      } else {
        units = operationID - 0x400
        if (units < IPPConstants.ERR_SERVER_ERROR.size) {
          return IPPConstants.ERR_SERVER_ERROR[units]
        }
      }
      return null
    }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var majorVersion: Byte = 1
  private var minorVersion: Byte = 1
  private var operationID: Short = 0
  private var requestID = 0
}
