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

import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.cross.VDynamicReport
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.pivottable.PivotTable
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.form.VDictionaryForm

/**
 * Represents a dictionary form.
 *
 * @param title The title of this form.
 * @param locale the window locale.
 */
abstract class DictionaryForm(title: String, locale: Locale? = null) : Form(title, locale) {
  /**
   * Searches for an existing record.
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the selected record after the search operation.
   *
   * @return The selected ID of the searched record.
   * @throws VException Any visual errors that occurs during search operation.
   */
  open fun search(): Int = model.search()

  /**
   * Edits an existing record.
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the edited record after the edit operation.
   *
   * @param id The record ID to be edited.
   * @return The edited record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  open fun edit(id: Int): Int = model.edit(id)

  /**
   * Adds a new record.
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the created record.
   *
   * @return The created record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  open fun add(): Int = model.add()

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by triggers
   */
  open fun editWithID(id: Int): Int = model.editWithID(id)

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by triggers
   */
  open fun openForQuery(): Int = model.openForQuery()

  /**
   * create a new record and returns id
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by triggers
   */
  open fun newRecord(): Int = model.newRecord()

  open fun saveFilledField() {
    model.saveFilledField()
  }

  /**
   *
   */
  open fun interruptRecursiveQuery() {
    model.interruptRecursiveQuery()
  }

  open fun isNewRecord(): Boolean = model.isNewRecord()

  open fun setCloseOnSave() {
    model.setCloseOnSave()
  }

  /**
   * create a customized report for this form
   */
  protected fun Block.createReport(reportbuilder: () -> Report) {
    model.createReport(block) {
      reportbuilder().model
    }
  }

  /**
   * create a customized chart for this form
   */
  protected fun Block.createChart(chartbuilder: () -> Chart) {
    model.createChart(block) {
      chartbuilder().model
    }
  }

  /**
   * create a report for this form
   */
  protected fun Block.createDynamicReport() {
    val field = this.block.activeField
    field?.validate()
    VDynamicReport.createDynamicReport(this.block)
  }

  /**
   * create a pivot table for this form
   */
  protected fun Block.createPivotTable(reportbuilder: () -> PivotTable) {
    model.createPivotTable(block) {
      reportbuilder().model
    }
  }

  // ----------------------------------------------------------------------
  // DICTIONARY FORM MODEL
  // ----------------------------------------------------------------------
  override val model: VDictionaryForm = object : VDictionaryForm(sourceFile) {
    init {
      setTitle(title)
    }

    override val locale get() = this@DictionaryForm.locale ?: ApplicationContext.getDefaultLocale() // TODO: !!

    override fun formClassName(): String = this@DictionaryForm.javaClass.name
  }
}
