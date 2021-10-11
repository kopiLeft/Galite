/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.ui.vaadin.window

import org.kopi.galite.visual.ui.vaadin.actor.Actor
import org.kopi.galite.visual.ui.vaadin.actor.VActorsNavigationPanel
import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.base.Utils.findMainWindow
import org.kopi.galite.visual.ui.vaadin.base.VScrollablePanel
import org.kopi.galite.visual.ui.vaadin.block.Block
import org.kopi.galite.visual.ui.vaadin.form.Form
import org.kopi.galite.visual.ui.vaadin.menu.VNavigationMenu

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * Abstract class for all window components.
 */
abstract class Window : VerticalLayout(), Focusable<Window> {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  protected val actors : VActorPanel = VActorPanel()
  private var content: Component? = null

  /**
   * The last focused text field in this form.
   */
  var lasFocusedField: Focusable<*>? = null

  init {
    className = Styles.WINDOW
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds an actor to this window view.
   * @param actor The actor to be added.
   */
  open fun addActor(actor: Actor) {
    actors.addActor(actor)
  }

  /**
   * Adds the actors menu to be shown.
   * @param panel The menu to be shown.
   */
  open fun addActorsNavigationPanel(panel: VActorsNavigationPanel) {
    actors.addActorsNavigationPanel(panel)
  }

  val navigationMenu get(): VNavigationMenu = actors.navigationMenu

  /**
   * Sets the window content.
   * @param content The content.
   */
  open fun setContent(content: Component) {
    if (this.content != null) {
      remove(this.content)
    }
    this.content = VScrollablePanel(content)
    add(this.content)
  }

  /**
   * Returns the window content.
   * @return The window content.
   */
  open fun getContent(): Component? {
    return content
  }

  /**
   * Returns `true` when this window has a focused text field before it looses focus.
   * @return `true` when this window has a focused text field before it looses focus.
   */
  open fun hasLastFocusedTextField(): Boolean {
    return lasFocusedField != null && (lasFocusedField as Component).isAttached
  }

  /**
   * Sets the focus to the last focused text field of this form.
   */
  open fun goBackToLastFocusedTextField() {
    if (lasFocusedField != null) {
      lasFocusedField!!.focus()
    }
  }

  /**
   * Sets the window caption.
   * @param caption The window caption.
   */
  open fun setCaption(caption: String) {
    // first look if we can set the title on the main window.
    val success = maybeSetMainWindowCaption(caption)

    if (!success) {
      // window does not belong to main window
      // It may be then belong to a popup window
      maybeSetPopupWindowCaption(caption)
    }
  }

  /**
   * Sets the window caption if it belongs to the main window.
   * @param caption The window caption.
   * @return `true` if the caption is set.
   */
  private fun maybeSetMainWindowCaption(caption: String): Boolean {
    val parent = findMainWindow()

    if (parent != null && parent.windowsList.contains(this)) {
      parent.updateWindowTitle(this, caption)
      return true
    }
    return false
  }


  /**
   * Sets the window caption if it belongs to a popup window.
   * @param caption The window caption.
   * @return `true` if the caption is set.
   */
  private fun maybeSetPopupWindowCaption(caption: String): Boolean {
    var parent: PopupWindow? = null

    getParent().ifPresent { popupContent ->
      parent = if(popupContent is PopupWindow) {
        popupContent
      } else {
        popupContent.parent.orElseGet(null) as? PopupWindow
      }
    }

    if (parent != null) {
      parent!!.setCaption(caption)
      return true
    }
    return false
  }

  /**
   * Cleans the dirty values of this window
   */
  open fun cleanDirtyValues(active: Block) {
    cleanDirtyValues(active, true)
  }

  /**
   * Cleans the dirty values of this window
   */
  open fun cleanDirtyValues(active: Block?, transferFocus: Boolean) {
    if (this.content is Form) {
      (this.content as Form).cleanDirtyValues(active, transferFocus)
    }
  }

  /**
   * Sets the actor having the given number to be enabled or disabled.
   * @param actor The actor connector instance.
   * @param enabled The enabled status.
   */
  open fun setActorEnabled(actor: Component, enabled: Boolean) {
    for (child in children) {
      if (child is Actor) {
        if (child == actor) {
          child.isEnabled = enabled
        }
      }
    }
  }

  override fun focus() {
    // Override this to define focus logic
  }
}
