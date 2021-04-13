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
package org.kopi.galite.ui.vaadin.actor

import org.kopi.galite.visual.VActor

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * Constructs a new actor navigation menu item that fires a command when it is selected.
 *
 * @param text the item's text
 * @param menu The parent menu name of this menu item.
 * @param acceleratorKey The accelerator key description.
 * @param icon The menu item icon.
 */
class VActorNavigationItem(text: String,
                           val menu: String?,
                           acceleratorKey: Key?,
                           keyModifier : KeyModifier?,
                           icon: VaadinIcon?,
                           val action: VActor?) : Button() {

  init {
    super.setText(text)

    if (icon != null) {
      val img = Icon(icon)

      super.setIcon(img)
      this.addClickListener {
        action?.performAction()
      }
    }

    if (acceleratorKey != null && acceleratorKey != Key.UNIDENTIFIED) {
      val modifier = keyModifier?.keys?.get(0)

      this.addToSuffix(Label(if(modifier != null) modifier + "-" + acceleratorKey.keys[0] else acceleratorKey.keys[0]))
      super.addClickShortcut(acceleratorKey)
    }

    className = getClassname()
  }

  protected fun getClassname(): String {
    return "actor-navigationItem"
  }
}
