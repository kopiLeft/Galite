/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.galite.visual.ui.vaadin.base

import org.kopi.galite.visual.base.Image
import org.kopi.galite.visual.ui.vaadin.common.VImage

/**
 * The vaadin implementation of an image model.
 *
 * @param resource The resource file attached to this image.
 */
class Image(val resource: String = "image", val source: ByteArray? = null) : VImage(), Image {
  //---------------------------------------------------
  // IMAGE IMPLEMENTATION
  //---------------------------------------------------

  override fun getImageWidth(): Int {
    return -1
  }

  override fun getImageHeight(): Int {
    return -1
  }

  override fun getDescription(): String {
    //TODO()
    return "TODO"
  }

  override fun getScaledInstance(width: Int,
                                 height: Int,
                                 hints: Int): Image {
    // FIXME: return the scaled image from theme images
    return this
  }
}
