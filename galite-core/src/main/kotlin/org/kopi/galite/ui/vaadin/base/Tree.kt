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
package org.kopi.galite.ui.vaadin.base

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.treegrid.TreeGrid

import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.Module
import org.kopi.galite.visual.UItemTree.UTreeComponent
import org.kopi.galite.visual.UMenuTree.UTree

/**
 * The vaadin implementation of an [UTreeComponent].
 *
 * @param root The root tree item.
 * @param isSuperUser Is it a super user ?
 */
@CssImport.Container(value = [
  CssImport("./styles/galite/menutree.css"),
  CssImport(value = "./styles/galite/menutree.css", themeFor = "vaadin-grid-tree-toggle")
])
class Tree(val root: TreeNode, private val isSuperUser: Boolean) : TreeGrid<TreeNode>(), UTree {

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

      nodeComponent.setIcon(it.isLeaf, it.parent == null, nodeComponent.module!!.accessibility)
      nodeComponent
    }
  }

  private fun getRootItems(): List<TreeNode> {
    val rootItems = mutableListOf<TreeNode>()
    rootItems.addNode(root)
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

  private fun addItemComponent(node: TreeNode): TreeNodeComponent {
    val module = getModule(node)
    val nodeComponent = TreeNodeComponent(node, module)

    itemsIds[module!!.id] = nodeComponent

    return nodeComponent
  }

  /**
   * Returns the [Module] corresponding of the given tree item.
   * @param itemId The tree item ID.
   * @return The corresponding tree item module.
   */
  fun getModule(itemId: TreeNode?): Module? =
      if(itemId == null) null else (itemId as DefaultMutableTreeNode).userObject as Module

  val selectedItem: TreeNode? get() = asSingleSelect().value

  fun getNodeComponent(id: Int): TreeNodeComponent? = itemsIds[id]

  var currentUI: UI? = null

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }
  //----------------------------------------------------------
  // TREE IMPLEMENTATION
  //----------------------------------------------------------
  override fun collapseRow(row: Int) {
    access(currentUI) {
      collapse(itemsIds[row]?.item)
    }
  }

  override fun expandRow(row: Int) {
    access(currentUI) {
      expand(itemsIds[row]?.item)
    }
  }

  override val selectionRow: Int get() = selectedItem?.let { getModule(it)?.id } ?: 0

  @Suppress("INAPPLICABLE_JVM_NAME")
  @JvmName("isExpanded1")
  override fun isExpanded(path: Any?): Boolean = super.isExpanded(path as? TreeNode)

  override fun isCollapsed(path: Any?): Boolean {
    return !isExpanded(path)
  }

  inner class TreeNodeComponent(val item: TreeNode, val module: Module?): HorizontalLayout() {

    private val nodeCaption = Div()
    private val text = Span(module?.description)
    private val icon = Image()

    init {
      nodeCaption.className = "tree-node-caption"
      icon.className = "icon"
      element.setProperty("title", module?.help.orEmpty())
      nodeCaption.add(icon, text)
      add(nodeCaption)
    }

    /**
     * Sets the item icon.
     *
     * @param isLeaf Is it a leaf tree item ?
     * @param isRoot Is it a root tree item ?
     * @param access The tree item access.
     */
    fun setIcon(isLeaf: Boolean, isRoot: Boolean, access: Int) {
      if (isRoot) {
        setItemIcon(Utils.getImage("home.png").resource)
      } else {
        if (isLeaf) {
          if (!isSuperUser) {
            if (item == selectedItem) {
              setItemIcon( Utils.getImage("form_selected.png").resource)
            } else {
              setItemIcon(Utils.getImage("forms.png").resource)
            }
          } else {
            setIcon(access, true)
          }
        } else {
          if (!isSuperUser) {
            if (isExpanded(item)) {
              setItemIcon(Utils.getImage("expanded.png").resource)
            } else {
              setItemIcon(Utils.getImage("collapsed.png").resource)
            }
          } else {
            setIcon(access, false)
          }
        }
      }
      lastModifiedItemId = item
    }

    /**
     * Set icon according to module accessibility.
     * @param access The module accessibility.
     * @param item The tree item.
     * @param isLeaf Is it a leaf node ?
     */
    fun setIcon(access: Int, isLeaf: Boolean) {
      access(currentUI) {
        when (access) {
          Module.ACS_FALSE -> if (isLeaf) {
            setItemIcon(Utils.getImage("form_p.png").resource)
          } else {
            if (isExpanded(item)) {
              setItemIcon(Utils.getImage("expanded_p.png").resource)
            } else {
              setItemIcon(Utils.getImage("collapsed_p.png").resource)
            }
          }
          Module.ACS_TRUE -> if (isLeaf) {
            setItemIcon(Utils.getImage("form_a.png").resource)
          } else {
            if (isExpanded(item)) {
              setItemIcon(Utils.getImage("expanded_a.png").resource)
            } else {
              setItemIcon(Utils.getImage("collapsed_a.png").resource)
            }
          }
          else -> if (isLeaf) {
            setItemIcon(Utils.getImage("forms.png").resource)
          } else {
            if (isExpanded(item)) {
              setItemIcon(Utils.getImage("expanded.png").resource)
            } else {
              setItemIcon(Utils.getImage("collapsed.png").resource)
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
