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
 * $Id: JActorFieldButton.java 35283 2018-01-05 09:00:51Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolTip;

public class JActorFieldButton extends JButton {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public JActorFieldButton(Action action) {
    super(action);
    setMargin(inset);
    setVerticalTextPosition(BOTTOM);
    setHorizontalTextPosition(CENTER);
    setRolloverEnabled(true);
    setPreferredSize(getStaticSize(true));
    setSize(getPreferredSize());
    setFocusable(false);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  public JToolTip createToolTip() {
    MultiLineToolTip tip = new MultiLineToolTip();
    tip.setComponent(this);
    
    return tip;
  }

  public Dimension getPreferredSize() {
    return getStaticSize(getText() != null);
  }
  
  public Dimension getMinimumSize() {
    return getStaticSize(getText() != null);
  }
  
  public Dimension getMaxmumSize() {
    return getStaticSize(getText() != null);
  }

  public void setEnabled(boolean enabled) {
    if (!enabled) {
      getModel().setRollover(false);
    }
    
    super.setEnabled(enabled);
  }

  /**
   * Returns the static size of the button.
   * @param showtext should we show the button text ?
   * @return The static button size
   */
  public static Dimension getStaticSize(boolean showtext) {
    return showtext ? dimension : dimensionSmall;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private static Dimension      dimension = new Dimension(140, 20);
  private static Dimension      dimensionSmall = new Dimension(60, 20);
  private static Insets         inset = new Insets(1, 1, 1, 1);
  private static final long     serialVersionUID = -2120770411233444725L;
}
