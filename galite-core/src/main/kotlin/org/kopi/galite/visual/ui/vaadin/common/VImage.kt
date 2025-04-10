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
package org.kopi.galite.visual.ui.vaadin.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.html.Image

/**
 * A widget that wraps image element.
 */
open class VImage : Image(), Focusable<VImage> {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the image border.
   * @param border The image border.
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

  /**
   * Creates the dynamic image name.
   * @param baseName The base name.
   * @return The dynamic image name.
   */
  fun createFileName(baseName: String): String =
    baseName + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + ".png"
}

