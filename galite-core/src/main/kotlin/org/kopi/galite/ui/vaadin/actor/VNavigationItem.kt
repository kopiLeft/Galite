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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span

abstract class VNavigationItem : Span() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  protected val caption = Div()
  protected val acceleratorKey = Span()
  protected var icon : Component? = null

  init {
    className = getClassname()
  }

  fun setCaption(text: String?) {
    caption.text = text
  }

  fun getCaption(): String? {
    return caption.element.text
  }

  protected abstract fun setDescription(key: Key?, keyModifier: KeyModifier?)

  protected abstract fun setIcon(iconName: Any?)

  protected abstract fun   getClassname(): String

  fun createComponent() {
    acceleratorKey.className = "acceleratorKey"
    icon?.let {
      add(it)
    }
    add(caption)
    add(acceleratorKey)
  }
}