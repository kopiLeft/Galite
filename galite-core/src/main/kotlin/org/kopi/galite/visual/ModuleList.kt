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

package org.kopi.galite.visual

import com.vaadin.flow.component.HasComponents
import java.util.*

import com.vaadin.flow.component.Component
/**
 * The module list server side component.
 */
class ModuleList : Component(), HasComponents {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Add a new item inside this item, thus creating a sub-menu. Command
   * can be null, but a caption must be given.
   * @param id The item ID.
   * @param caption The item caption.
   * @param description The item description
   * @param isLeaf Is it a leaf item.
   */
  fun addItem(id: String, caption: String, description: String, isLeaf: Boolean): ModuleItem {
    return addItem(ModuleItem(id, caption, description, isLeaf))
  }

  /**
   * Adds a child item to this item.
   * @param newItem The item to be added.
   * @return The added item.
   */
  fun addItem(newItem: ModuleItem): ModuleItem {
    requireNotNull(newItem.text) { "caption cannot be null" }
    moduleItems.add(newItem)
    newItem.setParent(this)
    return newItem
  }

  /**
   * Sets the animation ability of this module list.
   * @param animated The animation ability.
   */
  fun setAnimationEnabled(animated: Boolean) {
    state.animated = animated
  }

  /**
   * Detects whether the menubar is in a mode where top level menus are
   * automatically opened when the mouse cursor is moved over the menu.
   * Normally root menu opens only by clicking on the menu. Submenus always
   * open automatically.
   *
   * @return true if the root menus open without click, the default is false
   */
  /**
   * Using this method menubar can be put into a special mode where top level
   * menus opens without clicking on the menu, but automatically when mouse
   * cursor is moved over the menu. In this mode the menu also closes itself
   * if the mouse is moved out of the opened menu.
   *
   *
   * Note, that on touch devices the menu still opens on a click event.
   *
   * @param autoOpenTopLevelMenu
   * true if menus should be opened without click, the default is
   * false
   */
  var isAutoOpen: Boolean
    get() = state.openRootOnHover
    set(autoOpenTopLevelMenu) {
      if (autoOpenTopLevelMenu != state.openRootOnHover) {
        state.openRootOnHover = autoOpenTopLevelMenu
      }
    }

  /**
   * Sets the module list type.
   * The type is the root menu ID associated with this module list.
   * @param type The menu type
   */
  fun setType(type: Int) {
    state.type = type
  }

  /**
   * Registers a module list listener on this component.
   * @param listener The listener to be registered.
   */
  fun addModuleListListener(listener: ModuleListListener?) {

    addListener(ModuleListEvent::class.java, listener, ModuleListListener.CLICK_METHOD)
  }

  /**
   * Removes the module list listener.
   * @param listener The Listener to be removed.
   */
  fun removeActionListener(listener: ModuleListListener?) {
    removeListener(ModuleListEvent::class.java, listener, ModuleListListener.CLICK_METHOD)
  }

  /**
   * Fires an action performed event to all registered listeners.
   */
  protected fun fireClicked(itemId: String?) {
    fireEvent(ModuleListEvent(this, itemId))
  }

  operator fun iterator(): Iterator<Component> {
    return LinkedList<Component>(moduleItems).iterator()
  }

  protected val state: ModuleListState
    protected get() = super.getState() as ModuleListState

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  // Items of the top-level menu
  private val moduleItems: MutableList<ModuleItem>
  private val rpc: ModuleListServerRpc = object : ModuleListServerRpc() {
    fun onClick(itemId: String?) {
      fireClicked(itemId)
    }
  }
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new module list component.
   */
  init {
    registerRpc(rpc)
    setImmediate(true)
    moduleItems = LinkedList<ModuleItem>()
  }
}
