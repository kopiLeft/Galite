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

package org.kopi.galite.visual

import java.io.Serializable

/**
 * The `VColor` class is used to encapsulate colors in the default
 * sRGB color space.  Every color has an implicit alpha value of 1.0 or
 * an explicit one provided in the constructor.  The alpha value
 * defines the transparency of a color and can be represented by
 * a float value in the range 0.0&nbsp;-&nbsp;1.0 or 0&nbsp;-&nbsp;255.
 * An alpha value of 1.0 or 255 means that the color is completely
 * opaque and an alpha value of 0 or 0.0 means that the color is
 * completely transparent.
 */
class VColor : Serializable {
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  /**
   * Creates an sRGB color with the specified red, green, blue, and alpha
   * values in the range (0 - 255).
   * @param r the red component
   * @param g the green component
   * @param b the blue component
   * @param a the alpha component, default to 255
   * @throws IllegalArgumentException if `r`, `g`,
   * `b` or `a` are outside of the range
   * 0 to 255, inclusive
   */
  constructor(r: Int, g: Int, b: Int, a: Int = 255) {
    rGB = a and 0xFF shl 24 or (r and 0xFF shl 16) or (g and 0xFF shl 8) or (b and 0xFF shl 0)
    testColorValueRange(r, g, b, a)
  }

  /**
   * Creates an opaque sRGB color with the specified combined RGB value
   * consisting of the red component in bits 16-23, the green component
   * in bits 8-15, and the blue component in bits 0-7.  The actual color
   * used in rendering depends on finding the best match given the
   * color space available for a particular output device.  Alpha is
   * defaulted to 255.
   *
   * @param rgb the combined RGB components
   */
  constructor(rgb: Int) {
    rGB = -0x1000000 or rgb
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Creates a new `Color` that is a brighter version of this
   * `VColor`.
   */
  fun brighter(): VColor {
    var r = red
    var g = green
    var b = blue
    val alpha = alpha
    val i = (1.0 / (1.0 - FACTOR)).toInt()
    if (r == 0 && g == 0 && b == 0) {
      return VColor(i, i, i, alpha)
    }
    if (r in 1 until i) {
      r = i
    }
    if (g in 1 until i) {
      g = i
    }
    if (b in 1 until i) {
      b = i
    }

    val vColorRed = (r / FACTOR).toInt().coerceAtMost(255)
    val vColorGreen = (g / FACTOR).toInt().coerceAtMost(255)
    val vColorBlue = (b / FACTOR).toInt().coerceAtMost(255)
    return VColor(vColorRed, vColorGreen, vColorBlue, alpha)
  }

  /**
   * Creates a new `Color` that is a darker version of this
   * `Color`.
   */
  fun darker(): VColor {
    val vColorRed = (red * FACTOR).toInt().coerceAtLeast(0)
    val vColorGreen = (green * FACTOR).toInt().coerceAtLeast(0)
    val vColorBlue = (blue * FACTOR).toInt().coerceAtLeast(0)
    return VColor(vColorRed, vColorGreen, vColorBlue, alpha)
  }

  override fun hashCode(): Int = rGB

  override fun equals(other: Any?): Boolean =
          other is VColor && other.rGB == rGB

  override fun toString(): String =
          javaClass.name + "[r=" + red + ",g=" + green + ",b=" + blue + "]"

  /**
   * Returns the red component in the range 0-255 in the default sRGB space.
   * @return the red component.
   */
  val red: Int
    get() = rGB shr 16 and 0xFF

  /**
   * Returns the green component in the range 0-255 in the default sRGB space.
   * @return the green component.
   */
  val green: Int
    get() = rGB shr 8 and 0xFF

  /**
   * Returns the blue component in the range 0-255 in the default sRGB space.
   * @return the blue component.
   */
  val blue: Int
    get() = rGB shr 0 and 0xFF

  /**
   * Returns the alpha component in the range 0-255.
   * @return the alpha component.
   */
  val alpha: Int
    get() = rGB shr 24 and 0xff

  /**
   * The RGB value of the color in the default sRGB space.
   * (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are
   * blue).
   */
  val rGB: Int

  companion object {
    //---------------------------------------------------
    // UTILS
    //---------------------------------------------------
    /**
     * Checks the color integer components supplied for validity.
     * Throws an [IllegalArgumentException] if the value is out of
     * range.
     * @param r the Red component
     * @param g the Green component
     * @param b the Blue component
     */
    private fun testColorValueRange(r: Int, g: Int, b: Int, a: Int) {
      var rangeError = false
      var badComponentString = ""
      if (a < 0 || a > 255) {
        rangeError = true
        badComponentString = "$badComponentString Alpha"
      }
      if (r < 0 || r > 255) {
        rangeError = true
        badComponentString = "$badComponentString Red"
      }
      if (g < 0 || g > 255) {
        rangeError = true
        badComponentString = "$badComponentString Green"
      }
      if (b < 0 || b > 255) {
        rangeError = true
        badComponentString = "$badComponentString Blue"
      }
      require(!rangeError) { "Color parameter outside of expected range:$badComponentString" }
    }

    // color constants
    private const val FACTOR = 0.7

    /**
     * The color white. In the default sRGB space.
     */
    val WHITE = VColor(255, 255, 255)

    /**
     * The VColor light gray. In the default sRGB space.
     */
    val LIGHT_GRAY = VColor(192, 192, 192)

    /**
     * The VColor gray. In the default sRGB space.
     */
    val GRAY = VColor(128, 128, 128)

    /**
     * The VColor dark gray. In the default sRGB space.
     */
    val DARK_GRAY = VColor(64, 64, 64)

    /**
     * The VColor black. In the default sRGB space.
     */
    val BLACK = VColor(0, 0, 0)

    /**
     * The VColor red. In the default sRGB space.
     */
    val RED = VColor(255, 0, 0)

    /**
     * The VColor pink. In the default sRGB space.
     */
    val PINK = VColor(255, 175, 175)

    /**
     * The VColor orange. In the default sRGB space.
     */
    val ORANGE = VColor(255, 200, 0)

    /**
     * The VColor yellow. In the default sRGB space.
     */
    val YELLOW = VColor(255, 255, 0)

    /**
     * The VColor green. In the default sRGB space.
     */
    val GREEN = VColor(0, 255, 0)

    /**
     * The VColor magenta. In the default sRGB space.
     */
    val MAGENTA = VColor(255, 0, 255)

    /**
     * The VColor cyan.  In the default sRGB space.
     */
    val CYAN = VColor(0, 255, 255)

    /**
     * The VColor blue.  In the default sRGB space.
     */
    val BLUE = VColor(0, 0, 255)
  }
}
