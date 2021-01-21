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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.HasText
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.dom.ElementFactory

/**
 * The module item model.
 *
 * @param vaadinIcon The icon to add to this item.
 * @param help       The help text.
 */
open class ModuleItem(val vaadinIcon: VaadinIcon? = null,
                      val help: String? = null)
  : Div(), HasComponents, HasStyle {

  var parentMenu: ModuleListMenu? = null

  init {
    if(help != null) {
      element.setAttribute("help", help)
    }

    if(vaadinIcon != null) {
      add(Icon(vaadinIcon))
    }
  }

  /**
   * The sub-menu associated with this item.
   */
  var subMenu: ModuleListMenu? = null
    set(value) {
      field = value
    }

  /**
   * Sets the item caption.
   * @param caption The item caption.
   */
  open fun setCaption(caption: String) {
    if(vaadinIcon != null) {
      val stringPanel = VStrongPanel().also {
        it.text = caption
      }
      add(stringPanel)
    } else {
      text = caption
    }
  }

  /**
   * A simple component that wraps a strong element inside.
   */
  private class VStrongPanel : Component(ElementFactory.createStrong()), HasComponents, HasText
}
