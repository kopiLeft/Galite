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
 * Represents the set of colors to use with measures in charts.
 */
enum class Color(val red: Int, green: Int, blue: Int) {
  WHITE(255, 255, 255),
  LIGHT_GRAY(192, 192, 192),
  GRAY(128, 128, 128),
  DARK_GRAY(64, 64, 64),
  BLACK(0, 0, 0),
  RED(255, 0, 0),
  PINK(255, 175, 175),
  ORANGE(255, 200, 0),
  YELLOW(255, 255, 0),
  GREEN(0, 255, 0),
  MAGENTA(255, 0, 255),
  CYAN(0, 255, 255),
  BLUE(0, 0, 255)
}
