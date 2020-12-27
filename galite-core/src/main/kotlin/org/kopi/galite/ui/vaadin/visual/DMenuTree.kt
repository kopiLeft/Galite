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

import org.kopi.galite.visual.UMenuTree
import org.kopi.galite.visual.VMenuTree
import java.util.*

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
    if (!model.getShortcutsID().contains(module.getId())) {
      model.getShortcutsID().add(module.getId())
      addShortcutsInDatabase(module.getId())
    }
  }

  /**
   * Removes the given module from favorites.
   * @param module The module to be removed from favorites.
   */
  fun removeShortcut(module: Module) {
    if (model.getShortcutsID().contains(module.getId())) {
      model.getShortcutsID().remove(module.getId())
      removeShortcutsFromDatabase(module.getId())
    }
  }

  /**
   * Add a favorite into database.
   */
  protected fun addShortcutsInDatabase(id: Int) {
    try {
      val query: Query
      model.getDBContext().startWork() // !!! BEGIN_SYNC
      query = Query(model)
      if (model.getMenuTreeUser() != null) {
        query.run("INSERT INTO FAVORITEN VALUES ("
                          + "{fn NEXTVAL(FAVORITENId)}" + ", "
                          + (System.currentTimeMillis() / 1000).toInt() + ", "
                          + "(SELECT ID FROM KOPI_USERS WHERE Kurzname = \'" + model.getMenuTreeUser() + "\')" + ", "
                          + id
                          + ")")
      } else {
        query.run("INSERT INTO FAVORITEN VALUES ("
                          + "{fn NEXTVAL(FAVORITENId)}" + ", "
                          + (System.currentTimeMillis() / 1000).toInt() + ", "
                          + model.getUserID() + ", "
                          + id
                          + ")")
      }
      model.getDBContext().commitWork()
    } catch (e: SQLException) {
      try {
        model.getDBContext().abortWork()
      } catch (ef: SQLException) {
        ef.printStackTrace()
      }
      e.printStackTrace()
    }
  }

  /**
   * Remove favorite from database.
   */
  protected fun removeShortcutsFromDatabase(id: Int) {
    try {
      val query: Query
      model.getDBContext().startWork() // !!! BEGIN_SYNC
      query = Query(model)
      if (model.getMenuTreeUser() != null) {
        query.run("DELETE FROM FAVORITEN WHERE Benutzer = "
                          + "(SELECT ID FROM KOPI_USERS WHERE Kurzname = \'" + model.getMenuTreeUser() + "\')"
                          + " AND Modul = " + id)
      } else {
        query.run("DELETE FROM FAVORITEN WHERE Benutzer = " + model.getUserID().toString() + " AND Modul = " + id)
      }
      model.getDBContext().commitWork()
    } catch (e: SQLException) {
      e.printStackTrace()
      try {
        model.getDBContext().abortWork()
      } catch (e1: SQLException) {
        e1.printStackTrace()
      }
    }
  }

  /**
   * Move the focus from the activated frame to favorites frame.
   */
  fun gotoShortcuts() {}
  fun addSelectedElement() {
    val module = selectedModule
    module?.let { addShortcut(it) }
  }

  /**
   * Launches the selected form in the menu tree.
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   */
  @Throws(VException::class)
  fun launchSelectedForm() {
    val module = selectedModule
    if (module != null) {
      if (model.isSuperUser()) {
        if (tree.getParent(tree.getValue()) != null) {
          module.setAccessibility((module.getAccessibility() + 1) % 3)
          tree.setIcon(module.getAccessibility(), module.getId(), module.getObject() != null)
        }
      } else if (module.getObject() != null) {
        setWaitInfo(VlibProperties.getString("menu_form_started"))
        module.run(model.getDBContext())
        unsetWaitInfo()
      } else {
        if (tree.isExpanded(tree.getValue())) {
          tree.collapseItem(tree.getValue())
        } else {
          tree.expandItem(tree.getValue())
        }
      }
    }
  }

  fun setMenu() {
    val module = selectedModule
    model.setActorEnabled(VMenuTree.CMD_QUIT, !(model as VMenuTree).isSuperUser())
    model.setActorEnabled(VMenuTree.CMD_INFORMATION, true)
    model.setActorEnabled(VMenuTree.CMD_HELP, true)
    if (module != null) {
      (model as VMenuTree).setToolTip(module.getHelp())
      model.setActorEnabled(VMenuTree.CMD_SHOW, model.getShortcutsID().size() > 0)
      if (module.getObject() != null) {
        model.setActorEnabled(VMenuTree.CMD_OPEN, true)
        model.setActorEnabled(VMenuTree.CMD_ADD, !model.getShortcutsID().contains(module.getId()))
        model.setActorEnabled(VMenuTree.CMD_REMOVE, model.getShortcutsID().contains(module.getId()))
        model.setActorEnabled(VMenuTree.CMD_FOLD, false)
        model.setActorEnabled(VMenuTree.CMD_UNFOLD, false)
      } else {
        model.setActorEnabled(VMenuTree.CMD_OPEN, (model as VMenuTree).isSuperUser())
        model.setActorEnabled(VMenuTree.CMD_ADD, false)
        if (tree.isExpanded(tree.getValue())) {
          model.setActorEnabled(VMenuTree.CMD_FOLD, true)
          model.setActorEnabled(VMenuTree.CMD_UNFOLD, false)
        } else {
          model.setActorEnabled(VMenuTree.CMD_FOLD, false)
          model.setActorEnabled(VMenuTree.CMD_UNFOLD, true)
        }
      }
    }
  }

  fun removeSelectedElement() {
    val module = selectedModule
    module?.let { removeShortcut(it) }
  }

  /**
   * Returns the selected module.
   * @return The selected module.
   */
  val selectedModule: Module?
    get() = if (model.isSuperUser()) {
      tree.getModule(tree.getValue())
    } else {
      null
    }

  fun run() {
    setVisible(true)
    if (model.isSuperUser()) {
      tree.focus()
    }
  }

  /**
   * Calls the selected form in the tree menu.
   */
  private fun callSelectedForm() {
    model.performAsyncAction(object : KopiAction("menu_form_started2") {
      @Throws(VException::class)
      fun execute() {
        launchSelectedForm()
      }
    })
  }

  /**
   * Called to close the view (from the user), it does not
   * Definitely close the view(it may ask the user before)
   * Allowed to call outside the event dispatch thread
   */
  fun closeWindow() {
    if (!(model as VMenuTree).isSuperUser()) {
      model.ask(Message.getMessage("confirm_quit"), false)
    }
  }

  fun getActions(target: Any?, sender: Any?): Array<Action>? {
    val actions: MutableList<Action>
    actions = ArrayList<Action>()
    if (target != null) {
      val module: Module?
      module = getModuleByID((target as Int).toInt())
      if (module == null) {
        return null
      }
      if (!model.getShortcutsID().contains(target)) {
        if (module.getObject() != null) {
          actions.add(ADD_BOOKMARK)
        }
      } else {
        actions.add(REMOVE_BOOKMARK)
      }
    }
    return actions.toArray(arrayOfNulls<Action>(actions.size))
  }

  fun handleAction(action: Action, sender: Any?, target: Any?) {
    if (target != null) {
      if (action === ADD_BOOKMARK) {
        addShortcutsInDatabase((target as Int).toInt())
        model.getShortcutsID().add(target as Int?)
      } else if (action === REMOVE_BOOKMARK) {
        removeShortcutsFromDatabase((target as Int).toInt())
        model.getShortcutsID().remove(target)
      }
      markAsDirtyRecursive()
    }
  }

  /**
   * Returns the module having the given ID.
   * @param id The module ID.
   * @return The module object.
   */
  private fun getModuleByID(id: Int): Module? {
    for (i in 0 until (model as VMenuTree).getModuleArray().length) {
      if ((model as VMenuTree).getModuleArray().get(i).getId() === id) {
        return (model as VMenuTree).getModuleArray().get(i)
      }
    }
    return null
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  val bookmark: UBookmarkPanel
    get() = object : UBookmarkPanel() {
      var isVisible: Boolean
        get() = false
        set(visible) {}
      var isEnabled: Boolean
        get() = false
        set(enabled) {}

      fun toFront() {}
      fun show() {}
    }
  val model: VMenuTree
    get() = super.getModel() as VMenuTree

  // --------------------------------------------------
  // UMenuTree IMPLEMENTATION
  // --------------------------------------------------
  fun getTree(): UTree? {
    return tree
  }
  // --------------------------------------------------
  // LISTENERS
  // --------------------------------------------------
  /**
   * The `ItemClickHandler` is the menu tree implementation
   * of the [ItemClickListener].
   */
  private inner class ItemClickHandler : ItemClickListener {
    fun itemClick(event: ItemClickEvent) {
      tree.setValue(event.getItemId())
      if (event.isDoubleClick()) {
        callSelectedForm()
      } else {
        setMenu()
      }
    }
  }

  /**
   * The `CollapseHandler` is the menu tree implementation
   * of the [CollapseListener].
   */
  private inner class CollapseHandler : CollapseListener {
    fun nodeCollapse(event: CollapseEvent) {
      tree.setValue(event.getItemId())
      tree.valueChanged()
    }
  }

  /**
   * The `ExpandHandler` is the menu tree implementation
   * of the [ExpandListener].
   */
  private inner class ExpandHandler : ExpandListener {
    fun nodeExpand(event: ExpandEvent) {
      tree.setValue(event.getItemId())
      tree.valueChanged()
    }
  }

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private var tree: Tree? = null
  private val ADD_BOOKMARK: Action
  private val REMOVE_BOOKMARK: Action

  companion object {
    private const val serialVersionUID = -6740174181163603800L
  }
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  /**
   * Creates a new `DMenuTree` instance from a model.
   * @param model The menu tree model.
   */
  init {
    ADD_BOOKMARK = Action(model.getActor(VMenuTree.CMD_ADD).menuItem)
    REMOVE_BOOKMARK = Action(model.getActor(VMenuTree.CMD_REMOVE).menuItem)
    if (!model.isSuperUser()) {
      // if we are not in a super user context, the menu is
      // handled by the module menu component.
      // The menu tree is handled differently comparing to swing
      // version.
      // The tree component is used only in a super user context.
    } else {
      val content: Panel
      tree = Tree(model.getRoot(), model.isSuperUser())
      content = Panel(tree)
      tree.addActionHandler(this)
      tree.addItemClickListener(ItemClickHandler())
      tree.addCollapseListener(CollapseHandler())
      tree.addExpandListener(ExpandHandler())
      tree.addItemSetChangeListener(object : ItemSetChangeListener() {
        fun containerItemSetChange(event: ItemSetChangeEvent?) {
          setMenu()
        }
      })
      tree.addValueChangeListener(object : ValueChangeListener() {
        fun valueChange(event: ValueChangeEvent) {
          val itemId: Any = event.getProperty().getValue() ?: return
          // tree.restoreLastModifiedItem();
          tree.setIcon(itemId,
                       !tree.areChildrenAllowed(itemId),
                       tree.getParent(itemId) == null,
                       tree.getModule(itemId).getAccessibility())
          setMenu()
        }
      })
      model.setDisplay(this)
      tree.setSizeUndefined()
      tree.expandRow(0)
      setMenu()
      tree.setValue(null)
      tree.setNullSelectionAllowed(false)
      // allow scrolling when an overflow is detected
      content.setWidth(455, Unit.PIXELS)
      content.setHeight(600, Unit.PIXELS)
      setContent(content)
    }
  }
}