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

import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.hierarchy.TreeData
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider
import com.vaadin.flow.data.selection.MultiSelect
import org.kopi.galite.ui.base.Utils
import org.kopi.galite.visual.Tree.Companion.ITEM_PROPERTY_ICON
import org.kopi.galite.visual.Tree.Companion.ITEM_PROPERTY_NAME
import java.util.ArrayList
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode



/**
 * The vaadin implementation of an [UTreeComponent].
 * The component is based on the [com.vaadin.ui.Tree]
 */
class Tree(root: TreeNode,
           isNoEdit: Boolean,
           localised: Boolean)   : TreeGrid<Any>(TreeDataProvider(TreeDataSource(root,
                                                                                 isNoEdit,
                                                                                 localised))) {
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new `Tree` instance.
   * @param root The root tree item.
   * @param isNoEdit Is it a super user ?
   */
  init {
    val treeDataSource = TreeDataSource(root, isNoEdit, localised)
    val treeDataProvider = TreeDataProvider(treeDataSource)
    addHierarchyColumn(treeDataProvider.treeData)

    setItemCaptionPropertyId(ITEM_PROPERTY_NAME)
    setItemIconPropertyId(ITEM_PROPERTY_ICON)
    setItemCaptionMode(ItemCaptionMode.PROPERTY)
  }

class TreeDataSource(root: TreeNode,
                           private val isNoEdit: Boolean,
                           private val localised: Boolean) : TreeData<Any>() {

  /**
   * Sets the item icon.
   * @param item The item.
   * @param isLeaf Is it a leaf tree item ?
   */
  fun setIcon(item: Item, isLeaf: Boolean) {
    if (isNoEdit || item.id === -1) {
      if (isLeaf) {
        setIcon(item.id, Utils.getImage("item.png").getResource())
      } else {
        setIcon(item.id, Utils.getImage("node.png").getResource())
      }
    } else {
      setIcon(item)
    }
    lastModifiedItemId = item.id
  }

  /**
   * Restores the last modified tree item.
   */
  fun restoreLastModifiedItem() {
    if (lastModifiedItemId != null && !areChildrenAllowed(lastModifiedItemId)) {
      setItemIcon(lastModifiedItemId, Utils.getImage("forms.png").getResource())
    }
  }

  /**
   * Returns the [ITEM] corresponding of the given tree item.
   * @param itemId The tree item ID.
   * @return The corresponding tree item
   */
  fun getItem(itemId: Any?): Item? {
    val dataItem: Item? = getItem(itemId)
    return dataItem?.getItemProperty(ITEM_PROPERTY)?.getValue()
  }

  //----------------------------------------------------------
  // TREE IMPLEMENTATION
  //----------------------------------------------------------

  /**
   * Return items list
   */
  fun expandNode(node: Any?) {
    val childrenCollection: Collection<*>?
    val item: Item? = getItem(node)
    childrenCollection = getChildren(node)
    if (childrenCollection != null && childrenCollection.isNotEmpty()) {
      val childrenList: Array<Any?> = childrenCollection.toTypedArray()
      for (i in childrenList.indices) {
        expandNode(childrenList[i])
      }
    }
    if (item.selected || isExpanded(node)) {
      expandRow(item.parent)
    }
  }

  val selectionRow: Int
    get() = if (getValue() == null) {
      0
    } else (getValue() as Int).toInt()

  fun isCollapsed(path: Any): Boolean {
    return !isExpanded(path)
  }

  /**
   * Return items array
   */
  val items: Array<Item>?
    get() {
      val itemsList = getItems(getItem(-1))
      return if (itemsList != null) {
        Utils.toArray(itemsList, Item::class.java)
      } else null
    }

  /**
   * Return true if the item name done is unique in this tree
   */
  fun isUnique(name: String): Boolean {
    return isUnique(name, -1)
  }

  /**
   * Return true if the item name done is unique in this tree
   */
  fun isUnique(name: String, node: Any?): Boolean {
    val item: Item?
    val childrenCollection: Collection<*>?
    item = getItem(node)
    return if (name == item!!.name && item!!.id !== getValue() as Int) {
      false
    } else {
      childrenCollection = getChildren(node)
      if (childrenCollection != null && childrenCollection.size > 0) {
        val childrenList: Array<Any> = childrenCollection.toTypedArray()
        var isUnique = true
        var i = 0
        while (isUnique && i < childrenList.size) {
          isUnique = isUnique(name, childrenList[i])
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
  fun getItems(node: Any?): List<Item>? {
    val items: MutableList<Item>
    val item: Item
    val childrenCollection: Collection<*>?
    items = ArrayList()
    item = getItem(node)
    if (item.id >= 0) {
      items.add(item)
    }
    childrenCollection = getChildren(node)
    if (childrenCollection != null && childrenCollection.isNotEmpty()) {
      val childrenList: Array<Any> = childrenCollection.toTypedArray()!!
      item.childCount = (childrenList.size)
      for (i in childrenList.indices) {
        items.addAll(getItems(childrenList[i]))
      }
    } else {
      item.childCount = 0
    }
    return items
  }

  /**
   * Return items array
   */
  fun getRootItem(): Item {
    return getRootItem(-1)
  }


  /**
   * Return items list
   */
  fun getRootItem(node: Any?): Item {
    val childrenCollection: Collection<*>?
    val item: Item? = getItem(node)
    childrenCollection = getChildren(node)
    if (childrenCollection != null && childrenCollection.isNotEmpty()) {
      val children: Array<Item?>
      val childrenList: Array<Any> = childrenCollection.toTypedArray()!!
      children = arrayOfNulls(childrenList.size)
      for (i in childrenList.indices) {
        children[i] = getRootItem(childrenList[i])
      }
      item!!.childCount = (childrenList.size)
      item.children = (children)
    } else {
      item!!.childCount = (0)
      item.children = (null)
    }
    return item
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var lastModifiedItemId: Any? = null

}
  companion object {
    const val ITEM_PROPERTY_NAME = "name"
    const val ITEM_PROPERTY_ICON = "icon"
    const val ITEM_PROPERTY = "item"
  }

}