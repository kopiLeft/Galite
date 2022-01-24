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
 * $Id: KopiScrollBarUI.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.plaf.metal.MetalScrollButton;

public class KopiScrollBarUI extends MetalScrollBarUI {

  public static ComponentUI createUI(JComponent c) {
    return new KopiScrollBarUI();
  }

  protected JButton createDecreaseButton(int orientation) {
    decreaseButton = new KopiScrollButton(orientation, scrollBarWidth, isFreeStanding);
    return decreaseButton;
  }
  protected JButton createIncreaseButton(int orientation) {
    increaseButton = new KopiScrollButton(orientation, scrollBarWidth, isFreeStanding);
    return increaseButton;
  }

  static class KopiScrollButton extends MetalScrollButton {
    
	public KopiScrollButton(int direction, int width, boolean freeStanding) {
      super(direction, width, freeStanding);

      this.isFreeStanding = freeStanding;
    }

    public void paint(Graphics g) {
      boolean     leftToRight = getComponentOrientation().isLeftToRight();
      boolean     isEnabled = getParent().isEnabled();
      
      Color       arrowColor = isEnabled ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
      boolean     isPressed = getModel().isPressed();
      int         width = getWidth();
      int         height = getHeight();
      int         w = width;
      int         h = height;
      int         arrowHeight = (height+1) / 4;
     
      if (!isEnabled) {
        g.setColor(thumbColor1);
      } else if (isPressed) {
        g.setColor(MetalLookAndFeel.getControlShadow());
      } else {
        g.setColor(thumbColor1);
      }
      g.fillRect(0, 0, width, height);

      if (getDirection() == NORTH) {
        if (!isFreeStanding) {
          height +=1;
          g.translate(0, -1);
          if (!leftToRight) {
            width += 1;
            g.translate(-1, 0);
          } else {
            width += 2;
          }
        }

        // Draw the arrow
        g.setColor( arrowColor );
        
        int    startY = ((h+1) - arrowHeight) / 2;
        int    startX = (w / 2);
        
        for (int line = 0; line < arrowHeight; line++) {
          g.drawLine( startX-line, startY+line, startX +line+1, startY+line);
        }
	        
        if (isEnabled) {
          g.setColor(border_color);
          g.drawRect(0, 0, width-1, height-1);
           
//            if ( !isPressed ) {
//              g.drawLine( 1, 1, width - 3, 1 );
//              g.drawLine( 1, 1, 1, height - 1 );
//            }
           
//            //           g.drawLine( width - 1, 1, width - 1, height - 1 );
           
//            //           g.setColor( shadowColor );
// //            g.drawLine( 0, 0, width - 2, 0 );
// //            g.drawLine( 0, 0, 0, height - 1 );
// //            g.drawLine( width - 2, 2, width - 2, height - 1 );
// //           g.drawRect(0,0,width,height);
        } else {
//            drawDisabledBorder(g, 0, 0, width, height+1);
        }
        if ( !isFreeStanding ) {
          height -= 1;
          g.translate( 0, 1 );
          if ( !leftToRight ) {
            width -= 1;
            g.translate( 1, 0 );
          } else {
            width -= 2;
          }
        }
      } else if ( getDirection() == SOUTH ) {
        if ( !isFreeStanding ) {
          height += 1;
          if ( !leftToRight ) {
            width += 1;
            g.translate( -1, 0 );
          } else {
            width += 2;
          }
        }
        
        // Draw the arrow
        g.setColor( arrowColor );
        
        int startY = (((h+1) - arrowHeight) / 2)+ arrowHeight-1;
        int startX = (w / 2);
        
        //	    System.out.println( "startX2 :" + startX + " startY2 :"+startY);
         
        for (int line = 0; line < arrowHeight; line++) {
          g.drawLine( startX-line, startY-line, startX +line+1, startY-line);
        }
         
        if (isEnabled) {
          g.setColor(border_color);
          g.drawRect(0, 0, width-1, height-1);
        //   g.setColor( highlightColor );
          
//           if ( !isPressed ) {
//             g.drawLine( 1, 0, width - 3, 0 );
//             g.drawLine( 1, 0, 1, height - 3 );
//           }

//           g.drawLine( 1, height - 1, width - 1, height - 1 );
//           g.drawLine( width - 1, 0, width - 1, height - 1 );
          
//           g.setColor( shadowColor );
//           g.drawLine( 0, 0, 0, height - 2 );
//           g.drawLine( width - 2, 0, width - 2, height - 2 );
//           g.drawLine( 2, height - 2, width - 2, height - 2 );
        } else {
          drawDisabledBorder(g, 0,-1, width, height+1);
        }
         
        if ( !isFreeStanding ) {
          height -= 1;
          if ( !leftToRight ) {
            width -= 1;
            g.translate( 1, 0 );
          } else {
            width -= 2;
          }
        }
      } else if ( getDirection() == EAST ) {
        if ( !isFreeStanding ) {
          height += 2;
          width += 1;
        }
         
        // Draw the arrow
        g.setColor( arrowColor );
        
        int startX = (((w+1) - arrowHeight) / 2) + arrowHeight-1;
        int startY = (h / 2);
        
        //System.out.println( "startX2 :" + startX + " startY2 :"+startY);
        
        for (int line = 0; line < arrowHeight; line++) {
          g.drawLine( startX-line, startY-line, startX -line, startY+line+1);
        }

        if (isEnabled) {
          g.setColor(border_color);
          g.drawRect(0, 0, width-1, height-1);
//           g.setColor( highlightColor );
          
//           if ( !isPressed ) {
//             g.drawLine( 0, 1, width - 3, 1 );
//             g.drawLine( 0, 1, 0, height - 3 );
//           }
          
//           g.drawLine( width - 1, 1, width - 1, height - 1 );
//           g.drawLine( 0, height - 1, width - 1, height - 1 );
          
//           g.setColor( shadowColor );
//           g.drawLine( 0, 0,width - 2, 0 );
//           g.drawLine( width - 2, 2, width - 2, height - 2 );
//           g.drawLine( 0, height - 2, width - 2, height - 2 );
        } else {
          drawDisabledBorder(g,-1,0, width+1, height);
        }
        if ( !isFreeStanding ) {
          height -= 2;
          width -= 1;
        }
      } else if ( getDirection() == WEST ) {
        if ( !isFreeStanding ) {
          height += 2;
          width += 1;
          g.translate( -1, 0 );
        }
        
        // Draw the arrow
        g.setColor( arrowColor );
        
        int startX = (((w+1) - arrowHeight) / 2);
        int startY = (h / 2);

        for (int line = 0; line < arrowHeight; line++) {
          g.drawLine( startX+line, startY-line, startX +line, startY+line+1);
        }
        
        if (isEnabled) {
          g.setColor(border_color);
          g.drawRect(0, 0, width-1, height-1);
//           g.setColor( highlightColor );
          
//           if ( !isPressed ) {
//             g.drawLine( 1, 1, width - 1, 1 );
//             g.drawLine( 1, 1, 1, height - 3 );
//           }
          
//           g.drawLine( 1, height - 1, width - 1, height - 1 );
          
//           g.setColor( shadowColor );
//           g.drawLine( 0, 0, width - 1, 0 );
//           g.drawLine( 0, 0, 0, height - 2 );
//           g.drawLine( 2, height - 2, width - 1, height - 2 );
        } else {
          drawDisabledBorder(g,0,0, width+1, height);
        }
        
        if ( !isFreeStanding ) {
          height -= 2;
          width -= 1;
          g.translate( 1, 0 );
        }
      }
    }    
    
