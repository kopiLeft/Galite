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
 * $Id: DMenuItem.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;

/**
 * This subclass of JMenuItem redefines setAccelerator to enable unsetting
 * of key accelerators.
 */

public class DMenuItem extends JMenuItem {

	

/**
   * Creates a menu item with text.
   */
  public DMenuItem(Action action) {//String text) {
    super(action);
    setIcon(null);
  }

  /**
   * Sets the key combination which invokes the Menu Item's
   * action listeners without navigating the menu hierarchy
   *
   * This method redefines setAccelerator in JMenuItem to allow
   * unsetting of the accelerator.
   */
  public void setAccelerator(KeyStroke keyStroke) {
    if (accelerator != null) {
      unregisterKeyboardAction(accelerator);
    }

    if (keyStroke != null) {
      registerKeyboardAction(new AbstractAction() {
	/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = -3940868217103016855L;

	public void actionPerformed(ActionEvent e) {
	  MenuSelectionManager.defaultManager().clearSelectedPath();
	  doClick();
	}
      }, keyStroke, WHEN_IN_FOCUSED_WINDOW);
    }

    this.accelerator = keyStroke;
  }

  /**
   * Returns the KeyStroke which serves as an accelerator
   * for the menu item.
   *
   * This method redefines getAccelerator in JMenuItem because
   * accelerator is a private member of JMenuItem
   */
  public KeyStroke getAccelerator() {
    return this.accelerator;
  }

  private KeyStroke	accelerator;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 1355170976267948069L;
}
