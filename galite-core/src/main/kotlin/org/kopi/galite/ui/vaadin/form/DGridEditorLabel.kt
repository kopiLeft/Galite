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

import org.kopi.galite.form.UChartLabel
import org.kopi.galite.form.ULabel
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.base.Utils
import org.kopi.galite.visual.VActor

/**
 * The editor label used as grid component header.
 */
class DGridEditorLabel(text: String?, help: String?) : GridEditorLabel(text), ULabel, UChartLabel {
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun init(text: String?, tooltip: String?) {
    this.tooltip = tooltip
    //BackgroundThreadHandler.access(Runnable { TODO
      setCaption(text)
      setDescription(Utils.createTooltip(tooltip))
    //})
  }

  override fun orderChanged() {}

  override fun repaint() {}

  /**
   * Updates the label content.
   * @param model The field model.
   * @param row The field row.
   */
  fun update(model: VFieldUI, row: Int) {
    //BackgroundThreadHandler.access(Runnable { TODO
      if (model.model.getAccess(row) == VConstants.ACS_SKIPPED) {
        // Only show base help on a skipped field
        // Actors are not shown since they are not active.
        setDescription(Utils.createTooltip(tooltip))
      } else {
        setDescription(Utils.createTooltip(buildDescription(model, tooltip)))
      }
    //})
  }

  /**
   * Returns the label text.
   * @return The label text.
   */
  val text: String
    get() = getCaption()

  /**
   * Builds full field description.
   * @param model The field model.
   * @param tooltip The initial field tooltip.
   * @return The full field description.
   */
  protected fun buildDescription(model: VFieldUI, tooltip: String?): String {
    var tooltip = tooltip
    var description: String
    val commands = model.getAllCommands()
    if (tooltip == null) {
      tooltip = "" // avoid writing null in help tooltip.
    }
    description = tooltip
    if (commands.isNotEmpty()) {
      description = "<html>$tooltip"
      for (i in commands.indices) {
        if (commands[i].actor != null) {
          if (description.trim { it <= ' ' }.isNotEmpty()) {
            description += "<br>"
          }
          description += getDescription(commands[i].actor)
        }
      }
    }
    return description
  }

  /**
   * Returns the info text.
   * @return The info text.
   */
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  var infoText: String? = null
    set(info) {
      field = info
      //BackgroundThreadHandler.access(Runnable { TODO
        super@DGridEditorLabel.setInfoText(info)
      //})
    }
  private var tooltip: String? = null

  companion object {
    /**
     * Creates the actor description.
     * @param actor The actor model.
     * @return The actor description.
     */
    private fun getDescription(actor: VActor): String {
      return if (actor.acceleratorKey > 0) {
        if (actor.acceleratorModifier == 0) {
          actor.menuItem.toString() + " [" + KeyEvent.getKeyText(actor.acceleratorKey) + "]"
        } else {
          actor.menuItem.toString() + " [" + KeyEvent.getKeyModifiersText(
                  actor.acceleratorModifier) + "-" + KeyEvent.getKeyText(actor.acceleratorKey) + "]"
        }
      } else {
        actor.menuItem
      }
    }
  }

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    init(text, help)
  }
}