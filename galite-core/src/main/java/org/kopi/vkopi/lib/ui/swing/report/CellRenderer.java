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
 * $Id: CellRenderer.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.report;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JComponent;

import org.kopi.galite.visual.report.Constants;

/**
 * @version 1.0 11/09/98
 */
public class CellRenderer extends JComponent {

  protected CellRenderer(int state) {
    this.state = state;
  }

  @SuppressWarnings("deprecation")
  protected CellRenderer(int state,
                         int align,
                         Color back,
                         Color fore,
                         Font font) {
    this.state = state;
    this.align = align;
    this.back = back;
    this.fore = fore;
    this.font = font;
    this.metrics = Toolkit.getDefaultToolkit().getFontMetrics(font); // $$$
  }

  public void paint(Graphics g) {
    Dimension   size = getSize();
    Color	bg;
    switch (state) {
      case Constants.STA_FOLDED:
        bg = Color.lightGray;
        break;
      case Constants.STA_SEPARATOR:
        bg = Color.red;
        break;
      default:
        bg = selected ? Color.black : this.back == Color.white ? level : this.back;
    }
    g.setColor(bg);
    g.fillRect(0, 0, size.width, size.height);

    switch (state) {
      case Constants.STA_FOLDED:
      case Constants.STA_SEPARATOR:
        break;
      default:
        if (state != Constants.STA_SEPARATOR && str != null) {
          Color		fg = selected ? level : this.fore;
          int		left;
          int		index = 0, oldIndex = 0;
          int		line = 0;

          g.setColor(fg);
          g.setFont(font);

          while ((index = str.indexOf('\n', oldIndex)) != -1) {
            if (align == Constants.ALG_RIGHT) {
              left = size.width - metrics.stringWidth(str.substring(oldIndex, index)) - 2;
              //} else if (align == Constants.ALG_CENTER){
              //left = (size.width - metrics.stringWidth(str)) / 2 - 2;
            } else {
              left = 2;
            }
            g.drawString(str.substring(oldIndex, index), left, (line++ + 1) * metrics.getHeight() - 2);
            oldIndex = index + 1;
          }
          if (align == Constants.ALG_RIGHT) {
            left = size.width - metrics.stringWidth(str.substring(oldIndex)) - 2; // $$$
            //} else if (align == Constants.ALG_CENTER) {
            //left = (size.width - metrics.stringWidth(str)) / 2 - 2;
          } else {
            left = 2;
          }
          g.drawString(str.substring(oldIndex), left, (line++ + 1) * metrics.getHeight() - 2);
        }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public int getState() {
    return state;
  }

  public void set(String value, boolean selected, Color color) {
    str = value;
    level = color;
    this.selected = selected;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	int		state;
  private	int		align;
  private	Color		back;
  private	Color		fore;
  private	Font		font;
  private	FontMetrics	metrics;
  private	Color		level;
  private	boolean		selected;
  private	String		str;

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long	serialVersionUID = 6813974260529631662L;
}
