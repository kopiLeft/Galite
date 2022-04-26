/*
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH
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
 * $Id:$
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

import org.kopi.galite.visual.Item;
import org.kopi.vkopi.lib.ui.swing.base.Utils;

class ItemRenderer extends JLabel implements TreeCellRenderer {

  ItemRenderer(boolean noEdit,
               boolean singleSelection,
               boolean localised)
  {
    this.noEdit = noEdit;
    this.singleSelection = singleSelection;
    this.localised = localised;
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
                                                boolean hasFocus)
  {
    /* Set the color and the font based on the SampleData userObject. */
    Item        item = (Item)((DefaultMutableTreeNode)value).getUserObject();

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

    setText(item.getFormattedName(localised));

    if (row == 0) {
      setIcon(rootIcon);
    } else if (!noEdit) {
      if (item.isDefaultItem()) {
        if (hasFocus) {
          setIcon(defaultIcon_S);
        } else {
          setIcon(defaultIcon);
        }
      } else {
        if (!item.isSelected()) {
          if (hasFocus) {
            setIcon(uncheckedIcon_S);
          } else {
            setIcon(uncheckedIcon);
          }
        } else  {
          if (hasFocus) {
            setIcon(singleSelection ? radioCheckedIcon_S : checkedIcon_S);
          } else {
            setIcon(singleSelection ? radioCheckedIcon : checkedIcon);
          }
        }
      }
    } else {
      if (expanded) {
        setIcon(expandedIcon);
      } else if (!leaf) {
        setIcon(collapsedIcon);
      } else if (hasFocus) {
        setIcon(item.getSmallIcon() == null ? selectedFormIcon : (ImageIcon)item.getSmallIcon());
      } else {
        setIcon(item.getSmallIcon() == null ? formIcon : (ImageIcon)item.getSmallIcon());
      }
    }

    return this;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public static ImageIcon                       formIcon;
  private static ImageIcon                      rootIcon;
  private static ImageIcon                      collapsedIcon;
  private static ImageIcon                      expandedIcon;
  private static ImageIcon                      selectedFormIcon;
  private static ImageIcon                      checkedIcon;
  private static ImageIcon                      uncheckedIcon;
  private static ImageIcon                      defaultIcon;
  private static ImageIcon                      checkedIcon_S;
  private static ImageIcon                      uncheckedIcon_S;
  private static ImageIcon                      defaultIcon_S;
  private static ImageIcon                      radioCheckedIcon;
  private static ImageIcon                      radioCheckedIcon_S;

  private boolean                               noEdit;
  private boolean                               singleSelection;
  private boolean                               localised;

  private static Color                          CLR_PARENT;
  private static Color                          CLR_UNSELECT;
  private static Color                          CLR_FOCUS;
  private static Color                          CLR_FOCUS_BACK;

  static {
    collapsedIcon = Utils.getImage("collapsed.gif");
    expandedIcon = Utils.getImage("expanded.gif");
    formIcon = Utils.getImage("form.gif");
    selectedFormIcon = Utils.getImage("forms.png");
    checkedIcon = Utils.getImage("checked.png");
    uncheckedIcon = Utils.getImage("unchecked.png");
    defaultIcon = Utils.getImage("default.png");
    checkedIcon_S = Utils.getImage("checked_s.png");
    uncheckedIcon_S = Utils.getImage("unchecked_s.png");
    defaultIcon_S = Utils.getImage("default_s.png");
    radioCheckedIcon = Utils.getImage("radio_checked.png");
    radioCheckedIcon_S = Utils.getImage("radio_checked_s.png");

    rootIcon = Utils.getImage("desk.gif");
    if (rootIcon != null) {
      rootIcon = new ImageIcon(rootIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH));
    }
    CLR_PARENT = UIManager.getColor("kopi.menuitem.parent");
    CLR_UNSELECT = UIManager.getColor("kopi.menuitem.unselect");
    CLR_FOCUS = UIManager.getColor("kopi.menuitem.focus");
    CLR_FOCUS_BACK = UIManager.getColor("kopi.menuitem.focus.background");
  }

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long                     serialVersionUID = -926187515637797250L;
}
