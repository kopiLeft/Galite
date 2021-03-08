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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.ListItem
import com.vaadin.flow.data.provider.DataCommunicator
import com.vaadin.flow.data.provider.InMemoryDataProvider
import org.kopi.galite.form.VListDialog
import org.kopi.galite.ui.vaadin.base.Utils
import java.util.*

/**
 * A sortable data source container for list dialog object.
 */
class ListContainer(model: VListDialog) : Div(), InMemoryDataProvider {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  val containerPropertyIds: Collection<Int>
    get() = Collections.unmodifiableCollection(propertyIds)

  fun getContainerProperty(itemId: Any?, propertyId: Any?): ListProperty
          = ListProperty(model, itemId as Int, propertyId as Int)

  fun getType(propertyId: Any?): Class<*> = String::class.java

  protected fun getUnfilteredItem(itemId: Any?): ListItem? {
    return ListItem(this, itemId as Int?)
  }

  fun size(): Int {
    return if (!hasFilters()) {
      model.count - (if (model.isSkipFirstLine) 1 else 0)
    } else {
      super.size()
    }
  }

  fun sort(propertyId: Array<Any?>?, ascending: BooleanArray?) {
    sort(propertyId, ascending)
  }

  fun addContainerFilter(filter: ListFilter) {
    containerFilters
    add(filter as Component)
  }

  fun removeContainerFilter(filter: DataCommunicator.Filter<Any>) {
    remove(filter as Component)
  }

  fun removeAllContainerFilters() {
    removeAllContainerFilters()
  }

  fun addContainerFilter(propertyId: Any?,
                         filterString: String?,
                         ignoreCase: Boolean,
                         onlyMatchPrefix: Boolean) {
    try {
      addContainerFilter(ListFilter(propertyId, filterString!!, ignoreCase, onlyMatchPrefix))
    } catch (e: Exception) {
      // the filter instance created here is always valid for in-memory containers
    }
  }

  fun removeContainerFilters(propertyId: Any?) {
    removeContainerFilters(propertyId)
  }

  fun fireItemSetChange() {
    //super.fireEvent(this)
  }

  /**
   * Returns true is the data source contains installed filters.
   * @return True is the data source contains installed filters.
   */
  fun hasFilters(): Boolean {
    return !containerFilters.isEmpty()
  }

  /**
   * Looks for the item ID that its first property starts with the given pattern.
   * @param pattern The search pattern.
   * @return The found item ID or null if no item corresponds the the wanted pattern.
   */
  fun search(pattern: String?): Any? {
    val i: Iterator<Int> = allItemIds.iterator()
    while (i.hasNext()) {
      val id = i.next()
      if (startsWith(id, pattern)) {
        return id
      }
    }
    return null
  }

  /**
   * Looks if the first property of the container starts with the given pattern at the given item ID.
   * @param itemId The item ID.
   * @param pattern The search pattern
   * @return true if the first property of the container starts with the given pattern.
   */
  fun startsWith(itemId: Any?, pattern: String?): Boolean {
    val property: ListProperty? = item.getItemProperty(propertyIds[0])
    val propertyValue: Any? = property.getValue()
    val value: String = property.formatObject(propertyValue).toLowerCase()
    val item = getUnfilteredItem(itemId)

    if (property == null) {
      return false
    }
    if (propertyValue == null) {
      return false
    }
    return if (!value.contains(pattern!!)) {
      false
    } else true
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var filters: MutableSet<Filter?>? = HashSet<Filter>()

  val containerFilters: Collection<Any> = TODO()
  private val model: VListDialog = model
  private val propertyIds = Utils.buildIdList(model.getColumnCount())
  protected val allItemIds = Utils.buildIdList(model.count)
  val sortableContainerPropertyIds: Collection<*>
}
