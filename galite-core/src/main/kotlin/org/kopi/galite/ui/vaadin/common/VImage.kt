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
 * $Id: VImage.java 34961 2016-11-04 17:20:49Z hacheni $
 */
package org.kopi.galite.ui.vaadin.common

import com.vaadin.flow.component.html.Image

/**
 * A widget that wraps image element.
 */
class VImage : Image() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the image border.
   * @param width The image border.
   */
  fun setBorder(border: Int) {
    element.setProperty("border", border.toString())
  }

  /**
   * Returns `true` if no image is displayed.
   * @return `true` if no image is displayed.
   */
  val isEmpty: Boolean
    get() = src == null || "" == src

}
