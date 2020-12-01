/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.form.dsl

import org.kopi.galite.cross.VReportSelectionForm
import org.kopi.galite.report.VReport
import org.kopi.galite.report.dsl.Report

/**
 * Represents a report selection form.
 */
abstract class ReportSelectionForm: DictionaryForm() {

  /**
   * create a report for this form
   */
  protected abstract fun createReport(): Report

  /** Form model */
  override val model: VReportSelectionForm by lazy {
    genLocalization()
    object : VReportSelectionForm() {
      override fun init() {
        initialize()
      }

      override fun createReport(): VReport {
        return this@ReportSelectionForm.createReport().model
      }
    }
  }
}
