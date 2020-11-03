/*
 * Copyright (c) 1990-2018 kopiRight Managed Solutions GmbH
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
 * $Id: DItemTree.java 35325 2018-08-29 11:27:17Z iayadi $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.kopi.galite.util.base.Utils;
import org.kopi.galite.visual.Item;
import org.kopi.galite.visual.Action;
import org.kopi.galite.visual.MessageCode;
import org.kopi.galite.visual.UItemTree;
import org.kopi.galite.visual.VException;
import org.kopi.galite.visual.VExecFailedException;
import org.kopi.galite.visual.VItemTree;
import org.kopi.galite.visual.VlibProperties;

public class DItemTree extends DWindow implements UItemTree {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  @SuppressWarnings("deprecation")
  public DItemTree(VItemTree model) {
    super(model);
    tree = new Tree(model.getRoot());

    tree.addMouseListener(new MouseAdapter() {
      private long lastClick;

      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && getModel().isNoEdit()) {
          changeSelectionState();
        } else {
          if (e.getWhen() - lastClick < 400) {
            // for slow NT users
            changeSelectionState();
          }
        }
        lastClick = e.getWhen();
      }
    });

    tree.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE
            || (e.getKeyCode() == KeyEvent.VK_ENTER
            && getSelectedNode().isLeaf()))
        {
          e.consume();
          changeSelectionState();
        }
      }
    });

    tree.addTreeExpansionListener(new TreeExpansionListener() {

      public void treeExpanded(TreeExpansionEvent event) {
        setTree();
      }

      public void treeCollapsed(TreeExpansionEvent event) {
        setTree();
      }
    });

    tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        setTree();
      }
    });

    tree.setCellRenderer(new ItemRenderer(getModel().isNoEdit(), getModel().isSingleSelection(), getModel().isLocalised()));
    tree.putClientProperty("JTree.lineStyle", "None");

    /* Make tree ask for the height of each row. */
    tree.setRowHeight(-1);
    tree.setBackground(UIManager.getColor("menu.background"));

    JScrollPane sp = new JScrollPane();

    sp.setBorder(null);
    sp.getViewport().add(tree);
    getContentPanel().setLayout(new BorderLayout());
    getContentPanel().add(sp, BorderLayout.CENTER);

    if (tree.getRowCount() > 0) {
      tree.setSelectionInterval(0, 0);
    }
    setTree();
    tree.requestFocusInWindow();
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  public void setTree() {
    DefaultMutableTreeNode                      node;
    boolean                                     rootItem;

    if (getModel() != null) {   // The model can be destroyed by it's itemTreeManager:
                                // the save action close the Item Tree window
      node = getSelectedNode();
      getModel().setActorEnabled(VItemTree.CMD_QUIT, true);
      if (node != null) {
        rootItem = ((Item)node.getUserObject()).getId() == -1;
        getModel().setActorEnabled(VItemTree.CMD_ADD, getModel().isInsertMode());
        getModel().setActorEnabled(VItemTree.CMD_REMOVE, getModel().isInsertMode() && !rootItem);
        getModel().setActorEnabled(VItemTree.CMD_EDIT, getModel().isInsertMode() && !rootItem);
        getModel().setActorEnabled(VItemTree.CMD_LOCALISE, (getModel().isLocalised() && getModel().isInsertMode() && !rootItem));
        getModel().setActorEnabled(VItemTree.CMD_SELECT, !getModel().isNoEdit() && !rootItem);
        getModel().setActorEnabled(VItemTree.CMD_DEFAULT, getModel().isMultiSelectionDefaultValue() && !rootItem);
        if (node.isLeaf()) {
          getModel().setActorEnabled(VItemTree.CMD_FOLD, false);
          getModel().setActorEnabled(VItemTree.CMD_UNFOLD, false);
        } else {
          if (tree.isExpanded(tree.getSelectionPath())) {
            getModel().setActorEnabled(VItemTree.CMD_FOLD, true);
            getModel().setActorEnabled(VItemTree.CMD_UNFOLD, false);
          } else {
            getModel().setActorEnabled(VItemTree.CMD_FOLD, false);
            getModel().setActorEnabled(VItemTree.CMD_UNFOLD, true);
          }
        }
      } else {
        getModel().setActorEnabled(VItemTree.CMD_FOLD, false);
        getModel().setActorEnabled(VItemTree.CMD_UNFOLD, false);
        getModel().setActorEnabled(VItemTree.CMD_ADD, false);
        getModel().setActorEnabled(VItemTree.CMD_REMOVE, false);
        getModel().setActorEnabled(VItemTree.CMD_EDIT, false);
        getModel().setActorEnabled(VItemTree.CMD_LOCALISE, false);
        getModel().setActorEnabled(VItemTree.CMD_SELECT, false);
        getModel().setActorEnabled(VItemTree.CMD_DEFAULT, false);
      }
      getModel().setActorEnabled(VItemTree.CMD_SAVE, getModel().isChanged());
    }
  }

  public void run() throws VException {
    setVisible(true);
  }

  /**
   * Returns the TreeNode instance that is selected in the tree.
   * If nothing is selected, null is returned.
   */
  protected DefaultMutableTreeNode getSelectedNode() {
    TreePath    selPath = tree.getSelectionPath();

    if (selPath != null) {
      return (DefaultMutableTreeNode)selPath.getLastPathComponent();
    } else {
      return null;
    }
  }

  /**
   * Sets item selection state
   */
  public void setSelectedItem() {
    DefaultMutableTreeNode              selectedNode;
    Item                                selectedItem;
    DefaultTreeModel                    treeModel;
    DefaultMutableTreeNode              rootNode;

    selectedNode = getSelectedNode();
    if (selectedNode != null) {
      selectedItem = (Item)selectedNode.getUserObject();

      treeModel = ((DefaultTreeModel)tree.getModel());
      if (getModel().isSingleSelection()) {
        rootNode = (DefaultMutableTreeNode)treeModel.getRoot();
        if (!selectedItem.getSelected()) {
          unselectAll(treeModel, rootNode);
        }
      }
      selectedItem.setSelected(!selectedItem.getSelected());
      treeModel.nodeChanged(selectedNode);
      getModel().refresh();
    }
  }

  /**
   * Set selected item as default element
   */
  public void setDefaultItem() {
    DefaultMutableTreeNode              selectedNode;
    Item                                selectedItem;
    DefaultTreeModel                    treeModel;
    DefaultMutableTreeNode              rootNode;

    selectedNode = getSelectedNode();

    if (selectedNode != null) {
      treeModel = ((DefaultTreeModel)tree.getModel());
      rootNode = (DefaultMutableTreeNode)treeModel.getRoot();
      selectedItem = (Item)selectedNode.getUserObject();

      if (!selectedItem.getDefaultItem()) {
        setDefault(treeModel, rootNode);
      }
      selectedItem.setDefaultItem(!selectedItem.getDefaultItem());
      selectedItem.setSelected(true);
      treeModel.nodeChanged(selectedNode);
      getModel().refresh();
    }
  }

  /**
   * Set default value of all children
   */
  private void setDefault(DefaultTreeModel treeModel,
                          DefaultMutableTreeNode node)
  {
    for (int i = 0; i < node.getChildCount(); i++) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
      Item item = (Item)child.getUserObject();

      if (item.getDefaultItem()) {
        item.setDefaultItem(false);
        treeModel.nodeChanged(child);
      }
      if (child.getChildCount() > 0) {
        setDefault (treeModel, child);
      }
    }
  }

  /**
   * Set selection value of all children
   */
  private void unselectAll(DefaultTreeModel treeModel,
                           DefaultMutableTreeNode node)
  {
    for (int i = 0; i < node.getChildCount(); i++) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
      Item item = (Item)child.getUserObject();

      if (item.getSelected()) {
        item.setSelected(false);
        treeModel.nodeChanged(child);
      }
      if (child.getChildCount() > 0) {
        unselectAll(treeModel, child);
      }
    }
  }

  /**
   * Change item selection state
   */
  private void changeSelectionState() {
    getModel().performAsyncAction(new Action("change_selection_state") {
      public void execute() {
        if (!getModel().isNoEdit()) {
          setSelectedItem();
        }
      }
    });
  }

  /**
   * Insert new item
   */
  public void addItem() throws VException {
    DefaultMutableTreeNode              selectedNode;
    DefaultTreeModel                    treeModel;
    DefaultMutableTreeNode              newNode;
    String                              newItem;
    int                                 selectedRow;
    int                                 maxLength;
    Item                                parent;
    Item                                item;

    maxLength = Math.min(getModel().MAX_LENGTH, getModel().getNameLength());
    selectedRow = tree.getSelectionRow();
    selectedNode = getSelectedNode();
    if (selectedNode != null) {
      parent = (Item)selectedNode.getUserObject();
      if (getModel().getDepth() > 0 && ((parent.getLevel() + 1) > getModel().getDepth())) {
        throw new VExecFailedException(MessageCode.Companion.getMessage("VIS-00069" , getModel().getDepth()));
      }

      do {
        newItem = JOptionPane.showInputDialog(this,
                                              MessageCode.Companion.getMessage("VIS-00068", maxLength),
                                              VlibProperties.getString("New_item"),
                                              JOptionPane.PLAIN_MESSAGE);
      } while (newItem != null && newItem.length() > maxLength);
      if (!(newItem == null || newItem.trim().length() == 0)) {
        newItem = newItem.trim();
        treeModel = ((DefaultTreeModel)tree.getModel());
        item = new Item(getModel().getNextId(),
                        ((Item)selectedNode.getUserObject()).getId(),
                        newItem,
                        null,
                        null,
                        false,
                        false,
                        null,
                        newItem);
        item.setLevel(parent.getLevel() + 1);
        newNode = new DefaultMutableTreeNode(item);
        selectedNode.add(newNode);
        if(selectedNode.getChildCount() > 0) {
          treeModel.reload(selectedNode);
        } else {
          treeModel.nodeChanged(selectedNode);
        }
        if (selectedRow > 0) {
          tree.expandRow(selectedRow);
        }
      }
    }
  }

  /**
   * Remove selected item from the tree
   */
  public void removeSelectedItem() {
    DefaultMutableTreeNode              selectedNode;
    DefaultTreeModel                    treeModel;

    selectedNode = getSelectedNode();
    treeModel = ((DefaultTreeModel)tree.getModel());
    if (selectedNode != null) {
      treeModel = ((DefaultTreeModel)tree.getModel());
      DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();

      if (!getModel().isRemoveDescendantsAllowed()) {
        attacheToParent(treeModel, selectedNode, (DefaultMutableTreeNode)selectedNode.getParent());
      }
      removeItem(treeModel, rootNode, (Item)selectedNode.getUserObject());
    }
  }

  /**
   * Attache children to removed item parent
   */
  public void attacheToParent(DefaultTreeModel treeModel,
                              DefaultMutableTreeNode node,
                              DefaultMutableTreeNode parent)
  {
    for (int i = 0; i < node.getChildCount(); i++) {
      DefaultMutableTreeNode    child = (DefaultMutableTreeNode)node.getChildAt(i);
      Item                      childItem = (Item)child.getUserObject();

      childItem.setParent(((Item)parent.getUserObject()).getId());
      parent.add(child);
      if(parent.getChildCount() > 0){
        treeModel.reload(parent);
      } else {
        treeModel.nodeChanged(parent);
      }
      setLevel(treeModel, child);
    }
  }

  /**
   * Sets level of all children of an item
   */
  public void setLevel(DefaultTreeModel treeModel,
                       DefaultMutableTreeNode node)
  {
    for (int i = 0; i < node.getChildCount(); i++) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);

      if (child.getChildCount() > 0) {
        setLevel(treeModel, child);
      }
    }
    ((Item)node.getUserObject()).decrementLevel();
  }

  /**
   * Remove an item from the tree
   */
  private void removeItem(DefaultTreeModel treeModel,
                          DefaultMutableTreeNode node,
                          Item itemToRemove)
  {
    for (int i = 0; i < node.getChildCount(); i++) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
      Item childItem = (Item)child.getUserObject();
      if (childItem.getId() == itemToRemove.getId()) {
        getModel().getRemovedItems().add(itemToRemove);
        node.remove(i);
        treeModel.reload(node);
        return;
      } else {
        if (child.getChildCount() > 0) {
          removeItem(treeModel, child, itemToRemove);
        }
      }
    }
  }

  /**
   * Edit the selected item
   */
  public void editSelectedItem() {
    DefaultMutableTreeNode              selectedNode;
    Item                                selectedItem;
    DefaultTreeModel                    treeModel;
    String                              newName;
    int                                 maxLength;

    maxLength = Math.min(getModel().MAX_LENGTH, getModel().getNameLength());
    selectedNode = getSelectedNode();

    if (selectedNode != null) {
      selectedItem = (Item)selectedNode.getUserObject();
      do {
        newName = (String) JOptionPane.showInputDialog(this,
                                                       MessageCode.Companion.getMessage("VIS-00068", maxLength),
                                                       VlibProperties.getString("OpenLine"),
                                                       JOptionPane.PLAIN_MESSAGE,
                                                       null,
                                                       null,
                                                       selectedItem.getName());
      } while (newName != null && newName.length() > maxLength);
      if (!(newName == null || newName.trim().length() == 0)) {
        treeModel = ((DefaultTreeModel)tree.getModel());
        selectedItem.setName(newName);
        treeModel.nodeChanged(selectedNode);
      }
    }
  }

  /**
   * Localise the selected item
   */
  public void localiseSelectedItem() {
    DefaultMutableTreeNode              selectedNode;
    Item                                selectedItem;
    DefaultTreeModel                    treeModel;
    String                              localisedName;
    int                                 maxLength;

    maxLength = Math.min(getModel().MAX_LENGTH, getModel().getLocalisedNameLength());
    selectedNode = getSelectedNode();

    if (selectedNode != null) {
      selectedItem = (Item)selectedNode.getUserObject();
      do {
        localisedName = (String)JOptionPane.showInputDialog(this,
                                                            MessageCode.Companion.getMessage("VIS-00068", maxLength),
                                                            VlibProperties.getString("OpenLine"),
                                                            JOptionPane.PLAIN_MESSAGE,
                                                            null,
                                                            null,
                                                            selectedItem.getLocalisedName());
      } while (localisedName != null && localisedName.length() > maxLength);
      if (!(localisedName == null || localisedName.trim().length() == 0)) {
        treeModel = ((DefaultTreeModel)tree.getModel());
        selectedItem.setLocalisedName(localisedName);
        treeModel.nodeChanged(selectedNode);
      }
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public UTreeComponent getTree() {
    return tree;
  }

  public VItemTree getModel() {
    return (VItemTree) super.getModel();
  }

  //------------------------------------------------------------
  // UTREE IMPLEMENTATION
  //------------------------------------------------------------

  public class Tree extends JTree implements UTreeComponent {

    //--------------------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------------------

    public Tree(TreeNode root) {
      super(root);
      this.root = root;
    }

    public boolean isExpanded(Object path) {
      return super.isExpanded((TreePath) path);
    }

    public boolean isCollapsed(Object path) {
      return super.isCollapsed((TreePath) path);
    }

    public int getSelectionRow() {
      return super.getSelectionRows()[0];
    }

    /**
     * Return items array
     */
    public Item[] getItems() {
      List<Item> itemsList = getItems(root);

      if (itemsList != null && itemsList.size() > 0) {
        return (Item[])Utils.toArray(itemsList, Item.class);
      }

      return null;
    }

    /**
     * Return items list
     */
    public List<Item> getItems(TreeNode node) {
      List<Item>                items;
      Item                      item;

      items = new ArrayList<Item>();
      item = (Item)((DefaultMutableTreeNode)node).getUserObject();
      item.setChildCount(node.getChildCount());
      if (item.getId() >= 0) {
        items.add(item);
      }

      if (node.getChildCount() > 0) {
        for (int i = 0; i < node.getChildCount(); i++) {
          items.addAll(getItems(node.getChildAt(i)));
        }
      }

      return items;
    }

    /**
     * Return items array
     */
    @Override
    public Item getRootItem() {
      return getRootItem(root);
    }

    /**
     * Return items list
     */
    public Item getRootItem(TreeNode node) {
      Item[]            children;
      Item              item;

      item = (Item)((DefaultMutableTreeNode)node).getUserObject();
      item.setChildCount(node.getChildCount());

      if (node.getChildCount() > 0) {
        children = new Item[node.getChildCount()];
        for (int i = 0; i < node.getChildCount(); i++) {
          children[i] = getRootItem(node.getChildAt(i));
        }
        item.setChildren(children);
      }

      return item;
    }

    /**
     * Return true if the item name done is unique in this tree
     */
    public boolean isUnique(String name) {
      // TODO: Should be implemented when the item name
      //       may be not unique in the tree
      return true;
    }

    /**
     * Ensures that the selected nodes are expanded and viewable.
     */
    public void expandTree() {
    }

    //------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------

    TreeNode                            root;

    private static final long           serialVersionUID = -3954942255139034033L;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private Tree                          tree;

  private static final long             serialVersionUID = 8814499549529628999L;
}
