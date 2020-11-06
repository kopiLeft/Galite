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
 * $Id: KopiUserColors.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/**
 */
public class KopiUserColors {

  /**
   *
   */
  public KopiUserColors() {
    // resources = new PropertyManager(USR_COLOR_FILE);

    COLOR_1 = getUserColor("color1", COLOR_1);
    COLOR_2 = getUserColor("color2", COLOR_2);
    COLOR_3 = getUserColor("color3", COLOR_3);
    COLOR_4 = getUserColor("color4", COLOR_4);
    COLOR_5 = getUserColor("color5", COLOR_5);
    COLOR_6 = getUserColor("color6", COLOR_6);
    COLOR_7 = getUserColor("color7", COLOR_7);
    COLOR_8 = getUserColor("color8", COLOR_8);
    COLOR_9 = getUserColor("color9", COLOR_9);
    COLOR_10 = getUserColor("color10", COLOR_10);
    COLOR_11 = getUserColor("color11", COLOR_11);
    COLOR_12 = getUserColor("color12", COLOR_12);
    COLOR_13 = getUserColor("color13", COLOR_13);
    COLOR_14 = getUserColor("color14", COLOR_14);
  }

  public void installUserColors(UIDefaults table, String name, ColorUIResource def) {
    ColorUIResource      value;

    try {
      value = getUserColor(name, def);
    } catch (Exception e) {
      value = def;
    }

    table.put(name, value);
  }

  public ColorUIResource getUserColor(String key, ColorUIResource def) {
    //String               val = resources.getString(key);

    //return  (val == null)? def : new ColorUIResource(Color.decode(val));
    return def;
  }

  //private PropertyManager       resources;
  //private static String         USR_COLOR_FILE = "user";

  public  ColorUIResource COLOR_1  = new ColorUIResource(248, 247, 241);      // light brown
  public  ColorUIResource COLOR_2  = new ColorUIResource(214, 223, 247);      // light blue
  public  ColorUIResource COLOR_3  = new ColorUIResource(173, 170, 156);      // dark brown
  public  ColorUIResource COLOR_4  = new ColorUIResource(123, 158, 189);      // middle blue
  public  ColorUIResource COLOR_5  = new ColorUIResource(  0,  60, 115);      // near black
  public  ColorUIResource COLOR_6  = new ColorUIResource(239, 235, 222);      // middle brown
  public  ColorUIResource COLOR_7  = new ColorUIResource(  0,  69, 214);      // dark blue
  public  ColorUIResource COLOR_8  = new ColorUIResource( 66, 154, 255);      // middle blue (line)
  public  ColorUIResource COLOR_9  = new ColorUIResource( 49, 105, 198);      // middle blue (selection)
  public  ColorUIResource COLOR_10 = new ColorUIResource(231, 150,   0);      // dark orange (rollover)
  public  ColorUIResource COLOR_11 = new ColorUIResource(255, 219, 140);      // bright orange (rollover)
  public  ColorUIResource COLOR_12 = new ColorUIResource(107, 130, 239);      // dark blue (rollover)
  public  ColorUIResource COLOR_13 = new ColorUIResource(189, 215, 247);      // bright blue (rollover)
  public  ColorUIResource COLOR_14 = new ColorUIResource(255, 255, 231);      // light yellow (tooltip)
}
