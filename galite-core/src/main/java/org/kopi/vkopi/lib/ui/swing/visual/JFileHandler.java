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
 * $Id: JFileHandler.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.kopi.galite.visual.FileHandler;
import org.kopi.galite.visual.UWindow;

public class JFileHandler extends FileHandler {

  // ----------------------------------------------------------------------
  // FILE HANDLER IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * 
   */
  public File chooseFile(UWindow window, String defaultName) {
    File dir = new File(System.getProperty("user.home"));
    return chooseFile(window, dir, defaultName);
  }

  /**
   * 
   */
  public File chooseFile(UWindow window, File dir, String defaultName) {
    JFileChooser filechooser = new JFileChooser(dir);

    // Init our preferences
    //filechooser.setApproveButtonText("TXT1 YES");
    //filechooser.setApproveButtonToolTipText("TXT2");
    //filechooser.setDialogTitle("XXXTITLE");
    filechooser.setSelectedFile(new File(defaultName));

    int returnVal = filechooser.showSaveDialog((Component)window);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return filechooser.getSelectedFile();
    } else {
      return null;
    }
  }

  /**
   * 
   */
  public File openFile(UWindow window, String defaultName) {
    File dir = new File(System.getProperty("user.home"));
    return openFile(window, dir, defaultName);
  }

  /**
   * 
   */
  public File openFile(UWindow window, final FileFilter filter) {
    JFileChooser filechooser = new JFileChooser(new File(System.getProperty("user.home")));

    filechooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

      /**
       * 
       */
      public String getDescription() {
	return filter.getDescription();
      }

      /**
       * 
       */
      public boolean accept(File f) {
	return filter.accept(f);
      }
    });

    int returnVal = filechooser.showOpenDialog((Component)window);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return filechooser.getSelectedFile();
    } else {
      return null;
    }

  }

  /**
   * 
   */
  public File openFile(UWindow window, File dir, String defaultName) {
    JFileChooser filechooser = new JFileChooser(dir);

    filechooser.setSelectedFile(new File(defaultName));

    int returnVal = filechooser.showOpenDialog((Component)window);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return filechooser.getSelectedFile();
    } else {
      return null;
    }
  }
}
