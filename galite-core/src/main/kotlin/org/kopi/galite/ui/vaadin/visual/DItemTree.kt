/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.accessAndPush
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.Utils.findMainWindow
import org.kopi.galite.ui.vaadin.window.PopupWindow
import org.kopi.galite.visual.Item
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.UItemTree
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VItemTree
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VlibProperties

import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.ItemClickEvent
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

/**
 * The `DItemTree` is the vaadin implementation of the
 * [UItemTree].
 *
 *
 * The implementation is based on [DWindow]
 *
 * @param model The item tree model.
 *
 * TODO Externalize favorites handling.
 */
class DItemTree(model: VItemTree) : DWindow(model), UItemTree {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private var tree = Tree(model.root, model.isNoEdit(), model.isLocalised)
  private var inputDialog: PopupWindow? = null
  private lateinit var editTextField: TextField
  private var errorLabel: Label? = null
  private var newItem = false
  private var localisation = false

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  init {
    val content = VerticalLayout(tree)
    tree.addItemClickListener(ItemClickHandler())
    //tree.addCollapseListener(DItemTree.CollapseHandler())
    //tree.addExpandListener(DItemTree.ExpandHandler())
    content.className = "itemtree"
    /*tree.addItemSetChangeListener(object : ItemSetChangeListener() { TODO
      fun containerItemSetChange(event: ItemSetChangeEvent?) {
        setTree()
      }
    })
    tree.addValueChangeListener(object : ValueChangeListener() {
      fun valueChange(event: ValueChangeEvent) {
        val itemId: Any = event.getProperty().getValue() ?: return
        val item = tree.getITEM(itemId)
        tree.setIcon(item, isLeaf())
        setTree()
      }
    })*/
    model.setDisplay(this)
    tree.setSizeUndefined()
    if (model.isNoEdit()) {
      tree.expandRow(0)
    } else {
      tree.expandTree()
    }
    setTree()
    // allow scrolling when an overflow is detected
    content.width = "900px"
    content.height = "800px"
    setContent(content)
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  /**
   * Sets item selection state.
   */
  override fun setSelectedItem() {
    val item = getSelectedItem()
    if (item != null) {
      if (!getModel()!!.isNoEdit()) {
        if (getModel()!!.isSingleSelection()) {
          if (!item.isSelected) {
            unselectAll(-1)
            item.isSelected = true
          } else {
            item.isSelected = false
          }
        } else {
          item.isSelected = !item.isSelected
        }
        // tree.setIcon(item) TODO
        getModel()!!.refresh()
      } else {
        if (tree.isExpanded(tree.selectedItem)) {
          tree.collapse(tree.selectedItem)
        } else {
          tree.expand(tree.selectedItem)
        }
      }
    }
  }

  /**
   * Set selection value to false for all children
   */
  private fun unselectAll(itemID: Int) {
    val children = tree.getChildrenOf(itemID)
    children?.forEach {
      val item = tree.getITEM(it) as Item
      if (item.isSelected) {
        item.isSelected = false
        // tree.setIcon(item) TODO
      }
      val Grandsons = tree.getChildrenOf(item.id)
      if (Grandsons != null && Grandsons.isNotEmpty()) {
        unselectAll(item.id)
      }
    }
  }

  override fun setDefaultItem() {
    val item = getSelectedItem()
    if (item != null) {
      if (!item.isDefaultItem) {
        setDefault(-1)
        item.isDefaultItem = true
      } else {
        item.isDefaultItem = false
      }
      item.isSelected = true
      // tree.setIcon(item) TODO
      getModel()!!.refresh()
    }
  }

  /**
   * Set default value for all children
   */
  private fun setDefault(itemID: Int) {
    val children = tree.getChildrenOf(itemID)
    children?.forEach {
      val item = tree.getITEM(it) as Item
      if (item.isDefaultItem) {
        item.isDefaultItem = false
        // tree.setIcon(item) TODO
      }
      val Grandsons = tree.getChildrenOf(item.id)
      if (Grandsons != null && Grandsons.isNotEmpty()) {
        setDefault(item.id)
      }
    }
  }

  override fun addItem() {
    val item = getSelectedItem()
    if (item != null) {
      if (getModel()!!.depth > 0 && item.level + 1 > getModel()!!.depth) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00069", getModel()!!.depth))
      }
      localisation = false
      showInputDialog(item, true)
    }
  }

  override fun removeSelectedItem() {
    val item = getSelectedItem()
    if (item != null) {
      access(currentUI) {
        if (getModel()!!.isRemoveDescendantsAllowed) {
          removeChildren(item)
        } else {
          attacheToParent(item)
        }
        getModel()!!.removedItems.add(item)
        tree.treeData.removeItem(tree.getItem(item.id))
        tree.valueChanged()
      }
    }
  }

  /**
   * Remove all children of an item
   * @param parentId the parent ID
   */
  fun removeChildren(parent: Item) {
    val children = tree.getChildrenOf(parent.id)
    children?.forEach {
      removeChildren(tree.getITEM(it) as Item)
    }
    getModel()!!.removedItems.add(parent)
    tree.treeData.removeItem(tree.getItem(parent.id))
  }

