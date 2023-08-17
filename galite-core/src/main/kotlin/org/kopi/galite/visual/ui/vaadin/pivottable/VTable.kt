///*
// * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
// * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License version 2.1 as published by the Free Software Foundation.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
// */
//package org.kopi.galite.visual.ui.vaadin.pivottable
//
//import org.kopi.galite.visual.pivottable.MPivotTable
//
//import com.vaadin.flow.component.grid.Grid
//import com.vaadin.flow.data.provider.ListDataProvider
//import com.vaadin.flow.data.provider.Query
//import com.vaadin.flow.function.SerializablePredicate
//
///**
// * The VTable is a vaadin [Grid] data provider adapted
// * to dynamic reports needs.
// *
// * @param model The table model.
// */
//class VTable(
//  internal val model: MPivotTable,
//  reportItems: List<DPivotTable.ReportModelItem>
//): ListDataProvider<DPivotTable.ReportModelItem>(reportItems) {
//
//  init {
//    addFilter {
//      it != null
//    }
//  }
//
//  override fun size(query: Query<DPivotTable.ReportModelItem, SerializablePredicate<DPivotTable.ReportModelItem>>?): Int {
//    return model.getRowCount()
//  }
//
//  /**
//   * Notify the report table that the report content has been
//   * change in order to update the table content.
//   */
//  fun fireContentChanged() {
//    refreshAll()
//  }
//
//  /**
//   * Returns the column count.
//   * @return the column count.
//   */
//  fun getColumnCount(): Int = model.getColumnCount()
//
//  /**
//   * Returns the row count.
//   * @return the row count.
//   */
//  fun getRowCount(): Int = model.getRowCount()
//}
