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
package org.kopi.galite.visual.ui.vaadin.visual

import java.awt.Event
import java.awt.event.KeyEvent

import org.kopi.galite.visual.ui.vaadin.actor.VActorNavigationItem
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.base.Utils
import org.kopi.galite.visual.ui.vaadin.base.Utils.findMainWindow
import org.kopi.galite.visual.ui.vaadin.base.runAfterGetValue
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.ui.vaadin.form.DGridEditorField
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorTextField
import org.kopi.galite.visual.ui.vaadin.menu.VNavigationMenu
import org.kopi.galite.visual.ui.vaadin.window.Window
import org.kopi.galite.visual.UActor
import org.kopi.galite.visual.Actor

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.ShortcutEventListener
import com.vaadin.flow.component.UI
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
@CssImport("./styles/galite/actor.css")
class DActor(private var model: Actor)
  : org.kopi.galite.visual.ui.vaadin.actor.Actor(model.menuItem,
          Utils.createTooltip(getDescription(model)),
          model.menuName,
          Utils.getVaadinIcon(model.iconName),
          correctAcceleratorKey(model.acceleratorKey),
          correctAcceleratorModifier(model.acceleratorModifier)),
  UActor, ComponentEventListener<ClickEvent<Button>> {

  var item: VActorNavigationItem? = null

  init {
    isEnabled = false
    model.setDisplay(this)
    addClickListener(this)
  }

  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------
  override fun setModel(model: Actor) {
    this.model = model
  }

  override fun getModel(): Actor {
    return model
  }


  var currentUI: UI? = null

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }

  override fun setEnabled(enabled: Boolean) {
    access(currentUI) {
      if(!enabled) {
        super.getElement().setAttribute("part", Styles.ACTOR + "-disabled")
        super.getElement().setAttribute("class", Styles.ACTOR + "-disabled")
      } else {
        super.getElement().setAttribute("part", Styles.ACTOR)
        super.getElement().setAttribute("class", Styles.ACTOR)
      }
      super.setEnabled(enabled)
      item?.isEnabled = enabled
    }
  }

  override fun onComponentEvent(event: ClickEvent<Button>) {
    actionPerformed()
  }

  /**
   * Creates an equivalent menu Item for this actor.
   *
   * @param navigationMenu the navigation menu which contains the navigation items.
   * @return The actor menu item.
   */
  fun createNavigationItem(navigationMenu: VNavigationMenu): VActorNavigationItem {
    return VActorNavigationItem(text,
                                menu,
                                acceleratorKey,
                                modifiersKey,
                                icon,
                                navigationMenu,
                                ::actionPerformed)
      .also {
        item = it
        it.isEnabled = isEnabled
      }
  }

  fun actionPerformed() {
    // fire the actor action
    if (isEnabled) {
      model.performAction()
    }
  }

  fun shortcutActionPerformed() {
    // fire the actor action
    if (isEnabled) {
      val lasFocusedField = (findMainWindow()?.currentWindow as? Window)?.lasFocusedField

      val field = if (lasFocusedField != null) {
        // fires text change event for grid editors
        when (lasFocusedField) {
          is TextField -> lasFocusedField.inputField
          is DGridEditorField<*> -> (lasFocusedField.editor as? GridEditorTextField)?.wrappedField
          else -> null
        }
      } else {
        null
      }

      if (field == null) {
        model.performAction()
      } else {
        field.runAfterGetValue {

          if (lasFocusedField is TextField) {
            lasFocusedField.inputField.fieldConnector.valueChanged()
          } else if (lasFocusedField is DGridEditorField<*>) {
            lasFocusedField.valueChanged(lasFocusedField.editor.value?.toString())
          }

          model.performAction()
        }
      }
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
    private fun getDescription(model: Actor): String? {
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
            else -> throw Exception("Key is undefined")
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
