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
 * $Id: MultiLineToolTipUI.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

/**
 *
 */
public class MultiLineToolTipUI extends MetalToolTipUI {

  @SuppressWarnings("deprecation")
  public void paint(Graphics g, JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
    Dimension size = c.getSize();
    g.setColor(c.getBackground());
    g.fillRect(0, 0, size.width, size.height);
    g.setColor(c.getForeground());
    if (strs != null) {
      for (int i = 0; i < strs.length; i++) {
        g.drawString(strs[i], 3, (metrics.getHeight()) * (i+1));
      }
    }
  }

  @SuppressWarnings("deprecation")
  public Dimension getPreferredSize(JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());
    String tipText = ((JToolTip)c).getTipText();
    if (tipText == null) {
      tipText = "";
    }

    StringBuffer	tip = new StringBuffer();
    StringTokenizer	tok = new StringTokenizer(tipText);
    int			ps = 0;
    while (tok.hasMoreTokens()) {
      final String s = tok.nextToken();
      if (ps + s.length() > 66) {
	ps = 0;
	tip.append(s + (tok.hasMoreTokens() ? "\n" : ""));
      } else {
	tip.append(s + (tok.hasMoreTokens() ? " " : ""));
      }
      ps += s.length() + 1;
    }

    BufferedReader br = new BufferedReader(new StringReader(tip.toString()));
    String line;
    int maxWidth = 0;
    Vector<String> v = new Vector<String>();
    try {
      while ((line = br.readLine()) != null) {
        int width = SwingUtilities.computeStringWidth(metrics,line);
        maxWidth = (maxWidth < width) ? width : maxWidth;
        v.addElement(line);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    int lines = v.size();
    if (lines < 1) {
      strs = null;
      lines = 1;
    } else {
      strs = new String[lines];
      int i=0;
      for (Enumeration<String> e = v.elements(); e.hasMoreElements() ;i++) {
        strs[i] = e.nextElement();
      }
    }
    int height = metrics.getHeight() * lines;

    return new Dimension(maxWidth + 6, height + 4);
  }


  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String[] strs;

}
