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
package org.kopi.galite.ui.vaadin.event

import java.lang.reflect.Method

import com.vaadin.flow.component.Component

/**
 * Server side implementation of the boolean field
 *
 * @param trueRepresentation The representation of the true value.
 * @param falseRepresentation The representation of the false false
 */
class BooleanField(trueRepresentation: String?, falseRepresentation: String?) : ObjectField() {

  /**
   * Returns the field value.
   * @return The field value.
   */
  var value: Boolean? = null
    private set

  init {
    setImmediate(true)
    state.trueRepresentation = trueRepresentation
    state.falseRepresentation = falseRepresentation
    registerRpc(object : BooleanFieldServerRpc() {
      fun valueChanged(value: Boolean) {
        setValue(value)
        fireValueChangeEvent(value)
      }
    })
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  protected val state: BooleanFieldState
    protected get() = super.getState() as BooleanFieldState

  /**
   * Sets the field value
   * @param value The field value
   */
  fun setValue(value: Boolean) {
    state.value = value
    this.value = value
  }

  fun beforeClientResponse(initial: Boolean) {
    super.beforeClientResponse(initial)
    state.value = value
  }

  /**
   * Sets the field label.
   * @param label The field label.
   */
  fun setLabel(label: String) {
    state.label = label
  }

  fun setMandatory(mandatory: Boolean) {
    state.mandatory = mandatory
  }

  /**
   * Sets the field focus.
   * @param focus The field focus
   */
  fun setFocus(focus: Boolean) {
    getRpcProxy(BooleanFieldClientRpc::class.java).setFocus(focus)
  }

  /**
   * Sets the field in blink state.
   * @param blink The blink state.
   */
  fun setBlink(blink: Boolean) {
    getRpcProxy(BooleanFieldClientRpc::class.java).setBlink(blink)
  }

  /**
   * Registers a new value change listener on this editor label.
   * @param listener The value change listener object.
   */
  fun addValueChangeListener(listener: ValueChangeListener?) {
    addListener("handleValueChange", ValueChangeEvent::class.java, listener, ValueChangeEvent.VALUE_CHANGE)
  }

  /**
   * Removes a value change listener from this editor label.
   * @param listener The listener to remove.
   */
  fun removeValueChangeListener(listener: ValueChangeListener?) {
    removeListener("handleValueChange", ValueChangeEvent::class.java, listener)
  }

  /**
   * Fires a new value change event for this field.
   * @param value The new field value
   */
  protected fun fireValueChangeEvent(value: Boolean?) {
    fireEvent(ValueChangeEvent(this, value))
  }
  //---------------------------------------------------
  // VALUE CHANGE
  //---------------------------------------------------
  /**
   * A value change listener notifies registered objects of changes
   * in the boolean fields value.
   */
  interface ValueChangeListener : ConnectorEventListener {
    /**
     * Fired when a value change is detected.
     * @param event The value change event
     */
    fun valueChange(event: ValueChangeEvent?)
  }

  /**
   * The value change event attached with the boolean field.
   *
   * @param source The source component.
   * @param value The field new value.
   */
  class ValueChangeEvent(source: Component?, val value: Boolean?) : Event(source) {
    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------

    companion object {
      val VALUE_CHANGE: Method? = null

      init {
        try {
          // Set the header click method
          VALUE_CHANGE = ValueChangeListener::class.java.getDeclaredMethod("valueChange", ValueChangeEvent::class.java)
        } catch (e: NoSuchMethodException) {
          // This should never happen
          throw RuntimeException(e)
        }
      }
    }
  }
}
