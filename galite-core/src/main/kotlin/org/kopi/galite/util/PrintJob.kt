package org.kopi.galite.util

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class PrintJob {

  fun getInputStream(): InputStream {
    return FileInputStream(datafile)
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val datafile: File? = null
}