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

import org.kopi.galite.list.VList

abstract class VField {
  fun getSearchCondition(): String? = TODO()
  fun isChanged(): Boolean = TODO()
  fun hasFocus(): Boolean = TODO()
  fun checkType(displayedValue: Any) { TODO() }
  fun checkType(rec: Int, s: Any) { TODO() }
  open fun getHeight() : Int = TODO()
  fun getList(): VList? = TODO()
  fun getBlock() : VBlock = TODO()
    fun getForm():VForm = TODO()
  fun isNull(r:Int):Boolean =TODO()
  fun getListID(): Int =TODO()
  fun setNull(activeRecord: Int){TODO()}
   fun setValueID(id: Int){TODO()}
}
