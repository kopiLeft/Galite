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
package org.kopi.galite.ui.vaadin.common

import org.kopi.galite.ui.vaadin.base.ResourcesUtil
import org.kopi.galite.ui.vaadin.base.VAnchorPanel

import com.vaadin.flow.component.html.Image

/**
 * A logo component.
 */
class VLogo : VAnchorPanel() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the logo image.
   * @param url The image URL
   */
  fun setImage(url: String, alt: String?) {
    image.src = url
    if (alt != null) {
      image.setAlt(alt)
    }
    image.element.setProperty("border", 0.0)
    image.className = "logo-image"
    image.addClassName(ResourcesUtil.getResourceName(url))
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val image = Image()

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates the logo component instance.
   */
  init {
    add(image)
  }
}
