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
package org.kopi.galite.util

import com.lowagie.text.PageSize
import com.lowagie.text.Rectangle

import org.kopi.galite.base.Utils

import java.io.InputStream
import java.io.File
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.ByteArrayOutputStream

/**
 * PPage/Report creates a PrintJob
 *
 * A Printer creates a PrintTask from a PrintJob
 */
class PrintJob(var datafile: File?, var delete: Boolean, var format: Rectangle) {

  constructor(format: Rectangle) : this(Utils.getTempFile("galite", "pdf"), true, format)
  constructor(data: ByteArray, format: Rectangle) : this(writeToFile(ByteArrayInputStream(data)), true, format)
  constructor(dataStream: InputStream, format: Rectangle) : this(writeToFile(dataStream), true, format)

  // properties
  var title: String = ""
  lateinit var media: String
  var documentType = 0
  var dataType: Int
  var numberCopy: Int = 1
  var numberOfPages: Int

  init {
    numberOfPages = -1
    dataType = DAT_PS
    /* if the jvm is stopped before the objects are finalized the file must be deleted!*/
    if (delete) {
      datafile!!.deleteOnExit()
    }
  }

  protected fun finalize() {
    if (delete && datafile != null) {
      datafile!!.delete()
    }
  }

  override fun toString(): String {
    return "PrintJob (" + delete + ") " + datafile + "  " + super.toString()
  }

  /**
   * OutputStream has to be closed before using inputStream
   * use with care, know what you do!
   */
  val outputStream: OutputStream
    get() = FileOutputStream(datafile)

  /**
   * outputStream has to be closed before using inputStream
   */
  val inputStream: InputStream
    get() = FileInputStream(datafile)

  val bytes: ByteArray
    get() {
      val buffer = ByteArray(1024)
      var length: Int

      /**
       * use inputStream because it creates the stream if necessary
       */
      val data: InputStream = inputStream
      val output: ByteArrayOutputStream = ByteArrayOutputStream()
      while (data.read(buffer).also { length = it } != -1) {
        output.write(buffer, 0, length)
      }
      return output.toByteArray()
    }

  fun writeDataToFile(file: File) {
    writeToFile(inputStream, file)
  }

  fun setPrintInformation(title: String, format: Rectangle, numberOfPages: Int) {
    this.title = title
    this.format = format
    this.numberOfPages = numberOfPages
  }

  fun getWidth(): Int {
    return format.width.toInt()
  }

  fun getHeight(): Int {
    return format.height.toInt()
  }

  fun createFromThis(file: File, delete: Boolean): PrintJob {
    return PrintJob(file, delete, format)
  }

  companion object {
    private fun writeToFile(dataStream: InputStream): File {
      val tempFile: File = Utils.getTempFile("galite", "pdf")
      writeToFile(dataStream, tempFile)
      return tempFile
    }

    private fun writeToFile(dataStream: InputStream, outputFile: File) {
      val buffer = ByteArray(1024)
      var length: Int
      val output: OutputStream = FileOutputStream(outputFile)
      while (dataStream.read(buffer).also { length = it } != -1) {
        output.write(buffer, 0, length)
      }
      output.flush()
      output.close()
    }

    val DAT_PDF = 1
    val DAT_PS = 2

    // A5, A4, A3, Letter and Legal page format (portrait)
    val FORMAT_A5 = PageSize.A5
    val FORMAT_A4 = PageSize.A4
    val FORMAT_A3 = PageSize.A3
    val FORMAT_LETTER = PageSize.LETTER
    val FORMAT_LEGAL = PageSize.LEGAL

    // A5, A4, A3, Letter and Legal page format (landscape)
    val FORMAT_A5_R = Rectangle(PageSize.A5.rotate().width, PageSize.A5.rotate().height)
    val FORMAT_A4_R = Rectangle(PageSize.A4.rotate().width, PageSize.A4.rotate().height)
    val FORMAT_A3_R = Rectangle(PageSize.A3.rotate().width, PageSize.A3.rotate().height)
    val FORMAT_LETTER_R = Rectangle(PageSize.LETTER.rotate().width, PageSize.LETTER.rotate().height)
    val FORMAT_LEGAL_R = Rectangle(PageSize.LEGAL.rotate().width, PageSize.LEGAL.rotate().height)

    // Raw format (Used for label printers)
    val FORMAT_RAW = Rectangle(-1F, -1F)
  }
}
