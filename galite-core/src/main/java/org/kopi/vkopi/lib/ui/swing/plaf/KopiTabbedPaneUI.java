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
 * $Id: KopiTabbedPaneUI.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.text.View;

public class KopiTabbedPaneUI extends MetalTabbedPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new KopiTabbedPaneUI();
  }
  
  protected void paintText(Graphics g, int tabPlacement,
                           Font font, FontMetrics metrics, int tabIndex,
                           String title, Rectangle textRect, 
                           boolean isSelected) {
    g.setFont(font);
    
    View v = getTextViewForTab(tabIndex);
    if (v != null) {
      // html
      v.paint(g, textRect);
    } else {
      // plain text
      int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
      
      if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
        g.setColor(tabPane.getForegroundAt(tabIndex));
        BasicGraphicsUtils.drawStringUnderlineCharAt(g,
                                                     title, mnemIndex,
                                                     textRect.x, textRect.y + metrics.getAscent());
		
      } else { // tab disabled
        g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
        BasicGraphicsUtils.drawStringUnderlineCharAt(g,
                                                     title, mnemIndex,
                                                     textRect.x, textRect.y + metrics.getAscent());
//         g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
//         BasicGraphicsUtils.drawStringUnderlineCharAt(g,
//                                                      title, mnemIndex,
//                                                      textRect.x - 1, textRect.y + metrics.getAscent() - 1);
        
      }
    }
  }


  protected void paintTabBorder(Graphics g, int tabPlacement,
                                int tabIndex,
                                int x, int y, int w, int h, 
                                boolean isSelected ) {
    g.setColor(lightHighlight);  
        
    switch (tabPlacement) {
    case LEFT:
      g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight
      g.drawLine(x, y+2, x, y+h-3); // left highlight
      g.drawLine(x+1, y+1, x+1, y+1); // top-left highlight
      g.drawLine(x+2, y, x+w-1, y); // top highlight
      
      g.setColor(shadow);
      g.drawLine(x+2, y+h-2, x+w-1, y+h-2); // bottom shadow
      
      g.setColor(darkShadow);
      g.drawLine(x+2, y+h-1, x+w-1, y+h-1); // bottom dark shadow
      break;
    case RIGHT:
      g.drawLine(x, y, x+w-3, y); // top highlight
      
      g.setColor(shadow);
      g.drawLine(x, y+h-2, x+w-3, y+h-2); // bottom shadow
      g.drawLine(x+w-2, y+2, x+w-2, y+h-3); // right shadow
      
      g.setColor(darkShadow);
      g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right dark shadow
      g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
      g.drawLine(x+w-1, y+2, x+w-1, y+h-3); // right dark shadow
      g.drawLine(x, y+h-1, x+w-3, y+h-1); // bottom dark shadow
      break;              
    case BOTTOM:
      g.drawLine(x, y, x, y+h-3); // left highlight
      g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight
      
      g.setColor(shadow);
      g.drawLine(x+2, y+h-2, x+w-3, y+h-2); // bottom shadow
      g.drawLine(x+w-2, y, x+w-2, y+h-3); // right shadow
      
      g.setColor(darkShadow);
      g.drawLine(x+2, y+h-1, x+w-3, y+h-1); // bottom dark shadow
      g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
      g.drawLine(x+w-1, y, x+w-1, y+h-3); // right dark shadow
      break;
    case TOP:
    default:    
      Color     selectedColor = UIManager.getColor("TabbedPane.selected");

      g.setColor(selectedColor);

      if (isFirstTab(tabIndex)) {
        // first tab
        Polygon   p = new Polygon(new int[] {x,x+TAB_X_SPACE*2,x},new int[]{y,y,y+h-1},3);
      
        g.setColor(areaColor);  
        g.fillPolygon(p);

        g.setColor(lightHighlight);  
        g.drawLine(x, y+h, x+TAB_X_SPACE*2, y); // left highlight
        g.drawLine(x+TAB_X_SPACE*2, y, x+w-1, y); // top highlight
        //        g.drawLine(x+w-1, y, x+w-1, y+h); // right highlight

      } else {
        // later tabs
        Polygon   p = new Polygon(new int[] {x+TAB_X_SPACE, x+TAB_X_SPACE*2, x+TAB_X_SPACE}, new int[]{y, y, y+(h/2)-1}, 3);
       
        g.setColor(areaColor);  
        g.fillPolygon(p);

        if (isSelected) {
          g.setColor(tabPane.getBackgroundAt(tabIndex-1));

          Polygon   p1 = new Polygon(new int[] {x, x+TAB_X_SPACE-1, x+TAB_X_SPACE-1, x}, new int[]{y+2, y+2, y+(h/2)-1, y+h-1}, 4);
         
          g.fillPolygon(p1);    
    
          g.setColor(lightHighlight);  
          g.drawLine(x, y+h, x+TAB_X_SPACE*2, y); // left highlight
          g.drawLine(x+TAB_X_SPACE, y, x+TAB_X_SPACE, y+h/2); // right highlight              
        } else {
          g.setColor((tabIndex-1 == tabPane.getSelectedIndex()) ? selectedColor : tabPane.getBackgroundAt(tabIndex-1));

          Polygon   p1 = new Polygon(new int[] {x, x+TAB_X_SPACE, x+TAB_X_SPACE, x}, new int[]{y+2, y+2, y+h, y+h}, 4);
          
          g.fillPolygon(p1);  
  
          g.setColor(lightHighlight);  
          g.drawLine(x+TAB_X_SPACE, y+(h/2), x+TAB_X_SPACE*2, y); // left highlight
          g.drawLine(x+TAB_X_SPACE, y, x+TAB_X_SPACE, y+h); // right highlight              
        }
        g.setColor(lightHighlight);

        g.drawLine(x, y, x+TAB_X_SPACE, y); // top highlight              
        g.drawLine(x+TAB_X_SPACE*2, y, x+w-1, y); // top highlight
      }

      if (tabIndex == tabPane.getTabCount()-1) {
        g.setColor(lightHighlight);
        g.drawLine(x+w-1, y, x+w-1, y+h-1); // right highlight
      }
   //   g.setColor(shadow);  
//      g.drawLine(x+w-2, y+2, x+w-2, y+h-1); // right shadow
              
//      g.setColor(darkShadow); 
//      g.drawLine(x+w-1, y+2, x+w-1, y+h-1); // right dark-shadow
//      g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right shadow
    }
  }
 

  protected boolean isFirstTab(int tabIndex) {
    if (runCount == 1) {
      return (tabIndex == 0);
    }
    for (int i=0; i < runCount; i++) {
      if (tabIndex == tabRuns[i]) {
        return true;
      } 
    }
    return false;
  }

  protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                     Rectangle[] rects, int tabIndex, 
                                     Rectangle iconRect, Rectangle textRect,
                                     boolean isSelected) {
    
  }
  protected int getTabRunIndent(int tabPlacement, int run) {
    return 7*(run+1)+ TAB_X_SPACE*2*run;
  }

  protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
    Rectangle clipRect = g.getClipBounds();  
    
    g.setColor(areaColor);
    g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
    super.paintTabArea(g, tabPlacement, selectedIndex);
  }


  protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
    Icon icon = getIconForTab(tabIndex);
    Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
    int width = tabInsets.left + tabInsets.right + 3;

    if (icon != null) {
      width += icon.getIconWidth() + textIconGap;
    }
    View v = getTextViewForTab(tabIndex);
    if (v != null) {
      // html
      width += (int)v.getPreferredSpan(View.X_AXIS);
    } else {
      // plain text
      String title = tabPane.getTitleAt(tabIndex);
      width += SwingUtilities.computeStringWidth(metrics, title);
    }
    
    return width+TAB_X_SPACE;
  }

  protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                           int selectedIndex, 
                                           int x, int y, int w, int h) {
    Rectangle selRect = selectedIndex < 0? null :
      getTabBounds(selectedIndex, calcRect);

    g.setColor(lightHighlight);

    // Draw unbroken line if tabs are not on TOP, OR
    // selected tab is not in run adjacent to content, OR
    // selected tab is not visible (SCROLL_TAB_LAYOUT)
    //
    if (tabPlacement != TOP || selectedIndex < 0 || 
        (selRect.y + selRect.height + 1 < y) ||
        (selRect.x < x || selRect.x > x + w)) {
      g.drawLine(x, y, x+w-2, y);
    } else {
      // Break line to show visual connection to selected tab
      int       offset = 0;
      
      if (selectedIndex != tabPane.getTabCount()-1) {
        offset = TAB_X_SPACE;
      }

      g.drawLine(x, y, selRect.x - 1, y);
      if (selRect.x + selRect.width < x + w - 2 + offset) {
        g.drawLine(selRect.x + selRect.width + offset, y, x+w-2, y);
      } else {
        g.setColor(shadow); 
        g.drawLine(x+w-2, y, x+w-2, y);
      }
    }
  }

    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                               int selectedIndex,
                                               int x, int y, int w, int h) { 
    

    }
 
  protected void layoutLabel(int tabPlacement, 
                             FontMetrics metrics, int tabIndex,
                             String title, Icon icon,
                             Rectangle tabRect, Rectangle iconRect, 
                             Rectangle textRect, boolean isSelected ) {
    textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
    
    View v = getTextViewForTab(tabIndex);
    if (v != null) {
      tabPane.putClientProperty("html", v);
    }

    SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
                                       metrics, title, icon,
                                       SwingConstants.CENTER,
                                       SwingConstants.CENTER,
                                       SwingConstants.CENTER,
                                       SwingConstants.TRAILING,
                                       tabRect,
                                       iconRect,
                                       textRect,
                                       textIconGap);
    
    tabPane.putClientProperty("html", null);
    
    int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
    int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);

    iconRect.x += xNudge + TAB_X_SPACE;
    iconRect.y += yNudge;
    textRect.x += xNudge + TAB_X_SPACE;
    textRect.y += yNudge;
  }

  protected void installDefaults() {
    super.installDefaults();

    areaColor = UIManager.getColor("TabbedPaneUI.tabarea.background");
  }

  private static final int      TAB_X_SPACE = 10;
  private static Color          areaColor;
}
