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

import org.kopi.galite.ui.vaadin.base.ShortcutAction
import org.kopi.galite.ui.vaadin.block.ColumnView

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.customfield.CustomField

/**
 * The Object field component.
 */
abstract class ObjectField<T> : AbstractField<T>(), HasStyle {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  var columnView: ColumnView? = null

  private val listeners = mutableListOf<ObjectFieldListener>()

  /**
   * Creates a new `ObjectField` instance.
   */
  init {
    //registerRpc(rpc) TODO
    element.setAttribute("hideFocus", "true")
    element.setProperty("outline", "0px")
    NavigationHandler().createNavigatorKeys()
    addFocusListener(::onFocus)
    addBlurListener(::onBlur)
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

  open fun onFocus(event: FocusNotifier.FocusEvent<CustomField<Any>>?) {
    columnView!!.setAsActiveField()
  }

  open fun onBlur(event: BlurNotifier.BlurEvent<CustomField<Any>>?) {
    columnView!!.unsetAsActiveField()
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
  abstract override fun setColor(foreground: String?, background: String?)

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
  private inner class KeyNavigator(
    key: Key,
    modifiers: Array<out KeyModifier>,
    navigationAction: () -> Unit
  ) : ShortcutAction(key, modifiers, navigationAction) {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun performAction() {
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
      addKeyNavigator(Key.ENTER) { columnView!!.gotoNextField() }
      addKeyNavigator(Key.TAB) { columnView!!.gotoNextField() }
      addKeyNavigator(Key.TAB, KeyModifier.of("Shift")) { columnView!!.gotoPrevField() }
      addKeyNavigator(Key.ENTER, KeyModifier.of("Shift")) { fireGotoNextBlock() }
      addKeyNavigator(Key.PAGE_UP) { columnView!!.gotoPrevRecord() }
      addKeyNavigator(Key.PAGE_DOWN) { columnView!!.gotoNextRecord() }
      addKeyNavigator(Key.HOME) { columnView!!.gotoFirstRecord() }
      addKeyNavigator(Key.END) { columnView!!.gotoLastRecord() }
      addKeyNavigator(Key.ARROW_LEFT, KeyModifier.of("Control")) { columnView!!.gotoPrevField() }
      addKeyNavigator(Key.ARROW_RIGHT, KeyModifier.of("Control")) { columnView!!.gotoNextField() }
      addKeyNavigator(Key.ARROW_UP, KeyModifier.of("Control")) { columnView!!.gotoPrevRecord() }
      addKeyNavigator(Key.ARROW_DOWN, KeyModifier.of("Control")) { columnView!!.gotoNextRecord() }
    }

    /**
     * Adds a key navigator action to this handler.
     * @param key The key code.
     * @param modifiers The modifiers.
     * @param navigationAction lambda representing the action to perform
     */
    protected fun addKeyNavigator(key: Key, vararg modifiers: KeyModifier, navigationAction: () -> Unit) {
      KeyNavigator(key, modifiers, navigationAction)
        .registerShortcut(this@ObjectField)
    }
  }
}
