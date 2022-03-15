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
package org.kopi.galite.visual.dsl.form

import java.util.Locale

import org.kopi.galite.visual.cross.VReportSelectionForm
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.visual.ApplicationContext

/**
 * Represents a report selection form.
 *
 * @param title The title of this form.
 * @param locale the window locale.
 */
abstract class ReportSelectionForm(title: String, locale: Locale? = null) : DictionaryForm(title, locale) {

  /**
   * create a report for this form
   */
  protected fun Block.createReport(reportbuilder: () -> Report) {
    model.createReport(block) {
      reportbuilder().model
    }
  }

  // ----------------------------------------------------------------------
  // REPORT MODEL
  // ----------------------------------------------------------------------
  override val model: VReportSelectionForm = object : VReportSelectionForm(sourceFile) {
    init {
      setTitle(title)
    }

    override val locale get() = this@ReportSelectionForm.locale ?: ApplicationContext.getDefaultLocale()

    override fun formClassName(): String = this@ReportSelectionForm.javaClass.name
  }
}
