/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.actor

import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

open class VClickableNavigationItem : VNavigationItem() {
  private val DEPENDENT_STYLENAME_DISABLED_ITEM = "disabled"

  override fun setDescription(key: Key?, keyModifier: KeyModifier?) {
    if (key != null && key != Key.UNIDENTIFIED) {
      val modifier = keyModifier?.keys?.get(0)
      acceleratorKey.text = if(modifier != null) modifier + "-" + key.keys[0] else " " + key.keys[0]
    } else {
      acceleratorKey.text = ""
    }
  }

  override fun setIcon(iconName: Any?) {
    icon = if (iconName is VaadinIcon) {
      Icon(iconName)
    } else if (iconName is IronIcons) {
      iconName.create()
    } else {
      Icon("")
    }
  }

  override fun getClassname(): String = ""

  override fun setEnabled(isEnabled: Boolean) {
    className = if(isEnabled) {
      "actor-navigationItem"
    } else {
      "actor-navigationItem-$DEPENDENT_STYLENAME_DISABLED_ITEM"
    }
    super.setEnabled(isEnabled)
  }
}
