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
 * $Id: JApplicationContext.java 34982 2016-11-18 07:43:21Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import org.kopi.vkopi.lib.visual.Application;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.PreviewRunner;

/**
 * {@code JApplicationContext} is an application context
 * for swing applications.
 */
public class JApplicationContext extends ApplicationContext {
    
  //--------------------------------------------------
  // CONSTRUCTOR
  //--------------------------------------------------

  public Application getApplication() {
    return JApplication.getInstance();
  }

  /**
   * Returns the current PreviewRunner.
   */
  public PreviewRunner getPreviewRunner() {
    return  previewRunner == null ? previewRunner = new JPreviewRunner() : previewRunner ;
  }

  /**
   * Checks if we are in a web application context.
   */
  public boolean isWebApplicationContext() {
    return false;
  }
  
  //--------------------------------------------------
  // DATA MEMBEERS
  //--------------------------------------------------
  
  private JPreviewRunner			previewRunner;
}
