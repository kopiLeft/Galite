/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key

class DocumentationChart : Form(title = "Test Report Form", locale = Locale.UK) {

  //Menus Definition
  val file = menu("file")

  val graph = actor (
    ident =  "graph",
    menu =   file,
    label =  "Graph",
    help =   "show graph values",
  ) {
    key  =  Key.F9
    icon =  Icon.COLUMN_CHART
  }

  val simpleBlock = insertBlock(SimpleBlock())

  //simple block
  inner class SimpleBlock : Block(1, 10, "Simple Block") {
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }

    init {
      command(
        item = graph,
        modes = arrayOf(Mode.UPDATE, Mode.INSERT, Mode.QUERY)
      ) {
        showChart(DocumentationChartC())
      }
    }
  }
}

fun main() {
  runForm(formName = DocumentationChart())
}
