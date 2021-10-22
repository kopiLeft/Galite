/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.util

import java.awt.Dimension
import java.awt.Image
import java.awt.image.ColorModel
import java.awt.image.ImageConsumer
import java.util.Hashtable

import kotlin.math.abs

class PixelConsumer(picture: Image) : ImageConsumer {

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  /** the dimension of the image */
  var dimensions: Dimension? = null
    private set
  var isComplete = false
    private set
  private lateinit var pixelTable: Array<IntArray>
  private lateinit var transparent: Array<BooleanArray>

  /**
   * Constructs a new pixel consumer
   */
  init {
    picture.source.startProduction(this)
    var t = 50000
    while (t > 0 && !isComplete) {
      try {
        Thread.sleep(10)
      } catch (ignored: Throwable) {
      }
      t -= 10
    }
  }

  override fun imageComplete(status: Int) {
    // bug - STATICIMAGEDONE is sent twice before and a break at this
    // time leads to incomplete images.
    isComplete = status == ImageConsumer.IMAGEERROR
  }

  override fun setColorModel(ignored: ColorModel) {
    // we currently ignore the color model
  }

  override fun setDimensions(x: Int, y: Int) {
    dimensions = Dimension(y, x)
    pixelTable = Array(x) { IntArray(y) }
    transparent = Array(x) { BooleanArray(y) }
  }

  override fun setHints(ignored: Int) {
    // we currently ignore any hints
  }

  override fun setPixels(
    x1: Int,
    y1: Int,
    w: Int,
    h: Int,
    model: ColorModel,
    pixels: ByteArray,
    off: Int,
    scansize: Int
  ) {
    val x2 = x1 + w
    val y2 = y1 + h
    var sy = off
    for (y in y1 until y2) {
      var sx = sy
      for (x in x1 until x2) {
        transparent[x][y] = model.getAlpha(abs(pixels[sx].toInt())) > 0
        pixelTable[x][y] = model.getRGB(abs(pixels[sx++].toInt()))
      }
      sy += scansize
    }
  }

  override fun setPixels(
    x1: Int,
    y1: Int,
    w: Int,
    h: Int,
    model: ColorModel,
    pixels: IntArray,
    off: Int,
    scansize: Int
  ) {
    val x2: Int = x1 + w
    val y2: Int = y1 + h
    var sy: Int = off
    for (y in y1 until y2) {
      var sx = sy
      for (x in x1 until x2) {
        transparent[x][y] = false
        pixelTable[x][y] = model.getRGB(pixels[sx++])
      }
      sy += scansize
    }
  }

  override fun setProperties(ignored: Hashtable<*, *>) {
    // we currently ignore any properties
  }

  fun isTransparent(x: Int, y: Int): Boolean {
    return transparent[x][y]
  }

  fun getPixelAt(x: Int, y: Int): Int {
    return pixelTable[x][y]
  }
}
