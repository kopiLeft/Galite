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

class Item(val id: Int,
           var parent: Int,
           var name: String,
           var localisedName: String?,
           var description: String?,
           var isSelected: Boolean,
           var isDefaultItem: Boolean,
           icon: String?,
           var originalName: String) : Comparable<Item> {

  var icon: Image? = null
  var smallIcon: Image? = null
  var childCount = 0
  var children: Array<Item>? = null
  var level = 0

  init {
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
  fun getFormattedName(localised: Boolean): String {
    return if (localised && id != -1) {
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
  override operator fun compareTo(other: Item): Int {
    return if (id == other.id
            && parent == other.parent
            && name == other.name
            && localisedName == other.localisedName
            && description == other.description
            && isSelected == other.isSelected
            && isDefaultItem == other.isDefaultItem) 1 else -1
  }
}
