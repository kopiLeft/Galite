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
 * $Id: JBookmarkPanel.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.kopi.galite.visual.UMenuTree.UBookmarkPanel;

public class JBookmarkPanel extends JFrame implements UBookmarkPanel {

  //-------------------------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------------------------

  public JBookmarkPanel(String title) {
    super(title);

    buttons = new JPanel();
    buttons.setLayout(new GridLayout(1, 1));
    getContentPane().add(buttons);
    shortcuts = new Hashtable<Action, JComponent>();
    pack();
  }

  public void addShortcut(Action action) {
    JMenuItem	button = new JMenuItem(action);

    if (shortcuts.size() < 10) {
      button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0+shortcuts.size(), InputEvent.CTRL_DOWN_MASK));
    }
    buttons.add(button);
    shortcuts.put(action, button);

    int         countShortcut = shortcuts.size();

    buttons.setLayout(new GridLayout(countShortcut, 1));
    pack();
  }

  public void removeShortcut(Action action) {
    buttons.remove(shortcuts.remove(action));

    int         countShortcut = shortcuts.size();

    for (int i=0; i < buttons.getComponentCount() && i <10; i++) {
      ((JMenuItem) buttons.getComponent(i)).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0+i, InputEvent.CTRL_DOWN_MASK));
    }

    buttons.setLayout(new GridLayout(countShortcut, 1));
    pack();
  }

  //-------------------------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------------------------

  private JPanel                		buttons;
  private Hashtable<Action, JComponent>		shortcuts;

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long 			serialVersionUID = -349948284157504110L;
}
