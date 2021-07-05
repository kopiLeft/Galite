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
import com.vaadin.flow.component.dependency.CssImport
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
@CssImport("./styles/galite/list.css", themeFor = "vcf-enhanced-dialog-overlay")
class VWindowsMenu : EnhancedDialog(), HasStyle {

  private val items = VerticalLayout()
  private val windowsItemsMap = mutableMapOf<Component, VWindowsMenuItem>()
  private val headerText = Label()

  init {
    // Make sure that CSS styles specified for the default Menu classes
    // do not affect this menu
    className = Styles.MAIN_WINDOW
    val headerIcon = Icon(VaadinIcon.COPY_O)
    val header = HorizontalLayout()

    header.className = "window-items-title"
    header.add(headerText, headerIcon)
    header.alignItems = FlexComponent.Alignment.END
    this.setHeader(header)
    this.setThemeVariants(EnhancedDialogVariant.SIZE_SMALL)
    element.themeList.add(Styles.MAIN_WINDOW)
    this.setContent(items)
  }

  /**
   * Adds a window like an [VWindowsMenuItem] into the [VWindowsMenu].
   *
   * @param window The window to be added.
   * @param title The window title.
   */
  fun addWindow(window: Component, title: String): VWindowsMenuItem {
    val item = VWindowsMenuItem(title, window)

    items.className = "window-items-container"
    windowsItemsMap[window] = item
    items.add(item)

    return item
  }

  /**
   * Removes the given window item.
   * @param window The window item.
   */
  fun removeWindow(window: Component) {
    val toRemove = windowsItemsMap[window]
    toRemove?.let {
      items.remove(it)
    }
    windowsItemsMap.remove(window)
  }

  /**
   * Sets the header text.
   * @param text The header text.
   */
  fun setHeaderText(text: String?) {
    headerText.text = text
  }

  /**
   * Returns the item for the given window widget.
   * @param window The window widget.
   * @return The menu item.
   */
  fun getItemFor(window: Component?): VWindowsMenuItem? = windowsItemsMap[window]
}
