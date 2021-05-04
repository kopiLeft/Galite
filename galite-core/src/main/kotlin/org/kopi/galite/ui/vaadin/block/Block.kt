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
package org.kopi.galite.ui.vaadin.block

import java.io.Serializable

import org.kopi.galite.form.BlockListener
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VConstants
import org.kopi.galite.ui.vaadin.field.CheckTypeException
import org.kopi.galite.ui.vaadin.form.DBlock
import org.kopi.galite.ui.vaadin.form.Form
import org.kopi.galite.ui.vaadin.form.Page
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.notif.NotificationUtils

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.dnd.DragSource
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * The component of a simple block.
 * This UI component supports only laying components for simple
 * layout view.
 */
abstract class Block(private val droppable: Boolean) : VerticalLayout(), HasEnabled {

  /** The block layout. */
  var layout: BlockLayout? = null
  var caption: H4? = null
  private val listeners = mutableListOf<BlockListener>()
  private var fields = mutableListOf<ColumnView>()
  private var dndWrapper: DragSource<*>? = null
  var activeField: ColumnView? = null
  var detailMode = false
  private var oldActiveRecord = 0
  //cached infos
  protected var sortedToprec = 0 // first record displayed
  protected lateinit var sortedRecToDisplay: IntArray
  protected lateinit var displayToSortedRec: IntArray
  private var initialized = false
  private var doNotUpdateScrollPosition = false
  /**
   * Some browsers fires extra scroll event with wrong scroll position
   * when a chart block field is clicked. This flag is used to prevent
   * these events from propagation to the block UI and thus block view refresh
   * with wron top scroll record.
   */
  private var activeRecordSetFromDisplay = false

  /**
   * true if the layout of this block belongs to the grid row detail.
   */
  var isLayoutBelongsToGridDetail = false

  /**
   * Is the animation enabled for block view switch?
   */
  var isAnimationEnabled = false

  /**
   * The scroll page size.
   */
  var scrollPageSize = 0

  /**
   * The max scroll value.
   */
  var maxScrollValue = 0

  /**
   * Should we enable scroll bar ?
   */
  var enableScroll = false

  /**
   * The scroll value
   */
  var scrollValue = 0

  /**
   * The no move option for the block
   */
  var noMove = false

  /**
   * The model active record of this block.
   */
  var activeRecord = -1
    get() = if (field in 0 until bufferSize) field else -1
    set(value) {
      if (initialized) {
        if (isMulti() || value == 0) {
          field = value
        }
        fireActiveRecordChanged()
        refresh(false)
      }
    }

  /**
   * The sorted records of this block.
   */
  private var sortedRecords = IntArray(0)

  open fun setSortedRecords(sortedRecords: IntArray) {
    this.sortedRecords = sortedRecords
    if (initialized) {
      refresh(true)
    }
  }

  /**
   * The block records info changes buffer.
   */
  lateinit var recordInfo: IntArray

  /**
   * The cached field colors per record.
   */
  var cachedColors: MutableList<CachedColor> = mutableListOf()
    set(value) {
      field = value
      for (cachedColor in value) {
        setCachedColors(cachedColor.col, cachedColor.rec, cachedColor.foreground, cachedColor.background)
      }
    }

  /**
   * A serializable record info structure to be communicated
   * to the client side using the shared state mechanism
   *
   * @param rec The record number.
   * @param value The record info value.
   */
  class RecordInfo(var rec: Int, var value: Int) : Serializable {

    override fun equals(other: Any?): Boolean {
      return if (other is RecordInfo) {
        rec == other.rec && value == other.value
      } else {
        super.equals(other)
      }
    }

    override fun hashCode(): Int {
      return rec + value
    }
  }

  /**
   * A serializable cached value structure to be passed to the
   * client side using the shared state mechanism.
   *
   * @param col The column index.
   * @param rec The record number.
   * @param value The cached value.
   */
  class CachedValue(var col: Int, var rec: Int, value: String?) : Serializable {

    /**
     * The cached value.
     */
    val value = value ?: ""

    /**
     *
     * @param other : cached value
     * @return true if there is an existing cached value having the same key
     */
    fun hasSameKey(other: CachedValue): Boolean {
      return col == other.col && rec == other.rec
    }

    override fun hashCode(): Int {
      return col + rec + value.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
      return if (obj is CachedValue) {
        col == obj.col && rec == obj.rec && value == obj.value
      } else {
        super.equals(obj)
      }
    }
  }

  /**
   * A serializable cached color structure to be passed to the
   * client side using the shared state mechanism.
   *
   * @param col The column index.
   * @param rec The record number.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  class CachedColor(var col: Int, var rec: Int, foreground: String?, background: String?) : Serializable {

    /**
     * The foreground color
     */
    val foreground = foreground ?: ""

    /**
     * The background color
     */
    val background = background ?: ""

    override fun equals(obj: Any?): Boolean {
      return if (obj is CachedColor) {
        col == obj.col
                && rec == obj.rec
                && foreground == foreground
                && background == obj.background
      } else {
        super.equals(obj)
      }
    }

