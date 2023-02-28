/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.WindowController

class DocumentationReportTriggers : Form(title = "Test Report Form", locale = Locale.UK) {

  //Menus Definition
  val file = menu("file")

  val report = actor(
    menu = file,
    label = "CreateReport",
    help = "Create report",
  ) {
    key = Key.F8
    icon = Icon.REPORT
  }

    /** Calling reports **/
  // call report with  WindowController.windowController.doNotModal
  val simpleBlock = insertBlock(SimpleBlock())

  //simple block
  inner class SimpleBlock : Block("Simple Block", 1, 10) {
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }

    init {
      command(item = report) {
        WindowController.windowController.doNotModal(DocumentationReportTriggersR())
      }
    }
  }
}

fun main() {
  initReportDocumentationData()
  runForm(formName = DocumentationReportTriggers())
}
