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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.dependency.CssImport

/**
 * The window header component.
 */
@CssImport("./styles/galite/VHeader.css")
class VHeader : HorizontalLayout() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  /**
   * Sets the main menu component.
   *
   * @param moduleList The module list component.
   */
  fun setMainMenu(moduleList: Component) {
    addComponentAtIndex(1, moduleList)
  }

  /**
   * Sets the windows link component.
   *
   * @param windows The link component
   */
  fun setWindows(windows: Component) {
    add(windows)
  }

  /**
   * Sets the welcome component.
   *
   * @param welcome The welcome component.
   */
  fun setWelcome(welcome: Component) {
    addComponentAtIndex(1, welcome)
  }

  /**
   * Sets the href for the anchor element.
   *
   * @param href the href
   */
  fun setHref(href: String?) {
    logo.setHref(href)
  }

  /**
   * Sets the target frame.
   *
   * @param target The target frame.
   */
  fun setTarget(target: String) {
    logo.setTarget(target)
  }

  /**
   * Sets the company logo image.
   *
   * @param url The image URL.
   * @param alt The alternate text.
   */
  fun setImage(url: String, alt: String?) {
    logo.setImage(url, alt)
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val logo = VCompanyLogo()
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates the window header component.
   */
  init {
    setId("header")
    setWidthFull()
    add(logo)
  }
}