    override fun hashCode(): Int {
      return col + col + foreground.hashCode() + background.hashCode()
    }
  }

  /**
   * A color pair composed of a foreground and a background color.
   *
   * @param foreground The foreground color.
   * @param background The background color.
   */
  class ColorPair(var foreground: String?, var background: String?) : Serializable

  /**
   * The block buffer size.
   */
  var bufferSize = 0

  /**
   * The block display size.
   */
  var displaySize = 0

  /**
   * No chart option. Sets the block to not display the chart view even if it is a multi block.
   */
  var noChart = false

  /**
   * The block fields values per record.
   */
  var cachedValues: MutableList<CachedValue> = mutableListOf()
    set(value) {
      field = value
      for (cachedValue in value) {
        setCachedValue(cachedValue.col, cachedValue.rec, cachedValue.value)
      }
    }

  init {
    className = Styles.BLOCK
  }

  /**
   * Sets the block title.
   * @param title The block title.
   */
  fun setTitle(title: String?) {
    element.setAttribute("title", title)
    // TODO
  }

  /**
   * Switches the block view.
   * Switch is only performed when it is a multi block.
   * @param detail Should be switch to the detail view ?
   */
  fun switchView(detail: Boolean) {
    if (layout is MultiBlockLayout) {
      (layout as MultiBlockLayout).switchView(detail)
    }
    detailMode = detail
  }

  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enable is the scroll bar enabled ?
   * @param value The scroll position.
   */
  fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int) {
    // TODO
  }

  /**
   * Adds a component to this block.
   * @param component The component to be added.
   * @param x the x position.
   * @param y The y position.
   * @param width the column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll Use all available area ?
   */
  fun addComponent(
          component: Component?,
          x: Int,
          y: Int,
          width: Int,
          height: Int,
          alignRight: Boolean,
          useAll: Boolean,
  ) {
    buildLayout()
    layout!!.addComponent(component, x, y, width, height, alignRight, useAll)
  }

  /**
   * Builds the block layout.
   */
  fun buildLayout() {
    if (layout == null) {
      layout = createLayout()
      if (droppable) {
        //dndWrapper = DragAndDropWrapper(layout) TODO
        //dndWrapper.setImmediate(true)
        //setContent(dndWrapper)
        setContent(layout as Component)
      } else {
        setContent(layout as Component)
      }
    }
  }


  /**
   * Appends the given field to the block field list.
   * @param field The field to be added to block fields.
   */
  open fun addField(field: ColumnView) {
    if (field.index != -1) {
      if (field.index > fields.size) {
        fields.add(field)
      } else {
        fields.add(field.index, field)
      }
    }
  }

  /**
   * Registers a new block listener.
   * @param l The listener to be registered.
   */
  fun addBlockListener(l: BlockListener) {
    listeners.add(l)
  }

  /**
   * Removes a registered block listener.
   * @param l The listener to be removed.
   */
  fun removeBlockListener(l: BlockListener) {
    listeners.add(l)
  }

  /**
   * Fired when the scroll position has changed.
   * @param value The new scroll position.
   */
  protected fun fireOnScroll(value: Int) {
    // TODO
  }

  /**
   * Fired when the active record is changed from the client side.
   * @param record The new active record.
   * @param sortedTopRec The top sorted record.
   */
  protected fun fireOnActiveRecordChange(record: Int, sortedTopRec: Int) {
    // TODO
  }

  /**
   * Sends the active record to the client side.
   * @param record The new active record.
   */
  protected open fun fireActiveRecordChanged(record: Int) {
    activeRecord = record
  }

  /**
   * Send the new records order to the client side.
   * @param sortedRecords The new record orders.
   */
  protected fun fireOrderChanged(sortedRecords: IntArray) {
    this.sortedRecords = sortedRecords
  }

  /**
   * Sends the new records info to client side.
   * @param rec The record number.
   * @param info The record info value.
   */
  protected open fun fireRecordInfoChanged(rec: Int, info: Int) {
    recordInfo[rec] = info
    // TODO
    // forces the display after a record info update
    refresh(true)
  }

  /**
   * Notify the client side that the value of the record has changed.
   * @param col The column index.
   * @param rec The record number.
   * @param value The new record value.
   */
  internal open fun fireValueChanged(col: Int, rec: Int, value: String?) {
    val existingValue: CachedValue?

    val cachedValue = CachedValue(col, rec, value)
    existingValue = isAlreadyCached(cachedValue)
    if (existingValue != null) {
      cachedValues.remove(existingValue)
    }
    cachedValues.add(cachedValue)
  }

  /**
   *
   * @param cachedValue : cached value
   * @return the existing cached value if it exists
   */
  protected open fun isAlreadyCached(cachedValue: CachedValue?): CachedValue? {
    for (value in cachedValues) {
      if (value.hasSameKey(cachedValue!!)) {
        return value
      }
    }
    return null
  }

  /**
   * Sets the cached values of the block fields.
   */
  private fun setCachedValues() {
    for (cachedValue in cachedValues) {
      setCachedValue(cachedValue.col, cachedValue.rec, cachedValue.value)
    }
  }

  /**
   * Sets the cached values for the given column.
   * @param column The column view number.
   * @param rec The record number.
   * @param value The column value.
   */
  private fun setCachedValue(column: Int, rec: Int, value: String) {
    val field = fields[column]
    if (field != null) {
      field.setValueAt(rec, value)
      field.updateValue(rec)
    }
  }

  /**
   * Sets the block content.
   * @param content The block content.
   */
  protected open fun setContent(content: Component) {
    removeAll()
    add(content)
  }

  /**
   * Sets the block caption.
   * @param caption The block caption.
   * @param maxColumnPos The maximum column position.
   */
  protected open fun setCaption(caption: String?) {
    if (caption == null || caption.isEmpty()) {
      return
    }
    val page: Page<*>? = parentPage
    this.caption = H4(caption)
    this.caption!!.className = "block-title"
    page?.setCaption(this)
  }

  /**
   * The parent block page.
   */
  val parentPage: Page<*>?
    get() = parent.orElseGet { null } as? Page<*>

  /**
   * Layout components Creates the content of the block.
   */
  protected open fun layout() {
    // create detail block view.
    layout?.layout()
  }

  open fun clear() {
    super.removeAll()
    if (layout != null) {
      try {
        layout!!.removeAll()
      } catch (e: IndexOutOfBoundsException) {
        // ignore cause it can be cleared before
      }
      layout = null
    }
    caption = null
  }

  open fun delegateCaptionHandling(): Boolean {
    return false
  }

  open fun updateCaption(connector: Component?) {
    // not handled.
  }

  open fun  updateScroll() {
    if (layout != null) {
      layout!!.updateScroll(scrollPageSize,
                            maxScrollValue,
                            enableScroll,
                            scrollValue)
    }
  }

  open fun  setDisplaySize() {
    detailMode = displaySize == 1
  }

  open fun updateActiveRecord() {
    if (initialized) {
      activeRecord = activeRecord
      fireActiveRecordChanged()
      refresh(false)
    }
  }

  fun setBufferSize(bufferSize: Int, displaySize: Int) {
    this.bufferSize = bufferSize
    this.displaySize = displaySize
    recordInfo = IntArray(2 * bufferSize)
    if (isMulti()) {
      sortedRecords = IntArray(bufferSize)
      activeRecord = -1
      sortedRecToDisplay = IntArray(bufferSize)
      displayToSortedRec = IntArray(displaySize)
    } else {
      sortedRecords = IntArray(1)
      activeRecord = 0
      sortedRecToDisplay = IntArray(1)
      displayToSortedRec = IntArray(1)
    }
    // create the default order
    for (i in sortedRecords.indices) {
      sortedRecords[i] = i
    }
    addRecordPositionPanel()
    // build the cache buffers
    rebuildCachedInfos()
    initialized = true
  }

  /**
   * Sets the cached colors of a given column view.
   * @param column The column index.
   * @param rec The record number.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  private fun setCachedColors(column: Int, rec: Int, foreground: String, background: String) {
    val field = fields[column]
    if (field != null) {
      field.setForegroundColorAt(rec, foreground)
      field.setBackgroundColorAt(rec, background)
      field.updateColor(rec)
    }
  }

  /**
   * Handles the content widget.
   */
  protected open fun handleContentComponent() {
    // TODO
  }

  /**
   * Sets the block content.
   */
  protected open fun setContent() {
    // TODO
  }

  /**
   * Returns the column view of the given index.
   * @param index The column view index.
   * @return The column view.
   */
  protected open fun getField(index: Int): ColumnView? {
    return if (index >= fields.size) {
      null
    } else {
      fields[index]
    }
  }

  /**
   * Sets the ability of the hole block actors for column views.
   * @param enabled The actors ability.
   */
  open fun setColumnViewsActorsEnabled(enabled: Boolean) {
    for (field in fields) {
      field.setActorsEnabled(enabled)
    }
  }

  open fun onValueChange(event: HasValue.ValueChangeEvent<Int>) {
    // when active record is set from the display. It means that
    // one block field has fired a click event. Extra scroll event
    // are blocked cause some FF version fire scroll events with
    // wrong scroll position and force UI to refresh and change its
    // content.
    if (activeRecordSetFromDisplay) {
      activeRecordSetFromDisplay = false
    } else {
      setScrollPos(event.value)
    }
  }

  /**
   * Called when a column view is unregistered from a grid row detail.
   * We need to clear block fields cause layout was unregistered
   */
  protected open fun clearFields() {
    if (isLayoutBelongsToGridDetail && fields != null) {
      fields.clear()
    }
  }

  //---------------------------------------------------
  // NAVIGATION
  //---------------------------------------------------
  /**
   * Navigates to the next field in this block.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  open fun gotoNextField() {
    if (activeField == null) {
      return
    }
    // first check if the navigation should be delegated to server side.
    if (activeField!!.delegateNavigationToServer() && activeField!!.getFieldListener() != null) {
      activeField!!.getFieldListener()!!.gotoNextField()
    } else {
      var index: Int = fields.indexOf(activeField)
      var target: ColumnView? = null
      val old = activeField
      var i = 0
      while (target == null && i < fields.size) {
        index += 1
        if (index == fields.size) {
          index = 0
        }
        if (fields[index] != null && !fields[index].hasAction()
                && fields[index].access >= VConstants.ACS_VISIT && (detailMode && !fields[index].noDetail()
                        || !detailMode && !fields[index].noChart())) {
          target = fields[index]
        }
        i += 1
      }
      if (target == null) {
        old!!.enter()
      } else if (target.hasPreFieldTrigger() && activeField!!.getFieldListener() != null) {
        // the target field has a PREFLD trigger
        // delegate navigation to server side.
        activeField!!.getFieldListener()!!.transferFocus()
        activeField!!.getFieldListener()!!.gotoNextField()
      } else {
        // leave the active field
        try {
          activeField!!.leave(activeRecord)
        } catch (e: CheckTypeException) {
          e.displayError()
          return
        }
        // focus to the new one
        target.enter()
      }
    }
  }

  /**
   * Navigates to the previous field in this block.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  open fun gotoPrevField() {
    if (activeField == null) {
      return
    }
    if (activeField!!.delegateNavigationToServer() && activeField!!.getFieldListener() != null) {
      activeField!!.getFieldListener()!!.gotoPrevField()
    } else {
      var index: Int = fields.indexOf(activeField)
      var target: ColumnView? = null
      val old = activeField
      var i = 0
      while (target == null && i < fields.size) {
        if (index == 0) {
          index = fields.size
        }
        index -= 1
        if (fields[index] != null && !fields[index].hasAction()
                && fields[index].access >= VConstants.ACS_VISIT && (detailMode && !fields[index].noDetail()
                        || !detailMode && !fields[index].noChart())) {
          target = fields[index]
        }
        i += 1
      }
      if (target == null) {
        old!!.enter()
      } else if (target.hasPreFieldTrigger() && activeField!!.getFieldListener() != null) {
        // the target field has a PREFLD trigger
        // delegate navigation to server side.
        activeField!!.getFieldListener()!!.transferFocus()
        activeField!!.getFieldListener()!!.gotoPrevField()
      } else {
        // leave the active field
        try {
          activeField!!.leave(activeRecord)
        } catch (e: CheckTypeException) {
          e.displayError()
          return
        }
        // focus to the new one
        target.enter()
      }
    }
  }

  /**
   * Navigates to the next empty must fill field in this block.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  open fun gotoNextEmptyMustfill() {
    val current = activeField
    if (activeField != null && activeField!!.delegateNavigationToServer()) {
      if (activeField!!.getFieldListener() != null) {
        activeField!!.getFieldListener()!!.gotoNextEmptyMustfill()
      }
    } else {
      if (activeField != null) {
        try {
          activeField!!.leave(activeRecord)
        } catch (e: CheckTypeException) {
          e.displayError()
          return
        }
      } else {
        gotoFirstUnfilledField()
        return
      }
      var target: ColumnView? = null

      // found field
      var i = 0
      while (i < fields.size && fields.get(i) !== current) {
        i += 1
      }
      if (i >= fields.size) {
        return
      }
      i += 1
      // walk next to next
      while (target == null && i < fields.size) {
        if (fields[i] != null && !fields[i].hasAction()
                && fields[i].access == VConstants.ACS_MUSTFILL && fields[i].isNull) {
          target = fields[i]
        }
        i += 1
      }

      // redo from start
      i = 0
      while (target == null && i < fields.size) {
        if (fields[i] != null && fields[i].access == VConstants.ACS_MUSTFILL && fields[i].isNull) {
          target = fields[i]
        }
        i += 1
      }
      if (target == null) {
        gotoFirstUnfilledField()
      } else {
        target.enter()
      }
    }
  }

  /**
   * Navigates to the next record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  open fun gotoNextRecord() {
    if (activeField == null) {
      return
    }

    // first check if the navigation should be delegated to server side.
    if (isLayoutBelongsToGridDetail || activeField!!.delegateNavigationToServer() && activeField!!.getFieldListener() != null) {
      activeField!!.getFieldListener()!!.gotoNextRecord()
    } else {
      if (isMulti() && !detailMode) {
        var currentRec = activeRecord
        if (currentRec == -1) {
          return
        }

        // get position in sorted order
        currentRec = getSortedPosition(currentRec)
        /* search target record*/
        var i = currentRec + 1
        while (i < bufferSize) {
          if (!isSortedRecordDeleted(i)) {
            break
          }
          i += 1
        }
        if (i == bufferSize || !isRecordAccessible(getDataPosition(i))) {
          NotificationUtils.showError(null, this, MainWindow.locale, "00015")
        } else {
          // get position in data of next record in sorted order
          changeActiveRecord(getDataPosition(i))
        }
      } else if (activeField!!.getFieldListener() != null) {
        activeField!!.getFieldListener()!!.gotoNextRecord()
      }
    }
  }

  /**
   * Navigates to the previous record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  open fun gotoPrevRecord() {
    if (activeField == null) {
      return
    }
    // first check if the navigation should be delegated to server side.
    if (isLayoutBelongsToGridDetail || activeField!!.delegateNavigationToServer() && activeField!!.getFieldListener() != null) {
      activeField!!.getFieldListener()!!.gotoPrevRecord()
    } else {
      if (isMulti() && !detailMode) {
        var currentRec = activeRecord
        if (currentRec == -1) {
          return
        }

        // get position in sorted order
        currentRec = getSortedPosition(currentRec)
        /* search target record*/
        var i = currentRec - 1
        while (i >= 0) {
          if (!isSortedRecordDeleted(i)) {
            break
          }
          i -= 1
        }
        if (i == -1 || !isRecordAccessible(getDataPosition(i))) {
          NotificationUtils.showError(null, this, MainWindow.locale, "00015")
        } else {
          // get position in data of previous record in sorted order
          changeActiveRecord(getDataPosition(i))
        }
      } else if (activeField!!.getFieldListener() != null) {
        activeField!!.getFieldListener()!!.gotoPrevRecord()
      }
    }
  }

  /**
   * Navigates to the first record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  open fun gotoFirstRecord() {
    if (isLayoutBelongsToGridDetail || activeField == null) {
      return
    }
    // first check if the navigation should be delegated to server side.
    if (activeField!!.delegateNavigationToServer() && activeField!!.getFieldListener() != null) {
      activeField!!.getFieldListener()!!.gotoFirstRecord()
    } else {
      if (!isMulti() && activeField!!.getFieldListener() != null) {
        activeField!!.getFieldListener()!!.gotoFirstRecord()
      } else if (noMove()) {
        NotificationUtils.showError(null, this, MainWindow.locale, "00025")
      } else {
        if (activeRecord == -1) {
          return  //no active record we give up
        }
        val act = activeField
        /* search target record */
        var i = 0
        while (i < bufferSize) {
          if (!isSortedRecordDeleted(i)) {
            break
          }
          i += 1
        }
        if (i == bufferSize || !isRecordAccessible(i)) {
          NotificationUtils.showError(null, this, MainWindow.locale, "00015")
          return
        }
        try {
          leaveRecord()
        } catch (e: CheckTypeException) {
          e.displayError()
          return
        }
        enterRecord(i)
        act!!.enter()
        if (activeField!!.access < VConstants.ACS_VISIT || activeField!!.hasAction()) {
          gotoNextField()
        }
      }
    }
  }

  /**
   * Navigates to the lasts record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  open fun gotoLastRecord() {
    if (isLayoutBelongsToGridDetail || activeField == null) {
      return
    }
    // first check if the navigation should be delegated to server side.
    if (activeField!!.delegateNavigationToServer() && activeField!!.getFieldListener() != null) {
      activeField!!.getFieldListener()!!.gotoLastRecord()
    } else {
      if (!isMulti() && activeField!!.getFieldListener() != null) {
        activeField!!.getFieldListener()!!.gotoLastRecord()
      } else if (noMove()) {
        NotificationUtils.showError(null, this, MainWindow.locale, "00025")
      } else {
        if (activeRecord == -1) {
          return  //no active record we give up
        }
        val act = activeField
        /* search target record */
        var i = bufferSize + 1
        while (i >= 0) {
          if (!isSortedRecordDeleted(i)) {
            break
          }
          i -= 1
        }
        if (i == 0 || !isRecordAccessible(i)) {
          NotificationUtils.showError(null, this, MainWindow.locale, "00015")
          return
        }
        try {
          leaveRecord()
        } catch (e: CheckTypeException) {
          e.displayError()
          return
        }
        enterRecord(i)
        act!!.enter()
        if (activeField!!.access < VConstants.ACS_VISIT || activeField!!.hasAction()) {
          gotoNextField()
        }
      }
    }
  }

  /**
   * Changes the active record of this block.
   * @param rec The new active record.
   */
  protected open fun changeActiveRecord(rec: Int) {
    if (noMove()) {
      NotificationUtils.showError(null, this, MainWindow.locale, "00025")
    } else {
      var act: ColumnView?
      act = activeField
      val oldRecord = activeRecord
      try {
        if (oldRecord != -1) {
          leaveRecord()
        }
        enterRecord(rec)
        if (activeField != null) {
          act = activeField
          activeField!!.leave(activeRecord)
        }
        if (act == null || act.hasAction() || act.access < VConstants.ACS_VISIT) {
          gotoNextField()
        } else {
          act.enter()
        }
        fireActiveRecordChanged()
      } catch (e: CheckTypeException) {
        enterRecord(oldRecord)
        e.displayError()
      }
    }
  }

  /**
   * Leaves the active record.
   * @throws CheckTypeException
   */
  protected open fun leaveRecord() {
    if (isMulti() && activeRecord != -1) {
      if (activeField != null) {
        activeField!!.leave(activeRecord)
      }
      oldActiveRecord = activeRecord
    }
    activeField = null
    activeRecord = -1
  }

  /**
   * Enters to the given record.
   * @param recno The new record.
   */
  protected open fun enterRecord(recno: Int) {
    activeRecord = recno
    refresh(true)
  }

  /**
   * Refreshes the display of this block.
   */
  private fun refresh(force: Boolean) {
    var redisplay = false
    if (isLayoutBelongsToGridDetail || !isMulti()) {
      return
    }
    // row in view
    val recno: Int = if (activeRecord != -1) {
      getSortedPosition(activeRecord)
    } else {
      rebuildCachedInfos()
      for (field in fields) {
        field.scrollTo(sortedToprec)
      }
      return
    }
    if (recno < sortedToprec) {
      // record to be displayed is above screen => redisplay
      sortedToprec = recno

      // scroll some more, if there are some (non deleted) records
      var i = recno - 1
      var scrollMore = displaySize / 4
      while (scrollMore > 0 && i > 0) {
        // is there a non deleted record to see?
        if (!isSortedRecordDeleted(i)) {
          sortedToprec -= 1
          scrollMore--
        }
        i--
      }
      redisplay = true
    } else {
      var displine = 0
      var i = sortedToprec
      while (i < recno) {
        if (!isSortedRecordDeleted(i)) {
          displine += 1
        }
        i += 1
      }
      if (displine < displaySize) {
        // record should be visible => redisplay iff requested
        redisplay = force // do nothing
      } else {
        // scroll upwards until record is visible => redisplay
        do {
          if (!isSortedRecordDeleted(sortedToprec)) {
            displine -= 1
          }
          sortedToprec += 1
        } while (displine >= displaySize)

        // scroll some more, if there are some (non deleted) records
        var i = recno + 1
        var scrollMore = displaySize / 4
        while (scrollMore > 0 && i < bufferSize) {
          // is there a non deleted record to see?
          if (!isSortedRecordDeleted(i)) {
            sortedToprec += 1
            scrollMore--
          }
          i++
        }
        redisplay = true
      }
    }
    rebuildCachedInfos()
    if (redisplay) {
      for (field in fields) {
        field.scrollTo(sortedToprec)
      }
    }
    if (!doNotUpdateScrollPosition) {
      updateScrollbar()
    } else {
      doNotUpdateScrollPosition = false
    }
  }

  /**
   * Performs a scroll action.
   */
  protected open fun setScrollPos(value: Int) {
    var value = value
    if (value >= bufferSize) {
      return
    }
    doNotUpdateScrollPosition = true
    if (sortedToprec != value) {
      var recno = 0 //temp sortedToprec
      while (value > 0) {
        if (!isSortedRecordDeleted(recno)) {
          value--
        }
        recno++
      }
      if (noMove() || isSortedRecordDeleted(getDataPosition(recno))) {
        NotificationUtils.showError(null, this, MainWindow.locale, "00025")
      } else {
        var lastVisibleRec = recno
        var nbDisplay = displaySize - 1
        val activeRecord = activeRecord
        var inside = false // is active record still in the shown rows
        while (nbDisplay > 0) {
          if (!isSortedRecordDeleted(lastVisibleRec)) {
            nbDisplay--
          }
          if (activeRecord == getDataPosition(lastVisibleRec)) {
            // active record is still in the shown rows
            inside = true
          }
          lastVisibleRec += 1
        }
        sortedToprec = recno
        if (inside) {
          if (activeField != null) {
            activeField!!.updateValue()
          }
          refresh(true)
        } else {
          val nextRec = if (getSortedPosition(activeRecord) < recno) {
            getDataPosition(recno)
          } else {
            getDataPosition(lastVisibleRec)
          }
          if (noMove() || !isRecordAccessible(nextRec)) {
            NotificationUtils.showError(null, this, MainWindow.locale, "00025")
          } else {
            changeActiveRecord(nextRec)
          }
        }
      }
    }
  }

  /**
   * Navigates to the first unfilled field in this block.
   */
  protected open fun gotoFirstUnfilledField() {
    if (activeField != null) {
      try {
        activeField!!.leave(activeRecord)
      } catch (e: CheckTypeException) {
        e.displayError()
        return
      }
    }
    var target: ColumnView? = null
    var i = 0
    while (target == null && i < fields.size) {
      if ((fields[i] != null && !fields[i].hasAction()
                      && fields[i].access >= VConstants.ACS_VISIT)
              && fields[i].isNull) {
        target = fields[i]
      }
      i += 1
    }
    if (target == null) {
      gotoFirstField()
    } else {
      target.enter()
    }
  }

  /**
   * Navigates to the first field in this block.
   */
  protected open fun gotoFirstField() {
    if (activeField != null) {
      try {
        activeField!!.leave(activeRecord)
      } catch (e: CheckTypeException) {
        e.displayError()
        return
      }
    }
    var target: ColumnView? = null
    var i = 0
    while (target == null && i < fields.size) {
      if (fields[i] != null && !fields[i].hasAction()
              && fields[i].access >= VConstants.ACS_VISIT) {
        target = fields[i]
      }
      i += 1
    }
    target?.enter()
  }

  /**
   * Returns the old active record of this block.
   * @return The old active record of this block.
   */
  open fun getOldActiveRecord(): Int {
    return oldActiveRecord
  }

  /**
   * Sets the block active record from a given display line.
   * @param displayLine The display line.
   */
  open fun setActiveRecordFromDisplay(displayLine: Int) {
    activeRecordSetFromDisplay = true
    activeRecord = getRecordFromDisplayLine(displayLine)
    fireActiveRecordChanged()
    refresh(false)
  }

  /**
   * Returns `true` if the block is in detail mode.
   * @return `true` if the block is in detail mode.
   */
  open fun inDetailMode(): Boolean {
    return isMulti() && detailMode
  }

  /**
   * Cleans dirty fields. This means that all
   * values that were not sent to the server
   * will be sent from now.
   */
  open fun cleanDirtyValues(active: Block?, transferFocus: Boolean) {
    if (active == this) {
      if (activeField != null && getDisplayLine() >= 0) {
        if (activeRecord != -1) {
          if (!activeField!!.delegateNavigationToServer()) {
            try {
              activeField!!.checkValue(activeRecord)
            } catch (e: CheckTypeException) {
              e.displayError()
              return
            }
          } else {
            activeField!!.maybeHasDirtyValues(activeRecord)
          }
        }
        // tells the server side about the client active field.
        if (transferFocus) {
          activeField!!.transferFocus()
        }
      }
    }

    // send block dirty values
    for (field in fields) {
      if (field != null && field.isDirty) {
        field.cleanDirtyValues()
      }
    }
    if (activeField != null && activeRecord != -1) {
      // tells the server side about active record in client side
      sendActiveRecordToServerSide()
    }
  }

  /**
   * Sends the active record to the server side
   */
  open fun sendActiveRecordToServerSide() {
    sendActiveRecordToServerSide(activeRecord)
  }

  /**
   * Sends the active record to the server side
   * @param record The active record.
   */
  open fun sendActiveRecordToServerSide(record: Int) {
    if (isMulti() && activeRecord != record) {
      updateActiveRecord(record, sortedToprec)
    }
  }

  /**
   * Returns `true` if this block can display more than one record.
   * @return `true` if this block can display more than one record.
   */
  open fun isMulti(): Boolean {
    return bufferSize > 1
  }

  /**
   * Returns `true` if this block display only one record.
   * @return `true` if this block display only one record.
   */
  open fun noChart(): Boolean {
    return noChart
  }

  /**
   * Returns `true` if the block do not allow record move.
   * @return `true` if the block do not allow record move.
   */
  open fun noMove(): Boolean {
    return noMove
  }

  /**
   * Returns the sorted position of the given record.
   * @param rec The concerned record.
   * @return The sorted position of the record.
   */
  open fun getSortedPosition(rec: Int): Int {
    if (!isMulti()) {
      return rec
    }
    for (i in sortedRecords.indices) {
      if (sortedRecords[i] == rec) {
        return i
      }
    }
    return -1
  }

  /**
   * Returns the data position of the given record.
   * @param rec The concerned record.
   * @return The data position.
   */
  open fun getDataPosition(rec: Int): Int {
    return if (!isMulti() || rec == -1) {
      rec
    } else {
      sortedRecords[rec]
    }
  }

  /**
   * Returns `true` if the specified record has been deleted.
   * @param sortedRec The record number.
   * @return `true` if the specified record has been deleted.
   */
  open fun isSortedRecordDeleted(sortedRec: Int): Boolean {
    return recordInfo[sortedRecords[sortedRec]] and VConstants.RCI_DELETED != 0
  }

  /**
   * Returns `true` if the specified record is filled.
   * @param sortedRec The record number.
   * @return `true` if the specified record is filled.
   */
  open fun isSortedRecordFilled(sortedRec: Int): Boolean {
    return !isSortedRecordDeleted(sortedRec) && (isSortedRecordFetched(sortedRec) || isSortedRecordChanged(sortedRec))
  }

  /**
   * Returns `true` if the specified record has been fetched from the database
   * @param sortedRec The record number.
   * @return `true` if the specified record has been fetched from the database
   */
  open fun isSortedRecordFetched(sortedRec: Int): Boolean {
    return recordInfo[sortedRecords[sortedRec]] and VConstants.RCI_FETCHED != 0
  }

  /**
   * Returns `true` if the specified record has been changed
   * @param sortedRec The record number.
   * @return `true` if the specified record has been changed
   */
  open fun isSortedRecordChanged(sortedRec: Int): Boolean {
    return recordInfo[sortedRecords[sortedRec]] and VConstants.RCI_CHANGED != 0
  }

  /**
   * Returns `true` if the given record is accessible.
   * @param rec The concerned record.
   * @return `true` if the given record is accessible.
   */
  open fun isRecordAccessible(rec: Int): Boolean {
    return !(rec < 0 || rec >= bufferSize)
  }

  /**
   * Sets the record to be fetched.
   * @param rec The record number.
   * @param value The fetch value.
   */
  open fun setRecordFetched(rec: Int, value: Boolean) {
    val oldValue = recordInfo[rec]

    // calculate new value
    val newValue = if (value) {
      oldValue or VConstants.RCI_FETCHED
    } else {
      oldValue and VConstants.RCI_FETCHED.inv()
    }
    if (newValue != oldValue) {
      // set record info
      recordInfo[rec] = newValue
      // inform listener that the number of rows changed
      updateScrollbar()
    }
  }

  /**
   * Sets the record to be changed.
   * @param rec The record number.
   * @param value The change value.
   */
  fun setRecordChanged(rec: Int, value: Boolean) {
    val oldValue = recordInfo[rec]

    // calculate new value
    val newValue = if (value) {
      oldValue or VConstants.RCI_CHANGED
    } else {
      oldValue and VConstants.RCI_CHANGED.inv()
    }
    if (newValue != oldValue) {
      // set record info
      recordInfo[rec] = newValue
      // inform listener that the number of rows changed
      updateScrollbar()
    }
  }

  /**
   * Returns the display line for the given record.
   * @param recno The record number.
   * @return The display line.
   */
  open fun getDisplayLine(recno: Int): Int {
    // if the block layout belongs to a grid row detail
    // display line is always 0 cause it acts like a simple
    // block even is buffer size is > 1
    if (isLayoutBelongsToGridDetail || noChart()) {
      return 0
    }
    if (recno < 0) {
      return -1
    }
    val pos = getSortedPosition(recno)
    return if (pos < 0) {
      -1
    } else sortedRecToDisplay!![pos]
  }

  /**
   * Returns the current display line.
   * @return The current display line.
   */
  open fun getDisplayLine(): Int {
    return getDisplayLine(activeRecord)
  }

  /**
   * Returns the record number from the display line.
   * @param displayLine The display line.
   * @return The record number.
   */
  open fun getRecordFromDisplayLine(displayLine: Int): Int {
    return getDataPosition(displayToSortedRec!![displayLine])
  }

  /**
   * Rebuilds cached information
   */
  protected open fun rebuildCachedInfos() {
    var cnt = 0
    var i = 0

    // sortedRecToDisplay
    while (i < sortedToprec) {
      sortedRecToDisplay!![i] = -1
      i++
    }
    while (cnt < displaySize && i < bufferSize) {

      // sortedRecToDisplay: view pos not real record number
      sortedRecToDisplay!![i] = if (isSortedRecordDeleted(i)) -1 else cnt++
      i++
    }
    while (i < bufferSize) {
      sortedRecToDisplay!![i] = -1
      i++
    }

    // displayToSortedRec
    cnt = sortedToprec
    i = 0
    while (i < displaySize) {
      while (cnt < bufferSize && isSortedRecordDeleted(cnt)) {
        cnt++
      }
      // the last one can be deleted too
      if (cnt < bufferSize) {
        displayToSortedRec!![i] = cnt++
      }
      i++
    }
  }

  /**
   * Updates the scroll bar position.
   */
  protected open fun updateScrollbar() {
    if (!isMulti() || displaySize >= bufferSize) {
      return
    }
    val validRecords = getNumberOfValidRecord()
    val dispSize = displaySize
    if (layout != null) {
      layout!!.updateScroll(dispSize,
                            validRecords,
                            validRecords > dispSize,
                            getNumberOfValidRecordBefore(getRecordFromDisplayLine(0)))
    }
  }

  /**
   * Returns the number of valid records.
   * @return The number of valid records.
   */
  protected open fun getNumberOfValidRecord(): Int {
    return getNumberOfValidRecord(bufferSize)
  }

  /**
   * Returns the number of valid record before block refresh.
   * @param recno The record number.
   * @return the number of valid record before block refresh.
   */
  protected open fun getNumberOfValidRecordBefore(recno: Int): Int {
    return getNumberOfValidRecord(getSortedPosition(recno))
  }

  /**
   * Returns the number of valid records in this block.
   * @param recno The record number.
   * @return The number of valid records in this block.
   */
  protected open fun getNumberOfValidRecord(recno: Int): Int {
    // don't forget to fireValidRecordNumberChanged if
    // the valid number is changed!!
    var count = 0
    var lastFilled = 0
    for (i in 0 until recno) {
      if (!isSortedRecordDeleted(i)) {
        // && (nonEmptyReached || isRecordFilled(i))) {
        count += 1
        if (isSortedRecordFilled(i)) {
          lastFilled = count
        }
      }
    }
    // currently only used by the scrollbar.
    // make the size of the scrollbar only so big, that the top
    // most row is filled, when the srcollbar is on the bottom
    count = count.coerceAtMost(lastFilled + displaySize - 1)
    return count // $$$ May be optimised
  }

  /**
   * Notifies the form that the active record of this block has changed.
   */
  protected open fun fireActiveRecordChanged() {
    ((this as DBlock).parent.content).setCurrentPosition(getSortedPosition(getCurrentRecord() - 1) + 1, getRecordCount())
  }

  /**
   * Returns the record count.
   * @return The record count.
   */
  protected open fun getRecordCount(): Int {
    var count = 0
    if (isMulti()) {
      for (i in 0 until bufferSize) {
        if (isSortedRecordFilled(i)) {
          count++
        }
      }
    }
    return count
  }

  /**
   * Returns the current record.
   * @return The current record.
   */
  protected open fun getCurrentRecord(): Int {
    var current = 1
    if (isMulti()) {
      current = activeRecord + 1
    }
    return current
  }

  fun onKeyPress(keyDownEvent: ShortcutEvent?) {
    if (isMulti() && noChart()) {
      (parent.get() as Form).showBlockInfo()
    }
  }


  open fun addRecordPositionPanel() {
    if (isMulti() && noChart()) {
      Shortcuts.addShortcutListener(this, ::onKeyPress, Key.KEY_I, KeyModifier.of("Alt"))
    }
  }

  fun updateScrollPos(value: Int) {
    fireOnScroll(value)
  }

  fun updateActiveRecord(record: Int, sortedTopRec: Int) {
    activeRecord = record
    fireOnActiveRecordChange(record, sortedTopRec)
  }


  fun clearCachedValues(cachedValues: List<CachedValue>) {
    for (cachedValue in cachedValues) {
      this.cachedValues.remove(cachedValue)
    }
  }

  fun clearCachedColors(cachedColors: List<CachedColor>) {
    for (cachedColor in cachedColors) {
      this.cachedColors.remove(cachedColor)
    }
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  /**
   * Creates the block layout.
   * @return The created block layout.
   */
  abstract fun createLayout(): BlockLayout?
}
