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
 * $Id: JImageHandler.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.net.URL;

import javax.swing.ImageIcon;

import org.kopi.galite.visual.base.Image;
import org.kopi.galite.visual.ImageHandler;

public class JImageHandler extends ImageHandler {

  /**
   * 
   */
  public Image getImage(String image) {
    return new JImage(Utils.getImage(image).getImage());
  }

  /**
   * 
   */
  public Image getImage(byte[] image) {
    return new JImage(image);
  }

  /**
   * 
   */
  public String getURL(String image) {
    return Utils.Companion.getURLFromResource(image).toString();
  }

  //-------------------------------------------------------------------------
  // IMAGE IMPLEMENTATION
  //-------------------------------------------------------------------------

  public static class JImage extends ImageIcon implements Image {

    //------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------

    public JImage() {
      super();
    }

    public JImage(byte[] imageData, String description) {
      super(imageData, description);
    }

    public JImage(byte[] imageData) {
      super(imageData);
    }

    public JImage(java.awt.Image image, String description) {
      super(image, description);
    }

    public JImage(java.awt.Image image) {
      super(image);
    }

    public JImage(String filename, String description) {
      super(filename, description);
    }

    public JImage(String filename) {
      super(filename);
    }

    public JImage(URL location, String description) {
      super(location, description);
    }

    public JImage(URL location) {
      super(location);
    }

    //------------------------------------------------------------------
    // IMAGE IMPLEMENTATION
    //------------------------------------------------------------------

    /**
     * 
     */
    public int getImageWidth() {
      return getIconWidth();
    }

    /**
     * 
     */
    public int getImageHeight() {
      return getIconHeight();
    }

    /**
     * 
     */
    public String getDescription() {
      return super.getDescription();
    }

    /**
     * 
     */
    public Image getScaledInstance(int width, int height, int hints) {
      return new JImage(getImage().getScaledInstance(width, height, hints));
    }

    //------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------

    private static final long 			serialVersionUID = -5756200548104021844L;
  }
}
