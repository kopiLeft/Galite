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
 * $Id: KnownBugs.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.MediaTracker;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 
 */
public class KnownBugs {
   /**
   * 2003.08.14; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * save that a failure with paintIcon happend
   */
  public static boolean paintIconFailure;
  /**
   * 2003.09.23; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * save that an Icon was not correctly loaded
   */
  public static String paintIconReload;

  /**
   * 2003.09.23; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * save that a failure with paintIcon happend
   */
  public static void loadImage(Icon icon) {
    if (icon instanceof ImageIcon) {
      ImageIcon       imageIcon = ((ImageIcon) icon);

      if (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
        // reload image
        imageIcon.setImage(imageIcon.getImage());
        paintIconReload = imageIcon.toString()+"  status now: "+imageIcon.getImageLoadStatus()+" (ok if 8) ";
      }
    }
  }
}
