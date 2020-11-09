/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: MenuItemRenderer.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.kopi.vkopi.lib.ui.swing.base.Utils;
import org.kopi.galite.visual.Module;

class MenuItemRenderer extends JLabel implements TreeCellRenderer {

  MenuItemRenderer(boolean superUser) {
    this.superUser = superUser;
    //    setBackground(DObject.CLR_MNU_PARENT);
  }

  /**
   * This is messaged from JTree whenever it needs to get the size
   * of the component or it wants to draw it.
   * This attempts to set the font based on value, which will be
   * a TreeNode.
   */
  public Component getTreeCellRendererComponent(JTree tree,
						Object value,
						boolean selected,
						boolean expanded,
						boolean leaf,
						int row,
						boolean hasFocus) {
    /* Set the color and the font based on the SampleData userObject. */
    Module	module = (Module)((DefaultMutableTreeNode)value).getUserObject();

    if (hasFocus) {
      setOpaque(true);
      setBackground(CLR_FOCUS_BACK);
      setForeground(CLR_FOCUS);
    } else if (!leaf) {
      setOpaque(false);
      setForeground(CLR_PARENT);
    } else {
      setOpaque(false);
      setForeground(CLR_UNSELECT);
    }

    setText(module.getDescription());

    if (row == 0) {
      setIcon(rootIcon);
    } else if (superUser && (module.getAccessibility() != Module.ACS_PARENT)) {
      if (module.getAccessibility() == Module.ACS_FALSE) {
	if (expanded) {
	  setIcon(expandedIcon_F);
	} else if (!leaf) {
	  setIcon(collapsedIcon_F);
	} else if (hasFocus) {
	  setIcon(selectedFormIcon_F);
	} else {
	  setIcon(formIcon_F);
	}
      } else {
	if (expanded) {
	  setIcon(expandedIcon_T);
	} else if (!leaf) {
	  setIcon(collapsedIcon_T);
	} else if (hasFocus) {
	  setIcon(selectedFormIcon_T);
	} else {
	  setIcon(formIcon_T);
	}
      }
    } else {
      if (expanded) {
	setIcon(expandedIcon);
      } else if (!leaf) {
	setIcon(collapsedIcon);
      } else if (hasFocus) {
	setIcon(module.getSmallIcon() == null ? selectedFormIcon : (ImageIcon)module.getSmallIcon());
      } else {
	setIcon(module.getSmallIcon() == null ? formIcon : (ImageIcon)module.getSmallIcon());
      }
    }

    return this;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public static ImageIcon			formIcon;
  private static ImageIcon			rootIcon;
  private static ImageIcon			collapsedIcon;
  private static ImageIcon			expandedIcon;
  private static ImageIcon			selectedFormIcon;
  private static ImageIcon			collapsedIcon_F;
  private static ImageIcon			expandedIcon_F;
  private static ImageIcon			formIcon_F;
  private static ImageIcon			selectedFormIcon_F;
  private static ImageIcon			collapsedIcon_T;
  private static ImageIcon			expandedIcon_T;
  private static ImageIcon			formIcon_T;
  private static ImageIcon			selectedFormIcon_T;

  private boolean				superUser;

  private static Color          		CLR_PARENT;
  private static Color          		CLR_UNSELECT;
  private static Color          		CLR_FOCUS;
  private static Color          		CLR_FOCUS_BACK;

  static {
    collapsedIcon = Utils.getImage("collapsed.gif");
    expandedIcon = Utils.getImage("expanded.gif");
    formIcon = Utils.getImage("form.gif");
    selectedFormIcon = Utils.getImage("forms.png");
    collapsedIcon_F = Utils.getImage("collapsed_f.gif");
    expandedIcon_F = Utils.getImage("expanded_f.gif");
    formIcon_F = Utils.getImage("form_f.gif");
    selectedFormIcon_F = Utils.getImage("forms_f.gif");
    collapsedIcon_T = Utils.getImage("collapsed_t.gif");
    expandedIcon_T = Utils.getImage("expanded_t.gif");
    formIcon_T = Utils.getImage("form_t.gif");
    selectedFormIcon_T = Utils.getImage("forms_t.gif");
    rootIcon = Utils.getImage("desk.gif");
    if (rootIcon != null) {
      rootIcon = new ImageIcon(rootIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH));
    }
    CLR_PARENT   = UIManager.getColor("kopi.menuitem.parent");
    CLR_UNSELECT = UIManager.getColor("kopi.menuitem.unselect");
    CLR_FOCUS    = UIManager.getColor("kopi.menuitem.focus");
    CLR_FOCUS_BACK    = UIManager.getColor("kopi.menuitem.focus.background");
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 5890176862796914299L;
}
