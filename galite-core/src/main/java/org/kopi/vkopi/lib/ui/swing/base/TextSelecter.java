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
 * $Id: TextSelecter.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.text.JTextComponent;

public class TextSelecter extends FocusAdapter {

  public void focusGained(FocusEvent e) {
    // Select all, but put caret at first position
    selectText((JTextComponent) e.getComponent());
  }  

  public void selectText(JTextComponent txt) {
    // Select all, but put caret at first position
    txt.getCaret().setDot(txt.getText().length());
    txt.getCaret().moveDot(0);
  }

  public static final  TextSelecter   TEXT_SELECTOR = new TextSelecter(); 
}


