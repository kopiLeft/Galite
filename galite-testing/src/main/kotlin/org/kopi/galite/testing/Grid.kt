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
package org.kopi.galite.testing

import org.kopi.galite.visual.ui.vaadin.report.DTable
import com.github.mvysny.kaributesting.v10._fireEvent
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.checkEditableByUser
import com.github.mvysny.kaributesting.v10.expectRow
import com.github.mvysny.kaributesting.v10.expectRows
import com.github.mvysny.kaributools._internalId
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.ItemClickEvent

fun <T> Grid<T>.expect(data: Array<Array<String>>) {
  expectRows(data.size)

  data.forEachIndexed { index, it ->
    expectRow(index, *it)
  }
}

fun DTable._clickCell(rowIndex: Int, columnIndex: Int, clickCount: Int, duration: Long = 100) {
  checkEditableByUser()
  val itemKey: String = dataCommunicator.keyMapper.key(_get(rowIndex))
  val column = columns[columnIndex]._internalId
  val event = ItemClickEvent(this, true, itemKey, column, -1, -1, -1, -1, clickCount, 0, false, false, false, false)
  _fireEvent(event)

  waitAndRunUIQueue(duration)
}
