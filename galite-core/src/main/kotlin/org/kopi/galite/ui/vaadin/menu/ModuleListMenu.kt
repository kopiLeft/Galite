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
package org.kopi.galite.ui.vaadin.menu

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Tag

/**
 * The module list menu bar.
 */
@Tag(Tag.UL)
open class ModuleListMenu(val root: Boolean) :
        Component(), HasComponents, Focusable<ModuleListMenu>, BlurNotifier<ModuleListMenu> {

  private val items = mutableListOf<ModuleItem>()
  var autoOpen = true // always auto open.
  var isAnimationEnabled = true // always enable animation
  var focusOnHover = true
  private var shownChildMenu: ModuleListMenu? = null
  private var selectedItem: ModuleItem? = null
  var vertical = false


  init {
    this.vertical = !root // root menu is always horizontal. sub menus are vertical

    // Hide focus outline in Mozilla/Webkit/Opera
    element.style.set("outline", "0px")
    // Hide focus outline in IE 6/7
    element.setAttribute("hideFocus", "true")
    // Deselect items when blurring without a child menu.
    addBlurListener {
      if (shownChildMenu == null) {
        selectItem(null)
      }
    }
    tabIndex = 0

  }

  /**
   * Select the given VModuleItem, which must be a direct child of this MenuBar.
   *
   * @param item the VModuleItem to select, or null to clear selection
   */
  open fun selectItem(item: ModuleItem?) {
    assert(item == null || item.parentMenu == this)

    if (item == selectedItem) {
      return

    }
    if (selectedItem != null) {
      selectedItem!!.classNames.remove("active")
      selectedItem!!.setSelectionStyle(false)
    }

    item?.setSelectionStyle(true)

    selectedItem = item
  }

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------

  /**
   * Adds a menu item to the bar.
   *
   * @param item the item to be added
   * @return the [ModuleItem] object
   */
  open fun addItem(item: ModuleItem, root: Boolean, shortcutNavigation: Boolean): ModuleItem {
    item.root = root
    item.shortcutNavigation = shortcutNavigation
    item.buildContent()
    return insertItem(item)
  }

  /**
   * Removes an item from this bar.
   * @param item The item to be removed
   */
  open fun removeItem(item: ModuleItem?) {
    if (item != null) {
      items.remove(item)
      item.parentMenu = null
      item.setSelectionStyle(false)
      remove(item)
    }
  }

  /**
   * Adds a menu item to the bar.
   * @param id The item ID.
   * @param caption The item caption.
   * @return The added item.
   */
  open fun addItem(id: String?, caption: String?, root: Boolean, shortcutNavigation: Boolean): ModuleItem {
    val item = ModuleItem()
    item.setId(id)
    item.caption = caption
    return addItem(item, root, shortcutNavigation)
  }

  open fun clear() {
    for (item in items) {
      item.removeAll()
    }
    super.removeAll()
  }

  /**
   * Adds a menu item to the bar at a specific index.
   *
   * @param item the item to be inserted
   * @return the [VModuleItem] object
   */
  fun insertItem(item: ModuleItem): ModuleItem {
    items.add(item)

    // Setup the menu item
    item.parentMenu = this
    item.setSelectionStyle(false)
    add(item)
    return item
  }
}
