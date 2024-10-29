/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.list

import org.kopi.galite.visual.form.VListDialog

import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.KeyDownEvent
import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import org.kopi.galite.visual.list.VImageColumn
import org.kopi.galite.visual.ui.vaadin.base.Image
import org.kopi.galite.visual.ui.vaadin.form.DImageField
import java.io.ByteArrayInputStream

@CssImport("./styles/galite/list.css")
class ListTable(val model: VListDialog) : Grid<ListTable.ListDialogItem>() {
  internal var widthStyler = Div()

  init {
    isColumnReorderingAllowed = true
    buildRows()
    buildColumns()
    setTableWidth(model)
    installFilters(model)
  }

  private fun buildRows() {
    val items = (0 until model.count).map { ListDialogItem(it) }

    setItems(items)
  }

  private fun buildColumns() {
    for(col in 0 until model.getColumnCount()) {
      if (model.columns[col] is VImageColumn) {
        addColumn(
          ComponentRenderer { item: ListDialogItem ->
            val image = (item.getValueAt(col) as? Image)

            if (image != null) {
              image.apply {
                element.style["outline"] = "1px solid lightgreen"
                width = "100px"
                height = "100px"
                setBorder(0)
                element.setProperty("borderStyle", "none")
                setSrc(DImageField.DynamicImageResource(createFileName("image")) { ByteArrayInputStream(image.source) })
              }
              image
            } else {
              Image()
            }
          }
        ).setHeader(Span(model.getColumnName(col))).setKey(col.toString())
      } else {
        addColumn {
          it.getValueAt(col)
        }.setHeader(Span(model.getColumnName(col))).setKey(col.toString())
      }
    }
  }

  /**
   * Install filters on all properties.
   */
  fun installFilters(model: VListDialog) {
    val filterRow = appendHeaderRow()

    filterRow.also { element.classList.add("list-filter") }
    val filterFields = this.columns.mapIndexed { i, column ->
      val cell = filterRow.getCell(column)
      val filterField = TextField()
      val search = Icon(VaadinIcon.SEARCH)

      filterField.setWidthFull()
      filterField.suffixComponent = search
      filterField.className = "filter-text"
      if (this.model.columns[i] !is VImageColumn) {
        filterField.addValueChangeListener {
          (dataProvider as ListDataProvider).refreshAll()
        }

        filterField.valueChangeMode = ValueChangeMode.EAGER
      } else {
        filterField.isReadOnly = true
      }
      cell.setComponent(filterField)
      filterField
    }
    (dataProvider as ListDataProvider).filter = ListFilter(filterFields, model, true, false)

    element.classList.add("filtered")
  }

  /**
   * Adds a keydown listener to this component.
   * @param listener
   * @return a handle that can be used for removing the listener
   */
  fun addKeyDownListener(listener: ComponentEventListener<KeyDownEvent>) {
    this.addListener(KeyDownEvent::class.java, listener)
  }

  /**
   * Calculates the table width based on its content.
   * @param model The data model.
   */
  internal fun setTableWidth(model: VListDialog) {
    var width = 0
    for (col in 0 until model.getColumnCount()) {
      val columnWidth = getColumnWidth(model, col) + 36
      getColumnByKey(col.toString()).width = columnWidth.toString()+ "px"
      width += columnWidth
    }
    widthStyler.setWidth(width + 20f, Unit.PIXELS)
    widthStyler.setMinWidth(width + 20f, Unit.PIXELS)
  }

  /**
   * Calculates the column width based on the column rows content.
   * @param model The list data model.
   * @param col The column index.
   * @return The estimated column width.
   */
  private fun getColumnWidth(model: VListDialog, col: Int): Int {
    var width: Int
    width = 0
    for (row in 0 until model.count) {
      val value = model.columns[col]!!.formatObject(model.getValueAt(row, col)).toString()
      width = if (model.columns[col]!! is VImageColumn) {
        model.columns[col]!!.width
      } else {
        width.coerceAtLeast(value.length.coerceAtLeast(model.titles[col]!!.length))
      }
    }
    return 8 * width
  }

  val selectedItem: ListTable.ListDialogItem get() = asSingleSelect().value

  /**
   * List grid item model.
   *
   * @param row the row of the item in the grid.
   */
  inner class ListDialogItem(val row: Int) {

    /**
     * Returns the value of cell in position ([row], [col]).
     *
     * This method gets the value from the list model [VListDialog].
     *
     * It can be used in DataProvider to show the values of the grid initially of after refreshing data.
     */
    fun getValueAt(col: Int) = formatObject(model.data[col][model.translatedIdents[row]], col)

    /**
     * Formats an object according to the property nature.
     * @param o The object to be formatted.
     * @return The formatted property object.
     */
    private fun formatObject(o: Any?, col: Int): Any?{
      return if (model.columns[col]!!.formatObject(o) is Image) {
        model.columns[col]!!.formatObject(o)
      } else {
        model.columns[col]!!.formatObject(o).toString()
      }
    }
  }
}
