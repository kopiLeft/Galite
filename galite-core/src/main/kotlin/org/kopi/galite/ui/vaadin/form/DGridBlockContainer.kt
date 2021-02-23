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

import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField
import org.kopi.galite.ui.vaadin.base.Utils
import java.util.ArrayList
import kotlin.reflect.KClass

/**
 * Data source container for grid block
 */
class DGridBlockContainer(
        private val model: VBlock
) : AbstractInMemoryContainer<Int?, String?, GridBlockItem?>(), Sortable, Filterable, SimpleFilterable {

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  val containerPropertyIds: Collection<*>
    get() = Collections.unmodifiableCollection<Int>(propertyIds)

  fun getContainerProperty(itemId: Any, propertyId: Any?): Property {
    return GridBlockProperty(itemId as Int, model.getFields().get(propertyId as Int?))
  }

  fun getType(propertyId: Any?): Class<*> {
    return if (model.fields.get(propertyId as Int?).getDataType() === ByteArray::class.java) {
      Resource::class.java
    } else {
      String::class.java
    }
  }

  protected fun getUnfilteredItem(itemId: Any): GridBlockItem {
    return GridBlockItem(itemId as Int)
  }

  /**
   * Notifies registered listeners that content has changed.
   */
  fun fireContentChanged() {
    doFilterContainer(!getFilters().isEmpty())
    fireItemSetChange()
  }

  /**
   * Returns true is the data source contains installed filters.
   * @return True is the data source contains installed filters.
   */
  fun hasFilters(): Boolean {
    return !getFilters().isEmpty()
  }

  protected fun fireContainerPropertySetChange() {
    super.fireContainerPropertySetChange()
  }

  fun sort(propertyId: Array<Any?>?, ascending: BooleanArray?) {
    sortContainer(propertyId, ascending)
  }

  val sortableContainerPropertyIds: Collection<*>
    get() = getSortablePropertyIds()

  fun addContainerFilter(filter: Filter?) {
    addFilter(filter)
  }

  fun removeContainerFilter(filter: Filter?) {
    removeFilter(filter)
  }

  fun removeAllContainerFilters() {
    removeAllFilters()
    addFilter(deletedRecordFilter)
  }

  val containerFilters: Collection<Any>
    get() = super.getContainerFilters()

  fun addContainerFilter(
          propertyId: Any?,
          filterString: String,
          ignoreCase: Boolean,
          onlyMatchPrefix: Boolean
  ) {
    try {
      addFilter(DGridBlockFilter(propertyId, filterString, ignoreCase, onlyMatchPrefix))
    } catch (e: UnsupportedFilterException) {
      // the filter instance created here is always valid for in-memory containers
    }
  }

  fun removeContainerFilters(propertyId: Any?) {
    removeFilters(propertyId)
    addFilter(deletedRecordFilter)
  }

  fun size(): Int {
    return if (isReallyFilled) {
      super.size()
    } else if (!isFiltered()) {
      model.numberOfValidRecord
    } else {
      super.size()
    }
  }

  /**
   * This is a workaround for not changed records that contains non deleted records
   * but records are not really marked filled in the block since they are not changed
   * or fetched.
   * @return If the block model is filled but not marked as changed or fetched.
   */
  protected val isReallyFilled: Boolean
    protected get() = model.numberOfFilledRecords == 0 && model.numberOfValidRecord == 0 && super.size() > 0

  /*
   * Grid class declares an ItemSetChangeListener that cancels the editor
   * when #fireItemSetChange() is called. This will close editors widgets
   * and any reference done to the editors connectors is lost. To avoid
   * this side effect, we will not add the grid listener to do not cancel
   * the editor if an item set change event is fired. 
   */
  fun addItemSetChangeListener(listener: ItemSetChangeListener) {
    if (!Grid::class.java.isAssignableFrom(listener.getClass().getEnclosingClass())) {
      super.addItemSetChangeListener(listener)
    }
  }

  fun removeItemSetChangeListener(listener: ItemSetChangeListener) {
    if (!Grid::class.java.isAssignableFrom(listener.getClass().getEnclosingClass())) {
      super.removeItemSetChangeListener(listener)
    }
  }

  fun doSort() {
    super.doSort()
  }
  // --------------------------------------------------
  // INNER CLASSES
  // --------------------------------------------------
  /**
   * Grid block data source item
   */
  inner class GridBlockItem(private val record: Int) : Item {
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    fun getItemProperty(id: Any): Property<*> {
      return GridBlockProperty(record, model.fields[id as Int])
    }

    val itemPropertyIds: Collection<*>
      get() = containerPropertyIds

    @Throws(UnsupportedOperationException::class)
    fun addItemProperty(id: Any?, property: Property?): Boolean {
      return false
    }

    @Throws(UnsupportedOperationException::class)
    fun removeItemProperty(id: Any?): Boolean {
      return false
    }
  }

  /**
   * Grid Block data source property.
   */
  class GridBlockProperty(private val record: Int, private val field: VField) : Property<Any?> {
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
    fun formatObject(o: Any?): String {
      return field.toText(o)
    }

    override fun toString(): String {
      return formatObject(field.getObject(record))
    }

  }

  /**
   * Deleted Record Filter.
   */
  class DeletedRecordFilter(
          private val model: VBlock
  ) : Filter {
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    fun passesFilter(itemId: Any?, item: Item?): Boolean {
      return !model.isRecordDeleted(itemId as Int?)
    }

    fun appliesToProperty(propertyId: Any?): Boolean {
      return true
    }

  }

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
    setItemSorter(DGridBlockItemSorter(model))
    // filters deleted records
    addFilter(deletedRecordFilter)
  }
}
