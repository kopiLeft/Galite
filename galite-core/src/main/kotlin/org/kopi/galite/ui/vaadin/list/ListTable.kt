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
package org.kopi.galite.ui.vaadin.list

import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VListDialog
import org.kopi.galite.list.VBooleanColumn
import org.kopi.galite.list.VListColumn

class ListTable(model: VListDialog) : Grid<VListDialog>() {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Install filters on all properties.
   */
  fun installFilters(model: VListDialog?) {
    val filterRow: HeaderRow = appendHeaderRow()
    filterRow.also { element.classList.add("list-filter") }
    for (propertyId in containerDataSource.containerPropertyIds) {
      val cell: HeaderRow.HeaderCell = filterRow.getCell(columns[propertyId])
      val filter = TextField()
      filter.classNames.add("filter-text")
      //filter.setImmediate(true)
      filter.addValueChangeListener {
        fun textChange(event: HasValue.ValueChangeEvent<TextField>) {
          containerDataSource.removeContainerFilters(propertyId)
          if (event.value.label.isNotEmpty()) {
            containerDataSource.addContainerFilter(propertyId,
                                                   event.value.label,
                                                   true,
                                                   false)
            // select the first item when the content is filtered
            // to not loose grid focus and thus not loose navigation shortcuts
            select(containerDataSource[propertyId])
          }
        }
      }
      cell.setComponent(filter)
    }
    containerDataSource.containerPropertyIds.forEach {
      val cell: HeaderRow.HeaderCell = filterRow.getCell(it)
      val filter = TextField()
      filter.classNames.add("filter-text")
      //filter.setImmediate(true)
      filter.addValueChangeListener(object : ComponentEventListener<ComponentEvent<TextField>> {

        override fun onComponentEvent(event: ComponentEvent<TextField>?) {
          containerDataSource.removeContainerFilters(it)
          if (event!!.source.label.isNotEmpty()) {
            containerDataSource.addContainerFilter(it,
                                                   event.source.title,
                                                   ignoreCase = true,
                                                   onlyMatchPrefix = false)
            // select the first item when the content is filtered
            // to not loose grid focus and thus not loose navigation shortcuts
            select(containerDataSource.containerPropertyIds.stream())
          }
        }
      })
      cell.setComponent(filter)
    }

    element.classList.add("filtred")
  }

  /**
   * Looks the the item ID that its first column starts with the given pattern.
   * @param pattern The search pattern.
   * @return The found item ID or null if none of the search corresponds to the searched pattern
   */
  fun search(pattern: String?): Any? {
    return containerDataSource.search(pattern)
  }

  /**
   * Calculates the table width based on its content.
   * @param model The data model.
   */
  protected fun setTableWidth(model: VListDialog) {
    var width = 0
    for (col in model.columns) {
      val columnWidth = getColumnWidth(model, col!!.width) + 36
      col.width = columnWidth
      width += columnWidth
    }
    setWidth(Math.min(width, getWidth().toInt() - 20).toFloat(), Unit.PIXELS) //check getwidth().toint() TODO
  }

  /**
   * Calculates the column width based on the column rows content.
   * @param model The list data model.
   * @param col The column index.
   * @return The estimated wolumn width.
   */
  protected fun getColumnWidth(model: VListDialog, col: Int): Int {
    var width = 0

    for (row in 0 until model.count) {
      val value = model.columns[col]!!.formatObject(model.getValueAt(row, col)).toString()
      width = Math.max(width, Math.max(value.length, model.titles[col]!!.length))

    }
    return 8 * width
  }

  protected fun getListColumn(model: VListDialog, propertyId: Any?): VListColumn? {
    return model.columns[propertyId as Int]
  }

  /**
   * Install converters for values formatting.
   */
  protected fun installConverters(model: VListDialog) {
    for (column in columns) {
      if (getListColumn(model, column.id) is VBooleanColumn) {
        // column.setRenderer(createBooleanRenderer(), createBooleanConverter()) TODO
      } else {
        //column.setRenderer(TextRenderer(), ListConverter(model.columns[column.id as Int])) TODO
      }
    }
  }

  /**
   * Creates the conversion engine for grid data rendering.
   * @return The boolean converter
   */
  protected fun createBooleanConverter(): Converter<Boolean, Any> {
    return object : Converter<Boolean, Any> {

      override fun convertToModel(value: Boolean?, context: ValueContext?): Result<Any>? = value as Result<Any>

      override fun convertToPresentation(value: Any?, context: ValueContext?): Boolean = value as Boolean
      val modelType: Class<Any>
        get() = Any::class.java

      val presentationType: Class<Boolean>
        get() = Boolean::class.java
    }
  }

  /**
   * Creates the renderer for boolean column
   * @return The boolean renderer
   */
 /* protected fun createBooleanRenderer(): Renderer<Boolean> {
    return Renderer(trueRepresentation, falseRepresentation)
  }*/

  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  protected val trueRepresentation: String
    get() = VBooleanField.toText(java.lang.Boolean.TRUE)

  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  protected val falseRepresentation: String
    get() = VBooleanField.toText(java.lang.Boolean.FALSE)

  /**
   * Sets whether column collapsing is allowed or not.
   * @param collapsingAllowed specifies whether column collapsing is allowed.
   */
  fun setColumnCollapsingAllowed(collapsingAllowed: Boolean) {
    columns.forEach {
      it.isVisible = collapsingAllowed
    }
  }

  /**
   * Sets the columns headers of this list table.
   * @param headers The column headers.
   */
  fun setColumnHeaders(headers: Array<String?>) {
    for (i in headers.indices) {
      setColumnHeader(i, headers[i])
    }
  }

  /**
   * Sets the column header for the specified column;
   * @param propertyId the propertyId identifying the column.
   * @param header the header to set.
   */
  fun setColumnHeader(propertyId: Int, header: String?) {
    columns[propertyId].setHeader(header)
  }

  val containerDataSource: ListContainer
    get() = super.getDataProvider() as ListContainer //FIXME

  /**
   * Fires a container item set change.
   */
  fun tableChanged() {
    //  access{
    containerDataSource.fireItemSetChange()
    //  }
  }

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  init {
    setSelectionMode(SelectionMode.SINGLE)
    setColumnCollapsingAllowed(false)
    isColumnReorderingAllowed = true
    isEnabled = false
    setColumnHeaders(model.titles)
    installConverters(model)
    setTableWidth(model)
    installFilters(model)
    recalculateColumnWidths()
  }
}
