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

import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Image
/**
 * The actor component
 *
 * @param caption The actor caption.
 * @param description The actor help.
 * @param menu The menu to which this actor belongs to.
 * @param icon The actor icon.
 * @param acceleratorKey The accelerator key.
 * @param modifiersKey The modifiers key.
 */
open class Actor(val caption: String?,
                 description: String?,
                 val menu: String,
                 val icon: String?,
                 val acceleratorKey: Key,
                 val modifiersKey: KeyModifier?) : Button(), HasEnabled {

  init {
    super.setText(caption)

    if (icon != null) {
      val img = Image()
      img.src = icon
      super.setIcon(img)
    }

    if (modifiersKey != null) {
      super.addClickShortcut(acceleratorKey, modifiersKey)
    } else {
      super.addClickShortcut(acceleratorKey)
    }
  }
}
