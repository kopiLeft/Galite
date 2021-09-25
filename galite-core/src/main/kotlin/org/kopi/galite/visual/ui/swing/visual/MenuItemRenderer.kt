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
package org.kopi.galite.visual.ui.swing.visual

import javax.swing.JLabel
import javax.swing.tree.TreeCellRenderer
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import java.awt.Color
import java.awt.Component
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.UIManager

import org.kopi.galite.visual.visual.Module
import org.kopi.vkopi.lib.ui.swing.base.Utils

internal class MenuItemRenderer(private val superUser: Boolean) : JLabel(), TreeCellRenderer {
  /**
   * This is messaged from JTree whenever it needs to get the size
   * of the component or it wants to draw it.
   * This attempts to set the font based on value, which will be
   * a TreeNode.
   */
  override fun getTreeCellRendererComponent(
          tree: JTree,
          value: Any,
          selected: Boolean,
          expanded: Boolean,
          leaf: Boolean,
          row: Int,
          hasFocus: Boolean
  ): Component {
    /* Set the color and the font based on the SampleData userObject. */
    val module = (value as DefaultMutableTreeNode).userObject as Module
    if (hasFocus) {
      isOpaque = true
      background = CLR_FOCUS_BACK
      foreground = CLR_FOCUS
    } else if (!leaf) {
      isOpaque = false
      foreground = CLR_PARENT
    } else {
      isOpaque = false
      foreground = CLR_UNSELECT
    }
    text = module.description
    icon = if (row == 0) {
      rootIcon
    } else if (superUser && module.accessibility != Module.ACS_PARENT) {
      if (module.accessibility == Module.ACS_FALSE) {
        when {
          expanded -> expandedIcon_F
          !leaf -> collapsedIcon_F
          hasFocus -> selectedFormIcon_F
          else -> formIcon_F
        }
      } else {
        when {
          expanded -> expandedIcon_T
          !leaf -> collapsedIcon_T
          hasFocus -> selectedFormIcon_T
          else -> formIcon_T
        }
      }
    } else {
      when {
        expanded -> expandedIcon
        !leaf -> collapsedIcon
        hasFocus -> module.smallIcon as? ImageIcon ?: selectedFormIcon
        else -> module.smallIcon as ImageIcon? ?: formIcon
      }
    }
    return this
  }

  companion object {
    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------
    var formIcon: ImageIcon? = null
    private var rootIcon: ImageIcon? = null
    private var collapsedIcon: ImageIcon? = null
    private var expandedIcon: ImageIcon? = null
    private var selectedFormIcon: ImageIcon? = null
    private var collapsedIcon_F: ImageIcon? = null
    private var expandedIcon_F: ImageIcon? = null
    private var formIcon_F: ImageIcon? = null
    private var selectedFormIcon_F: ImageIcon? = null
    private var collapsedIcon_T: ImageIcon? = null
    private var expandedIcon_T: ImageIcon? = null
    private var formIcon_T: ImageIcon? = null
    private var selectedFormIcon_T: ImageIcon? = null
    private var CLR_PARENT: Color? = null
    private var CLR_UNSELECT: Color? = null
    private var CLR_FOCUS: Color? = null
    private var CLR_FOCUS_BACK: Color? = null

    /**
     * Comment for `serialVersionUID`
     */
    private const val serialVersionUID = 1L

    init {
      collapsedIcon = Utils.getImage("collapsed.gif")
      expandedIcon = Utils.getImage("expanded.gif")
      formIcon = Utils.getImage("form.gif")
      selectedFormIcon = Utils.getImage("forms.png")
      collapsedIcon_F = Utils.getImage("collapsed_f.gif")
      expandedIcon_F = Utils.getImage("expanded_f.gif")
      formIcon_F = Utils.getImage("form_f.gif")
      selectedFormIcon_F = Utils.getImage("forms_f.gif")
      collapsedIcon_T = Utils.getImage("collapsed_t.gif")
      expandedIcon_T = Utils.getImage("expanded_t.gif")
      formIcon_T = Utils.getImage("form_t.gif")
      selectedFormIcon_T = Utils.getImage("forms_t.gif")
      rootIcon = Utils.getImage("desk.gif")
      if (rootIcon != null) {
        rootIcon = ImageIcon(
                rootIcon!!.image.getScaledInstance(16, 16, Image.SCALE_SMOOTH))
      }
      CLR_PARENT = UIManager.getColor("kopi.menuitem.parent")
      CLR_UNSELECT = UIManager.getColor("kopi.menuitem.unselect")
      CLR_FOCUS = UIManager.getColor("kopi.menuitem.focus")
      CLR_FOCUS_BACK = UIManager.getColor("kopi.menuitem.focus.background")
    }
  }
}
