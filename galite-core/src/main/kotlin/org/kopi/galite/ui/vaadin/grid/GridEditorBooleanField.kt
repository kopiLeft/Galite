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
package org.kopi.galite.ui.vaadin.grid

import java.io.Serializable
import java.lang.reflect.Method
import java.util.EventListener

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent

/**
 * Server side implementation of the editor boolean field.
 */
class GridEditorBooleanField(trueRepresentation: String, falseRepresentation: String) : GridEditorField<Boolean?>() {
  override var value: Any?
    get() {
      return value
    }
    set(newFieldValue) {
      internalValue = newFieldValue as? Boolean
    }

  /**
   * Sets the boolean field to be mandatory
   * This will remove to choose the null option
   * from the two check boxes
   */
  var mandatory = false

  /**
   * The label attached with this field.
   * Needed to set the for attribute of the input radio
   * in the field widget.
   */
  var label: String? = null

  /**
   * The localized true value for this field
   */
  var trueRepresentation: String = trueRepresentation

  /**
   * The localized false value for this field
   */
  var falseRepresentation: String = falseRepresentation

  /**
   * Fires a new value change event for this field.
   * @param value The new field value
   */
  protected fun fireValueChangeEvent(value: Boolean) {
    fireEvent(ValueChangeEvent(this, value))
  }

  //---------------------------------------------------
  // VALUE CHANGE
  //---------------------------------------------------

  //---------------------------------------------------
  // VALUE CHANGE
  //---------------------------------------------------
  /**
   * A value change listener notifies registered objects of changes
   * in the boolean fields value.
   */
  interface ValueChangeListener : EventListener, Serializable {
    /**
     * Fired when a value change is detected.
     * @param event The value change event
     */
    fun valueChange(event: ValueChangeEvent?)
  }

  /**
   * Creates a new value change event instance.
   * @param source The source component.
   * @param value The field new value.
   */
  class ValueChangeEvent(source: Component?, val value: Boolean) : ComponentEvent<Component>(source, true) {

    companion object {
      val VALUE_CHANGE: Method? = null

      init {
        TODO()
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var internalValue: Boolean? = null
}
