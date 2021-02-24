/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: ActorField.java 35283 2018-01-05 09:00:51Z hacheni $
 */
package org.kopi.galite.ui.vaadin.field

import java.lang.reflect.Method

/**
 * The actor field server side implementation
 */
class ActorField : VObjectField() {
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  protected val state: ActorFieldState
    protected get() = super.getState() as ActorFieldState

  protected fun getState(markAsDirty: Boolean): ActorFieldState {
    return super.getState(markAsDirty) as ActorFieldState
  }

  /**
   * Sets the field icon name.
   * @param icon The icon name.
   */
  fun setIcon(icon: String) {
    state.icon = icon
  }

  /**
   * Sets the field foreground and background colors
   * @param foreground The foreground color
   * @param background The background color
   */
  public override fun setColor(foreground: String, background: String) {
    state.foreground = foreground
    state.background = background
  }

  /**
   * Registers a new click listener on this editor label.
   * @param listener The click listener object.
   */
  fun addClickListener(listener: ClickListener?) {
    addListener("handleClick", ClickEvent::class.java, listener, ClickEvent.CLICK_METHOD)
  }

  /**
   * Removes a click listener from this editor label.
   * @param listener The listener to remove.
   */
  fun removeClickListener(listener: ClickListener?) {
    removeListener("handleClick", ClickEvent::class.java, listener)
  }

  /**
   * Fires a click event on this editor label
   */
  protected fun fireClickEvent() {
    fireEvent(ClickEvent(this))
  }
  //---------------------------------------------------
  // LISTENERS & EVENTS
  //---------------------------------------------------
  /**
   * Click listener on editor labels
   */
  interface ClickListener : ConnectorEventListener {
    /**
     * Notifies registered objects that a click event is fired on the editor label.
     * @param event The click event object.
     */
    fun onClick(event: ClickEvent?)
  }

  /**
   * The editor label click event
   */
  class ClickEvent  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  (source: Component?) : Event(source) {
    companion object {
      //---------------------------------------------------
      // DATA MEMBERS
      //---------------------------------------------------
      val CLICK_METHOD: Method? = null

      init {
        try {
          // Set the header click method
          CLICK_METHOD = ClickListener::class.java.getDeclaredMethod("onClick", ClickEvent::class.java)
        } catch (e: NoSuchMethodException) {
          // This should never happen
          throw RuntimeException(e)
        }
      }
    }
  }

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    setImmediate(true)
    registerRpc(object : ActorFieldServerRpc() {
      fun clicked() {
        fireClickEvent()
      }
    })
  }
}