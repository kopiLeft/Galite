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
 * $Id: Stateful.java 35256 2017-10-17 11:26:13Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.Color;

public interface Stateful {
  int getState();
  boolean getAutofill();
  boolean isAlert();
  boolean hasCriticalValue();
  boolean hasAction();
  Color getBgColor();
  Object getModel();
}