    protected void drawDisabledBorder(Graphics g, int x, int y, int width, int height) {
      g.drawRect(x, y, width, height);
    }

    private boolean     isFreeStanding;
    private Color       border_color = UIManager.getColor("ScrollBar.highlight");
    private Color       thumbColor1 = UIManager.getColor("ScrollBar.thumb");
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 7155986697007639719L;
  }
  

  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    Graphics2D          g2d = (Graphics2D) g;
    GradientPaint       gp;

    g.setColor(highlightColor);
    g.drawRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    g.setColor(trackColor);

    if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
      gp = new GradientPaint(trackBounds.x,
                             trackBounds.y,
                             trackColor,
                             trackBounds.x + trackBounds.width,
                             trackBounds.y, // + trackBounds.height,
                             Color.white);
    } else {
      gp = new GradientPaint(trackBounds.x,
                             trackBounds.y,
                             trackColor,
                             trackBounds.x,
                             trackBounds.y + trackBounds.height,
                             Color.white);
    }    
    g2d.setPaint(gp);
    g.fillRect(trackBounds.x+1, trackBounds.y, trackBounds.width-2, trackBounds.height);
  }

  protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds) {
    if (!c.isEnabled()) {
      return;
    }

    boolean             leftToRight = c.getComponentOrientation().isLeftToRight();
    
    g.translate( thumbBounds.x, thumbBounds.y );
    if ( scrollbar.getOrientation() == Adjustable.VERTICAL ) {
      if ( !isFreeStanding ) {
        if ( !leftToRight ) {
          thumbBounds.width += 1;
          g.translate( -1, 0 );
        } else {
          thumbBounds.width += 2;
        }
      }

      g.setColor( thumbColor );
      g.fillRect( 1, 0, thumbBounds.width - 2, thumbBounds.height);

      int         middle = thumbBounds.height / 2;

      g.setColor(highlightColor);
      g.drawLine(2, middle-3, thumbBounds.width-5, middle-3);
      g.drawLine(2, middle, thumbBounds.width-5, middle);
      g.drawLine(2, middle+3, thumbBounds.width-5, middle+3);
//       g.setColor(thumbShadow);
//       g.drawLine(3, middle-2, thumbBounds.width-5, middle-2);
//       g.drawLine(3, middle+1, thumbBounds.width-5, middle+1);
//       g.drawLine(3, middle+4, thumbBounds.width-5, middle+4);

      
//       g.setColor( thumbShadow );
//       g.drawRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );
      
      if ( !isFreeStanding ) {
        if ( !leftToRight ) {
          thumbBounds.width -= 1;
          g.translate( 1, 0 );
        } else {
          thumbBounds.width -= 2;
        }
      }
    } else { // HORIZONTAL
      
      if ( !isFreeStanding ) {
        thumbBounds.height += 2;
      }

      g.setColor( thumbColor );
      g.fillRect( 1, 0, thumbBounds.width - 2, thumbBounds.height);

      int         middle = thumbBounds.width / 2;

      g.setColor(highlightColor);
      g.drawLine(middle-3, 3, middle-3, thumbBounds.height-5);
      g.drawLine(middle,   3, middle,   thumbBounds.height-5);
      g.drawLine(middle+3, 3, middle+3, thumbBounds.height-5);
//       g.setColor( thumbColor );
//       g.fillRect( 0, 0, thumbBounds.width - 1, thumbBounds.height - 2 );

//       g.setColor( thumbShadow );
//       g.drawRect( 0, 0, thumbBounds.width - 1, thumbBounds.height - 2 );
      
      if ( !isFreeStanding ) {
        thumbBounds.height -= 2;
      }
    }
    
    g.translate( -thumbBounds.x, -thumbBounds.y );
  }

  protected void configureScrollBarColors() {
    super.configureScrollBarColors();
    
    highlightColor      = UIManager.getColor("ScrollBar.highlight");
    thumbColor          = UIManager.getColor("ScrollBar.thumb");
   
  }
 
  /**
   * WORK AROUND: 20021126
   * 
   * In a multiblock, the mouse released event of the scrollbar could be 
   * (depends on timing) consumed by the modal error message dialog.
   * Therefore the scrollbar send endless adjustment events (which
   * cause also error messages).
   */
  public void stopIt() {
    isDragging = false;
    scrollTimer.stop();
    scrollbar.setValueIsAdjusting(false);
  }

  protected ScrollListener createScrollListener(){
    return new KopiScrollListener();
  }
  protected TrackListener createTrackListener(){
    return new KopiTrackListener();
  }

  protected BasicScrollBarUI.ArrowButtonListener createArrowButtonListener(){
    return new ArrowButtonListener();
  }

  protected class KopiTrackListener extends TrackListener
  {
    public void mousePressed(MouseEvent e) {
      final Window  window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();

      window.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            /*
             * WORK AROUND: 20021126
             *
             * In a multiblock, the mouse released event of the scrollbar could be
             * (depends on timing) consumed by the modal error message dialog.
             * Therefore the scrollbar send endless adjustment events (which
             * cause also error messages).
             */
            stopIt();
          }
        });
      super.mousePressed(e);
    }
  }
  protected class ArrowButtonListener extends BasicScrollBarUI.ArrowButtonListener {
    public void mousePressed(MouseEvent e) {
      final Window  window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();

      window.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            /*
             * WORK AROUND: 20021126
             *
             * In a multiblock, the mouse released event of the scrollbar could be
             * (depends on timing) consumed by the modal error message dialog.
             * Therefore the scrollbar send endless adjustment events (which
             * cause also error messages).
             */
            stopIt();
          }
        });
      super.mousePressed(e);
    }
  }

  protected class KopiScrollListener extends ScrollListener {

    public KopiScrollListener() {
    }

    public KopiScrollListener(int dir, boolean block)	{
      super(dir, block);
    }
					
    public void actionPerformed(ActionEvent e) {
      if (!isChildOf(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow())) {
        /*
         * WORK AROUND: 20021126
         * 
         * In a multiblock, the mouse released event of the scrollbar could be 
         * (depends on timing) consumed by the modal error message dialog.
         * Therefore the scrollbar send endless adjustment events (which
         * cause also error messages).
         */
        stopIt();
      } else {
        super.actionPerformed(e);
      }
    }
      
    private boolean isChildOf(Container con) {
      if (con == null) {
        return false;
      }

      Component       parent = scrollbar;
        
      while (parent != null) {
        if (parent == con) {
          return true;
        }
        parent = parent.getParent();
      }
      return false;
    }
  }
  private static Color highlightColor;
  private static Color thumbColor; 
}
