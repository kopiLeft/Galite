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

import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.db.insertIntoModule
import org.kopi.galite.tests.form.FormWithReport
import org.kopi.galite.tests.ui.vaadin.triggers.FormToTestTriggers

fun initModules() {
  transaction {
    insertIntoModule("1000", "org/kopi/galite/test/Menu", 0)
    insertIntoModule("1001", "org/kopi/galite/test/Menu", 1, "1000", FormWithReport::class)
    insertIntoModule("2000", "org/kopi/galite/test/Menu", 100)
    insertIntoModule("2001", "org/kopi/galite/test/Menu", 101, "2000", CommandsForm::class)
    insertIntoModule("2002", "org/kopi/galite/test/Menu", 102, "2000", FormExample::class)
    insertIntoModule("2003", "org/kopi/galite/test/Menu", 103, "2000", MultipleBlockForm::class)
    insertIntoModule("2004", "org/kopi/galite/test/Menu", 104, "2000", FormToTestSaveMultipleBlock::class)
    insertIntoModule("2005", "org/kopi/galite/test/Menu", 105, "2000", TestFieldsForm::class)
    insertIntoModule("2006", "org/kopi/galite/test/Menu", 105, "2000", FormToTestTriggers::class)
    insertIntoModule("2007", "org/kopi/galite/test/Menu", 200)
    insertIntoModule("2008", "org/kopi/galite/test/Menu", 201, "2007", DocumentationFieldsForm::class)
    insertIntoModule("2009", "org/kopi/galite/test/Menu", 202, "2007", DocumentationBlockForm::class)
    insertIntoModule("2010", "org/kopi/galite/test/Menu", 203, "2007", DocumentationForm::class)
    insertIntoModule("2011", "org/kopi/galite/test/Menu", 204, "2007", DocumentationReport::class)
    insertIntoModule("2012", "org/kopi/galite/test/Menu", 205, "2007", DocumentationReportTriggers::class)
    insertIntoModule("2013", "org/kopi/galite/test/Menu", 206, "2007", DocumentationChart::class)
  }
}
