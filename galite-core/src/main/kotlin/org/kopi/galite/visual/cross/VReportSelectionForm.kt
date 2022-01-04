/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.cross

import org.kopi.galite.visual.db.DBContext
import org.kopi.galite.visual.db.DBContextHandler
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VDictionaryForm
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.report.VNoRowException
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.visual.Message
import org.kopi.galite.visual.visual.MessageCode

abstract class VReportSelectionForm : VDictionaryForm {
  protected constructor()
  protected constructor(caller: VForm) : super(caller)
  protected constructor(caller: DBContextHandler) : super(caller)
  protected constructor(caller: DBContext) : super(caller)

  /**
   * Implements interface for COMMAND CreateReport
   */
  fun createReport(b: VBlock, report: VReport) {
    b.validate()
    try {
      setWaitInfo(Message.getMessage("report_generation"))
      report.dBContext = dBContext
      report.doNotModal()
      unsetWaitInfo()
    } catch (e: VNoRowException) {
      unsetWaitInfo()
      error(MessageCode.getMessage("VIS-00057"))
    }
    b.setRecordChanged(0, false)
  }

  companion object {
    /**
     * static call to createReport.
     */
    fun createReport(report: VReport, b: VBlock) {
      b.validate()
      try {
        report.setWaitInfo(Message.getMessage("report_generation"))
        report.dBContext = report.dBContext
        report.doNotModal()
        report.unsetWaitInfo()
      } catch (e: VNoRowException) {
        report.unsetWaitInfo()
        report.error(MessageCode.getMessage("VIS-00057"))
      }
      b.setRecordChanged(0, false)
    }
  }
}
