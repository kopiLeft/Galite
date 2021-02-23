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

import org.kopi.galite.ui.vaadin.actor.Actor
import org.kopi.galite.ui.vaadin.block.ColumnView

import com.vaadin.flow.component.html.Div

/**
 * The field component. Contains one text input field or other component
 * like image field.
 *
 * @param hasIncrement has increment button ?
 * @param hasDecrement has decrement button ?
 * TODO: Implement this class with appropriate component
 */
open class Field(val hasIncrement: Boolean, val hasDecrement: Boolean) : Div() {
  private val listeners = mutableListOf<FieldListener>()

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

  var content: AbstractField? = null

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  fun setFieldContent(component: AbstractField) {
    content = component
    addComponentAsFirst(component)
  }

  /**
   * Updates the value of this field according to its position.
   */
  fun updateValue() {
    setValue(columnView!!.getValueAt(position))
  }

  /**
   * Sets the value of the this field.
   * @param o The field value.
   */
  fun setValue(o: Any?) {
    if (content != null) {
      content!!.value = o.toString().toIntOrNull() ?: o
    }
    // TODO
  }

  /**
   * Adds the given actors to this field.
   * @param actors The actors to be associated with field.
   */
  fun addActors(actors: Collection<Actor?>) {
    //TODO()
  }

  /**
   * Registers a field listener.
   * @param l The listener to be registered.
   */
  fun addFieldListener(l: FieldListener) {
    // TODO()
  }

  /**
   * Removes a field listener.
   * @param l The listener to be removed.
   */
  fun removeFieldListener(l: FieldListener) {
    TODO()
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
   * Fired when a navigation to the next field event is detected.
   */
  protected fun fireGotoNextField() {
    for (l in listeners) {
      l.gotoNextField()
    }
  }

  /**
   * Fired when a navigation to the previous field event is detected.
   */
  protected fun fireGotoPrevField() {
    for (l in listeners) {
      l.gotoPrevField()
    }
  }

  /**
   * Fired when a navigation to the next empty must fill field event is detected.
   */
  protected fun fireGotoNextEmptyMustfill() {
    for (l in listeners) {
      l.gotoNextEmptyMustfill()
    }
  }

  /**
   * Fired when a navigation to the next record event is detected.
   */
  protected fun fireGotoNextRecord() {
    for (l in listeners) {
      l.gotoNextRecord()
    }
  }

  /**
   * Fired when a navigation to the previous record event is detected.
   */
  protected fun fireGotoPrevRecord() {
    for (l in listeners) {
      l.gotoPrevRecord()
    }
  }

  /**
   * Fired when a navigation to the first event is detected.
   */
  protected fun fireGotoFirstRecord() {
    for (l in listeners) {
      l.gotoFirstRecord()
    }
  }

  /**
   * Fired when a navigation to the last record event is detected.
   */
  protected fun fireGotoLastRecord() {
    for (l in listeners) {
      l.gotoLastRecord()
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
}
