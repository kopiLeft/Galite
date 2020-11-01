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

import java.util.*

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import java.util.stream.Stream

/**
 * The module item model.
 */
class ModuleItem(id: String,
                 caption: String,
                 description: String,
                 isLeaf: Boolean) : Component(), HasComponents {
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
  fun addItem(id: String,
              caption: String,
              description: String,
              isLeaf: Boolean): ModuleItem {
    return addItem(ModuleItem(id, caption, description, isLeaf))
  }

  /**
   * Adds a child item to this item.
   * @param newItem The item to be added.
   * @return The added item.
   */
  fun addItem(newItem: ModuleItem): ModuleItem {
    newItem.parentItem = this
    if (children == null) {
      children = Stream<Component>()
    }
    children!!.add(newItem)
    newItem.setParent(this)
    return newItem
  }

  /**
   * Checks if the item has children (if it is a sub-menu).
   *
   * @return True if this item has children
   */
  fun hasChildren(): Boolean {
    return !children!!.isEmpty()
  }

  /**
   * This will return the children of this item or null if there are none.
   *
   * @return List of children items, or null if there are none
   */
  override fun getChildren(): Stream<Component>? {
    return children
  }

  /**
   * Gets the objects text
   *
   * @return The text
   */
  val text: String
    get() = getCaption()

  /**
   * Returns the number of children.
   *
   * @return The number of child items
   */
  val size: Int
    get() = if (children != null) {
      children!!.size
    } else -1

  /**
   * Get the unique identifier for this item.
   * @return The id of this item
   */
  val id: String
    get() = state.itemId

  /**
   * Returns `true` if it is a leaf item.
   * @return `true` if it is a leaf item.
   */
  val isLeaf: Boolean
    get() = state.isLeaf

  /**
   *
   *
   * Gets the items's description. The description can be used to briefly
   * describe the state of the item to the user. The description string
   * may contain certain XML tags:
   *
   *
   *
   *
   * <table border=1>
   * <tr>
   * <td width=120>**Tag**</td>
   * <td width=120>**Description**</td>
   * <td width=120>**Example**</td>
  </tr> *
   * <tr>
   * <td>&lt;b></td>
   * <td>bold</td>
   * <td>**bold text**</td>
  </tr> *
   * <tr>
   * <td>&lt;i></td>
   * <td>italic</td>
   * <td>*italic text*</td>
  </tr> *
   * <tr>
   * <td>&lt;u></td>
   * <td>underlined</td>
   * <td><u>underlined text</u></td>
  </tr> *
   * <tr>
   * <td>&lt;br></td>
   * <td>linebreak</td>
   * <td>N/A</td>
  </tr> *
   * <tr>
   * <td>&lt;ul><br></br>
   * &lt;li>item1<br></br>
   * &lt;li>item1<br></br>
   * &lt;/ul></td>
   * <td>item list</td>
   * <td>
   *
   *  * item1
   *  * item2
   *
  </td> *
  </tr> *
  </table> *
   *
   *
   *
   *
   * These tags may be nested.
   *
   *
   * @return item's description `String`
   */
  val description: String
    get() = super.getDescription()

  operator fun iterator(): Iterator<Component> {
    return LinkedList(children).iterator()
  }

  protected val state: ModuleItemState
    protected get() = super.getState() as ModuleItemState
  /**
   * For the containing item. This will return null if the item is in the
   * top-level menu bar.
   *
   * @return The containing [ModuleItem] , or null if there is none
   */
  /**
   * Set the parent of this item. This is called by the addItem method.
   * @param parent The parent item
   */
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  var parentItem: ModuleItem? = null
    protected set
  private var children: Stream<Component>?
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new module item.
   * @param id The item ID.
   * @param caption The item caption.
   * @param description The item description
   * @param isLeaf Is it a leaf item.
   */
  init {
    setImmediate(true)
    state.itemId = id
    setCaption(caption)
    setDescription(description)
    state.isLeaf = isLeaf
    children = LinkedList()
  }
}
