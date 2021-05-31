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

import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * The company logo component that contains an image.
 */
class VCompanyLogo : HorizontalLayout() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the href for the anchor element.
   * @param href the href
   */
  fun setHref(href: String?) {
    logo.href = href
  }

  /**
   * Sets the target frame.
   * @param target The target frame.
   */
  fun setTarget(target: String) {
    logo.setTarget(target)
  }

  /**
   * Sets the company logo image.
   * @param url The image URL.
   * @param alt The alternate text.
   */
  fun setImage(url: String, alt: String?) {
    logo.setImage(url, alt)
  }

  private val logo: VLogo

  init {
    setId("companyLogo")
    logo = VLogo()
    logo.element.setProperty("border", "0")
    add(logo)
  }
}
