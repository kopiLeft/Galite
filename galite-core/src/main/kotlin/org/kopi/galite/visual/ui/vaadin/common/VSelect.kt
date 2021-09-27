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
package org.kopi.galite.visual.ui.vaadin.common

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.Tag
import com.vaadin.flow.data.binder.HasItems
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.dom.Element

@Tag(Tag.SELECT)
class VSelect : Component(), HasSize, HasItems<String> {
  override fun setItems(items: MutableCollection<String>) {
    this.items = items.toMutableList()

    items.map { item: Any? ->
      addItem(item.toString(), item.toString())
    }

    setSelectedValue(this.items.iterator().next())
  }

  /**
   * Adds a select option into the list box
   *
   * @param item the text of the item to be added
   * @param value the item's value, to be submitted; cannot be `null`
   */
  fun addItem(item: String, value: String?) {
    insertItem(item, value, INSERT_AT_END)
  }

  /**
   * Inserts a select option into the list box
   *
   * @param item the text of the item to be inserted
   * @param value the item's value, to be submitted
   * @param index the index at which to insert the select option
   */
  fun insertItem(item: String, value: String?, index: Int) {
    val option = Element(Tag.OPTION)

    items.add(item)
    option.text = item
    option.setProperty("value", value)
    if (index == INSERT_AT_END) {
      element.appendChild(option)
    } else {
      element.insertChild(index, option)
    }
  }

  fun getSelectedValue(): String {
    return selectedValue
  }

  fun setSelectedValue(value: String) {
    selectedValue = value
    element.setProperty("value", value)
  }

  private fun handleSelectionChange(event: DomEvent) {
    val eventData = event.eventData
    val selectedValue = eventData
            .getString("event.target.value")
    this.selectedValue = selectedValue
  }

  fun addChangeHandler(listener: (DomEvent?) -> Unit) {
    element.addEventListener("change", listener)
            .addEventData("event.target.value")
  }

  private lateinit var selectedValue: String
  private var items = mutableListOf<String>()
  private val INSERT_AT_END = -1

  init {
    addChangeHandler { event: DomEvent? ->
      handleSelectionChange(event!!)
    }
  }
}
