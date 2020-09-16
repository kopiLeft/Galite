package org.kopi.galite.util.ipp

import org.kopi.galite.util.base.InconsistencyException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.experimental.or

class IPPInputStream {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  constructor(`is`: InputStream?) {
    this.`is` = `is`
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  fun peekByte(): Byte {
    val read: Int
    verify()
    `is`!!.mark(1)
    read = read()
    `is`!!.reset()
    return read.toByte()
  }

  fun peekShortAfterFirstByte(): Short {
    var i = 0
    verify()
    `is`!!.mark(3)
    read()
    i = i or (read() shl 8)
    i = i or read()
    `is`!!.reset()
    return i.toShort()
  }

  fun readByte(): Byte {
    return read().toByte()
  }

  fun readShort(): Short {
    var i: Short = 0
    i = i or (read() shl 8).toShort()
    i = i or read().toShort()
    return i
  }

  fun readInteger(): Int {
    var i = 0
    i = i or (read() shl 24)
    i = i or (read() shl 16)
    i = i or (read() shl 8)
    i = i or read()
    return i
  }

  fun readString(length: Int): String? {
    val buf = ByteArray(length)
    var nread = 0
    nread = `is`!!.read(buf, 0, length)
    if (nread != length) {
      throw IOException("Error reading socket: unexpected end of transmission")
    }
    return String(buf)
  }

  fun readLine(): String? {
    val sb = StringBuffer()
    var c: Int
    var end = false
    while (!end) {
      c = read()
      if (c == -1 || c == '\n'.toInt()) {
        end = true
      } else if (c != '\r'.toInt()) {
        sb.append(c.toChar())
      }
    }
    return sb.toString()
  }

  fun readArray(): ByteArray? {
    val buf = ByteArray(1024)
    val outputStream = ByteArrayOutputStream()
    var nread = 0
    while (`is`!!.read(buf).also { nread = it } > 0) {
      outputStream.write(buf, 0, nread)
    }
    return outputStream.toByteArray()
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private fun read(): Int {
    val c = `is`!!.read()
    if (c == -1) {
      throw IOException("Error reading socket: unexpected end of transmission")
    }
    return c
  }

  private fun verify() {
    if (!`is`!!.markSupported()) {
      throw InconsistencyException("Mark is not supported")
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var `is`: InputStream? = null
}
