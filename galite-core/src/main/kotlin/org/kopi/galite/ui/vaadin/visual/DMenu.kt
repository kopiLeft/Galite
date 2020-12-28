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

import org.kopi.galite.ui.vaadin.menu.ModuleList
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.UMenuTree
import org.kopi.galite.visual.VMenuTree
import java.io.File

/**
 * A module menu implementation that uses the menu tree
 * model. This will not display a menu tree but an horizontal
 * menu with vertical sub menus drops.
 *
 * @param model The menu tree model.
 */
abstract class DMenu protected constructor(val model: VMenuTree): ModuleList(), UMenuTree {
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------

  /**
   * Returns the module having the given id.
   * @param id
   * @return
   */
  protected fun getModuleById(id: String?): Module? {
    return modules[Integer.valueOf(id)]
  }


  /**
   * Launches the given [Module].
   * @param module The module to be launched.
   */
  protected fun launchModule(module: Module) {
    TODO()
  }

  /**
   * Returns the current application instance.
   * @return the current application instance.
   */
  protected val application: VApplication
    get() = ApplicationContext.applicationContext.getApplication() as VApplication

  //---------------------------------------------------
  // MENU TREE IMPLEMENTATION
  //---------------------------------------------------
  override fun run() {
  }

  override fun setTitle(title: String) {}
  override fun setInformationText(text: String?) {}
  override fun setTotalJobs(totalJobs: Int) {}
  override fun setCurrentJob(currentJob: Int) {}
  override fun updateWaitDialogMessage(message: String) {}
  override fun closeWindow() {
    application.logout()
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {}
  override fun performBasicAction(action: Action) {
    action.run()
  }

  override fun performAsyncAction(action: Action) {
    TODO()
  }

  override fun modelClosed(type: Int) {}
  override fun setWaitDialog(message: String, maxtime: Int) {}
  override fun unsetWaitDialog() {}
  override fun setWaitInfo(message: String) {
    TODO()
  }

  override fun unsetWaitInfo() {
    TODO()
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {}
  override fun unsetProgressDialog() {}
  override fun fileProduced(file: File, name: String) {}
  override fun getTree(): UMenuTree.UTree? = null
  override fun getBookmark(): UMenuTree.UBookmarkPanel? = null

  override fun launchSelectedForm() {
  }

  override fun addSelectedElement() {}
  override fun setMenu() {}
  override fun removeSelectedElement() {}
  override fun gotoShortcuts() {
    // TODO
  }

  override fun showApplicationInformation(message: String) {}
  // --------------------------------------------------
  // ABSTRACT METHODS
  // --------------------------------------------------
  /**
   * Returns the type associated with this menu.
   * This is the ID of a root menu provided by the
   * menu tree model.
   * @return The type of this menu.
   */
  abstract val type: Int

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  protected val modules = hashMapOf<Int, Module>()
}
