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

package org.kopi.galite.visual

import java.awt.event.KeyEvent
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale

import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.dsl.common.Trigger

/**
 * A special window that display an html help
 */
class VHelpViewer : VWindow() {

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
  var url: URL? = null

  init {
    setTitle(VlibProperties.getString("help_viewer"))
    addActors(arrayOf(
      Actor("File",
            HELPVIEWER_LOCALIZATION_RESOURCE,
            "Close",
            HELPVIEWER_LOCALIZATION_RESOURCE,
            "quit",
            KeyEvent.VK_ESCAPE,
            0)
    ))

    // localize the help viewer using the default locale
    localize(locale)
    getActor(CMD_QUIT).number = CMD_QUIT
  }

  companion object {
    private const val HELPVIEWER_LOCALIZATION_RESOURCE = "org/kopi/galite/visual/HelpViewer"

    const val CMD_QUIT = 0

    init {
      WindowController.windowController.registerWindowBuilder(Constants.MDL_HELP, object : WindowBuilder {
        override fun createWindow(model: VWindow): UWindow {
          return UIFactory.uiFactory.createView(model) as UWindow
        }
      })
    }
  }

  override fun getType(): Int = Constants.MDL_HELP

  /**
   * The user want to show an help
   */
  fun showHelp(surl: String?) {
    if (surl != null) {
      setURL(surl)
      WindowController.windowController.doNotModal(this)
    }
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  override val locale: Locale  get() = Locale.getDefault()

  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  fun localize(locale: Locale) {
    // Add localization here if you have new items in the help viewer.
  }

  /**
   * Performs the appropriate action.
   *
   * @param        VKT_Type                the number of the actor.
   * @return        true if an action was found for the specified number
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    when (VKT_Type) {
      CMD_QUIT -> close(0)
    }
  }

  /**
   * Performs a void trigger
   *
   * @param    trigger    the trigger
   */
  override fun executeVoidTrigger(trigger: Trigger?) {
    // DO NOTHING !
  }

  fun setURL(surl: String) {
    url = try {
      URL(surl)
    } catch (exc: MalformedURLException) {
      System.err.println("Bad URL: $surl")
      null
    }
  }
}
