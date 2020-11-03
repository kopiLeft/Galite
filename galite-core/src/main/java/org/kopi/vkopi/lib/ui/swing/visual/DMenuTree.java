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
 * $Id: DMenuTree.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
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

import org.kopi.vkopi.lib.ui.swing.base.Utils;
import org.kopi.galite.visual.Message;
import org.kopi.galite.visual.Module;
import org.kopi.galite.visual.UMenuTree;
import org.kopi.galite.visual.VException;
import org.kopi.galite.visual.VMenuTree;
import org.kopi.galite.visual.VlibProperties;
import org.kopi.galite.db.Query;

public class DMenuTree extends DWindow implements UMenuTree {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  @SuppressWarnings("deprecation")
  public DMenuTree(VMenuTree model) {
    super(model);
    tree = new Tree(model.getRoot());
    shortcuts = new Hashtable<Module, Action>();
    orderdShorts = new ArrayList<Action>();
    modules = new ArrayList<Module>();

    tree.addMouseListener(new MouseAdapter() {
      private long lastClick;

      public void mouseClicked(MouseEvent e) {
	if (e.getClickCount() == 2 && !getModel().isSuperUser()) {
	  callSelectedForm();
	} else {
	  if (e.getWhen() - lastClick < 400) {
	    // for slow NT users
	    callSelectedForm();
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
	  callSelectedForm();
	}
      }
    });

    tree.addTreeExpansionListener(new TreeExpansionListener() {

      public void treeExpanded(TreeExpansionEvent event) {
	setMenu();
      }

      public void treeCollapsed(TreeExpansionEvent event) {
	setMenu();
      }
    });

    tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
	setMenu();
      }
    });

    tree.setCellRenderer(new MenuItemRenderer(getModel().isSuperUser()));
    tree.putClientProperty("JTree.lineStyle", "None");

    /* Make tree ask for the height of each row. */
    tree.setRowHeight(-1);
    tree.setBackground(UIManager.getColor("menu.background"));

    JScrollPane sp = new JScrollPane();

    sp.setBorder(null);
    sp.getViewport().add(tree);
    getContentPanel().setLayout(new BorderLayout());
    getContentPanel().add(sp, BorderLayout.CENTER);
    toolbar = new JBookmarkPanel(VlibProperties.getString("toolbar-title"));


    for (int i = 0; i < getModel().getShortcutsID().size() ; i++) {
      int       id = ((Integer)getModel().getShortcutsID().get(i)).intValue();

      for (int j = 0; j < getModel().getModuleArray().length; j++) {
	if (getModel().getModuleArray()[j].getId() == id) {
	  addShortcut(getModel().getModuleArray()[j]);
	}
      }
    }
    if (!getModel().getShortcutsID().isEmpty() && !getModel().isSuperUser()) {
      toolbar.show();
      toolbar.toFront();
    }

    if (tree.getRowCount() > 0) {
      tree.setSelectionInterval(0, 0);
    }
    setMenu();
    tree.requestFocusInWindow();
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  /**
   * Show Application Information
   */
  public void showApplicationInformation(String message) {
    SwingThreadHandler.verifyRunsInEventThread("DWindow showApplicationInformation");
    verifyNotInTransaction("DWindow.showApplicationInformation(" + message + ")");

    Object[] options = {VlibProperties.getString("CLOSE")};

    JOptionPane.showOptionDialog(getFrame(),
                                 message,
                                 VlibProperties.getString("Notice"),
                                 JOptionPane.DEFAULT_OPTION,
                                 JOptionPane.INFORMATION_MESSAGE,
                                 Utils.getImage("info.gif"),
                                 options,
                                 options[0]);
   }

  /**
   * Adds the given module to avorites
   */
  public void addShortcut(final Module module) {
    if (!shortcuts.containsKey(module)) {
      AbstractAction    action = new AbstractAction(module.getDescription(), (ImageIcon)module.getIcon()) {

	public void actionPerformed(ActionEvent e) {
	  setWaitInfo(VlibProperties.getString("menu_form_started"));
	  getModel().performAsyncAction(new org.kopi.galite.visual.Action("menu_form_started") {
	    public void execute() {
	      module.run(getModel().getDBContext());
	      unsetWaitInfo();
	    }
	  });
	}

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long	serialVersionUID = -3982166081244304443L;
      };

      toolbar.addShortcut(action);
      shortcuts.put(module, action);
      orderdShorts.add(action);
      modules.add(module);
    }
  }

  /**
   * Removes the given module from favorites
   */
  public void removeShortcut(final Module module) {
    if (shortcuts.containsKey(module)) {
      modules.remove(module);
      Action    removed = (Action) shortcuts.remove(module);

      orderdShorts.remove(removed);
      toolbar.removeShortcut(removed);
    }
  }

  /**
   * Resets all favorites
   */
  public void resetShortcutsInDatabase() {
    try {
      getModel().getDBContext().startWork();    // !!! BEGIN_SYNC

      new Query(getModel()).run("DELETE FROM FAVORITEN WHERE Benutzer = " + getModel().getUserID());

      for (int i = 0; i < modules.size(); i++) {
        Module  module = (Module)modules.get(i);

        new Query(getModel()).run("INSERT INTO FAVORITEN VALUES ("
                                  + "{fn NEXTVAL(FAVORITENId)}" + ", "
                                  + (int)(System.currentTimeMillis()/1000) + ", "
                                  + getModel().getUserID() + ", "
                                  + module.getId()
                                  + ")");
      }

      getModel().getDBContext().commitWork();
    } catch (SQLException e) {
      try {
        getModel().getDBContext().abortWork();
      } catch (SQLException ef) {
        ef.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  /**
   * Move the focus from the activated frame to favorites frame.
   */
  public void gotoShortcuts() {
    if (!getModel().isSuperUser()) {
      try {
        if (toolbar.isVisible()) {
          toolbar.setVisible(false);
        }
        toolbar.setVisible(true);
        toolbar.toFront();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Launches the selected form in the menu tree.
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   */
  public void launchSelectedForm() throws VException {
    DefaultMutableTreeNode      node = getSelectedNode();

    if (node != null) {
      final Module      module = (Module)node.getUserObject();

      if (getModel().isSuperUser()) {
	module.setAccessibility((module.getAccessibility() + 1) % 3);
	((DefaultTreeModel)tree.getModel()).nodeChanged(node);
      } else if (node.isLeaf()) {
	setWaitInfo(VlibProperties.getString("menu_form_started"));

	module.run(getModel().getDBContext());
	unsetWaitInfo();
      }
    }
  }

  
  public void addSelectedElement() {
    DefaultMutableTreeNode      node = getSelectedNode();

    if (node != null && node.isLeaf()) {
      addShortcut((Module)node.getUserObject());
      resetShortcutsInDatabase();
    }
  }

  
  public void setMenu() {
    DefaultMutableTreeNode      node = getSelectedNode();

    getModel().setActorEnabled(VMenuTree.CMD_QUIT, !getModel().isSuperUser());
    getModel().setActorEnabled(VMenuTree.CMD_INFORMATION, true);
    getModel().setActorEnabled(VMenuTree.CMD_HELP, true);

    if (node != null) {
      Module    module = (Module)node.getUserObject();

      getModel().setToolTip(module.getHelp());
      getModel().setActorEnabled(VMenuTree.CMD_SHOW, shortcuts.size() > 0);
      if (node.isLeaf()) {
	getModel().setActorEnabled(VMenuTree.CMD_OPEN, true);
	getModel().setActorEnabled(VMenuTree.CMD_ADD, !shortcuts.containsKey(module));
	getModel().setActorEnabled(VMenuTree.CMD_REMOVE, shortcuts.containsKey(module));
	getModel().setActorEnabled(VMenuTree.CMD_FOLD, false);
	getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, false);
      } else {
	getModel().setActorEnabled(VMenuTree.CMD_OPEN, getModel().isSuperUser());
	getModel().setActorEnabled(VMenuTree.CMD_ADD, false);
	if (tree.isExpanded(tree.getSelectionPath())) {
	  getModel().setActorEnabled(VMenuTree.CMD_FOLD, true);
	  getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, false);
	} else {
	  getModel().setActorEnabled(VMenuTree.CMD_FOLD, false);
	  getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, true);
	}
      }
    }
  }

  
  public void removeSelectedElement() {
    DefaultMutableTreeNode      node = getSelectedNode();

    if (node != null && node.isLeaf()) {
      removeShortcut((Module)node.getUserObject());
      resetShortcutsInDatabase();
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
    TreePath   selPath = tree.getSelectionPath();

    if (selPath != null) {
      return (DefaultMutableTreeNode)selPath.getLastPathComponent();
    } else {
      return null;
    }
  }

  /**
   * Calls the selected form in the tree menu
   */
  private void callSelectedForm() {
    getModel().performAsyncAction(new KopiAction("menu_form_started2") {
      public void execute() throws VException {
	launchSelectedForm();
      }
    });
  }

  /**
   * Called to close the view (from the user), it does not
   * Definitely close the view(it may ask the user before)
   * Allowed to call outside the event dispatch thread
   */
  public final void closeWindow() {
    // ensure that it is executed in event dispatch Thread
    SwingThreadHandler.startAndWait(new Runnable() {
      public void run () {
	if (!getModel().isSuperUser()
	    && askUser(Message.getMessage("confirm_quit"), false))
	{
	  JApplication.quit();
	}
      }
    });
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  
  public UTree getTree() {
    return tree;
  }

  
  public UBookmarkPanel getBookmark() {
    return toolbar;
  }

  
  public VMenuTree getModel() {
    return (VMenuTree) super.getModel();
  }

  /**
   * @return The book mark swing actions
   */
  public Action[] getBookmarkActions() {
    return (Action[]) orderdShorts.toArray(new Action[shortcuts.size()]);
  }

  //------------------------------------------------------------
  // UTREE IMPLEMENTATION
  //------------------------------------------------------------

  public class Tree extends JTree implements UTree {

    //--------------------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------------------

    public Tree(TreeNode root) {
      super(root);
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

    //------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------

    private static final long 		serialVersionUID = 6483277701915117544L;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private Tree					tree;
  private JBookmarkPanel        		toolbar;
  private Map<Module, Action>              	shortcuts;
  private List<Action>             		orderdShorts;
  private List<Module>                  	modules;
  private static final long 			serialVersionUID = -6740174181163603800L;
}
