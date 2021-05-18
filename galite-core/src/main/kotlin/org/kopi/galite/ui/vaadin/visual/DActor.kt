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
package org.kopi.galite.ui.vaadin.visual

import java.awt.Event
import java.awt.event.KeyEvent

import org.kopi.galite.ui.vaadin.actor.Actor
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.base.Utils
import org.kopi.galite.visual.UActor
import org.kopi.galite.visual.VActor

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.ShortcutEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport

/**
 * The `DActor` is the vaadin implementation of
 * the [UActor]. The actor can be represented by a [Button]
 * if it has a valid icon name.
 *
 * The actor action is handled by a [ShortcutEventListener] registered
 * of the [DWindow] which is the receiver of all actors actions.
 *
 * @param model The actor model.
 *
 */
@CssImport("./styles/galite/Actor.css")
class DActor(private var model: VActor)
  : Actor(model.menuItem,
          Utils.createTooltip(getDescription(model)),
          model.menuName,
          Utils.getVaadinIcon(model.iconName),
          correctAcceleratorKey(model.acceleratorKey),
          correctAcceleratorModifier(model.acceleratorModifier)),
        UActor,
        ComponentEventListener<ClickEvent<Button>> {

  init {
    isEnabled = false
    model.setDisplay(this)
    addClickListener(this)
  }

  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------
  override fun setModel(model: VActor) {
    this.model = model
  }

  override fun getModel(): VActor {
    return model
  }

  override fun setEnabled(enabled: Boolean) {
    access {
      super.setEnabled(enabled)
    }
  }

  override fun onComponentEvent(event: ClickEvent<Button>) {
    // fire the actor action
    if (isEnabled) {
      // clean all dirty values in the client side of the parent window.
      /*getWindow().cleanDirtyValues(getBlock()) TODO
      if (VEditorTextField.getLastFocusedEditor() != null) {
        // fires text change event for grid editors
        VEditorTextField.getLastFocusedEditor().valueChanged(false)
      }*/
      model.performAction()
    }
  }

  companion object {
    // --------------------------------------------------
    // PRIVATE METHODS
    // --------------------------------------------------
    /**
     * Creates the actor description.
     * @param model The actor model.
     * @return The actor description.
     */
    private fun getDescription(model: VActor): String? {
      return if (model.acceleratorKey > 0) {
        if (model.acceleratorModifier == 0) {
          model.help + " [" + KeyEvent.getKeyText(model.acceleratorKey) + "]"
        } else {
          model.help + " [" + KeyEvent.getKeyModifiersText(model.acceleratorModifier) +
                  "-" + KeyEvent.getKeyText(model.acceleratorKey) + "]"
        }
      } else {
        model.help
      }
    }

    /**
     * Returns the corrected accelerator key.
     * @param acceleratorKey The original accelerator key.
     * @return The corrected accelerator key.
     */
    private fun correctAcceleratorKey(acceleratorKey: Int): Key =
      if (acceleratorKey == 10) {
        Key.UNIDENTIFIED // TODO
      } else {
        try {
          when (acceleratorKey) {
            KeyEvent.VK_F1 -> Key.F1
            KeyEvent.VK_F2 -> Key.F2
            KeyEvent.VK_F3 -> Key.F3
            KeyEvent.VK_F4 -> Key.F4
            KeyEvent.VK_F5 -> Key.F5
            KeyEvent.VK_F6 -> Key.F6
            KeyEvent.VK_F7 -> Key.F7
            KeyEvent.VK_F8 -> Key.F8
            KeyEvent.VK_F9 -> Key.F9
            KeyEvent.VK_F10 -> Key.F10
            KeyEvent.VK_F11 -> Key.F11
            KeyEvent.VK_F12 -> Key.F12
            KeyEvent.VK_ESCAPE -> Key.ESCAPE
            else -> throw Exception("Key Undefined")
          }
        } catch (e: Exception) {
          Key.UNIDENTIFIED
        }
      }

    /**
     * Returns the corrected modifier accelerator key.
     * @param acceleratorModifier The original modifier accelerator key.
     * @return The corrected modifier accelerator key.
     */
    private fun correctAcceleratorModifier(acceleratorModifier: Int): KeyModifier? =
            when (acceleratorModifier) {
              Event.SHIFT_MASK -> KeyModifier.of("Shift")
              Event.ALT_MASK -> KeyModifier.of("Alt")
              Event.CTRL_MASK -> KeyModifier.of("Control")
              Event.META_MASK -> KeyModifier.of("Meta")
              else -> null
            }
  }
}
