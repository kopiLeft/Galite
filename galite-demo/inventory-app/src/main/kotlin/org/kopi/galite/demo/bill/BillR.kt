package org.kopi.galite.demo.bill

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Bill
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport
import java.util.*

class BillR : Report("Bill Report", Locale.UK) {
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

  val idB = field(INT(10)) {
    label = "ID"
    help = "ID Bill"
    align = FieldAlignment.LEFT
  }

  val date = field(DATE) {
    label = "Date"
    help = "Date Bill"
    align = FieldAlignment.LEFT
  }

  val priceT = field(DECIMAL(10, 5)) {
    label = "Total price"
    help = "Total price "
    align = FieldAlignment.LEFT
  }

  val payMeth = field(STRING(10)) {
    label = "Payment method"
    help = "Payment method"
    align = FieldAlignment.LEFT
    format { v ->
      v.toUpperCase()
    }
  }

  val payDate = field(DATE) {
    label = "Payment Date"
    help = "Payment Date"
    align = FieldAlignment.LEFT
  }

  val bills = Bill.selectAll()

  init {
    transaction {
      bills.forEach() { result ->
        add {
          this[idB] = result[Bill.idB]
          this[date] = result[Bill.date]
          this[priceT] = result[Bill.priceT]
          this[payMeth] = result[Bill.payMeth]
          this[payDate] = result[Bill.payDate]
        }
      }
    }
  }

}