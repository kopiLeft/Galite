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
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.html.Label

/**
 * A label used at grid headers
 */
open class GridEditorLabel(caption: String?) : Label(caption), HasEnabled {

  /**
   * The label can execute the field action trigger.
   */
  var hasAction = false

  /**
   * The info text used to display search operator.
   */
  open var infoText = ""

  /**
   * Fires a click event on this editor label
   */
  protected open fun fireClickEvent() {
    fireEvent(ClickEvent(this))
  }

  //---------------------------------------------------
  // LISTENERS & EVENTS
  //---------------------------------------------------
  /**
   * Click listener on editor labels
   */
  interface ClickListener : EventListener, Serializable {
    /**
     * Notifies registered objects that a click event is fired on the editor label.
     * @param event The click event object.
     */
    fun onClick(event: ClickEvent?)
  }

  /**
   * The editor label click event
   */
  class ClickEvent(source: Component?) : ComponentEvent<Component>(source, true) {
    companion object {
      //---------------------------------------------------
      // DATA MEMBERS
      //---------------------------------------------------
      val CLICK_METHOD: Method? = null

      init {
        TODO()
      }
    }
  }
}
