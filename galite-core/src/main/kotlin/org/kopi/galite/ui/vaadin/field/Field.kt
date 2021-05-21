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
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.customfield.CustomField
import com.vaadin.flow.component.html.Div
import org.kopi.galite.ui.vaadin.actor.Actor
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.block.Block
import org.kopi.galite.ui.vaadin.block.ColumnView
import org.kopi.galite.ui.vaadin.form.DBlock
import org.kopi.galite.ui.vaadin.form.DField
import org.kopi.galite.ui.vaadin.window.Window

/**
 * The field component. Contains one text input field or other component
 * like image field.
 *
 * @param hasIncrement has increment button ?
 * @param hasDecrement has decrement button ?
 * TODO: Implement this class with appropriate component
 */
abstract class Field(val hasIncrement: Boolean, val hasDecrement: Boolean)
  : Div(), FieldListener, HasStyle {

  private var listeners = mutableListOf<FieldListener>()

  /**
   * Has an action trigger ?
   */
  var hasAction = false

  /**
   * Is the action trigger enabled ?
   */
  var isActionEnabled = false

  /**
   * The field visibility
   *
   * TODO: Do wee need this or super.visible is fine.
   */
  var _visible = true

  /**
   * The visible field height needed to create layout.
   */
  var visibleHeight = 1

  /**
   * Tells if this field is never displayed in the detail view.
   */
  var noDetail = false

  /**
   * Tells if this field is never displayed in the chart view.
   */
  var noChart = false

  /**
   * The navigation delegation to server mode.
   */
  var navigationDelegationMode = NavigationDelegationMode.ALWAYS

  /**
   * Tells that this field has a PREFLD trigger. This will tell that
   * the navigation should be delegated to server if the next target
   * field has a PREFLD trigger.
   */
  var hasPreFieldTrigger = false

  /**
   * The actors associated with this field.
   */
  var actors = mutableListOf<Actor>()

  /**
   * The field dynamic access
   */
  var dynAccess = 0

  /**
   * The default field access.
   */
  var defaultAccess = 0

  /**
   * The position of the field for chart layout.
   */
  open var position = 0

  /**
   * The column view index of this field.
   */
  var index = 0

  var columnView: ColumnView? = null

  lateinit var wrappedField: AbstractField<*>

  /**
   * `true` if the content of this field has changed.
   */
  var isChanged = false

  /**
   * `true` if this connector is dirty.
   */
  var isDirty = false

  protected var dirtyValues: MutableMap<Int, String?>? = null

  /**
   * Enables and disables the leave action of the active field.
   */
  val doNotLeaveActiveField = false

  init {
    className = Styles.FIELD
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  fun setFieldContent(component: AbstractField<*>) {
    wrappedField = component
    wrappedField.addFocusListener {
      fireClicked()
    }
    add(component)
  }

  /**
   * Adds the given actors to this field.
   * @param actors The actors to be associated with field.
   */
  fun addActors(actors: Collection<Actor>) {
    this.actors.addAll(actors)
  }

  /**
   * Registers a field listener.
   * @param l The listener to be registered.
   */
  fun addFieldListener(l: FieldListener) {
    listeners.add(l)
  }

  /**
   * Removes a field listener.
   * @param l The listener to be removed.
   */
  fun removeFieldListener(l: FieldListener) {
    listeners.remove(l)
  }

  /**
   * Fires an increment action.
   */
  protected fun fireIncremented() {
    for (l in listeners) {
      l.onIncrement()
    }
  }

  /**
   * Fires an decrement action.
   */
  protected fun fireDecremented() {
    for (l in listeners) {
      l.onDecrement()
    }
  }

  /**
   * Fired when this field is clicked.
   */
  protected fun fireClicked() {
    for (l in listeners) {
      l.onClick()
    }
  }

  /**
   * Fired when the focus is transferred to this field.
   */
  protected fun fireFocusTransferred() {
    for (l in listeners) {
      l.transferFocus()
    }
  }

  /**
   * Fired when a navigation to the last record event is detected.
   */
  protected fun fireActionPerformed() {
    for (l in listeners) {
      l.fireAction()
    }
  }

  val isConnectorEnabled: Boolean
    get() = true


  //---------------------------------------------------
  // IMPLMENTATIONS
  //---------------------------------------------------
  /**
   * Initializes the field widget.
   * @param hasIncrement Has increment button ?
   * @param hasDecrement Has decrement button ?
   */
  open fun init(hasIncrement: Boolean, hasDecrement: Boolean) {
    /* TODO
    addDomHandler(object : ClickHandler() {
      fun onClick(event: ClickEvent?) {
        fireClicked()
      }
    }, ClickEvent.getType())*/
  }

  /**
   * Checks the content of this field.
   * @param rec The active record.
   * @throws CheckTypeException When the field content is not valid
   */
  open fun checkValue(rec: Int) {
    TODO()
  }

  /**
   * Checks if the content of this field is empty.
   * @return `true` if this field is empty.
   */
  open fun isNull(): Boolean = TODO()


  /**
   * Gains the focus on this field.
   */
  open fun focus() {
    wrappedField.focus()
  }

  open fun iniComponent() {
    init(hasIncrement, hasDecrement)
  }

  open fun delegateCaptionHandling(): Boolean {
    // do not delegate caption handling
    return false
  }

  open fun updateCaption(connector: Component?) {
    // not handled.
  }

  override fun onIncrement() {
    fireIncremented()
  }

  override fun onDecrement() {
    fireDecremented()
  }

  override fun transferFocus() {
    fireFocusTransferred()
  }

  /**
   * Tells the server side the the action field should be performed
   */
  open fun actionPerformed() {
    if (hasAction && isActionEnabled) {
      fireActionPerformed()
    }
  }

  /**
   * Leaves this field by performing validations that does not depend on server side.
   * @param rec The active record.
   * @throws CheckTypeException
   */
  open fun leave(rec: Int) {
    if (!columnView!!.isBlockActiveField) {
      throw AssertionError("wrong active field")
    }
    if (isChanged) {
      checkValue(rec)
    }
    if (!doNotLeaveActiveField) {
      columnView!!.unsetAsActiveField()
      setActorsEnabled(false)
      columnView!!.disableBlockActors()
    }
  }

  /**
   * Enters to this field. This will obtain the focus to this field.
   */
  open fun enter() {
    if (doNotLeaveActiveField) {
      return
    }
    if (columnView!!.blockActiveRecord == -1) {
      throw AssertionError("wrong active record")
    }
    if (!columnView!!.isBlockActiveFieldNull) {
      throw AssertionError("wrong active field")
    }
    isChanged = false
    focus()
    columnView!!.setAsActiveField()
    setActorsEnabled(true)
  }

  /**
   * Returns the field access for the given record.
   * @param record The concerned record.
   * @return The field access.
   */
  open fun getAccess(record: Int): Int {
    return if (record == -1) {
      defaultAccess
    } else {
      dynAccess
    }
  }

  /**
   * Checks if the navigation from this field should be delegated to server.
   * @return `true` if the navigation should be delegated to server.
   */
  open fun delegateNavigationToServer(): Boolean {
    return when (navigationDelegationMode) {
      NavigationDelegationMode.ALWAYS -> {
        true
      }
      NavigationDelegationMode.ONCHANGE -> {
        isChanged
      }
      NavigationDelegationMode.ONVALUE -> {
        !isNull() || isChanged
      }
      else -> {
        false
      }
    }
  }

  /**
   * Sets this field to not be a dirty one.
   */
  open fun unsetDirty() {
    isDirty = false
    isChanged = false
  }

  /**
   * Marks the value of this field to be dirty for its current value.
   * @param rec The concerned record number.
   */
  open fun markAsDirty(rec: Int) {
    markAsDirty(rec, if (wrappedField.value == null) "" else wrappedField.value.toString())
  }

  /**
   * Marks the given text field connector to be dirty.
   * This means that its value should be synchronized
   * with the server as soon as possible.
   * @param rec The value record.
   * @param value The text value to be sent for the given record
   */
  internal open fun markAsDirty(rec: Int, value: String?) {
    if (dirtyValues == null) {
      dirtyValues = HashMap()
    }
    if (rec != -1) {
      dirtyValues!![rec] = value
      // set internal cached value
      columnView!!.setValueAt(rec, value)
      isDirty = true
    }
  }

  /**
   * Sets the cached value of this field for the given record.
   * @param rec The record number.
   * @param value The text value.
   */
  protected open fun setCachedValueAt(rec: Int, value: String?) {
    if (!columnView!!.getRecordValueAt(rec).equals(value) && rec != -1) {
      columnView!!.setValueAt(rec, value)
      isChanged = true
    }
  }

  /**
   * Returns the field cached value at the given record.
   * @param rec The record number.
   * @return The cached value.
   */
  internal open fun getCachedValueAt(rec: Int): String? {
    return columnView!!.getRecordValueAt(rec)
  }

  /**
   * Sets the actors associated with this field to be enabled or disabled.
   * @param enabled The enabled status
   */
  open fun setActorsEnabled(enabled: Boolean) {
    val window = ((this as DField).model.blockView as DBlock).parent
    for (actor in actors) {
      window.setActorEnabled(actor, enabled)
    }
  }

  /**
   * Cleans the dirty values. This will send all buffered values
   * for the server side.
   */
  open fun cleanDirtyValues() {
    if (!isEnabled) {
      return
    }
    if (dirtyValues != null && dirtyValues!!.isNotEmpty()) {
      (wrappedField as TextField).sendTextToServer()
      (wrappedField as TextField).sendDirtyValuesToServer(HashMap(dirtyValues))
      dirtyValues!!.clear()
    }
    isDirty = false
  }

  /**
   * Updates the value of this field according to its position.
   */
  fun updateValue() {
    wrappedField.value = columnView!!.getValueAt(position)
    // wrappedField.updateValue() // TODO: Do we need this?
  }

  /**
   * Updates the color of this field according to its position.
   */
  open fun updateColor() {
    setColor(columnView!!.getForegroundColorAt(position),
             columnView!!.getBackgroundColorAt(position))
  }

  /**
   * Sets the field background and foreground colors.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  open fun setColor(foreground: String?, background: String?) {
    // TODO
  }

  /**
   * Returns the parent window of this field.
   * @return The parent window of this field.
   */
  protected open fun getWindow(): Window? = ((this as DField).model.blockView as DBlock).parent

  /**
   * Returns the parent block of this field.
   * @return The parent block of this field.
   */
  protected open fun getBlock(): Block? = (this as DField).model.blockView as Block

  /**
   * The navigation delegation to server mode.
   */
  enum class NavigationDelegationMode {
    /**
     * do not delegate navigation to server
     */
    NONE,

    /**
     * delegate navigation to server if the content of this field has changed
     */
    ONCHANGE,

    /**
     * delegate navigation to server side when the field is not empty.
     */
    ONVALUE,

    /**
     * Always delegate navigation to server.
     */
    ALWAYS
  }

  companion object {
    /**
     * Enables and disables the leave action of the active field.
     * This is used to simulate the modal popups that blocks the execution
     * Thread since Javascript can not handle multi thread and it is a single threaded.
     * @param doNotLeaveActiveField Should we leave the active field.
     */
    var doNotLeaveActiveField = false
  }
}
