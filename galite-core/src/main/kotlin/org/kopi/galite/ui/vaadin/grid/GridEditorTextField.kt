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

import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.base.ShortcutAction
import org.kopi.galite.ui.vaadin.base.Utils
import org.kopi.galite.ui.vaadin.base.runAfterGetValue

import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.textfield.TextField

/**
 * A text field used as editor
 */
open class GridEditorTextField(val width: Int) : GridEditorField<String>() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  internal val wrappedField = TextField()

  override fun onAttach(attachEvent: AttachEvent?) {
    super.onAttach(attachEvent)
    className = "editor-field"
    wrappedField.setWidthFull()
    wrappedField.maxLength = width
    wrappedField.addValueChangeListener {
      if(!check(it.value.orEmpty())) {
        value = it.oldValue
      }
      setModelValue(value, it.isFromClient)
    }
    createNavigationActions()
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    wrappedField.value = newPresentationValue.toString()
  }

  override fun initContent(): Component {
    return wrappedField
  }

  override fun getValue(): String = wrappedField.value

  override fun doFocus() {
    wrappedField.focus()
  }

  override fun addFocusListener(focusFunction: () -> Unit) {
    wrappedField.addFocusListener {
      focusFunction()
    }
  }

  /**
   * Returns true if it is a multi line editor field.
   * @return True if it is a multi line editor field.
   */
  protected open val isMultiLine: Boolean = false

  /**
   * Sets this field to be an auto fill field
   */
  fun setAutofill() {
    val autofillIcon  = IronIcons.ARROW_DROP_DOWN.create()
    autofillIcon!!.style["cursor"] = "pointer" // TODO: move to css
    autofillIcon.addClickListener {
      dGridEditorField.onAutofill()
    }
    wrappedField.suffixComponent = autofillIcon
  }

  /**
   * Validates the given text according to the field type.
   * @param text The text to be validated
   * @return true if the text is valid.
   */
  protected open fun check(text: String): Boolean {
    return true
  }

  open fun validate() {
    // to be overridden when needed
  }

  /**
   * Sets the blink state of the boolean field.
   * @param blink The blink state.
   */
  override fun setBlink(blink: Boolean) {
    if(className != null) {
      if (blink) {
        element.classList.add("$className-blink")
      } else {
        element.classList.remove("$className-blink")
      }
    }
  }

  //---------------------------------------------------
  // NAVIGATION
  //---------------------------------------------------
  /**
   * Creates the navigation actions.
   */
  protected open fun createNavigationActions() {
    addNavigationAction(Key.ENTER, KeyModifier.of("Control")) { dGridEditorField.onGotoNextRecord() }
    addNavigationAction(Key.ENTER, KeyModifier.of("Shift")) { dGridEditorField.onGotoNextBlock() }
    addNavigationAction(Key.KEY_D, KeyModifier.of("Control")) {
      val ui = UI.getCurrent()

      Thread {
        val text = StringBuffer(value)
        text.insert(Utils.getCursorPos(wrappedField), "\u00D8")
        access(ui) {
          value = text.toString()
        }
      }.start()
    }
    addNavigationAction(Key.HOME, KeyModifier.of("Shift")) { dGridEditorField.onGotoFirstRecord() }
    addNavigationAction(Key.END, KeyModifier.of("Shift")) { dGridEditorField.onGotoLastRecord() }
    addNavigationAction(Key.ARROW_LEFT, KeyModifier.of("Control")) { dGridEditorField.onGotoPrevField() }
    addNavigationAction(Key.TAB, KeyModifier.of("Shift")) { dGridEditorField.onGotoPrevField() }
    addNavigationAction(Key.ARROW_UP, KeyModifier.of("Shift")) { dGridEditorField.onGotoPrevField() }
    addNavigationAction(Key.ARROW_RIGHT, KeyModifier.of("Control")) { dGridEditorField.onGotoNextField() }
    addNavigationAction(Key.TAB) { dGridEditorField.onGotoNextField() }
    addNavigationAction(Key.ARROW_DOWN, KeyModifier.of("Shift")) { dGridEditorField.onGotoNextField() }
    // the magnet card reader sends a CNTR-J as last character
    addNavigationAction(Key.KEY_J, KeyModifier.of("Control")) { dGridEditorField.onGotoNextField() }
    if (!isMultiLine) {
      // In multiline fields these keys are used for other stuff
      addNavigationAction(Key.ARROW_UP) { dGridEditorField.onGotoPrevField() }
      addNavigationAction(Key.ARROW_DOWN) { dGridEditorField.onGotoNextField() }
      addNavigationAction(Key.ENTER) { dGridEditorField.onGotoNextField() }
    }
  }

  /**
   * Adds a key navigator action to this handler.
   *
   * @param key The key code.
   * @param modifiers The modifiers.
   * @param navigationAction lambda representing the action to perform
   */
  protected open fun addNavigationAction(key: Key, vararg modifiers: KeyModifier, navigationAction: () -> Unit) {
    NavigationAction(this, key, modifiers, navigationAction)
      .registerShortcut()
  }

  //---------------------------------------------------
  // NAVIGATION ACTION
  //---------------------------------------------------
  /**
   * A navigation action
   */
  inner class NavigationAction(
    field: GridEditorField<*>,
    key: Key,
    modifiers: Array<out KeyModifier>,
    navigationAction: () -> Unit
  ) : ShortcutAction<GridEditorField<*>>(field, key, modifiers, navigationAction) {
    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    override fun performAction() {
      val oldValue = value

      wrappedField.runAfterGetValue {
        // block any navigation request if suggestions is showing
        /*if (suggestionDisplay != null && suggestionDisplay.isSuggestionListShowingImpl()) { TODO
          return
        }*/

        // first sends the text value to model if changed
        dGridEditorField.valueChanged(oldValue)
        navigationAction()
      }
    }
  }
}
