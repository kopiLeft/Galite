/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.field

import org.kopi.galite.visual.ui.vaadin.base.JSKeyDownHandler
import org.kopi.galite.visual.ui.vaadin.base.ShortcutAction
import org.kopi.galite.visual.ui.vaadin.base.addJSKeyDownListener

import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier

/**
 * The Object field component.
 */
abstract class ObjectField<T> : AbstractField<T>(), HasStyle, JSKeyDownHandler {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val listeners = mutableListOf<ObjectFieldListener>()
  override val keyNavigators = mutableMapOf<String, ShortcutAction<*>>()

  /**
   * Creates a new `ObjectField` instance.
   */
  init {
    //registerRpc(rpc) TODO
    element.setAttribute("hideFocus", "true")
    element.setProperty("outline", "0px")
    NavigationHandler().createNavigatorKeys()
    addJSKeyDownListener(keyNavigators)
    //sinkEvents(Event.ONKEYDOWN) TODO
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Registers an object field listener.
   * @param l The object field listener.
   */
  fun addObjectFieldListener(l: ObjectFieldListener) {
    listeners.add(l)
  }

  /**
   * Removes an object field listener.
   * @param l The object field listener.
   */
  fun removeObjectFieldListener(l: ObjectFieldListener) {
    listeners.remove(l)
  }

  /**
   * Fires a goto previous record event on this text field.
   */
  protected fun fireGotoPrevRecord() {
    for (l in listeners) {
      l.gotoPrevRecord()
    }
  }

  /**
   * Fires a goto previous field event on this text field.
   */
  protected fun fireGotoPrevField() {
    for (l in listeners) {
      l.gotoPrevField()
    }
  }

  /**
   * Fires a goto next record event on this text field.
   */
  protected fun fireGotoNextRecord() {
    for (l in listeners) {
      l.gotoNextRecord()
    }
  }

  /**
   * Fires a goto next field event on this text field.
   */
  protected fun fireGotoNextField() {
    for (l in listeners) {
      l.gotoNextField()
    }
  }

  /**
   * Fires a goto next block event on this text field.
   */
  protected fun fireGotoNextBlock() {
    for (l in listeners) {
      l.gotoNextBlock()
    }
  }

  /**
   * Fires a goto last record event on this text field.
   */
  protected fun fireGotoLastRecord() {
    for (l in listeners) {
      l.gotoLastRecord()
    }
  }

  /**
   * Fires a goto first record event on this text field.
   */
  protected fun fireGotoFirstRecord() {
    for (l in listeners) {
      l.gotoFirstRecord()
    }
  }

  /**
   * Returns `true` if this object field is `null`.
   * @return `true` if this object field is `null`.
   */
  override abstract val isNull: Boolean

  /**
   * Sets the object field color properties.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  abstract fun setColor(foreground: String?, background: String?)

  /**
   * Checks the internal value of this field.
   * @param rec The active record.
   */
  abstract override fun checkValue(rec: Int)

  /**
   * Sets the component visibility from the parent field.
   * @param visible The visibility state
   */
  protected abstract fun setParentVisibility(visible: Boolean)

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * The object field key navigator.
   */
  inner class KeyNavigator(
    field: ObjectField<*>,
    key: Key,
    modifiers: Array<out KeyModifier>,
    navigationAction: () -> Unit
  ) : ShortcutAction<ObjectField<*>>(field, key, modifiers, navigationAction) {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun performAction(eagerValue: String?) {
      navigationAction()
    }
  }

  /**
   * The object field navigation handler.
   */
  private inner class NavigationHandler {

    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    /**
     * Creates the navigation actions.
     */
    fun createNavigatorKeys() {
      addKeyNavigator(Key.ENTER) { fireGotoNextField() }
      addKeyNavigator(Key.TAB) { fireGotoNextField() }
      addKeyNavigator(Key.TAB, KeyModifier.of("Shift")) { fireGotoPrevField() }
      addKeyNavigator(Key.ENTER, KeyModifier.of("Shift")) { fireGotoNextBlock() }
      addKeyNavigator(Key.PAGE_UP) { fireGotoPrevRecord() }
      addKeyNavigator(Key.PAGE_DOWN) { fireGotoNextRecord() }
      addKeyNavigator(Key.HOME) { fireGotoFirstRecord() }
      addKeyNavigator(Key.END) { fireGotoLastRecord() }
      addKeyNavigator(Key.ARROW_LEFT, KeyModifier.of("Control")) { fireGotoPrevField() }
      addKeyNavigator(Key.ARROW_RIGHT, KeyModifier.of("Control")) { fireGotoNextField() }
      addKeyNavigator(Key.ARROW_UP, KeyModifier.of("Control")) { fireGotoPrevRecord() }
      addKeyNavigator(Key.ARROW_DOWN, KeyModifier.of("Control")) { fireGotoNextRecord() }
    }

    /**
     * Adds a key navigator action to this handler.
     * @param key The key code.
     * @param modifiers The modifiers.
     * @param navigationAction lambda representing the action to perform
     */
    protected fun addKeyNavigator(key: Key, vararg modifiers: KeyModifier, navigationAction: () -> Unit) {
      val navigator = KeyNavigator(this@ObjectField, key, modifiers, navigationAction)
      val keyNavigator = navigator.getKey()

      keyNavigators[keyNavigator] = navigator
    }
  }
}
