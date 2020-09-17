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
class PrintJob(var datafile: File, var delete: Boolean, var format: Rectangle) {
  /**
   * use with care, do only read from the file, not not manipulate
   */
  // properties
  private var title: String = ""
  lateinit var media: String
  private var documentType = 0
  private var dataType: Int
  var numberCopy: Int
  private var numberOfPages: Int

  init {
    numberCopy = 1
    numberOfPages = -1
    dataType = DAT_PS
    // if the jvm is stopped before the objects are
    // finalized the file must be deleted!
    if (delete) {
      datafile.deleteOnExit()
    }
  }

  constructor(format: Rectangle) : this(Utils.getTempFile("kopi", "pdf"), true, format)
  constructor(data: ByteArray, format: Rectangle) : this(writeToFile(ByteArrayInputStream(data)), true, format)
  constructor(dataStream: InputStream, format: Rectangle) : this(writeToFile(dataStream), true, format)

  protected fun finalize() {
    if (delete && datafile != null) {
      datafile!!.delete()
    }
  }

  override fun toString(): String {
    return "PrintJob (" + delete + ") " + datafile + "  " + super.toString()
  }

  /**
   * getOutputStream has to be closed before calling getInputStream
   * use with care, know waht you do!
   */
  fun getOutputStream(): OutputStream {
    return FileOutputStream(datafile)
  }

  /**
   * getOutputStream has to be closed before calling getInputStream
   */
  fun getInputStream(): InputStream {
    return FileInputStream(datafile)
  }

  /**
   * use getInputStream because in creates the stream if necessary
   */
  val bytes: ByteArray
    get() {
      val buffer = ByteArray(1024)
      var length: Int

      /**
       * use getInputStream because in creates the stream if necessary
       */
      val data: InputStream = getInputStream()
      val output: ByteArrayOutputStream = ByteArrayOutputStream()
      while (data.read(buffer).also { length = it } != -1) {
        output.write(buffer, 0, length)
      }
      return output.toByteArray()
    }

  fun writeDataToFile(file: File) {
    writeToFile(getInputStream(), file)
  }

  fun setPrintInformation(title: String, format: Rectangle, numberOfPages: Int) {
    this.title = title
    this.format = format
    this.numberOfPages = numberOfPages
  }

  fun getWidth(): Int {
    return format.width as Int
  }

  fun getHeight(): Int {
    return format.height as Int
  }

  fun createFromThis(file: File, delete: Boolean): PrintJob {
    return PrintJob(file, delete, format)
  }

  /**
   * Kind of data to print (pdf, ps)
   *
   * @return A number representing the document type.
   */
  fun getDataType(): Int {
    return dataType
  }

  companion object {
    private fun writeToFile(dataStream: InputStream): File {
      val tempFile: File = Utils.getTempFile("kopi", "pdf")
      writeToFile(dataStream, tempFile)
      return tempFile
    }

    private fun writeToFile(dataStream: InputStream, outputfile: File) {
      val buffer = ByteArray(1024)
      var length: Int
      val output: OutputStream = FileOutputStream(outputfile)
      while (dataStream.read(buffer).also { length = it } != -1) {
        output.write(buffer, 0, length)
      }
      output.flush()
      output.close()
    }

    var DAT_PDF = 1
    var DAT_PS = 2

    // A5, A4, A3, Letter and Legal page format (portrait)
    var FORMAT_A5 = PageSize.A5
    var FORMAT_A4 = PageSize.A4
    var FORMAT_A3 = PageSize.A3
    var FORMAT_LETTER = PageSize.LETTER
    var FORMAT_LEGAL = PageSize.LEGAL

    // A5, A4, A3, Letter and Legal page format (landscape)
    var FORMAT_A5_R = Rectangle(PageSize.A5.rotate().width, PageSize.A5.rotate().height)
    var FORMAT_A4_R = Rectangle(PageSize.A4.rotate().width, PageSize.A4.rotate().height)
    var FORMAT_A3_R = Rectangle(PageSize.A3.rotate().width, PageSize.A3.rotate().height)
    var FORMAT_LETTER_R = Rectangle(PageSize.LETTER.rotate().width, PageSize.LETTER.rotate().height)
    var FORMAT_LEGAL_R = Rectangle(PageSize.LEGAL.rotate().width, PageSize.LEGAL.rotate().height)

    // Raw format (Used for label printers)
    var FORMAT_RAW = Rectangle(-1F, -1F)
  }
}