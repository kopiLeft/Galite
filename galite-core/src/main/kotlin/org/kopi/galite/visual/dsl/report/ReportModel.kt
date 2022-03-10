/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.dsl.report

import java.util.Locale

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.report.MReport
import org.kopi.galite.visual.report.VCalculateColumn
import org.kopi.galite.visual.report.VCellFormat
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.report.VSeparatorColumn
import org.kopi.galite.visual.visual.ApplicationContext

open class ReportModel(val report: Report): VReport() {

  init {
    setTitle(report.title)
    setPageTitle(report.title)
    help = report.help
    source = report.sourceFile

    if (report.reportCommands) {
      addDefaultReportCommands()
    }

    model.columns.add(VSeparatorColumn()) // TODO!!!

    // localize the report using the default locale
    localize()
  }

  override fun init() {
    report.fields.forEach {
      it.initField()

      if (it.group != null) {
        it.groupID = report.fields.indexOf(it.group)
        it.columnModel.groups = it.groupID
      }
    }
  }

  override fun add() {
    // TODO
  }
}
