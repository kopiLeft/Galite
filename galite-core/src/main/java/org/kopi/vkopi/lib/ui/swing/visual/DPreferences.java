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
 * $Id: DPreferences.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.Point;

/**
 * !!! Not used in Kopi. Check if used by an application
 */
public class DPreferences extends java.util.Hashtable<String, Object> {


  /**
   *
   */
  public void setButtonSize(boolean small) {
    put(PRF_BUTTON_SIZE, new Boolean(small));
  }

  /**
   *
   */
  public boolean getButtonSize() {
    if (containsKey(PRF_WINDOW_LOCATION)) {
      return ((Boolean)get(PRF_BUTTON_SIZE)).booleanValue();
    } else {
      return false;  // default value
    }
  }

  /**
   *
   */
  public void setWindowLocation(Point location) {
    put(PRF_WINDOW_LOCATION, location);
  }

  /**
   *
   */
  public Point getWindowLocation() {
    return (Point)get(PRF_WINDOW_LOCATION);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private static final String	PRF_WINDOW_LOCATION	= "window_location";
  private static final String	PRF_BUTTON_SIZE		= "button_size";
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 8427341992030423029L;
}
