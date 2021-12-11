/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.report

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

@CssImport(value = "./styles/galite/columnselector.css")
class ColumnsSelector : Div() {
  private val contextMenu = ContextMenu()
  private val button = Button()

  init {
    button.icon = Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT)
    contextMenu.target = button
    contextMenu.isOpenOnClick = true
    className = "columns-selector"
    button.className = "columns-selector-button"

    add(button, contextMenu)
  }

  fun build(table: DTable) {
    contextMenu.removeAll()

    table.viewColumns.forEach {
      val checkbox = Checkbox(table.model.model.columns[it]?.label)
      val column = table.getColumnByKey(it.toString())

      checkbox.className = "checkbox-selector"
      checkbox.value = column.isVisible
      checkbox.addValueChangeListener { e: AbstractField.ComponentValueChangeEvent<Checkbox?, Boolean?> ->
        column.isVisible = e.value!!
      }
      val item = contextMenu.addItem(checkbox)

      item.element.setAttribute("onClick", "event.stopPropagation()")
      item.element.classList.add("column-item-selector")
    }
  }
}
