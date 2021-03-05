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

import java.lang.reflect.Method

import kotlin.collections.Collection

import org.kopi.galite.ui.vaadin.actor.Actor
import org.kopi.galite.ui.vaadin.field.AbstractField
import org.kopi.galite.ui.vaadin.field.Field

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.router.NavigationEvent

/**
 * A grid editor field server side implementation.
 */
abstract class GridEditorField<T> protected constructor() : AbstractField() {

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
  var actors: MutableList<Actor> = mutableListOf()

  /**
   * The foreground color of the field.
   */
  var foreground: String? = null

  /**
   * The background color of the field.
   */
  var background: String? = null

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

  init {
    //registerRpc(NavigationRpcHandler())
    //registerRpc(ClickRpcHandler())
  }
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the navigation delegation mode of this editor field.
   * @param navigationDelegationMode The navigation delegation mode.
   */
  fun setNavigationDelegationMode(navigationDelegationMode: Field.NavigationDelegationMode) {
    // state.navigationDelegationMode = navigationDelegationMode TODO
  }

  /**
   * Adds the given actors list to the editor fields actors.
   * @param actors The list of field actors.
   */
  fun addActors(actors: Collection<Actor>) {
    this.actors .addAll(actors)
  }

  /**
   * Sets the color properties of this editor field.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(foreground: String, background: String) {
    this.foreground = foreground
    this.background = background
  }

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * The grid editor field navigation listener
   */
  interface NavigationListener {
    /**
     * Fired when a goto next field event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoNextField(event: NavigationEvent?)

    /**
     * Fired when a goto previous field event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoPrevField(event: NavigationEvent?)

    /**
     * Fired when a goto next block event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoNextBlock(event: NavigationEvent?)

    /**
     * Fired when a goto previous record event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoPrevRecord(event: NavigationEvent?)

    /**
     * Fired when a goto next field event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoNextRecord(event: NavigationEvent?)

    /**
     * Fired when a goto first record event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoFirstRecord(event: NavigationEvent?)

    /**
     * Fired when a goto last record event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoLastRecord(event: NavigationEvent?)

    /**
     * Fired when a goto next empty mandatory field event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoNextEmptyMustfill(event: NavigationEvent?)
  }

  /**
   * The click listener for grid editor fields
   */
  interface ClickListener {
    /**
     * Fired when a click event is detected on editor field.
     * @param event The click event object.
     */
    fun onClick(event: ClickEvent<*>?)
  }

  interface AutofillListener {
    /**
     * Fired when an autofill action is launched on the editor
     * @param event The autofill event.
     */
    fun onAutofill(event: AutofillEvent?)
  }

  /**
   * The editor field click event
   */
  class AutofillEvent(source: Component?) {
    companion object {
      //---------------------------------------------------
      // DATA MEMBERS
      //---------------------------------------------------
      val AUTOFILL_METHOD: Method? = null

      init {
        // TODO
      }
    }
  }
  // TODO
}
