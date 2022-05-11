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
 * $Id: DMenuBar.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.kopi.galite.visual.VlibProperties;

public class DMenuBar extends JMenuBar {


/**
   * Constructs a menu panel
   */
  /*package*/ DMenuBar() {
    setBorderPainted(false);
    setFocusable(false);
  }

  /**
   * Adds a menu item
   */
  DMenuItem addItem(DActor actor) { //, DActor dactor) {
    JMenu		menu = null;
    DMenuItem		item;

    //!!! NOT YET IMPLEMENTED IN SWING
    if (actor.getModel().getMenuIdent$galite_core().equals("Help")) {
      if ((menu = getHelpMenu()) == null) {
	menu = new JMenu(actor.getModel().getMenuName$galite_core());
	setHelpMenu(menu);
      }
    } else {
      /* lookup menu with name menuName, add it if necessary */
      for (int i = 0; menu == null && i < getMenuCount(); i++) {
	if (getMenu(i).getText().equals(actor.getModel().getMenuName$galite_core())) {
	  menu = getMenu(i);
	}
      }

      if (menu == null) {
	menu = this.add(new JMenu(actor.getModel().getMenuName$galite_core()));
      }
    }

    /* add menu item */
    item = (DMenuItem) menu.add(new DMenuItem(actor.getAction())); //actor.menuItem));

    return item;
  }

  public void addSeparator(String menuName) {
    JMenu		menu = null;

    //!!! NOT YET IMPLEMENTED IN SWING
    if (menuName.equals(VlibProperties.getString("help-menu"))) {
      if ((menu = getHelpMenu())== null) {
	menu = new JMenu(menuName);
	setHelpMenu(menu);
      }
    } else {
      for (int i = 0; menu == null && i < getMenuCount(); i++) {
	if (getMenu(i).getText().equals(menuName)) {
	  menu = getMenu(i);
	}
      }

      if (menu == null) {
	menu = this.add(new JMenu(menuName));
      }
    }

    menu.addSeparator();
  }

  // !!! TO BE REMVED WHEN SWING WILL IMPLEMENT IT
  public void setHelpMenu(JMenu menu) {
    help = menu;
  }
  public JMenu getHelpMenu() {
    return help;
  }
  public void finalizeMenu() {
    if (getHelpMenu() != null) {
      this.add(getHelpMenu());
    }
  }

  // ---------------------------------------------------------------------
  // ACTION LISTENER PANEL CLASS
  // ---------------------------------------------------------------------

  private JMenu 	help;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 5957063959837257811L;

}
