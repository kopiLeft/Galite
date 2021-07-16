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

import org.kopi.galite.common.Window
import org.kopi.galite.preview.VPreviewWindow
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.startAndWait
import org.kopi.galite.ui.vaadin.window.PopupWindow
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VHelpViewer
import org.kopi.galite.visual.VMenuTree
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.WindowController

/**
 * The `VWindowController` is the vaadin implementation
 * of the [WindowController] specifications.
 */
class VWindowController : WindowController() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun doModal(model: VWindow): Boolean {
    return try {
      val viewStarter = ModalViewRunner(model)
      startAndWait(model as Object) {
        viewStarter.run()
      }
      if (viewStarter.getView() == null) false else viewStarter.getView()!!.returnCode == VWindow.CDE_VALIDATE
    } finally {
      // This is a turn around to kill delayed wait dialog displayed in modal windows
      model.unsetWaitInfo()
    }
  }

  override fun doModal(model: Window): Boolean {
    return doModal(model.model)
  }

  override fun doNotModal(model: VWindow) {
    val builder = getWindowBuilder(model)
    if (builder != null) {
      try {
        access {
          val view = builder.createWindow(model) as DWindow
          view.run()
          val application = getApplication()
          if (application != null) {
            if (model is VPreviewWindow
              || model is VHelpViewer
              || model is VMenuTree) {
              showNotModalPopupWindow(view, model.getTitle())
            } else {
              application.addWindow(view, model.getTitle())
            }
          }
        }
      } catch (e: VException) {
        throw VRuntimeException(e.message, e)
      }
    }
  }

  override fun doNotModal(model: Window) {
    doNotModal(model.model)
  }

  /**
   * Shows a modal window in a popup view. This will handle
   * a window view not in a tabsheet but in a non modal
   * popup window.
   * @param application The application instance.
   * @param view The window view.
   * @param title The window title.
   */
  protected fun showNotModalPopupWindow(
          view: DWindow,
          title: String,
  ) {
    val popup = PopupWindow()
    popup.isModal = false
    popup.setContent(view)
    popup.setCaption(title) // put popup title
    access {
      popup.open()
    }
  }

  private fun getApplication(): VApplication? {
    val ui = BackgroundThreadHandler.locateUI()
    return if (ui == null) {
      null
    } else {
      ui.children
        .filter { component -> component is VApplication }
        .findFirst()
        .orElse(null) as? VApplication
    }
  }

  //---------------------------------------------------
  // MODAL VIEW STARTER
  //---------------------------------------------------
  /**
   * A modal view runner background task.
   */
  /*package*/
  internal inner class ModalViewRunner(val model: VWindow) : Runnable {

    private var view: DWindow? = null

    //---------------------------------------
    // IMPLEMENTATION
    //---------------------------------------
    override fun run() {
      val builder = getWindowBuilder(model)
      if (builder != null) {
        try {
          access {
            view = builder.createWindow(model) as DWindow
            view!!.run()
            val application = getApplication()
            if (application != null) {
              val popup = PopupWindow()
              popup.isModal = true
              popup.setContent(view!!)
              popup.setCaption(model.getTitle()) // put popup title
              popup.open()
            }
          }
        } catch (e: VException) {
          throw VRuntimeException(e.message, e)
        }
      }
    }

    /**
     * Returns the window view.
     * @return The window view.
     */
    fun getView(): DWindow? {
      return view
    }
  }
}
