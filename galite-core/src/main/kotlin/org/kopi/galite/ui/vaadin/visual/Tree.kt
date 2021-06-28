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

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

import org.kopi.galite.ui.vaadin.base.Utils
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.Item
import org.kopi.galite.visual.UItemTree.UTreeComponent

import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.treegrid.TreeGrid

/**
 * The vaadin implementation of an [UTreeComponent].
 *
 * @param root The root tree item.
 * @param isNoEdit Is selection mode set to no edit ?
 */
class Tree(
  val root: TreeNode?,
  private val isNoEdit: Boolean,
  private val localised: Boolean
) : TreeGrid<TreeNode>(), UTreeComponent {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var lastModifiedItemId: TreeNode? = null
  private var itemsIds = mutableMapOf<Int, TreeNodeComponent>()

  init {
    setSizeFull()
    setSelectionMode(SelectionMode.SINGLE)
    buildTreeItems()
  }
  //----------------------------------------------------------
  // IMPLEMENTATIONS
  //----------------------------------------------------------
  /**
   * Builds the tree container from a root item.
   * @return The tree container.
   */
  private fun buildTreeItems() {
    setItems(getRootItems(), ::getChildItemProvider)
    addComponentHierarchyColumn {
      val nodeComponent = addItemComponent(it)

      nodeComponent.setIcon(it.isLeaf)
      nodeComponent
    }
  }

  private fun getRootItems(): List<TreeNode> {
    val rootItems = mutableListOf<TreeNode>()

    root?.let { rootItems.addNode(it) }
    return rootItems
  }

  private fun getChildItemProvider(parent: TreeNode): List<TreeNode> {
    val childItems = mutableListOf<TreeNode>()

    for (i in 0 until parent.childCount) {
      val node = parent.getChildAt(i)
      addItemComponent(node)
      childItems.add(node)
    }

    return childItems
  }

  private fun MutableList<TreeNode>.addNode(node: TreeNode) {
    if(node.parent == null) {
      addItemComponent(node)
      add(node)
    }

    for (i in 0 until node.childCount) {
      addNode(node.getChildAt(i))
    }
  }

  internal fun addItemComponent(node: TreeNode): TreeNodeComponent {
    val item = getITEM(node)
    val nodeComponent = TreeNodeComponent(node, item)

    itemsIds[item!!.id] = nodeComponent

    return nodeComponent
  }

  /**
   * Returns the [Item] corresponding of the given tree item.
   *
   * @param itemId The tree item.
   *
   * @return The corresponding tree item
   */
  fun getITEM(itemId: TreeNode?): Item? =
    if(itemId == null) null else (itemId as DefaultMutableTreeNode).userObject as? Item

  /**
   * Emits the value change event. The value contained in the field is validated before the event is created.
   */
  fun valueChanged() {
    dataProvider.refreshAll()
  }

  val selectedItem: TreeNode? get() = asSingleSelect().value

  fun getNodeComponent(id: Int): TreeNodeComponent? = itemsIds[id]

  //----------------------------------------------------------
  // TREE IMPLEMENTATION
  //----------------------------------------------------------
  override fun collapseRow(row: Int) {
    access {
      collapse(itemsIds[row]?.nodeItem)
    }
  }

  override fun expandRow(row: Int) {
    access {
      expand(itemsIds[row]?.nodeItem)
    }
  }

  override fun expandTree() {
    expandNode(getItem(-1))
  }

  /**
   * Return items list
   */
  fun expandNode(node: TreeNode?) {
    val item = getITEM(node)
    val childrenCollection = getChildrenOf(node)
    if (childrenCollection != null && childrenCollection.isNotEmpty()) {
      childrenCollection.forEach {
        expandNode(it)
      }
    }
    if (item!!.isSelected || isExpanded(node)) {
      expandRow(item.parent)
    }
  }

  override fun getSelectionRow(): Int = getITEM(selectedItem)?.id ?: -1

  @Suppress("INAPPLICABLE_JVM_NAME")
  @JvmName("isExpanded1")
  override fun isExpanded(path: Any?): Boolean = super.isExpanded(path as? TreeNode)

  override fun isCollapsed(path: Any?): Boolean {
    return !isExpanded(path)
  }

  override fun getItems(): Array<Item>? {
    val itemsList = getItems(getItem(-1))

    return if(itemsList.isEmpty()) null else itemsList.toTypedArray()
  }

  override fun isUnique(name: String): Boolean = isUnique(name, getItem(-1))

  override fun getRootItem(): Item = getRootItem(getItem(-1))

  /**
   * Return true if the item name done is unique in this tree
   */
  fun isUnique(name: String?, node: TreeNode?): Boolean {
    val item = getITEM(node)
    return if (name == item!!.name && item.id != getITEM(selectedItem)?.id) {
      false
    } else {
      val childrenCollection = getChildrenOf(node)
      if (childrenCollection != null && childrenCollection.isNotEmpty()) {
        var isUnique = true
        var i = 0
        while (isUnique && i < childrenCollection.size) {
          isUnique = isUnique(name, childrenCollection[i])
          i++
        }
        isUnique
      } else {
        true
      }
    }
  }

  /**
   * Return items list
   */
  fun getItems(node: TreeNode?): List<Item> {
    val items = mutableListOf<Item>()
    val item = getITEM(node)
    if (item!!.id >= 0) {
      items.add(item)
    }
    val childrenCollection = getChildrenOf(node)
    if (childrenCollection != null && childrenCollection.isNotEmpty()) {
      item.childCount = childrenCollection.size
      childrenCollection.forEach {
        items.addAll(getItems(it))
      }
    } else {
      item.childCount = 0
    }
    return items
  }

  /**
   * Return items list
   */
  fun getRootItem(node: TreeNode?): Item {
    val childrenCollection = getChildrenOf(node)
    val item = getITEM(node)

    if (childrenCollection != null && childrenCollection.isNotEmpty()) {
      val children = arrayOfNulls<Item>(childrenCollection.size)
      childrenCollection.forEachIndexed { index, treeNode ->
        children[index] = getRootItem(treeNode)
      }
      item!!.childCount = childrenCollection.size
      item.children = children.requireNoNulls()
    } else {
      item!!.childCount = 0
      item.children = null
    }
    return item
  }

  fun getItem(itemId: Int): TreeNode? = itemsIds[itemId]?.nodeItem

  fun getChildrenOf(itemId: Int): Array<TreeNode>? = treeData.getChildren(getItem(itemId))?.toTypedArray()

  fun getChildrenOf(node: TreeNode?): Array<TreeNode>? = treeData.getChildren(node)?.toTypedArray()

  inner class TreeNodeComponent(val nodeItem: TreeNode, val item: Item?): HorizontalLayout() {

    private val text = Span(item?.getFormattedName(localised))
    private val icon = Image()

    init {
      add(icon, text)
    }

    /**
     * Sets the item icon.
     *
     * @param isLeaf Is it a leaf tree item ?
     */
    fun setIcon(isLeaf: Boolean) {
      if (isNoEdit) {
        if (isLeaf) {
          setItemIcon(Utils.getImage("item.png").resource)
        } else {
          setItemIcon(Utils.getImage("node.png").resource)
        }
      } else {
        setIcon(item)
      }

      lastModifiedItemId = nodeItem
    }

    /**
     * Set icon according to item status.
     * @param item The tree item .
     */
    fun setIcon(item: Item?) {
      access {
        if (item!!.id != -1) {
          if (item.isDefaultItem) {
            setItemIcon(Utils.getImage("default.png").resource)
          } else {
            if (item.isSelected) {
              setItemIcon(Utils.getImage("checked.png").resource)
            } else {
              setItemIcon(Utils.getImage("unchecked.png").resource)
            }
          }
        }
      }
    }

    private fun setItemIcon(image: String) {
      icon.src = image // TODO
    }
  }
}
