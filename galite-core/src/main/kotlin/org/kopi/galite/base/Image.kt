/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.base

import java.io.Serializable

/**
 * The interface class <code>Image</code> is the superInterface of all
 * classes that represent graphical images. The image must be
 * obtained in a platform-specific manner.
 */
interface Image : Serializable {
  /**
   * Returns the <code>Image</code> width
   * @return The image width
   */
  val width: Int

  /**
   * Returns the <code>Image</code> height
   * @return The image height
   */
  val height: Int

  /**
   * Returns the <code>Image</code> description
   * @return The image description
   */
  val description: String

  /**
   * Creates a scaled version of this image.
   * A new <code>Image</code> object is returned which will render
   * the image at the specified <code>width</code> and
   * <code>height</code> by default.
   *
   * @param width the width to which to scale the image.
   * @param height the height to which to scale the image.
   * @param hints flags to indicate the type of algorithm to use
   * for image resampling.
   * @return a scaled version of the image.
   * @exception IllegalArgumentException if <code>width</code> or <code>height</code> is zero.
   */
  fun getScaledInstance(width: Int, height: Int, hints: Int): Image

  companion object {
    //-----------------------------------------------------
    // SCALING CONSTANTS
    //-----------------------------------------------------
    val SCALE_DEFAULT = 1
    val SCALE_FAST = 2
    val SCALE_SMOOTH = 4
    val SCALE_REPLICATE = 8
    val SCALE_AREA_AVERAGING = 16
  }
}