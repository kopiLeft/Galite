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

import org.kopi.galite.visual.Action
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.Module
import org.kopi.galite.visual.UMenuTree
import org.kopi.galite.visual.VMenuTree

/**
 * The `DMenuTree` is the vaadin implementation of the
 * [UMenuTree].
 *
 *
 * The implementation is based on [DWindow]
 *
 * TODO Externalize favorites handling.
 */
class DMenuTree(model: VMenuTree) : DWindow(model), UMenuTree {
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  /**
   * Adds the given module to favorites.
   * @param module The module to be added to favorites.
   */
  fun addShortcut(module: Module) {
    if (!getModel().getShortcutsID().contains(module.id)) {
      getModel().getShortcutsID().add(module.id)
      addShortcutsInDatabase(module.id)
    }
  }

  /**
   * Removes the given module from favorites.
   * @param module The module to be removed from favorites.
   */
  fun removeShortcut(module: Module) {
    if (getModel().getShortcutsID().contains(module.id)) {
      getModel().getShortcutsID().remove(module.id)
      removeShortcutsFromDatabase(module.id)
    }
  }

  /**
   * Add a favorite into database.
   */
  protected fun addShortcutsInDatabase(id: Int) {
    TODO()
  }

  /**
   * Remove favorite from database.
   */
  protected fun removeShortcutsFromDatabase(id: Int) {
    TODO()
  }

  /**
   * Move the focus from the activated frame to favorites frame.
   */
  override fun gotoShortcuts() {}

  override fun addSelectedElement() {
    selectedModule?.let { addShortcut(it) }
  }

  /**
   * Launches the selected form in the menu tree.
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   */
  override fun launchSelectedForm() {
    TODO()
  }

  override fun setMenu() {
    TODO()
  }

  override fun removeSelectedElement() {
    val module = selectedModule
    module?.let { removeShortcut(it) }
  }

  /**
   * Returns the selected module.
   * @return The selected module.
   */
  val selectedModule: Module?
    get() =
      TODO()

  override fun run() {
    TODO()
  }

  /**
   * Calls the selected form in the tree menu.
   */
  private fun callSelectedForm() {
    getModel().performAsyncAction(object : Action("menu_form_started2") {
      override fun execute() {
        launchSelectedForm()
      }
    })
  }

  /**
   * Called to close the view (from the user), it does not
   * Definitely close the view(it may ask the user before)
   * Allowed to call outside the event dispatch thread
   */
  override fun closeWindow() {
    if (!getModel().isSuperUser) {
      getModel().ask(Message.getMessage("confirm_quit"), false)
    }
  }

  fun getActions(target: Any?, sender: Any?): Array<Action>? {
    TODO()
  }

  fun handleAction(action: Action, sender: Any?, target: Any?) {
    TODO()
  }

  /**
   * Returns the module having the given ID.
   * @param id The module ID.
   * @return The module object.
   */
  private fun getModuleByID(id: Int): Module? {
    TODO()
  }

  override fun getModel(): VMenuTree {
    return super.getModel() as VMenuTree
  }

  // --------------------------------------------------
  // UMenuTree IMPLEMENTATION
  // --------------------------------------------------
  override fun getTree(): UMenuTree.UTree? {
    TODO("Not yet implemented")
  }

  override fun getBookmark(): UMenuTree.UBookmarkPanel? {
    TODO("Not yet implemented")
  }

  override fun showApplicationInformation(message: String) {
    TODO("Not yet implemented")
  }
}
