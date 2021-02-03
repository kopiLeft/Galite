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

import com.vaadin.flow.component.html.Div

/**
 * The field component. Contains one text input field or other component
 * like image field.
 *
 * @param hasIncrement has increment button ?
 * @param hasDecrement has decrement button ?
 * TODO: Implement this class with appropriate component
 */
open class Field(hasIncrement: Boolean, hasDecrement: Boolean) : Div() {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the text input widget.
   * @param textField The input widget.
   */
  open fun setTextField(textField: TextField) {
    addComponentAsFirst(textField) // add it at first position.
  }

  /**
   * Sets the default access of the field.
   * @param defaultAccess The field default access.
   */
  fun setDefaultAccess(defaultAccess: Int) {
    TODO()
  }

  /**
   * Sets the dynamic access of the field.
   * @param dynAccess the dynamic access of the field.
   */
  fun setDynAccess(dynAccess: Int) {
    TODO()
  }

  /**
   * Sets the field to be visible in chart view.
   * @param noChart The visibility in chart view.
   */
  fun setNoChart(noChart: Boolean) {
    TODO()
  }

  /**
   * Sets the field to be visible in detail view.
   * @param noDetail The visibility in detail view.
   */
  fun setNoDetail(noDetail: Boolean) {
    TODO()
  }

  /**
   * Sets the position of the field in the chart layout.
   * @param position The position of the field.
   */
  open fun setPosition(position: Int) {
    TODO()
  }

  /**
   * Sets the column view index of this field.
   * @param index The column view index.
   */
  fun setIndex(index: Int) {
    TODO()
  }

  /**
   * Adds the given actors to this field.
   * @param actors The actors to be associated with field.
   */
  fun addActors(actors: Collection<Actor?>?) {
    TODO()
  }

  /**
   * Sets the field to has an action trigger.
   * @param hasAction The field has an action trigger ?
   */
  fun setHasAction(hasAction: Boolean) {
    TODO()
  }

  /**
   * Sets the action trigger to be enabled/disabled on this field.
   * @param isActionEnabled Is the action enabed ?
   */
  fun setActionEnabled(isActionEnabled: Boolean) {
    TODO()
  }

  /**
   * Sets this field to has a PREFLD trigger.
   * @param hasPreFieldTrigger The PREFDL trigger flag.
   */
  fun setHasPreFieldTrigger(hasPreFieldTrigger: Boolean) {
    TODO()
  }

  /**
   * Registers a field listener.
   * @param l The listener to be registered.
   */
  fun addFieldListener(l: FieldListener) {
    TODO()
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

  /**
   * Sets the field visible height.
   * @param visibleHeight The visible height.
   */
  fun setVisibleHeight(visibleHeight: Int) {
    TODO()
  }

  val isConnectorEnabled: Boolean
    get() = true

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val listeners = mutableListOf<FieldListener>()
}
