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
 * $Id: JButtonPanel.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class JButtonPanel extends JPanel  {
  
public JButtonPanel() {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    //    setBorder(new LineBorder(borderColor));
    setBackground(backColor);
    setOpaque(true);
    setBorder(new PanelBorder(borderColor));
    setAlignmentY(0);
  }

  static class PanelBorder extends LineBorder {
	    
	public PanelBorder(Color color)  {
      super(color, 1, true);
    }    

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      
      g.setColor(borderColor);
      g.drawLine(x, y, width, y);
      g.drawLine(x, height-1, width, height-1);
      g.setColor(oldColor);
    }
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -5268179554807018796L;
  }

  private static final Color    borderColor = UIManager.getColor("ButtonPanel.border");
  private static final Color    backColor = UIManager.getColor("ButtonPanel.back");
  /**
	 * Comment for <code>serialVersionUID</code>
  */
  private static final long serialVersionUID = -7046386378185269176L;
}
