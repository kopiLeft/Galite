package org.kopi.galite.demo.provider

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport
import java.util.*

class ProviderR : Report("Provider", Locale.UK) {
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

  val idPro = field(INT(10)) {
    label = "ID"
    help = "ID Provider"
    align = FieldAlignment.LEFT
  }

  val fName = field(STRING(10)) {
    label = "firstName"
    help = "FirstName"
    align = FieldAlignment.LEFT
    format { v ->
      v.toUpperCase()
    }
  }

  val lName = field(STRING(10)) {
    label = "lastName"
    help = "lastName"
    align = FieldAlignment.LEFT
    format { v ->
      v.toUpperCase()
    }
  }

  val accNum = field(STRING(15)) {
    label = "Account Number"
    help = "Account Number"
    align = FieldAlignment.LEFT
    format { v ->
      v.toUpperCase()
    }
  }

  val tel = field(INT(8)) {
    label = "Telephone"
    help = "Telephone"
    align = FieldAlignment.LEFT
  }
  val provider = Provider.selectAll()

  init {
    transaction {
      provider.forEach() { result ->
        add {
          this[idPro] = result[Provider.idPro]
          this[fName] = result[Provider.firstName]
          this[lName] = result[Provider.lastName]
          this[accNum] = result[Provider.accountNum]
          this[tel] = result[Provider.telephone]
        }
      }
    }
  }


}