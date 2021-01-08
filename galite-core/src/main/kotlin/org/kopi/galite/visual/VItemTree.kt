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
import java.util.Locale

import javax.swing.tree.TreeNode

import kotlin.system.exitProcess

import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.base.InconsistencyException

/**
 * Represents a new instance of VItemTree.
 *
 * @param rootName the rot item name
 * @param items the items list
 * @param isInsertMode enable Add and Remove items
 * @param selectionType item selection mode :
 *        1 : no edit
 *        2 : Single selection
 *        3 : Multi selection
 *        4 : Multi selection with default value
 * @param isLocalised if true, enable item localisation
 * @param itemTreeManager the tree save manager
 * @param isRemoveDescendantsAllowed if true, remove item descendants when removing item
 * @param nameLength max length of item name
 * @param localisedNameLength max length of item localisation
 * @param descriptionLength max length of item description
 */
class VItemTree(rootName: String?,
                private val items: Array<Item>,
                val depth: Int,
                val isInsertMode: Boolean,
                private val selectionType: Int,
                val isLocalised: Boolean,
                private val itemTreeManager: ItemTreeManager,
                val isRemoveDescendantsAllowed: Boolean,
                val nameLength: Int,
                val localisedNameLength: Int,
                val descriptionLength: Int) : VWindow() {

  companion object {
    const val MAX_LENGTH = 32
    const val NO_EDIT = 1
    const val SINGLE_SELECTION = 2
    const val MULTI_SELECTION = 3
    const val MULTI_SELECTION_DEFAULT_VALUE = 4
    const val CMD_QUIT = 0
    const val CMD_SELECT = 1
    const val CMD_DEFAULT = 2
    const val CMD_FOLD = 3
    const val CMD_UNFOLD = 4
    const val CMD_SAVE = 5
    const val CMD_LOCALISE = 6
    const val CMD_EDIT = 7
    const val CMD_ADD = 8
    const val CMD_REMOVE = 9
    private const val MENU_LOCALIZATION_RESOURCE = "org/kopi/galite/Menu"

    init {
      WindowController.windowController.registerWindowBuilder(Constants.MDL_ITEM_TREE, object : WindowBuilder {
        override fun createWindow(model: VWindow): UWindow {
          return UIFactory.uiFactory.createView(model) as UItemTree
        }
      })
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  /**
   * Localize an actor
   *
   * @param     locale  the locale to use
   */
  fun localizeActors(locale: Locale?) {
    var manager: LocalizationManager?

    manager = LocalizationManager(locale, Locale.getDefault())
    try {
      super.localizeActors(manager) // localizes the actors in VWindow
    } catch (e: InconsistencyException) {
      ApplicationContext.reportTrouble("ItemTree Actor localization",
                                       "ItemTreeModel.localize",
                                       e.message,
                                       e)
      exitProcess(1)
    }
    manager = null
  }

  /**
   * Enables or disable the given actor
   */
  override fun setActorEnabled(actor: Int, enabled: Boolean) {
    treeActors[actor]!!.handler = this
    treeActors[actor]!!.isEnabled = enabled
  }

  /**
   * Returns the actor having the given number.
   */
  override fun getActor(at: Int): VActor = treeActors[at]!!

  /**
   * Creates a new actor
   */
  private fun createActor(number: Int,
                          menu: String,
                          item: String,
                          icon: String,
                          key: Int,
                          modifier: Int) {
    treeActors[number] = VActor(menu,
                                MENU_LOCALIZATION_RESOURCE,
                                item,
                                MENU_LOCALIZATION_RESOURCE,
                                icon,
                                key,
                                modifier)
    treeActors[number]!!.number = number
  }

  /**
   * Performs the appropriate action.
   *
   * @param   key           the number of the actor.
   */
  override fun executeVoidTrigger(key: Int) {
    val currentDisplay = getDisplay()

    when (key) {
      CMD_QUIT -> if (isChanged) {
        if (ask(Message.getMessage("confirm_quit"), false)) {
          currentDisplay.closeWindow()
        }
      } else {
        currentDisplay.closeWindow()
      }
      CMD_SELECT -> currentDisplay.setSelectedItem()
      CMD_ADD -> {
        currentDisplay.addItem()
        refresh()
      }
      CMD_REMOVE -> if (ask(Message.getMessage("confirm_delete"), false)) {
        currentDisplay.removeSelectedItem()
        refresh()
      }
      CMD_EDIT -> {
        currentDisplay.editSelectedItem()
        refresh()
      }
      CMD_FOLD -> currentDisplay.getTree().collapseRow(currentDisplay.getTree().getSelectionRow())
      CMD_UNFOLD -> currentDisplay.getTree().expandRow(currentDisplay.getTree().getSelectionRow())
      CMD_DEFAULT -> currentDisplay.setDefaultItem()
      CMD_LOCALISE -> {
        currentDisplay.localiseSelectedItem()
        refresh()
      }
      CMD_SAVE -> if (isChanged && itemTreeManager != null) {
        setWaitInfo("")
        itemTreeManager.save()
        unsetWaitInfo()
        isChanged = false
        currentDisplay?.setTree()
      }
      else -> super.executeVoidTrigger(key)
    }
  }

  /**
   * Refresh the item Tree view
   */
  fun refresh() {
    isChanged = true
    getDisplay().setTree()
  }

  /**
   * Builds the item tree.
   */
  private fun createTree() {
    rootItem = RootItem(-1, rootName)
    rootItem!!.createTree(items)
    root = rootItem!!.root
  }

  /**
   * Return items array
   */
  fun getItems(): Array<Item> = getDisplay().getTree().getItems()

  /**
   * Return the root item
   */
  fun getRootItem(): Item = getDisplay().getTree().getRootItem()

  /**
   * Init value of max ID
   */
  private fun initMaxId() {
    maxId = -1
    for (i in items.indices) {
      if (maxId < items[i].id) {
        maxId = items[i].id
      }
    }
  }

  override fun getType(): Int = Constants.MDL_ITEM_TREE

  override fun getDisplay(): UItemTree = super.getDisplay() as UItemTree

  fun isNoEdit(): Boolean = selectionType == NO_EDIT

  fun isSingleSelection(): Boolean = selectionType == SINGLE_SELECTION

  fun isMultiSelection(): Boolean = selectionType == MULTI_SELECTION

  fun isMultiSelectionDefaultValue(): Boolean = selectionType == MULTI_SELECTION_DEFAULT_VALUE

  fun getNextId(): Int {
    maxId++
    return maxId
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  /**
   * The root Item of this tree.
   */
  var root: TreeNode? = null
    private set
  private var rootItem: RootItem? = null
  private val treeActors: Array<VActor?> = arrayOfNulls(10)
  private var maxId = 0
  private val rootName: String = rootName ?: "Items"

  /**
   * Returns true if the model is changed
   */
  var isChanged: Boolean = false

  /**
   * The removed items list.
   */
  val removedItems = mutableListOf<Item>()

  init {
    rootName?.let { setTitle(it) }
    createActor(CMD_QUIT, "File", "Close", "quit", 0 /*KeyEvent.VK_ESCAPE*/, 0)
    createActor(CMD_SELECT, "Edit", "Select", "done", KeyEvent.VK_ENTER, 0)
    createActor(CMD_ADD, "Edit", "AddItem", "insertline", 0, 0)
    createActor(CMD_REMOVE, "Edit", "RemoveItem", "deleteline", 0, 0)
    createActor(CMD_EDIT, "Edit", "EditItem", "edit", 0, 0)
    createActor(CMD_FOLD, "Edit", "Fold", "fold", KeyEvent.VK_ENTER, 0)
    createActor(CMD_UNFOLD, "Edit", "Unfold", "unfold", KeyEvent.VK_ENTER, 0)
    createActor(CMD_DEFAULT, "Edit", "Default", "top", 0, 0)
    createActor(CMD_LOCALISE, "Edit", "Localise", "bold", 0, 0)
    createActor(CMD_SAVE, "Edit", "Save", "save", 0, 0)
    addActors(treeActors.requireNoNulls())
    localizeActors(ApplicationContext.getDefaultLocale())
    createTree()
    if (isInsertMode) {
      initMaxId()
    }
  }
}
