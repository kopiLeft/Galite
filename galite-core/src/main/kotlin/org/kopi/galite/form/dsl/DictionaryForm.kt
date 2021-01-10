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

import org.kopi.galite.db.DBContext
import org.kopi.galite.form.VDictionary
import org.kopi.galite.form.VDictionaryForm
import org.kopi.galite.visual.VWindow

/**
 * Represents a dictionary form.
 */
abstract class DictionaryForm : VDictionary, Form() {

  override fun search(parent: VWindow): Int = model.search(parent)

  override fun edit(parent: VWindow, id: Int): Int = model.edit(parent, id)

  override fun add(parent: VWindow): Int = model.add(parent)

  override var dBContext: DBContext?
    get() = model.dBContext
    set(value) {
      model.dBContext = value
    }

  override fun doNotModal() {
    model.doNotModal()
  }

  /** Form model */
  override val model: VDictionaryForm by lazy {
    genLocalization()
    object : VDictionaryForm() {
      override fun init() {
        initialize()
      }
    }
  }
}
