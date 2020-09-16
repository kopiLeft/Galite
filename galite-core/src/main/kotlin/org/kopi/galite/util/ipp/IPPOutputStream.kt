package org.kopi.galite.util.ipp


import java.io.OutputStream
import java.nio.charset.Charset

class IPPOutputStream {
  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  constructor(os: OutputStream?) {
    this.os = os
  }
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

// --------------------------------------------------------------------
// ACCESSORS
// --------------------------------------------------------------------

  // open fun writeByte(b: Int): Unit {
// os?.write((b.and(0xff)))
//}
  open fun writeByte(b: Int) {
    os?.write((b and 0xff)) as Byte
  }
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  fun writeShort(s: Int) {
    os?.write(s and 0xff00 shr 8)
    os?.write(s and 0xff)
  }


  fun writeInteger(i: Int) {
    os?.write(i and -0x1000000 shr 24)
    os?.write(i and 0xff0000 shr 16)
    os?.write((i and 0xff00 shr 8))
    os?.write((i and 0xff))
  }


  fun writeString(s: String) {
    val charset = Charset.forName("iso-8859-1")
    for (element in s) {
      os?.write(element as Int)
    }
  }

  fun writeArray(array: ByteArray) {
    os!!.write(array, 0, array.size)
  }

  private var os: OutputStream? = null
}