  /**
   * Attache children to removed item parent
   */
  fun attacheToParent(item: Item) {
    val children = tree.getChildrenOf(item.id)
    children?.forEach {
      tree.treeData.setParent(it, tree.getItem(item.parent))
      tree.getITEM(it)!!.parent = item.parent
      setLevel(it)
    }
  }

  /**
   * Sets level value for all children of an item
   * @param itemId the item ID
   */
  fun setLevel(itemId: TreeNode?) {
    val children = tree.getChildrenOf(itemId)
    children?.forEach {
      setLevel(it)
    }
    tree.getITEM(itemId)!!.decrementLevel()
  }

  /**
   * Localise the selected item
   */
  override fun localiseSelectedItem() {
    val item = getSelectedItem()
    if (item != null) {
      localisation = true
      showInputDialog(item, false)
    }
  }

  /**
   * Edit selected item's name
   */
  override fun editSelectedItem() {
    val item = getSelectedItem()
    if (item != null) {
      localisation = false
      showInputDialog(item, false)
    }
  }

  /**
   * Shows a modal window in an inputDialog view. This will handle
   * a window view
   * @param item item to edit or add new child
   * @param newItem if true, it is a new item to insert
   */
  protected fun showInputDialog(
    item: Item,
    newItem: Boolean
  ) {
    val maxLength =
      VItemTree.MAX_LENGTH.coerceAtMost(if (localisation) getModel()!!.localisedNameLength else getModel()!!.nameLength)
    if (inputDialog == null) {
      createInputDialog(localisation)
    }
    accessAndPush(currentUI) {
      editTextField.value = if (newItem) "" else if (localisation) if (item.localisedName != null) item.localisedName else "" else item.name
      editTextField.maxLength = maxLength
      editTextField.width = "" + maxLength + "em"
      this.newItem = newItem
      inputDialog!!.setCaption(if (localisation) item.name else VlibProperties.getString(if (newItem) "New_item" else "OpenLine"))
      editTextField.focus()
      inputDialog!!.open()
    }
  }

  /**
   * Create an input dialog
   *
   * @param localisation if true, the edit text will
   * contain the localised item's name
   */
  private fun createInputDialog(localisation: Boolean) {
    val popupContent = VerticalLayout()
    popupContent.width = "400px"
    editTextField = TextField("")
    //editTextField.setWidth(180, Unit.PIXELS);
    editTextField.className = Styles.TEXT_INPUT
    errorLabel = Label(MessageCode.getMessage("VIS-00020", getModel()!!.getTitle()))
    errorLabel!!.isVisible = false
    errorLabel!!.addClassName("notificationlabel")
    popupContent.add(errorLabel)
    popupContent.add(editTextField)
    // popupContent.setComponentAlignment(editTextField, Alignment.MIDDLE_CENTER) TODO
    val buttonsContainer = HorizontalLayout()
    val okButton = Button("OK") {
      editItem(getSelectedItem())
    }
    okButton.width = "80px"
    okButton.className = "inputbutton"
    val cancelButton = Button("Annuler") {
      errorLabel!!.isVisible = false
      inputDialog!!.close()
    }
    cancelButton.width = "80px"
    cancelButton.className = "inputbutton"
    buttonsContainer.add(okButton)
    buttonsContainer.add(cancelButton)
    popupContent.add(buttonsContainer)
    // popupContent.setComponentAlignment(buttonsContainer, Alignment.MIDDLE_CENTER) TODO
    inputDialog = PopupWindow(findMainWindow())
    inputDialog!!.isModal = true
    inputDialog!!.setContent(popupContent)
    inputDialog!!.className = "inputdialog"
  }

  /**
   * Edit or add new item to the tree
   *
   * @param item        the selected item
   */
  private fun editItem(item: Item?) {
    val itemName: String = editTextField.value
    if (itemName.isNotEmpty()) {
      if (getTree().isUnique(itemName)) {
        errorLabel!!.isVisible = false
        inputDialog!!.close()
        if (newItem) {
          addItem(itemName, null, null, item)
        } else {
          if (localisation) {
            item!!.localisedName = itemName
          } else {
            item!!.name = itemName
          }
          /*tree.getItem(item.id).getItemProperty(Tree.ITEM_PROPERTY_NAME).setValue( TODO
            item.getFormattedName(
              getModel()!!.isLocalised
            )
          )*/
        }
        tree.valueChanged()
      } else {
        errorLabel!!.isVisible = true
        editTextField.focus()
      }
    }
  }

  /**
   * Add new Item
   *
   * @param itemName            the new item's name
   * @param localisedName       the itme localised name
   * @param description         the itme description
   * @param parent              the parent item
   */
  private fun addItem(
    itemName: String,
    localisedName: String?,
    description: String?,
    parent: Item?
  ) {
    val node = DefaultMutableTreeNode()
    val item = Item(
      getModel()!!.getNextId(),
      parent!!.id,
      itemName,
      localisedName,
      description,
      false,
      false,
      null,
      itemName
    )
    node.userObject = item
    val nodeComponent = tree.addItemComponent(node)

    item.level = parent.level + 1
    // dataItem.getItemProperty(Tree.ITEM_PROPERTY_ICON).setValue(item.icon) TODO
    tree.treeData.addItem(tree.getItem(parent.id), tree.getItem(item.id))
    nodeComponent.setIcon(item)
    tree.expand(tree.getItem(parent.id))
  }

