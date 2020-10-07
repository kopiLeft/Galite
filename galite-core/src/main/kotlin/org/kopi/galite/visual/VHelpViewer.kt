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

package org.kopi.galite.visual

import java.awt.event.KeyEvent
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale

import org.kopi.galite.l10n.LocalizationManager

/**
 * A special window that display an html help
 */
class VHelpViewer : VWindow() {
  companion object {
    private const val HELPVIEWER_LOCALIZATION_RESOURCE = "org/kopi/vkopi/lib/resource/HelpViewer"

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
  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  fun localize(locale: Locale) {
    var manager: LocalizationManager?
    manager = LocalizationManager(locale, ApplicationContext.getDefaultLocale())

    // localizes the actors in VWindow
    super.localizeActors(manager)
    manager = null
  }

  /**
   * Performs the appropriate action.
   *
   * @param        key                the number of the actor.
   * @return        true if an action was found for the specified number
   */
  override fun executeVoidTrigger(key: Int) {
    when (key) {
      CMD_QUIT -> close(0)
    }
  }

  fun setURL(surl: String) {
    url = try {
      URL(surl)
    } catch (exc: MalformedURLException) {
      System.err.println("Bad URL: $surl")
      null
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
  var url: URL? = null

  init {
    setTitle(VlibProperties.getString("help_viewer"))
    setActors(arrayOf(
            VActor("File",
                    HELPVIEWER_LOCALIZATION_RESOURCE,
                    "Close",
                    HELPVIEWER_LOCALIZATION_RESOURCE,
                    "quit",
                    KeyEvent.VK_ESCAPE,
                    0)
    ))

    // localize the help viewer using the default locale
    localize(Locale.getDefault())
    getActor(CMD_QUIT).number = CMD_QUIT
  }
}
