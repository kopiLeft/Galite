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
 * $Id: KopiPasswordFieldUI.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;

public class KopiPasswordFieldUI extends BasicPasswordFieldUI {

  public static ComponentUI createUI(JComponent c) {
    return new KopiPasswordFieldUI();
  }


  public void installUI(JComponent c) {
    super.installUI(c);

    c.setBorder(new KopiUtils.KopiFieldBorder((JTextComponent) c));
  }

  /**
   * Paints a background for the view.  This will only be
   * called if isOpaque() on the associated component is
   * true.  The default is to paint the background color 
   * of the component.
   *
   * @param g the graphics context
   */
  protected void paintBackground(Graphics g) {
    JTextComponent c = (JTextComponent) getComponent();

    KopiUtils.drawBackground(g, c, new Rectangle(0, 0, c.getWidth(), c.getHeight()));
  }

}
