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

import org.kopi.galite.visual.cross.VDynamicReport
import org.kopi.galite.visual.db.Connection
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.form.VDictionaryForm
import org.kopi.galite.visual.visual.VException

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
   * @param parent The parent window.
   * @return The selected ID of the searched record.
   * @throws VException Any visual errors that occurs during search operation.
   */
  fun search(parent: Window): Int = model.search(parent.model)

  /**
   * Edits an existing record.
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the edited record after the edit operation.
   *
   * @param parent The parent window.
   * @param id The record ID to be edited.
   * @return The edited record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  fun edit(parent: Window, id: Int): Int = model.edit(parent.model, id)

  /**
   * Adds a new record.
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the created record.
   *
   * @param parent The parent window.
   * @return The created record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  fun add(parent: Window): Int = model.add(parent.model)

  var dBConnection: Connection?
    get() = model.dBConnection
    set(value) {
      model.dBConnection = value
    }

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception        org.kopi.galite.visual.visual.VException        an exception may be raised by triggers
   */
  fun editWithID(parent: Window, id: Int): Int = model.editWithID(parent.model, id)

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception        org.kopi.galite.visual.visual.VException        an exception may be raised by triggers
   */
  fun openForQuery(parent: Window): Int = model.openForQuery(parent.model)

  /**
   * create a new record and returns id
   * @exception        org.kopi.galite.visual.visual.VException        an exception may be raised by triggers
   */
  fun newRecord(parent: Window): Int = model.newRecord(parent.model)

  /**
   * close the form
   */
  fun close(code: Int) {
    model.close(code)
  }

  fun saveFilledField() {
    model.saveFilledField()
  }

  /**
   *
   */
  fun interruptRecursiveQuery() {
    model.interruptRecursiveQuery()
  }

  fun isNewRecord(): Boolean = model.isNewRecord()

  fun setCloseOnSave() {
    model.setCloseOnSave()
  }

  /**
   * create a report for this form
   */
  protected fun Block.createDynamicReport() {
    val field = this.block.activeField
    field?.validate()
    VDynamicReport.createDynamicReport(this.block)
  }

  // ----------------------------------------------------------------------
  // DICTIONARY FORM MODEL
  // ----------------------------------------------------------------------
  override val model: VDictionaryForm by lazy { DictionaryFormModel(this) }
}
