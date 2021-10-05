/*
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH
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
 * $Id: KopiTheme.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class KopiTheme extends DefaultMetalTheme {
  public static KopiUserColors  USER_COLORS = new KopiUserColors();

  // primary colors
  //----------------
  // border of menu / key stroke tip in menu
  private static final ColorUIResource PRIMARY1 = USER_COLORS.getUserColor("primay1", USER_COLORS.COLOR_3);
  // menu header if pressed / menu sleceted item
  private static final ColorUIResource PRIMARY2 = USER_COLORS.getUserColor("primay2", USER_COLORS.COLOR_9);
  // tooltip background color if enabled
  private static final ColorUIResource PRIMARY3 = USER_COLORS.getUserColor("primay3", USER_COLORS.COLOR_14);

  // secondary colors
  //------------------
  // border of components/ text in disabled menus
  private static final ColorUIResource SECONDARY1 = USER_COLORS.getUserColor("secondary1", USER_COLORS.COLOR_3);
  // disabled buttons in scrollbars
  private static final ColorUIResource SECONDARY2 = USER_COLORS.getUserColor("secondary2", USER_COLORS.COLOR_3);
  // background of components
  private static final ColorUIResource SECONDARY3 = USER_COLORS.getUserColor("secondary3", USER_COLORS.COLOR_1);

  // methods
  public String getName() { return "Default Kopi Theme"; }

  protected ColorUIResource getPrimary1() { return PRIMARY1; }
  protected ColorUIResource getPrimary2() { return PRIMARY2; }
  protected ColorUIResource getPrimary3() { return PRIMARY3; }

  protected ColorUIResource getSecondary1() { return SECONDARY1; }
  protected ColorUIResource getSecondary2() { return SECONDARY2; }
  protected ColorUIResource getSecondary3() { return SECONDARY3; }

  // background color of pressed Button
  public ColorUIResource getControlShadow() { return USER_COLORS.COLOR_6; } 
  public ColorUIResource getInactiveControlTextColor() { return SECONDARY1; }  
  public ColorUIResource getMenuDisabledForeground() { return getSecondary1(); }
}
