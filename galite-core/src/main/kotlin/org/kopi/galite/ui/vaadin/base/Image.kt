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
 * $Id: Image.java 34961 2016-11-04 17:20:49Z hacheni $
 */
package org.kopi.galite.ui.vaadin.base

import org.kopi.galite.base.Image

/**
 * The vaadin implementation of an image model.
 */
class Image : Image {
  //---------------------------------------------------
  // IMAGE IMPLEMENTATION
  //---------------------------------------------------

  override fun getWidth(): Int {
    return -1
  }

  override fun getHeight(): Int {
    return -1
  }

  override fun getDescription(): String {
    TODO()
  }

  override fun getScaledInstance(width: Int,
                                 height: Int,
                                 hints: Int): Image {
    // FIXME: return the scaled image from theme images
    return this
  }
}
