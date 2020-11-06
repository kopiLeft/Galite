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
 * $Id: KopiProgressBarUI.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalProgressBarUI;

public class KopiProgressBarUI extends MetalProgressBarUI {

  public static ComponentUI createUI(JComponent x ) {
    return new KopiProgressBarUI();
  }

  /**
   * Draws a bit of special highlighting on the progress bar.
   * The core painting is deferred to the BasicProgressBar's
   * <code>paintDeterminate</code> method.
   */ 
  public void paintDeterminate(Graphics g, JComponent c) {
    super.paintDeterminate(g, c);
  }

  /**
   * Draws a bit of special highlighting on the progress bar
   * and bouncing box.
   * The core painting is deferred to the BasicProgressBar's
   * <code>paintIndeterminate</code> method.
   */ 
  public void paintIndeterminate(Graphics g, JComponent c) {
    super.paintIndeterminate(g, c);    

    Insets  b = progressBar.getInsets(); // area for border
    int     barRectWidth = progressBar.getWidth() - (b.left + b.right);
    int     barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
    int     startX, startY, endX, endY;
    
    // The progress bar border is painted according to a light source.
    // This light source is stationary and does not change when the
    // component orientation changes.
    startX = b.left;
    startY = b.top;
    endX = b.left + barRectWidth - 1;
    endY = b.top + barRectHeight - 1;

    g.setColor(c.getBackground());
    g.drawRect(startX, startY, endX-startX, endY-startY);
    g.drawRect(startX+1, startY+1, endX-startX-1, endY-startY-1);
  }
}
