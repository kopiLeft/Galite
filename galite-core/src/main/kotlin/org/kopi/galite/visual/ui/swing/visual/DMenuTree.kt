/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.swing.visual

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.sql.SQLException

import javax.swing.AbstractAction
import javax.swing.ImageIcon
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.UIManager
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.database.Favorites
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.Message.getMessage
import org.kopi.galite.visual.Module
import org.kopi.galite.visual.UMenuTree
import org.kopi.galite.visual.UMenuTree.UBookmarkPanel
import org.kopi.galite.visual.UMenuTree.UTree
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VMenuTree
import org.kopi.galite.visual.VlibProperties.getString
import org.kopi.vkopi.lib.ui.swing.base.Utils
import org.kopi.vkopi.lib.ui.swing.visual.DWindow
import org.kopi.vkopi.lib.ui.swing.visual.JApplication
import org.kopi.vkopi.lib.ui.swing.visual.JBookmarkPanel
import org.kopi.vkopi.lib.ui.swing.visual.SwingThreadHandler

class DMenuTree(model: VMenuTree) : DWindow(model), UMenuTree {
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  /**
   * Show Application Information
   */
  override fun showApplicationInformation(message: String) {
    SwingThreadHandler.verifyRunsInEventThread("DWindow showApplicationInformation")
    verifyNotInTransaction("DWindow.showApplicationInformation($message)")
    val options = arrayOf<Any>(getString("CLOSE"))
    JOptionPane.showOptionDialog(frame,
                                 message,
                                 getString("Notice"),
                                 JOptionPane.DEFAULT_OPTION,
                                 JOptionPane.INFORMATION_MESSAGE,
                                 Utils.getImage("info.gif"),
                                 options,
                                 options[0])
  }

  /**
   * Adds the given module to avorites
   */
  fun addShortcut(module: Module) {
    if (!shortcuts.containsKey(module)) {
      val action: AbstractAction = object : AbstractAction(module.description, module.icon as ImageIcon?) {
        override fun actionPerformed(e: ActionEvent) {
          setWaitInfo(getString("menu_form_started"))
          getModel().performAsyncAction(object : Action("menu_form_started") {
            override fun execute() {
              module.run(getModel().dBConnection!!)
              unsetWaitInfo()
            }
          })
        }
      }
      toolbar.addShortcut(action)
      shortcuts[module] = action
      orderdShorts.add(action)
      modules.add(module)
    }
  }

  /**
   * Removes the given module from favorites
   */
  fun removeShortcut(module: Module) {
    if (shortcuts.containsKey(module)) {
      modules.remove(module)
      val removed = shortcuts.remove(module)
      orderdShorts.remove(removed)
      toolbar.removeShortcut(removed)
    }
  }

  /**
   * Resets all favorites
   */
  fun resetShortcutsInDatabase() {
    try {
      transaction {
        Favorites.deleteWhere {
          Favorites.user eq getModel().getUserID()
        }

        for (i in modules.indices) {
          Favorites.insert {
            it[ts] = (System.currentTimeMillis() / 1000).toInt()
            it[user] = getModel().getUserID()
            it[module] = modules[i].id
          }
        }
      }
    } catch (e: SQLException) {
      e.printStackTrace()
    }
  }

