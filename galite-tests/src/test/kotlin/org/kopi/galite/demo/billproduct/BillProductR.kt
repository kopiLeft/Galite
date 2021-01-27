package org.kopi.galite.demo.billproduct

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VReport
import org.kopi.galite.type.Decimal
import java.util.*

object BillProductR : Report() {

  override val locale = Locale.FRANCE

  override val title = "Bill Product report"

  val action = menu("Action")

  val greeting = actor(
          ident = "greeting",
          menu = action,
          label = "Greeting",
          help = "Click me to show greeting",
  ) {
    key = Key.F1          // key is optional here
    icon = "ask"  // icon is optional here
  }

  val csv = actor(
          ident = "CSV",
          menu = action,
          label = "CSV",
          help = "Obtenir le format CSV",
  ) {
    key = Key.F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val xls = actor(
          ident = "XLS",
          menu = action,
          label = "XLS",
          help = "Obtenir le format Excel (XLS)",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val xlsx = actor(
          ident = "XLSX",
          menu = action,
          label = "XLSX",
          help = "Obtenir le format Excel (XLSX)",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val pdf = actor(
          ident = "PDF",
          menu = action,
          label = "PDF",
          help = "Obtenir le format PDF",
  ) {
    key = Key.F9          // key is optional here
    icon = "export"  // icon is optional here
  }

  val cmdCSV = command(item = csv) {
    action = {
      model.export(VReport.TYP_CSV)
    }
  }

  val cmdPDF = command(item = pdf) {
    action = {
      model.export(VReport.TYP_PDF)
    }
  }

  val cmdXLS = command(item = xls) {
    action = {
      model.export(VReport.TYP_XLS)
    }
  }

  val cmdXLSX = command(item = xlsx) {
    action = {
      model.export(VReport.TYP_XLSX)
    }
  }

  val quantity = field(Domain<Int>(25)) {
    label = "quantity"
    help = "quantity"
    align = FieldAlignment.LEFT
  }

  val amountHT = field(Domain<Int>(25)) {
    label = "amountHT"
    help = "amountHT"

  }
  val amountTTC = field(Domain<Decimal>(50)) {
    label = "amountTTC"
    help = "amountTTC"
    align = FieldAlignment.LEFT
  }

  init {
  }
}