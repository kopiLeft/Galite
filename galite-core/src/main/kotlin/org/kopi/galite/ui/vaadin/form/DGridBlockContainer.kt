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
package org.kopi.galite.ui.vaadin.form

import java.util.ArrayList

import kotlin.reflect.KClass

import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField
import org.kopi.galite.ui.vaadin.base.Utils

import com.vaadin.flow.data.binder.HasItemComponents
import com.vaadin.flow.data.provider.InMemoryDataProvider
import com.vaadin.flow.dom.Element

/**
 * Data source container for grid block
 * TODO: Implement it, it shouldn't be abstract
 */
abstract class DGridBlockContainer(
        private val model: VBlock
) : InMemoryDataProvider<DGridBlockContainer.GridBlockItem?>
/*Sortable,
Filterable,
SimpleFilterable TODO */ {

  private val propertyIds: MutableList<Int>
  protected val allItemIds: List<Int>
  private val deletedRecordFilter = DeletedRecordFilter(model)

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  init {
    propertyIds = ArrayList()
    for (i in 0 until model.getFieldCount()) {
      if (!model.fields[i].isInternal() && !model.fields[i].noChart()) {
        propertyIds.add(i)
      }
    }
    allItemIds = Utils.buildIdList(model.bufferSize)
    // redefine sort strategy for empty records
    //setSortOrder(DGridBlockItemSorter(model)) TODO
    // filters deleted records
    //addFilter(deletedRecordFilter) TODO
  }

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------

  protected fun getUnfilteredItem(itemId: Any): GridBlockItem {
    return GridBlockItem(itemId as Int)
  }

  /**
   * Notifies registered listeners that content has changed.
   */
  fun fireContentChanged() {
    // TODO
  }

  fun sort(propertyId: Array<Any?>?, ascending: BooleanArray?) {
    // TODO
  }

  fun removeAllContainerFilters() {
    // TODO
  }

  fun size(): Int {
    TODO()
  }

  /**
   * This is a workaround for not changed records that contains non deleted records
   * but records are not really marked filled in the block since they are not changed
   * or fetched.
   * @return If the block model is filled but not marked as changed or fetched.
   */
  protected val isReallyFilled: Boolean
    get() = TODO()

  fun doSort() {
    // TODO
  }

  // --------------------------------------------------
  // INNER CLASSES
  // --------------------------------------------------
  /**
   * Grid block data source item
   */
  class GridBlockItem(private val record: Int) : HasItemComponents.ItemComponent<GridBlockItem> {
    override fun getElement(): Element {
      TODO("Not yet implemented")
    }

    override fun getItem(): GridBlockItem {
      TODO("Not yet implemented")
    }
    // TODO
  }

  /**
   * Grid Block data source property.
   */
  class GridBlockProperty(private val record: Int, private val field: VField) /*: Property<Any?> TODO */ {
    // not used, parse displayed value to set the model value
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    var value: Any?
      get() = this.field.getObject(record)
      set(newValue) {
        // not used, parse displayed value to set the model value
      }

    val type: KClass<out Any>
      get() = this.field.getDataType()

    var isReadOnly: Boolean
      get() = this.field.isNoEdit()
      set(newStatus) {}

    /**
     * Formats an object according to the property nature.
     * @param o The object to be formatted.
     * @return The formatted property object.
     */
    fun formatObject(o: Any?): String? {
      return field.toText(o)
    }

    override fun toString(): String {
      return formatObject(field.getObject(record)).orEmpty()
    }

  }

  /**
   * Deleted Record Filter.
   */
  class DeletedRecordFilter(private val model: VBlock) /*: Filter TODO*/ {
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------

    fun appliesToProperty(propertyId: Any?): Boolean = true

  }
}
