package org.kopi.galite.util.ipp

import java.io.IOException


class BooleanValue : IPPValue {
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  constructor(value: Boolean) {
    this.value = value
  }

  constructor(iPPInputStream: IPPInputStream) {
    iPPInputStream.readShort() // value-length
    value = (iPPInputStream.readByte()).toInt() != 0 // value
  }// value-length + value

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  override val size: Int
    get() = 2 + 1 // value-length + value

  override fun write(os: IPPOutputStream) {
    os.writeShort(1)
    os.writeByte(if (value) 1 else 0)
  }

  override fun dump() {
    println("\tboolean : $value")
  }

  override fun toString(): String {
    return value.toString() + ""
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var value: Boolean
}