  override fun setTree() {
    val item: Item?
    if (getModel() != null) {   // The model can be destroyed by it's itemTreeManager:
      // the save action close the Item Tree window
      item = getSelectedItem()
      getModel()!!.setActorEnabled(VItemTree.CMD_QUIT, true)
      if (item != null) {
        getModel()!!.setActorEnabled(VItemTree.CMD_ADD, getModel()!!.isInsertMode)
        getModel()!!.setActorEnabled(VItemTree.CMD_REMOVE, getModel()!!.isInsertMode && item.id != -1)
        getModel()!!.setActorEnabled(VItemTree.CMD_EDIT, getModel()!!.isInsertMode && item.id != -1)
        getModel()!!.setActorEnabled(
          VItemTree.CMD_LOCALISE,
          getModel()!!.isInsertMode && getModel()!!.isLocalised && item.id != -1
        )
        getModel()!!.setActorEnabled(VItemTree.CMD_SELECT, !getModel()!!.isNoEdit() && item.id != -1)
        getModel()!!.setActorEnabled(VItemTree.CMD_DEFAULT, getModel()!!.isMultiSelectionDefaultValue() && item.id != -1)
        if (isLeaf()) {
          getModel()!!.setActorEnabled(VItemTree.CMD_FOLD, false)
          getModel()!!.setActorEnabled(VItemTree.CMD_UNFOLD, false)
        } else {
          if (tree.isExpanded(tree.selectedItem)) {
            getModel()!!.setActorEnabled(VItemTree.CMD_FOLD, true)
            getModel()!!.setActorEnabled(VItemTree.CMD_UNFOLD, false)
          } else {
            getModel()!!.setActorEnabled(VItemTree.CMD_FOLD, false)
            getModel()!!.setActorEnabled(VItemTree.CMD_UNFOLD, true)
          }
        }
      } else {
        getModel()!!.setActorEnabled(VItemTree.CMD_FOLD, false)
        getModel()!!.setActorEnabled(VItemTree.CMD_UNFOLD, false)
        getModel()!!.setActorEnabled(VItemTree.CMD_ADD, false)
        getModel()!!.setActorEnabled(VItemTree.CMD_REMOVE, false)
        getModel()!!.setActorEnabled(VItemTree.CMD_EDIT, false)
        getModel()!!.setActorEnabled(VItemTree.CMD_LOCALISE, false)
        getModel()!!.setActorEnabled(VItemTree.CMD_SELECT, false)
        getModel()!!.setActorEnabled(VItemTree.CMD_DEFAULT, false)
      }
      getModel()!!.setActorEnabled(VItemTree.CMD_SAVE, getModel()!!.isChanged)
    }
  }

  override fun run() {
    isVisible = true
    tree.focus()
    tree.expand(tree.getItem(-1))
  }

  /**
   * Returns the selected item.
   * @return The selected item.
   */
  fun getSelectedItem(): Item? {
    return if (tree.selectedItem != null) {
      tree.getITEM(tree.selectedItem)
    } else null
  }

  /**
   * Returns true if the selected item is leaf
   */
  fun isLeaf(): Boolean {
    return !tree.dataProvider.hasChildren(tree.selectedItem)
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  override fun getModel(): VItemTree? = super.getModel() as VItemTree?

  // --------------------------------------------------
  // UItemTree IMPLEMENTATION
  // --------------------------------------------------
  override fun getTree(): UItemTree.UTreeComponent = tree
  // --------------------------------------------------
  // LISTENERS
  // --------------------------------------------------
  /**
   * The `ItemClickHandler` is the item tree implementation
   * of the [ItemClickListener].
   */
  private inner class ItemClickHandler : ComponentEventListener<ItemClickEvent<TreeNode>> {
    override fun onComponentEvent(event: ItemClickEvent<TreeNode>) {
      if (event.clickCount == 2) {
        try {
          setSelectedItem()
        } catch (e: VException) {
          throw VRuntimeException(e.message, e)
        }
      } else {
        setTree()
      }
    }
  }

  /**
   * The `CollapseHandler` is the item tree implementation
   * of the [CollapseListener].
   */
  /*private inner class CollapseHandler : ComponentEventListener<CollapseEvent<TreeNode, TreeGrid<TreeNode>>> {
    override fun onComponentEvent(event: CollapseEvent<TreeNode, TreeGrid<TreeNode>>) {
      tree.valueChanged()
    }
  }*/

  /**
   * The `ExpandHandler` is the item tree implementation
   * of the [ExpandListener].
   */
  /*private inner class ExpandHandler : ComponentEventListener<ExpandEvent<TreeNode, TreeGrid<TreeNode>>> {
    override fun onComponentEvent(event: ExpandEvent<TreeNode, TreeGrid<TreeNode>>) {
      tree.valueChanged()
    }
  }*/
}
