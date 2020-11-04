/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.form

import org.kopi.galite.list.VListColumn
import org.kopi.galite.visual.VWindow

class VListDialog(list: Array<VListColumn>, values: Array<Array<Any?>>?) {
  fun selectFromDialog(form: VForm, field: VField?): Int = TODO()
  fun selectFromDialog(form: VForm?, window: VWindow?, field: VField?): Int {
    TODO()
  }

  fun getCount(): Int = TODO()

  fun convert(pos: Int): Int = TODO()
  fun getForm(): VForm = TODO()
  fun setForm(form: VForm) {
    TODO()
  }

  fun getNewForm(): VDictionary = TODO()
  fun getColumns(): Array<VListColumn> = TODO()
  fun getSizes(): IntArray = TODO()
  fun isForceNew(): Boolean = TODO()
  fun isTooManyRows(): Boolean = TODO()
  fun getData(): Array<Array<Any>> = TODO()
  fun getTitles(): Array<String?> = TODO()
  fun getColumnCount(): Int = TODO()
  fun isSkipFirstLine(): Boolean = TODO()
  fun getColumnName(column: Int): String = TODO()
  fun sort(left: Int) {
    TODO()
  }
  fun getIdents(): IntArray= TODO()
  fun getTranslatedIdents(): IntArray = TODO()

  companion object {
    var NEW_CLICKED = -2
  }
}