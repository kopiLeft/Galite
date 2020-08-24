package org.galite.visual.report

import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.transform.stream.StreamSource


class ReportToPDF : StreamSource() {
  private val os = ByteArrayOutputStream()

  fun ReportToPDF(report: Report) {
    var document: Document? = null
    try {
      document = Document(PageSize.A4, 50F, 50F, 50F, 50F)
      PdfWriter.getInstance(document, os)
      document.open()
      val table = PdfPTable(report.report.columns.size)
      addTableHeader(table, report)
      report.rows.forEach { row -> row.values.forEach { value -> table.addCell(value) } }
      document.add(table)
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      if (document != null) {
        document.close()
      }
    }
  }

  private fun addTableHeader(table: PdfPTable, report: Report) {
    report.report.columns.map { column -> column.key }
        .forEach { columnTitle ->
          val header = PdfPCell()
          header.backgroundColor = BaseColor.LIGHT_GRAY
          header.borderWidth = 2f
          header.phrase = Phrase(columnTitle)
          table.addCell(header)
        }
  }

  fun getStream(): InputStream? {
    // Here we return the pdf contents as a byte-array
    return ByteArrayInputStream(os.toByteArray())
  }

}

