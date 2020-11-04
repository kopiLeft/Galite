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
 * $Id: Utils.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JPopupMenu;

/**
 *
 */
public class Utils extends org.kopi.vkopi.lib.ui.swing.base.Utils {

  
  public static Component getRoot (Component c, Class<?> type) {
    Component   root = null;

    for(Component p = c; p != null;) {
      if (type.isAssignableFrom(p.getClass())) {
        root = p;
      }
      if (p instanceof Window) {
        return root;
      }
      if (p instanceof JPopupMenu) {
        p = ((JPopupMenu)p).getInvoker();
      } else {
        p = p.getParent();
      }
    }
    return root;
  }

  public static Window getWindowAncestor (Component c) {
    return (Window)getRoot (c, Window.class);
  }

  public static Frame getFrameAncestor (Component c) {
    return (Frame)getRoot (c, Frame.class);
  }

  public static Rectangle calculateBounds(Component component, Point position, Frame frame) {
    Dimension   componentSize;
    Dimension   screen;
    int         minX, minY, maxX, maxY;
    int         maxWidth, maxHeight;
    Insets      insets;
    Rectangle   location; //result

    location = new Rectangle();

    // not the whole screen is usable e.g. System-Toolbar
    insets = getScreenInsets();
    // component size
    componentSize = component.getPreferredSize();
    // screen size
    screen = Toolkit.getDefaultToolkit().getScreenSize();

    // minimum / maximum sizes
    minX = insets.left;
    minY = insets.top;
    maxX = screen.width - insets.right;
    maxY = screen.height - insets.bottom;
    maxWidth = screen.width - insets.right - insets.left;
    maxHeight = screen.height - insets.bottom - insets.top;

    if (position == null) {
      // no position provided => center it to active frame or screen
      Point     center;

      if (frame != null) {
        // active window
        Point   frameLocationOnScreen = frame.getLocationOnScreen();

        center = new Point(frameLocationOnScreen.x + frame.getSize().width /2,
                           frameLocationOnScreen.y + frame.getSize().height /2);
      } else {
        // screen
        GraphicsEnvironment       ge;

        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        center = ge.getCenterPoint();
      }

      // center component
      position = new Point();
      position.x = center.x - (componentSize.width / 2);
      position.y = center.y - (componentSize.height / 2);
    }


    // X X X X X X X X X X
    if (maxWidth <= componentSize.width) {
      // component is bigger than the available screen size
      location.x = minX;
    } else if (position.x < minX) {
      // the provide position is too small
      location.x = minX;
    } else if ((position.x + componentSize.width) <= maxX) {
      // the provide position is OK
      location.x = position.x;
    } else {
      // the provide position is too big
      location.x = maxX - componentSize.width;
    }

    // Y Y Y Y Y Y Y Y Y Y
    if (maxHeight <= componentSize.height) {
      // component is bigger than the available screen
      location.y = minY;
    } else if (position.y < minY) {
      // the provide position is too small
      location.y = minY;
    } else if ((position.y + componentSize.height) <= maxY) {
      // the provide position is OK
      location.y = position.y;
    } else {
      // the provide position is too big
      location.y = maxY - componentSize.height;
    }

    // witdh
    location.width = Math.min(maxWidth, componentSize.width);
    // height
    location.height = Math.min(maxHeight, componentSize.height);

    return location;
  }

  private static Insets getScreenInsets() {
    try {
      if (System.getProperty("os.name").startsWith("Linux")) {
        // Lackner 18.08.2003 jdk1.4.1
        // Under Linux you get no information about the taskbar
        // KDE reposition the panel itself, that it is ot over the taskbar
        // but ohter linux-window-manager?
        return SCN_INSESTS;
      } else {
        // under windows to get the information you need
        // (but this code does not work under Linux)
        Toolkit                   tk;
        GraphicsEnvironment       ge;
        GraphicsConfiguration     gc;
        Insets                    insets;

        tk = Toolkit.getDefaultToolkit();
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

        insets = tk.getScreenInsets(gc);

        return insets;
      }

    } catch (Exception e) {
      return SCN_INSESTS;
    }
  }

  private static final Insets   SCN_INSESTS = new Insets(22, 22, 22, 22);
  
}
