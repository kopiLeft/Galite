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
 * $Id: KopiFieldLabelUI.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import org.kopi.vkopi.lib.ui.swing.base.FieldStates;
import org.kopi.vkopi.lib.ui.swing.base.JFieldLabel;

public class KopiFieldLabelUI extends KopiLabelUI {
  protected static KopiFieldLabelUI kopiFieldLabelUI = new KopiFieldLabelUI();

  public static ComponentUI createUI(JComponent x ) {
    return kopiFieldLabelUI;
  }
  
  protected void paintEnabledText(JLabel l, 
                                  Graphics g, 
                                  String s, 
                                  int textX, 
                                  int textY) 
  {
    if (l instanceof JFieldLabel) {
      JFieldLabel       fl = (JFieldLabel) l;
      int               mnemIndex = l.getDisplayedMnemonicIndex();

      if ((fl.getState() & FieldStates.FOCUSED) != 0) {
        g.setColor(color_focused);
      } else {
        switch (fl.getState() & FieldStates.FLD_MASK) {
        case FieldStates.SKIPPED:
          g.setColor(color_skipped);
          break;
        case FieldStates.MUSTFILL:
          g.setColor(color_mustfill);
          break;
        case FieldStates.VISIT:
          g.setColor(color_visit);
          break;
        default:
          g.setColor(Color.BLACK);
        }
      }
      BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemIndex, 0, textY+2);
      // info text e.g. the search operator
      if (fl.getInfoText() != null) {
        g.setFont(font);

        FontMetrics       fm = g.getFontMetrics();
        int               width = (int) fm.getStringBounds(fl.getInfoText(), g).getWidth();      
        int               height = (int) fm.getStringBounds(fl.getInfoText(), g).getHeight();

        g.setColor(color_info);
        g.drawString(fl.getInfoText(), l.getWidth() - width - 1,  height - 3);
        //      BasicGraphicsUtils.drawStringUnderlineCharAt(g, fl.getInfoText(), mnemIndex, 0, textY+2);
      }
    } else {
      super.paintEnabledText(l, g, s, 0, textY+2);
    }
  }

  public void paint(Graphics g, 
                    JComponent c) {
    super.paint(g, c);

 

    if (c instanceof JFieldLabel) {
      JFieldLabel               fl = (JFieldLabel) c;
      Rectangle                 clipRect = new Rectangle(0, 0, c.getWidth(), c.getHeight());  

      if (clipRect.height-underline_width <= 0 || fl.getText() == null || fl.getText().length() == 0) {
        return;
      }

      if ((fl.getState() & FieldStates.CHART) == 0 || fl.isInDetailMode()) {
        switch (fl.getState() & FieldStates.FLD_MASK) {
        case FieldStates.SKIPPED:
          g.setColor(color_underline_skipped);
          break;
        case FieldStates.MUSTFILL:
          g.setColor(color_underline_mustfill);
          break;
        case FieldStates.VISIT:
          g.setColor(color_underline_visit);
          break;
        default:
          g.setColor(color_underline);
        }
        g.fillRect(clipRect.x, clipRect.y, clipRect.width, underline_width);
      }
    } 
  }

  protected static final Color       color_focused;
  protected static final Color       color_skipped;
  protected static final Color       color_mustfill;
  protected static final Color       color_visit;

  protected static final Color       color_underline;
  protected static final Color       color_underline_mustfill;
  protected static final Color       color_underline_visit;
  protected static final Color       color_underline_skipped;

  protected static final Color       color_info;

  protected static final int         underline_width;
  private static final Font          font;

  static {
    color_focused   = UIManager.getColor("KopiLabel.focused.color");
    color_skipped   = UIManager.getColor("KopiLabel.skipped.color");
    color_mustfill  = UIManager.getColor("KopiLabel.mustfill.color");
    color_visit     = UIManager.getColor("KopiLabel.visit.color");

    color_underline             = UIManager.getColor("KopiLabel.ul.color");
    color_underline_visit       = UIManager.getColor("KopiLabel.ul.visit.color");
    color_underline_skipped     = UIManager.getColor("KopiLabel.ul.skipped.color");
    color_underline_mustfill    = UIManager.getColor("KopiLabel.ul.mustfill.color");

    color_info          = UIManager.getColor("KopiLabel.info.color");
    font                = UIManager.getFont("KopiLabel.info.font");

    underline_width = UIManager.getInt("KopiLabel.ul.width");
  }
}
