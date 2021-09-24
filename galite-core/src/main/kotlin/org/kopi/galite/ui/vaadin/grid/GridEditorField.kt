/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import kotlin.collections.Collection

import org.kopi.galite.ui.vaadin.actor.Actor
import org.kopi.galite.ui.vaadin.field.Field
import org.kopi.galite.ui.vaadin.form.DBlock
import org.kopi.galite.ui.vaadin.form.DGridEditorField
import org.kopi.galite.ui.vaadin.window.Window

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.converter.Converter

/**
 * A grid editor field implementation.
 */
abstract class GridEditorField<T> protected constructor() : AbstractCompositeField<Component, GridEditorField<T>, T>(null),
  Focusable<GridEditorField<T>>,
  HasSize,
  ClickNotifier<GridEditorField<T>>, HasStyle {

  lateinit var dGridEditorField: DGridEditorField<*>

  /**
   * The navigation delegation to server mode. Default to [NavigationDelegationMode.ALWAYS].
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

  override fun onAttach(attachEvent: AttachEvent?) {
    setWidthFull()
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

  override fun focus() {
    super.focus()
    parentWindow?.lasFocusedField = this
    doFocus()
  }

  abstract fun doFocus()

  abstract fun addFocusListener(focusFunction: () -> Unit)

  /**
   * Sets the blink state of this editor field.
   * @param blink The blink state.
   */
  abstract fun setBlink(blink: Boolean)

  fun setConverter(converter: Converter<T, Any?>) {
    val binder = Binder(String::class.java)

    binder.forField(this)
      .withConverter(converter)
      .bind({ it }, { _, _ -> })
  }

  /**
   * Returns the parent window of this text editor.
   * @return The parent window of this text editor.
   */
  val parentWindow: Window?
    get() = (dGridEditorField.columnView.blockView as? DBlock)?.parent

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
    fun onGotoNextField()

    /**
     * Fired when a goto previous field event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoPrevField()

    /**
     * Fired when a goto next block event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoNextBlock()

    /**
     * Fired when a goto previous record event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoPrevRecord()

    /**
     * Fired when a goto next field event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoNextRecord()

    /**
     * Fired when a goto first record event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoFirstRecord()

    /**
     * Fired when a goto last record event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoLastRecord()

    /**
     * Fired when a goto next empty mandatory field event is called by the user.
     * @param event The navigation event object
     */
    fun onGotoNextEmptyMustfill()
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
     */
    fun onAutofill()
  }
}
