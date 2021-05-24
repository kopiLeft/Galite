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

import javax.swing.tree.TreeNode

import org.kopi.galite.visual.Action
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.Module
import org.kopi.galite.visual.UMenuTree
import org.kopi.galite.visual.VMenuTree
import org.kopi.galite.visual.VlibProperties

import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.selection.SelectionEvent
import com.vaadin.flow.data.selection.SelectionListener

/**
 * The `DMenuTree` is the vaadin implementation of the
 * [UMenuTree].
 *
 *
 * The implementation is based on [DWindow]
 *
 * @param model The menu tree model.
 *
 * TODO Externalize favorites handling.
 */
class DMenuTree(model: VMenuTree) : DWindow(model), UMenuTree {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private lateinit var tree: Tree
  //private val ADD_BOOKMARK: Command TODO
  //private val REMOVE_BOOKMARK: Command

  init {
    //ADD_BOOKMARK = Action(model.getActor(VMenuTree.CMD_ADD).menuItem)
    //REMOVE_BOOKMARK = Action(model.getActor(VMenuTree.CMD_REMOVE).menuItem)
    if (!model.isSuperUser) {
      // if we are not in a super user context, the menu is
      // handled by the module menu component.
      // The menu tree is handled differently comparing to swing
      // version.
      // The tree component is used only in a super user context.
    } else {
      tree = Tree(model.root!!, model.isSuperUser)
      val content = VerticalLayout(tree)
      //tree.addActionHandler(this)
      tree.addSelectionListener(ItemSelectionHandler())
      /*tree.dataProvider.addDataProviderListener { TODO
        val itemId: Any = event.getProperty().getValue() ?: return
        // tree.restoreLastModifiedItem();
        tree.setIcon(
          itemId,
          !tree.areChildrenAllowed(itemId),
          tree.getParent(itemId) == null,
          tree.getModule(itemId).accessibility
        )
        setMenu()
      }*/
      model.setDisplay(this)
      tree.setSizeUndefined()
      tree.expandRow(0)
      setMenu()
      // allow scrolling when an overflow is detected
      content.setWidth(455f, Unit.PIXELS)
      content.setHeight(600f, Unit.PIXELS)
      setContent(content)
    }
  }
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
    val module = getSelectedModule()
    module?.let { addShortcut(it) }
  }

  /**
   * Launches the selected form in the menu tree.
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   */
  override fun launchSelectedForm() {
    val module = getSelectedModule()
    if (module != null) {
      if (!getModel().isSuperUser) {
        if (tree.dataCommunicator.getParentItem(tree.selectedItem) != null) {
          module.accessibility = (module.accessibility + 1) % 3
          tree.getNodeComponent(module.id)?.setIcon(module.accessibility, module.objectName != null)
        }
      } else if (module.objectName != null) {
        setWaitInfo(VlibProperties.getString("menu_form_started"))
        module.run(getModel().dBContext!!)
        unsetWaitInfo()
      }
    }
  }

  override fun setMenu() {
    val module = getSelectedModule()
    getModel().setActorEnabled(VMenuTree.CMD_QUIT, !getModel().isSuperUser)
    getModel().setActorEnabled(VMenuTree.CMD_INFORMATION, true)
    getModel().setActorEnabled(VMenuTree.CMD_HELP, true)
    if (module != null) {
      getModel().setToolTip(module.help)
      getModel().setActorEnabled(VMenuTree.CMD_SHOW, getModel().getShortcutsID().size > 0)
      if (module.objectName != null) {
        getModel().setActorEnabled(VMenuTree.CMD_OPEN, true)
        getModel().setActorEnabled(VMenuTree.CMD_ADD, !getModel().getShortcutsID().contains(module.id))
        getModel().setActorEnabled(VMenuTree.CMD_REMOVE, getModel().getShortcutsID().contains(module.id))
        getModel().setActorEnabled(VMenuTree.CMD_FOLD, false)
        getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, false)
      } else {
        getModel().setActorEnabled(VMenuTree.CMD_OPEN, getModel().isSuperUser)
        getModel().setActorEnabled(VMenuTree.CMD_ADD, false)
        if (tree.isExpanded(tree.selectedItem)) {
          getModel().setActorEnabled(VMenuTree.CMD_FOLD, true)
          getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, false)
        } else {
          getModel().setActorEnabled(VMenuTree.CMD_FOLD, false)
          getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, true)
        }
      }
    }
  }

  override fun removeSelectedElement() {
    val module = getSelectedModule()
    module?.let { removeShortcut(it) }
  }

  /**
   * Returns the selected module.
   * @return The selected module.
   */
  fun getSelectedModule(): Module? {
    return if (getModel().isSuperUser) {
      tree.getModule(tree.selectedItem)
    } else {
      null
    }
  }

  override fun run() {
    isVisible = true
    if (getModel().isSuperUser) {
      tree.focus()
    }
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

  /*fun getActions(target: Any?, sender: Any?): Array<Action>? { TODO
    val actions: MutableList<Action>
    actions = ArrayList<Action>()
    if (target != null) {
      val module: Module?
      module = getModuleByID((target as Int).toInt())
      if (module == null) {
        return null
      }
      if (!getModel().shortcutsID.contains(target)) {
        if (module.getObject() != null) {
          actions.add(ADD_BOOKMARK)
        }
      } else {
        actions.add(REMOVE_BOOKMARK)
      }
    }
    return actions.toTypedArray()
  }*/

  /*fun handleAction(action: Action, sender: Any?, target: Any?) { TODO
    if (target != null) {
      if (action === ADD_BOOKMARK) {
        addShortcutsInDatabase((target as Int).toInt())
        getModel().shortcutsID.add(target as Int?)
      } else if (action === REMOVE_BOOKMARK) {
        removeShortcutsFromDatabase((target as Int).toInt())
        getModel().shortcutsID.remove(target)
      }
      markAsDirtyRecursive()
    }
  }*/

  /**
   * Returns the module having the given ID.
   * @param id The module ID.
   * @return The module object.
   */
  private fun getModuleByID(id: Int): Module? {
    for (i in getModel().moduleArray.indices) {
      if (getModel().moduleArray[i].id == id) {
        return getModel().moduleArray[i]
      }
    }
    return null
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  override fun getBookmark(): UMenuTree.UBookmarkPanel {
    return object : UMenuTree.UBookmarkPanel {
      override fun setVisible(visible: Boolean) {}

      override fun setEnabled(enabled: Boolean) {}

      override fun isVisible(): Boolean = false

      override fun isEnabled(): Boolean = false

      override fun toFront() {}

      override fun show() {}
    }
  }

  override fun getModel(): VMenuTree = super.getModel() as VMenuTree

  // --------------------------------------------------
  // UMenuTree IMPLEMENTATION
  // --------------------------------------------------
  override fun getTree(): UMenuTree.UTree? = tree
  // --------------------------------------------------
  // LISTENERS
  // --------------------------------------------------
  /**
   * The `ItemSelectionHandler` is the menu tree implementation
   * of the item selection listener.
   */
  private inner class ItemSelectionHandler : SelectionListener<Grid<TreeNode>, TreeNode> {

    override fun selectionChange(event: SelectionEvent<Grid<TreeNode>, TreeNode>?) {
      callSelectedForm()
    }
  }
}