  /**
   * Move the focus from the activated frame to favorites frame.
   */
  override fun gotoShortcuts() {
    if (!getModel().isSuperUser) {
      try {
        if (toolbar.isVisible) {
          toolbar.isVisible = false
        }
        toolbar.isVisible = true
        toolbar.toFront()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  /**
   * Launches the selected form in the menu tree.
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   */
  override fun launchSelectedForm() {
    val node = selectedNode
    if (node != null) {
      val module = node.userObject as Module
      if (getModel().isSuperUser) {
        module.accessibility = (module.accessibility + 1) % 3
        (tree.model as DefaultTreeModel).nodeChanged(node)
      } else if (node.isLeaf) {
        setWaitInfo(getString("menu_form_started"))
        module.run(getModel().dBConnection!!)
        unsetWaitInfo()
      }
    }
  }

  override fun addSelectedElement() {
    val node = selectedNode
    if (node != null && node.isLeaf) {
      addShortcut(node.userObject as Module)
      resetShortcutsInDatabase()
    }
  }

  override fun setMenu() {
    val node = selectedNode

    getModel().setActorEnabled(VMenuTree.CMD_QUIT, true)
    getModel().setActorEnabled(VMenuTree.CMD_INFORMATION, true)
    getModel().setActorEnabled(VMenuTree.CMD_HELP, true)
    if (node != null) {
      val module = node.userObject as Module
      getModel().setToolTip(module.help)
      getModel().setActorEnabled(VMenuTree.CMD_SHOW, shortcuts.size > 0)
      if (node.isLeaf) {
        getModel().setActorEnabled(VMenuTree.CMD_OPEN, true)
        getModel().setActorEnabled(VMenuTree.CMD_ADD, !shortcuts.containsKey(module))
        getModel().setActorEnabled(VMenuTree.CMD_REMOVE, shortcuts.containsKey(module))
        getModel().setActorEnabled(VMenuTree.CMD_FOLD, false)
        getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, false)
      } else {
        getModel().setActorEnabled(VMenuTree.CMD_OPEN, getModel().isSuperUser)
        getModel().setActorEnabled(VMenuTree.CMD_ADD, false)
        if (tree.isExpanded(tree.selectionPath)) {
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
    val node = selectedNode
    if (node != null && node.isLeaf) {
      removeShortcut(node.userObject as Module)
      resetShortcutsInDatabase()
    }
  }

  @Throws(VException::class)
  override fun run() {
    isVisible = true
  }

  /**
   * Returns the TreeNode instance that is selected in the tree.
   * If nothing is selected, null is returned.
   */
  internal val selectedNode: DefaultMutableTreeNode?
    get() {
      val selPath = tree.selectionPath
      return if (selPath != null) {
        selPath.lastPathComponent as DefaultMutableTreeNode
      } else {
        null
      }
    }

  /**
   * Calls the selected form in the tree menu
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
    // ensure that it is executed in event dispatch Thread
    SwingThreadHandler.startAndWait {
      if (!getModel().isSuperUser
              && askUser(getMessage("confirm_quit"), false)) {
        JApplication.quit()
      }
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  override fun getTree(): UTree {
    return tree
  }

  override fun getBookmark(): UBookmarkPanel {
    return toolbar
  }

  override fun getModel(): VMenuTree {
    return super.getModel() as VMenuTree
  }

  /**
   * @return The book mark swing actions
   */
  val bookmarkActions: Array<javax.swing.Action?>
    get() = orderdShorts.toTypedArray()

  //------------------------------------------------------------
  // UTREE IMPLEMENTATION
  //------------------------------------------------------------
  inner class Tree(root: TreeNode?) : JTree(root), UTree {
    override fun isExpanded(path: Any?): Boolean {
      return super.isExpanded(path as TreePath)
    }

    override fun isCollapsed(path: Any?): Boolean {
      return super.isCollapsed(path as TreePath)
    }

    override fun getSelectionRow(): Int = super.getSelectionRows()[0]
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private val tree: Tree = Tree(model.root)
  private val toolbar: JBookmarkPanel
  private val shortcuts: MutableMap<Module, javax.swing.Action> = mutableMapOf()
  private val orderdShorts: MutableList<javax.swing.Action?> = mutableListOf()
  private val modules: MutableList<Module> = mutableListOf()

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  init {
    tree.addMouseListener(object : MouseAdapter() {
      private var lastClick: Long = 0
      override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount == 2 && !getModel().isSuperUser) {
          callSelectedForm()
        } else {
          if (e.getWhen() - lastClick < 400) {
            // for slow NT users
            callSelectedForm()
          }
        }
        lastClick = e.getWhen()
      }
    })
    tree.addKeyListener(object : KeyAdapter() {
      override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_SPACE
                || (e.keyCode == KeyEvent.VK_ENTER
                        && selectedNode!!.isLeaf)) {
          e.consume()
          callSelectedForm()
        }
      }
    })
    tree.addTreeExpansionListener(object : TreeExpansionListener {
      override fun treeExpanded(event: TreeExpansionEvent) {
        setMenu()
      }

      override fun treeCollapsed(event: TreeExpansionEvent) {
        setMenu()
      }
    })
    tree.selectionModel.addTreeSelectionListener { setMenu() }
    tree.cellRenderer = MenuItemRenderer(getModel().isSuperUser)
    tree.putClientProperty("JTree.lineStyle", "None")

    /* Make tree ask for the height of each row. */tree.rowHeight = -1
    tree.background = UIManager.getColor("menu.background")
    val sp = JScrollPane()
    sp.border = null
    sp.viewport.add(tree)
    contentPanel.layout = BorderLayout()
    contentPanel.add(sp, BorderLayout.CENTER)
    toolbar = JBookmarkPanel(getString("toolbar-title"))
    for (i in getModel().getShortcutsID().indices) {
      val id = getModel().getShortcutsID()[i].toInt()
      for (j in getModel().moduleArray.indices) {
        if (getModel().moduleArray[j].id == id) {
          addShortcut(getModel().moduleArray[j])
        }
      }
    }
    if (!getModel().getShortcutsID().isEmpty() && !getModel().isSuperUser) {
      toolbar.show()
      toolbar.toFront()
    }
    if (tree.rowCount > 0) {
      tree.setSelectionInterval(0, 0)
    }
    setMenu()
    tree.requestFocusInWindow()
  }
}
