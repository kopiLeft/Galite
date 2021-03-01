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
package org.kopi.galite.ui.vaadin.main

import org.kopi.galite.ui.vaadin.base.Styles

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.componentfactory.theme.EnhancedDialogVariant
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * The already opened windows menu.
 * The menu aims to show the opened windows by the user.
 * From this menu, the user can switch to another window.
 */
class VWindowsMenu : EnhancedDialog(), HasStyle {

  init {
    className = Styles.MAIN_WINDOW
    val headerIcon = Icon(VaadinIcon.COPY_O)
    val headerText = Label("Changer de fenêtre")
    val header = HorizontalLayout()

    header.add(headerText, headerIcon)
    header.alignItems = FlexComponent.Alignment.END
    this.setHeader(header)
    this.setThemeVariants(EnhancedDialogVariant.SIZE_SMALL)
  }

  /**
   * Adds a window like an [VWindowsMenuItem] into the [VWindowsMenu].
   * @param container The container of the window.
   * @param window The window to be added.
   * @param title The window title.
   */
  fun addWindow(container : VWindowContainer, window : Component, title : String) {
    val item = VWindowsMenuItem(title, window, container)

    item.addClickListener { this.close() }
    items.add(item)
    this.setContent(items)
  }

  private var items = VerticalLayout()
}
