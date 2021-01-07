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
package org.kopi.galite.ui.vaadin.form

import java.awt.event.KeyEvent

import org.kopi.galite.form.ULabel
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.label.SortableLabel
import org.kopi.galite.visual.VActor

/**
 * Creates a new `DLabel` instance.
 * @param text The label text.
 * @param help The label help.
 */
open class DLabel(text: String?, help: String?) : SortableLabel(text), ULabel {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Prepares the label's snapshot.
   * @param activ The field state.
   */
  fun prepareSnapshot(activ: Boolean) {
    // TODO
  }

  override fun init(text: String?, help: String?) {
    TODO()
  }

  /**
   * Updates the label content.
   * @param model The field model.
   * @param row The field row.
   */
  fun update(model: VFieldUI, row: Int) {
    TODO()
  }

  /**
   * Updates the label styles according to the field access.
   * @param access The field access
   */
  private fun updateStyles(access: Int, focused: Boolean) {
    element.classList.remove("visit")
    element.classList.remove("skipped")
    element.classList.remove("mustfill")
    element.classList.remove("hidden")
    element.classList.remove("focused")
    setMandatory(access == VConstants.ACS_MUSTFILL)
    // The focus style is the major style
    if (focused) {
      element.classList.add("focused")
    } else {
      when (access) {
        VConstants.ACS_VISIT -> element.classList.add("visit")
        VConstants.ACS_SKIPPED -> element.classList.add("skipped")
        VConstants.ACS_MUSTFILL -> element.classList.add("mustfill")
        VConstants.ACS_HIDDEN -> element.classList.add("hidden")
        else -> element.classList.add("visit")
      }
    }
  }

  /**
   * Returns the label text.
   * @return The label text.
   */
  val text: String
    get() = TODO()

  /**
   * Builds full field description.
   * @param model The field model.
   * @param tooltip The initial field tooltip.
   * @return The full field description.
   */
  protected fun buildDescription(model: VFieldUI, tooltip: String?): String? {
    var description: String?
    val commands = model.getAllCommands()
    description = if (tooltip != null) {
      "<html>$tooltip"
    } else {
      null
    }
    if (commands.isNotEmpty()) {
      if (description == null) {
        description = "<html>"
      }
      for (i in commands.indices) {
        if (commands[i].actor != null) {
          if (!description.endsWith("<html>")) {
            description += "<br>"
          }
          description += getDescription(commands[i].actor)
        }
      }
    }
    return description
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  /**
   * The info text.
   */
  var infoText: String? = null
    set(info) {
      TODO()
    }

  /**
   * `true` is the label is in detail mode.
   */
  var isInDetail = false
  private var tooltip: String? = null

  companion object {
    /**
     * Creates the actor description.
     * @param actor The actor model.
     * @return The actor description.
     */
    private fun getDescription(actor: VActor?): String {
      return if (actor!!.acceleratorKey > 0) {
        if (actor.acceleratorModifier == 0) {
          actor.menuItem + " [" + KeyEvent.getKeyText(actor.acceleratorKey) + "]"
        } else {
          actor.menuItem + " [" + KeyEvent.getKeyModifiersText(
                  actor.acceleratorModifier) + "-" + KeyEvent.getKeyText(actor.acceleratorKey) + "]"
        }
      } else {
        actor.menuItem
      }
    }
  }
}
