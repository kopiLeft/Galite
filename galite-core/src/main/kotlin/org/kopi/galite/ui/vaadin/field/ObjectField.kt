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
 * The Object field server side component.
 */
abstract class ObjectField<T> : CustomField<T>(), HasStyle {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  var columnView: ColumnView? = null

  private val listeners = mutableListOf<ObjectFieldListener>()

  /*private val rpc: ObjectFieldServerRpc = object : ObjectFieldServerRpc() { TODO
    fun gotoPrevRecord() {
      fireGotoPrevRecord()
    }

    fun gotoPrevField() {
      fireGotoPrevField()
    }

    fun gotoNextRecord() {
      fireGotoNextBlock()
    }

    fun gotoNextField() {
      fireGotoNextField()
    }

    fun gotoNextBlock() {
      fireGotoNextBlock()
    }

    fun gotoLastRecord() {
      fireGotoLastRecord()
    }

    fun gotoFirstRecord() {
      fireGotoFirstRecord()
    }
  }*/

  /**
   * Creates a new `ObjectField` instance.
   */
  init {
    //registerRpc(rpc) TODO
    element.setAttribute("hideFocus", "true")
    element.setProperty("outline", "0px")
    //addKeyDownHandler(NavigationHandler()) TODO
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

  /*fun onFocus(event: FocusNotifier.FocusEvent<ObjectField<T>>?) { // TODO
    fieldConnector.getColumnView().setAsActiveField()
  }

  fun onBlur(event: BlurNotifier.BlurEvent<ObjectField<T>>?) {
    fieldConnector.getColumnView().unsetAsActiveField()
  }*/

  open fun onFocus(event: FocusNotifier.FocusEvent<CustomField<Any>>?) {
    // getFieldConnector().getColumnView().setAsActiveField() TODO
  }

  open fun onBlur(event: BlurNotifier.BlurEvent<CustomField<Any>>?) {
    // getFieldConnector().getColumnView().unsetAsActiveField() TODO
  }

  /**
   * Returns `true` if this object field is `null`.
   * @return `true` if this object field is `null`.
   */
  protected abstract val isNull: Boolean

  /**
   * Sets the object field color properties.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  internal abstract fun setColor(foreground: String?, background: String?)

  /**
   * Checks the internal value of this field.
   * @param rec The active record.
   */
  protected abstract fun checkValue(rec: Int)

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
          private val code: Int,
          key: Key,
          modifiers: Array<out KeyModifier>,
  ) : ShortcutAction("key-navigator $code", key, *modifiers) {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun performAction() {
      /*if (fieldConnector == null) { TODO
        return
      }
      connector.sendValueToServer()*/
      when (code) {
        KEY_TAB -> columnView!!.gotoNextField()
        KEY_STAB -> columnView!!.gotoPrevField()
        KEY_REC_UP -> columnView!!.gotoPrevRecord()
        KEY_REC_DOWN -> columnView!!.gotoNextRecord()
        KEY_REC_FIRST -> columnView!!.gotoFirstRecord()
        KEY_REC_LAST -> columnView!!.gotoLastRecord()
        // KEY_BLOCK -> connector.getServerRpc().gotoNextBlock() TODO
        else -> {
        }
      }
    }
  }

  /**
   * The object field navigation handler.
   */
  /*private inner class NavigationHandler : ShortcutActionHandler() {

    init {
      createNavigatorKeys()
    }

    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    /**
     * Creates the navigation actions.
     * @param fieldConnector The text field getConnector().
     */
    protected fun createNavigatorKeys() { TODO
      addKeyNavigator(KEY_TAB, Key.ENTER, 0)
      addKeyNavigator(KEY_TAB, Key.TAB, 0)
      addKeyNavigator(KEY_STAB, Key.TAB, KeyModifier.SHIFT)
      addKeyNavigator(KEY_BLOCK, Key.ENTER, KeyModifier.SHIFT)
      addKeyNavigator(KEY_REC_UP, Key.PAGE_UP, 0)
      addKeyNavigator(KEY_REC_DOWN, Key.PAGE_DOWN, 0)
      addKeyNavigator(KEY_REC_FIRST, Key.HOME, 0)
      addKeyNavigator(KEY_REC_LAST, Key.END, 0)
      addKeyNavigator(KEY_STAB, Key.ARROW_LEFT, KeyModifier.CONTROL)
      addKeyNavigator(KEY_TAB, Key.ARROW_RIGHT, KeyModifier.CONTROL)
      addKeyNavigator(KEY_REC_UP, Key.ARROW_UP, KeyModifier.CONTROL)
      addKeyNavigator(KEY_REC_DOWN, Key.ARROW_DOWN, KeyModifier.CONTROL)
    }

    *//**
   * Adds a key navigator action to this handler.
   * @param fieldConnector The object field connector
   * @param code The navigator code.
   * @param key The key code.
   * @param modifiers The modifiers.
   *//*
    protected fun addKeyNavigator(code: Int, key: Key, vararg modifiers: KeyModifier) {
      addAction(KeyNavigator(code, key, modifiers))
    }
  }*/

  companion object {
    private const val KEY_TAB = 0
    private const val KEY_STAB = 1
    private const val KEY_REC_UP = 2
    private const val KEY_REC_DOWN = 3
    private const val KEY_REC_FIRST = 4
    private const val KEY_REC_LAST = 5
    private const val KEY_BLOCK = 6
  }
}
