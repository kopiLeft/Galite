/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.common

/**
 * Represents the set of default colors to use with measures in charts.
 */
enum class Color(val red: Int, green: Int, blue: Int) {
  /**
   * The white color in the RGB space.
   */
  WHITE(255, 255, 255),

  /**
   * The light gray color in the RGB space.
   */
  LIGHT_GRAY(192, 192, 192),

  /**
   * The gray color in the RGB space.
   */
  GRAY(128, 128, 128),

  /**
   * The dark gray color in the RGB space.
   */
  DARK_GRAY(64, 64, 64),

  /**
   * The black color in the RGB space.
   */
  BLACK(0, 0, 0),

  /**
   * The red color in the RGB space.
   */
  RED(255, 0, 0),

  /**
   * The pink color in the RGB space.
   */
  PINK(255, 175, 175),

  /**
   * The orange color in the RGB space.
   */
  ORANGE(255, 200, 0),

  /**
   * The yellow color in the RGB space.
   */
  YELLOW(255, 255, 0),

  /**
   * The green color in the RGB space.
   */
  GREEN(0, 255, 0),

  /**
   * The magenta color in the RGB space.
   */
  MAGENTA(255, 0, 255),

  /**
   * The cyan color in the RGB space.
   */
  CYAN(0, 255, 255),

  /**
   * The blue color in the RGB space.
   */
  BLUE(0, 0, 255)
}
