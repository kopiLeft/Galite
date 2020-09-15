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

import org.kopi.galite.base.Image

class Item(private var id: Int,
           private var parent: Int,
           private var name: String,
           private var localisedName: String,
           private var description: String,
           private var selected: Boolean,
           private var defaultItem: Boolean,
           icon: String,
           private var originalName: String) : Comparable<Item> {

  private lateinit var icon: Image
  private lateinit var smallIcon: Image
  private var childCount = 0
  private lateinit var children: Array<Item>
  private var level = 0

  // ---------------------------------------------------------------------
  // CONSTUCTOR
  // ---------------------------------------------------------------------
  init {
    this.id = id
    this.parent = parent
    this.name = name
    this.localisedName = localisedName
    this.description = description
    this.selected = selected
    this.defaultItem = defaultItem
    this.originalName = originalName
    if (icon != null) {
      this.icon = ImageHandler.imageHandler.getImage(icon)
      smallIcon = ImageHandler.imageHandler.getImage(icon)
      if (smallIcon == null) {
        smallIcon = smallIcon?.getScaledInstance(16, 16, Image.SCALE_SMOOTH)
      }
    }
  }

  /**
   * Sets the current item name
   */
  fun getFormattedName(localiserd: Boolean): String? {
    return if (localiserd && id != -1) {
      "[" + name + "]  " + if (localisedName != null) localisedName else "........"
    } else {
      name
    }
  }

  /**
   * Return the name
   */
  override fun toString(): String {
    return name
  }

  /**
   * Decrement the item level
   */
  fun decrementLevel() {
    level--
  }

  // ---------------------------------------------------------------------
  // COMPARABLE IMPLEMENTATION
  // ---------------------------------------------------------------------
  override operator fun compareTo(item: Item): Int {
    return if (id == item.id && parent == item.parent && name == item.name && localisedName == item.localisedName
            && description == item.description && selected == item.selected && defaultItem == item.defaultItem) 1 else -1
  }
}
