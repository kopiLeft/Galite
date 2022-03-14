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
package org.kopi.galite.visual.cross

import org.kopi.galite.visual.chart.VChart
import org.kopi.galite.visual.chart.VNoChartRowException
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VDictionaryForm
import org.kopi.galite.visual.visual.Message
import org.kopi.galite.visual.visual.MessageCode

abstract class VChartSelectionForm : VDictionaryForm() {

  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------
  /**
   * static call to createReport.
   */
  open fun createChart(chart: VChart, b: VBlock) {
    b.validate()
    try {
      chart.setWaitInfo(Message.getMessage("chart_generation"))
      chart.doNotModal()
      chart.unsetWaitInfo()
    } catch (e: VNoChartRowException) {
      chart.unsetWaitInfo()
      chart.error(MessageCode.getMessage("VIS-00057"))
    }
    b.setRecordChanged(0, false)
  }

  /**
   * Implements interface for COMMAND CreateChart
   */
  open fun createChart(b: VBlock) {
    b.validate()
    try {
      setWaitInfo(Message.getMessage("chart_generation"))
      val chart = createChart()
      chart.doNotModal()
      unsetWaitInfo()
    } catch (e: VNoChartRowException) {
      unsetWaitInfo()
      error(MessageCode.getMessage("VIS-00057"))
    }
    b.setRecordChanged(0, false)
  }

  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------
  /**
   * create a report for this form
   */
  protected abstract fun createChart(): VChart
}
