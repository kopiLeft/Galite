package org.kopi.galite.demo.product

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Order
import org.kopi.galite.demo.database.Product
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

class ProductR : Report("Product Report", Locale.UK) {
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

  val idP = field(INT(10)) {
    label = "ID"
    help = "ID Product"
    align = FieldAlignment.LEFT
  }

  val nameP = field(STRING(20)) {
    label = "Name"
    help = "Name Product"
    align = FieldAlignment.LEFT
    format { v ->
      v.toUpperCase()
    }
  }

  val qtyP = field(INT(10)) {
    label = "Quantity"
    help = "Quantity"
    align = FieldAlignment.LEFT
  }

  val priceTP = field(DECIMAL(10, 5)) {
    label = "Price/Total"
    help = "Price/Total"
    align = FieldAlignment.LEFT
  }

  val products = Product.selectAll()

  init {
    transaction {
      products.forEach() { result ->
        add {
          this[idP] = result[Product.idP]
          this[nameP] = result[Product.nameP]
          this[qtyP] = result[Product.qtyP]
          this[priceTP] = result[Product.priceTP]
        }
      }
    }
  }
}
