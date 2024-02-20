package org.kopi.galite.demo.order

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Bill
import org.kopi.galite.demo.database.Order
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport
import java.util.*

class OrderR : Report("Order Report", Locale.UK) {
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

  val idOrd = field(INT(10)) {
    label = "ID"
    help = "ID Order"
    align = FieldAlignment.LEFT
  }

  val dateOrd = field(DATE) {
    label = "Date"
    help = "Date "
    align = FieldAlignment.LEFT
  }

  val qtyOrd = field(INT(10)) {
    label = "Quantity"
    help = "Quantity"
    align = FieldAlignment.LEFT
  }

  val orders = Order.selectAll()

  init {
    transaction {
      orders.forEach() { result ->
        add {
          this[idOrd] = result[Order.idOrd]
          this[dateOrd] = result[Order.dateOrd]
          this[qtyOrd] = result[Order.qtyOrd]
        }
      }
    }
  }
}