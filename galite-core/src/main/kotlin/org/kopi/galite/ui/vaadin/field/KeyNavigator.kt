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
 * @param key The key code.
 * @param modifiers The modifiers to be used
 * @param navigationAction lambda representing the action to perform
 */
class KeyNavigator(private val field: InputTextField<*>,
                   key: Key,
                   modifiers: Array<out KeyModifier>,
                   navigationAction: () -> Unit
) : ShortcutAction(key, modifiers, navigationAction) {

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
    // maybeSendDirtyValues() FIXME We don't need this. TODO: Remove useless code
    // perform the navigation action.
    doNavigatorAction()
  }

  /**
   * Executes the navigation action.
   */
  protected fun doNavigatorAction() {
    navigationAction()
  }
}
