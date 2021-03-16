package org.kopi.galite.ui.vaadin.field

import org.kopi.galite.ui.vaadin.base.ShortcutAction

import com.vaadin.flow.component.KeyDownEvent
import com.vaadin.flow.component.KeyNotifier

/**
 * A shortcut action handler based on key down event.
 */
open class ShortcutActionHandler  : KeyNotifier {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Handles the action of a given key down event.
   * @param event The key down event.
   */
  fun handleAction(event: KeyDownEvent) {
    val action: ShortcutAction? = actions[ShortcutAction.createKey(event.key, getKeyboardModifiers(event))]
    if (action != null) {
      //event.preventDefault() // prevent default action. TODO
      action.performAction()
    }
  }

  fun onKeyDown(event: KeyDownEvent) {
    handleAction(event)
  }
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  /**
   * Adds an action to this handler.
   * @param action the action.
   */
  fun addAction(action: ShortcutAction) {
    actions[action.getKey()] = action
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val actions: MutableMap<String, ShortcutAction>

  companion object {
    /**
     * Gets the keyboard modifiers associated with a DOMEvent.
     *
     * @param event the event.
     * @return the modifiers as defined in [KeyboardListener].
     */
    fun getKeyboardModifiers(event: KeyDownEvent): Int {
      return ((if (event.equals("event.shiftKey")) 1 else 0)
              or (if (event.equals("event.metaKey")) 8 else 0)
              or (if (event.equals("event.ctrlKey")) 2 else 0)
              or if (event.equals("event.altKey")) 4 else 0)
    }
  }
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new `ShortcutActionHandler` instance.
   */
  init {
    actions = HashMap<String, ShortcutAction>()
  }
}
