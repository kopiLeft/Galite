package org.kopi.galite.demo.client

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VCellFormat
import org.kopi.galite.report.VReport
import java.util.Locale

/**
 * Simple Report with two fields.
 */
object ClientR : Report() {
  override val locale = Locale.FRANCE

  override val title = "ClientReport"

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

  val nameClt = field(Domain<String>(25)) {
    label = "name client"
    help = "The client name"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val fstnameClt = field(Domain<String>(25)) {
    label = "client firstname"
    help = "The client firstname"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }
  val addressClt = field(Domain<String>(50)) {
    label = "client address"
    help = "The client address"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val ageClt = field(Domain<Int>(2)) {
    label = "client age"
    help = "The client age"
    align = FieldAlignment.LEFT
  }

  val cityClt = field(Domain<String>(50)) {
    label = "client city"
    help = "The client city"
    align = FieldAlignment.LEFT
  }

  val postalCodeClt = field(Domain<Int>(2)) {
    label = "client postal code"
    help = "The client postal code"
    align = FieldAlignment.LEFT
  }

  init {
    add {
      this[nameClt] = "Salah"
      this[fstnameClt] = "Mohamed"
      this[addressClt] = "10,Rue du Lac"
      this[cityClt] = "Megrine"
      this[postalCodeClt] = 2001
      this[ageClt] = 40
    }
    add {
      this[nameClt] = "Guesmi"
      this[fstnameClt] = "Khaled"
      this[addressClt] = "14,Rue Mongi Slim"
      this[cityClt] = "Tunis"
      this[postalCodeClt] = 6000
      this[ageClt] = 35
    }
    add {
      this[nameClt] = "Bouaroua"
      this[fstnameClt] = "Ahmed"
      this[addressClt] = "10,Rue du Lac"
      this[cityClt] = "Mourouj"
      this[postalCodeClt] = 5003
      this[ageClt] = 22
    }
  }
}