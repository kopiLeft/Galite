package org.kopi.galite.demo.quote

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Quote
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport
import java.util.*

class QuoteR : Report("Quote Report", Locale.UK) {
  val act: Menu = menu("action")

  val pdf = actor(
    menu = act,
    label = "PDF",
    help = "PDF"
  )
  {
    key = Key.F11
    icon = Icon.EXPORT_PDF
  }

  val xlsx = actor(
    menu = act,
    label = "XLSX",
    help = "XLSX"
  )
  {
    key = Key.F10
    icon = Icon.EXPORT_XLSX
  }

  val csv = actor(
    menu = act,
    label = "CSV",
    help = "CSV"
  )
  {
    key = Key.F8
    icon = Icon.EXPORT_CSV
  }

  val xls = actor(
    menu = act,
    label = "XLS",
    help = "XLS"
  )
  {
    key = Key.F7
    icon = Icon.EXPORT_XLSX
  }

  val helps = actor(
    menu = act,
    label = "Help",
    help = "Help"
  )
  {
    key = Key.F9
    icon = Icon.HELP
  }

  /**
   * Actions
   */

  val cmdpdf = command(item = pdf)
  {
    model.export(VReport.TYP_PDF)
  }

  val cmdxls = command(item = xls)
  {
    model.export(VReport.TYP_XLS)
  }

  val cmdxlsx = command(item = xlsx)
  {
    model.export(VReport.TYP_XLSX)
  }

  val cmdcsv = command(item = csv)
  {
    model.export(VReport.TYP_CSV)
  }

  val idQ = field(INT(10)) {
    label = "ID"
    help = "ID Quote"
    align = FieldAlignment.LEFT
  }

  val date = field(DATE) {
    label = "Date"
    help = "Date"
    align = FieldAlignment.LEFT
  }

  val quotes = Quote.selectAll()

  init {
    transaction {
      quotes.forEach() { result ->
        add {
          this[idQ] = result[Quote.idQ]
          this[date] = result[Quote.date]
        }
      }
    }
  }


}