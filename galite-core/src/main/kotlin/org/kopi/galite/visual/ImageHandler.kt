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

package org.kopi.galite.visual

import org.kopi.galite.visual.base.Image

abstract class ImageHandler {
  //------------------------------------------------------------------
  // ABSTRACT METHODS
  //------------------------------------------------------------------
  /**
   * Returns the [Image] having the `image` name.
   * @param image The image name.
   * @return The [Image] having the `image` name.
   */
  abstract fun getImage(image: String): Image?

  /**
   * Returns the [Image] having the `image` content.
   * @param image The image content.
   * @return The [Image] having the `image` content.
   */
  abstract fun getImage(image: ByteArray): Image?

  /**
   * Returns the URL of a given image name.
   * @param image The image name.
   * @return The URL of the image.
   */
  abstract fun getURL(image: String): String

  companion object {
    lateinit var imageHandler: ImageHandler
  }
}
