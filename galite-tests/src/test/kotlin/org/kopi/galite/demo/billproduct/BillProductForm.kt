package org.kopi.galite.demo.billproduct

import org.kopi.galite.demo.Bill
import org.kopi.galite.demo.BillProduct
import org.kopi.galite.demo.Product
import org.kopi.galite.demo.client.BlockClient
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.client.ClientR
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.type.Decimal
import java.util.Locale

object BillProductForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
  override val title = "bill product form"
  val page = page("page")
  val action = menu("act")
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "preview"  // icon is optional here
  }
  val tb1 = insertBlock(BlockBill, page){
    command(item = report) {
      action = {
        createReport(BlockBill)
      }
    }
  }

  override fun createReport(): Report {
    return BillProductR
  }
}

object BlockBill : FormBlock(1, 1, "block bill product") {
  val u = table(BillProduct)
  val v = table(Product)
  val w = table(Bill)

  val idBPdt = hidden(domain = Domain<Int>(20)) {
    label = "bill product id"
    help = "The bill product id"
    columns(u.idBPdt, v.idPdt)
  }
  val quantity = mustFill(domain = Domain<Int>(30), position = at(2, 1)) {
    label = "quantity"
    help = "quantity"
    columns(u.quantity)
  }
  val amountHT = visit(domain = Domain<Int>(20), position = at(4, 1)) {
    label = "amount HT"
    help = "amount HT"
    columns(u.amountHT)
  }
  val amountTTC = visit(domain = Domain<Decimal>(20), position = at(5, 1)) {
    label = "amount TTC"
    help = "amount TTC"
    columns(u.amountTTC,w.amountTTC)
  }
}

fun main() {
  Application.runForm(formName = BillProductForm)
}