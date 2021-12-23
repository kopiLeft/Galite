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
package org.kopi.galite.visual.dsl.form

import org.kopi.galite.visual.cross.VDynamicReport
import org.kopi.galite.visual.db.DBContext
import org.kopi.galite.visual.form.VDictionaryForm
import org.kopi.galite.visual.visual.VException

/**
 * Represents a dictionary form.
 */
abstract class DictionaryForm : Form() {

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
  fun search(): Int = model.search(model)

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
  fun edit(id: Int): Int = model.edit(model, id)

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
  fun add(): Int = model.add(model)

  var dBContext: DBContext?
    get() = model.dBContext
    set(value) {
      model.dBContext = value
    }

  fun doNotModal() {
    model.doNotModal()
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
