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
 * $Id: KopiTitledBorder.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class KopiTitledBorder extends TitledBorder {
  
/**
   * Creates a KopiTitledBorder instance.
   * 
   * @param title  the title the border should display
   */
  public KopiTitledBorder(Border border, String title)     {
    super(border, title);
  }

  /**
   * Paints the border for the specified component with the 
   * specified position and size.
   * @param c the component for which this border is being painted
   * @param g the paint graphics
   * @param x the x position of the painted border
   * @param y the y position of the painted border
   * @param width the width of the painted border
   * @param height the height of the painted border
   */
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Border          border = getBorder();
    Rectangle       grooveRect = new Rectangle(x + EDGE_SPACING, y + EDGE_SPACING,
                                               width - (EDGE_SPACING * 2),
                                               height - (EDGE_SPACING * 2));
    Font            font = g.getFont();
    Color           color = g.getColor();

    g.setFont(borderFont);
    
    FontMetrics fm = g.getFontMetrics();
    int         fontHeight = fm.getHeight();
    int         descent = fm.getDescent();

    GradientPaint gp = new GradientPaint(grooveRect.x, 
                                         grooveRect.y + fontHeight + 4,
                                         borderColor,
                                         (float) ((grooveRect.x + grooveRect.width) * 0.9),
                                         grooveRect.y + fontHeight + 4, 
                                         backColor);
    Graphics2D g2d = (Graphics2D) g;
    
    g2d.setPaint(gp);
    g.fillRect(grooveRect.x, grooveRect.y + fontHeight +2, (int)(grooveRect.width * 0.9), 2);
    
    border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                       grooveRect.width, grooveRect.height);
    
    g.setColor(foreColor);
    g.drawString(getTitle(), grooveRect.x+5, grooveRect.y+fontHeight+2-descent);
    
    g.setFont(font);
    g.setColor(color);
  }

  /** 
   * Reinitialize the insets parameter with this Border's current Insets. 
   * @param c the component for which this border insets value applies
   * @param insets the object to be reinitialized
   */
  public Insets getBorderInsets(Component c, Insets insets) {
    FontMetrics fm;
    int         height = 16;
    
    Border border = getBorder();
    if (border != null) {
      if (border instanceof AbstractBorder) {
        ((AbstractBorder)border).getBorderInsets(c, insets);
      } else {
        // Can't reuse border insets because the Border interface
        // can't be enhanced.
        Insets i = border.getBorderInsets(c);
        insets.top = i.top;
        insets.right = i.right;
        insets.bottom = i.bottom;
        insets.left = i.left;
      }
    } else {
      insets.left = insets.top = insets.right = insets.bottom = 0;
    }
    
    insets.left += EDGE_SPACING + TEXT_SPACING;
    insets.right += EDGE_SPACING + TEXT_SPACING;
    insets.top += EDGE_SPACING + TEXT_SPACING;
    insets.bottom += EDGE_SPACING + TEXT_SPACING;
    
    if(c == null || getTitle() == null || getTitle().equals(""))    {
      return insets;
    }
    
    fm = c.getFontMetrics(borderFont);
    
    if(fm != null) {
      height = fm.getHeight();
    }
    
    insets.top += height;
      
    return insets;
  }
  

  private static final Color    borderColor = UIManager.getColor("KopiTitleBorder.border");
  private static final Color    foreColor = UIManager.getColor("KopiTitleBorder.foreground");
  private static final Color    backColor = UIManager.getColor("KopiTitleBorder.background");

  private static final Font     borderFont = UIManager.getFont("KopiTitleBorder.font");

  public static final Border    BRD_EMPTY =   UIManager.getBorder("KopiTitleBorder.border.emtpy");
  public static final Border    BRD_RAISED =  UIManager.getBorder("KopiTitleBorder.border.raised");
  public static final Border    BRD_ETCHED =  UIManager.getBorder("KopiTitleBorder.border.etched");
  public static final Border    BRD_LINE =    UIManager.getBorder("KopiTitleBorder.border.line");
  public static final Border    BRD_LOWERED = UIManager.getBorder("KopiTitleBorder.border.lowered");
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -1785569445115086891L;
}
