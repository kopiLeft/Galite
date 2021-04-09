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

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import org.kopi.galite.ui.vaadin.base.ShortcutAction

/**
 * A text field input key navigator action.
 *
 * @param field The text input connector (for action fire)
 * @param code The action code
 * @param key The key code.
 * @param modifiers The modifiers to be used
 */
class KeyNavigator(private val field: InputTextField<*>,
                   private val code: Int,
                   key: Key,
                   modifiers: Array<out KeyModifier>
) : ShortcutAction("key-navigator$code", key, *modifiers) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun performAction() {
    internalPerformAction()
  }

  /**
   * Checks if a suggestions query has been performed for a field.
   */
  /*protected fun maybeCancelSuggestionsQuery() { TODO: Suggestions
    *//*
     * If we are waiting for a suggestion list to be shown
     * the navigation action will be executed and the suggestions
     * list well be cancelled.
     *//*
    if (field.isAboutShowingSuggestions()) {
      field.cancelSuggestions()
    }
  }*/

  /**
   * Checks if the dirty values should be sent before performing
   * the accelerator action.
   */
  protected fun maybeSendDirtyValues() {
    /*
     * When the navigation is delegated to the server side,
     * all pending values must be sent to server side to be sure
     * that the client state and the server state are synchronized
     * before executing any server trigger.
     */
    if (field.delegateNavigationToServer()) {
      field.sendDirtyValuesToServerSide()
    }
  }

  /**
   * Internally performs the navigation action.
   */
  protected fun internalPerformAction() {
    /*
     * If a suggestions list is already shown
     * we let the user choose an entry before.
     */
    /*if (field.isShowingSuggestions()) { TODO: Suggestion
      return
    }*/
    // check if suggestions should be cancelled.
    // maybeCancelSuggestionsQuery() TODO: Suggestion
    // check if dirty values should be communicated
    // to server side.
    maybeSendDirtyValues()
    // perform the navigation action.
    doNavigatorAction()
  }

  /**
   * Executes the navigation action according to its code.
   */
  protected fun doNavigatorAction() {
    when (code) {
      KEY_NEXT_FIELD -> field.fieldConnector.columnView!!.gotoNextField()
      KEY_PREV_FIELD -> field.fieldConnector.columnView!!.gotoPrevField()
      //KEY_NEXT_BLOCK -> connector.getServerRpc().gotoNextBlock() TODO
      KEY_REC_UP -> field.fieldConnector.columnView!!.gotoPrevRecord()
      KEY_REC_DOWN -> field.fieldConnector.columnView!!.gotoNextRecord()
      KEY_REC_FIRST -> field.fieldConnector.columnView!!.gotoFirstRecord()
      KEY_REC_LAST -> field.fieldConnector.columnView!!.gotoLastRecord()
      KEY_EMPTY_FIELD -> field.fieldConnector.columnView!!.gotoNextEmptyMustfill()
      KEY_DIAMETER -> {
        val text = StringBuffer(field.value)
        //text.insert(field.getCursorPos(), "\u00D8") TODO
        field.value = text.toString()
      }
      KEY_ESCAPE -> {
      }
      //KEY_PRINTFORM -> connector.getServerRpc().printForm() TODO
      //KEY_PREV_VAL -> connector.getServerRpc().previousEntry() TODO
      //KEY_NEXT_VAL -> connector.getServerRpc().nextEntry() TODO
      else -> {
      }
    }
  }

  companion object {
    // navigation constants.
    const val KEY_NEXT_FIELD = 0
    const val KEY_PREV_FIELD = 1
    const val KEY_REC_UP = 2
    const val KEY_REC_DOWN = 3
    const val KEY_REC_FIRST = 4
    const val KEY_REC_LAST = 5
    const val KEY_EMPTY_FIELD = 6
    const val KEY_NEXT_BLOCK = 7
    const val KEY_PREV_VAL = 8
    const val KEY_NEXT_VAL = 9
    const val KEY_DIAMETER = 10
    const val KEY_ESCAPE = 11
    const val KEY_PRINTFORM = 12
  }
}
