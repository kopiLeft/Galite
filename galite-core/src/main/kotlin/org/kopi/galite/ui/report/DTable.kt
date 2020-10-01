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

package org.kopi.galite.ui.report

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.ItemClickEvent
import org.kopi.galite.report.UReport.UTable
import org.kopi.galite.report.VReportRow
import java.io.Serializable
import java.lang.reflect.Method


/**
 * The `DTable` is a vaadin [Table] implementing the [Utable]
 * specifications.
 *
 * @param model The table model.
 */
class DTable(val model: VTable) : Grid<VReportRow>(), UTable, ComponentEventListener<ItemClickEvent<ReportTable>> {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int {
    val visibleColumns = columns.filter { it.isVisible }
    return columns.indexOf(viewColumnIndex)
  }

  override fun convertColumnIndexToView(modelColumnIndex: Int): Int {
    val visibleColumns = columns.filter { it.isVisible }
    visibleColumns.forEach {
      val modelIndex = visibleColumns.indexOf(it)
      if (modelIndex == modelColumnIndex) {
        return modelIndex
      }
    }
    return -1
  }

  /**
   * Sets the table visible columns.
   * @param visibleColumns The table visible columns.
   */
  fun setVisibleColumns(visibleColumns: IntArray) {
    TODO()
  }

  /**
   * Notifies the table model that table structure has been changed.
   */
  fun fireStructureChanged() {
    TODO()
  }

  /**
   * Returns the table column count.
   * @return The table column count.
   */
  fun getColumnCount(): Int {
    TODO()
  }

  /**
   * Returns the table row count.
   * @return The table row count.
   */
  fun getRowCount(): Int {
    return model.model.getRowCount()
  }

  /**
   * Registers a [ColumnCollapseListener] to this table.
   * @param listener The listener to be registered.
   */
  fun addListener(listener: ColumnCollapseListener?) {
    TODO()
  }

  override fun onComponentEvent(event: ItemClickEvent<ReportTable>?) {
    TODO("Not yet implemented")
  }

  /**
   * Reset all columns widths.
   */
  fun resetWidth() {
    TODO()
  }

  /**
   * Resets the column size at a given position.
   * @param pos The column position.
   */
  private fun resetColumnSize(pos: Int) {
    TODO()
  }

  /**
   * Resets the table cached information.
   */
  fun resetCachedInfos() {
    selectedRow = -1
    selectedColumn = -1
    select(null)
  }
  //---------------------------------------------------
  // COLUMN COLLAPSE LISTENER
  //---------------------------------------------------
  /**
   * The `ColumnCollapseListener` notifies registered
   * objects that a column collapse event happened.
   */
  interface ColumnCollapseListener : Serializable {
    /**
     * Fired when a column collapse event happens.
     * @param event The columns collapse event.
     */
    fun <T : Component> columnCollapsed(event: ColumnCollapseEvent<T>)
  }
  //----------------------------------------------
  // COLUMN COLLAPSE EVENT
  //----------------------------------------------
  /**
   * The `ColumnCollapseEvent` is a [Component.Event]
   * that handle column collapse events.
   *
   * @param source The source component.
   * @param propertyIds The collapsed columns IDs.
   */
  class ColumnCollapseEvent<T : Component>(source: T, val propertyIds: Array<Any>?) : ComponentEvent<T>(source, false) {

    companion object {
      var METHOD: Method? = null

      init {
        try {
          METHOD = ColumnCollapseListener::class.java.getDeclaredMethod("columnCollapsed", *arrayOf<Class<*>>(ColumnCollapseEvent::class.java))
        } catch (e: NoSuchMethodException) {
          // This should never happen
          throw RuntimeException(e)
        }
      }
    }
    //-------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------
  }

  fun getColumnHeader(propertyId: Any): String? {
    return model.getColumnName((propertyId as Int).toInt())
  }
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  /**
   * The selected row.
   */
  var selectedRow = -1
  /**
   * The selected column.
   */
  var selectedColumn = -1

  /**
   * Creates a new `DTable` instance.
   * @param model The table model.
   */
  init {
    width = "100%"
  }
}
