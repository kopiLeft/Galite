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
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.base.Utils
import org.kopi.galite.ui.vaadin.label.SortableLabel
import org.kopi.galite.visual.VActor

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.UI

/**
 * Creates a new `DLabel` instance.
 * @param text The label text.
 * @param help The label help.
 */
open class DLabel(text: String?, help: String?) : SortableLabel(text), ULabel {

  init {
    //setSortable(false)
    init(text, help)
  }

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

  override fun init(text: String?, toolTip: String?) {
    tooltip = toolTip
    access(currentUI) {
      this.text = text
      if (toolTip != null) {
        element.setProperty("title", toolTip)
      }
    }
  }

  /**
   * Updates the label content.
   * @param model The field model.
   * @param row The field row.
   */
  open fun update(model: VFieldUI, row: Int) {
    access(currentUI) {
      updateStyles(model.model.getAccess(row), model.model.hasFocus())
      if (model.model.getAccess(row) == VConstants.ACS_SKIPPED) {
        // Only show base help on a skipped field
        // Actors are not shown since they are not active.
        if (tooltip != null) {
          element.setProperty("title", Utils.createTooltip(tooltip))
        }
      } else {
        val description = buildDescription(model, tooltip)
        if (description != null) {
          element.setProperty("title", Utils.createTooltip(description))
        }
      }
      if (model.model.getAccess(row) == VConstants.ACS_HIDDEN) {
        if (isVisible) {
          isVisible = false
        }
      } else {
        if (!isVisible) {
          isVisible = true
        }
      }
    }
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
    mandatory = access == VConstants.ACS_MUSTFILL
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
   * Builds full field description.
   * @param model The field model.
   * @param tooltip The initial field tooltip.
   * @return The full field description.
   */
  protected fun buildHtmlDescription(model: VFieldUI, tooltip: String?): String? {
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

  /**
   * Builds full field description.
   * @param model The field model.
   * @param tooltip The initial field tooltip.
   * @return The full field description.
   */
  protected fun buildDescription(model: VFieldUI, tooltip: String?): String? {
    var description = tooltip.orEmpty()
    val commands = model.getAllCommands()
    if (commands.isNotEmpty()) {
      for (i in commands.indices) {
        if (commands[i].actor != null) {
          if (description.isNotEmpty()) {
            description += "\n"
          }
          description += getDescription(commands[i].actor)
        }
      }
    }
    return description
  }


  var currentUI: UI? = null

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  /**
   * Sets the info text.
   */
  override var infoText: String? = null
    set(info) {
      field = info
      access(currentUI) {
        super@DLabel.infoText = info
      }
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
    private fun getDescription(actor: VActor?): String? {
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
