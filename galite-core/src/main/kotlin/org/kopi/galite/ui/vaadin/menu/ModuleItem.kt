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

import org.kopi.galite.ui.vaadin.base.VAnchorPanel

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.dom.ElementFactory

/**
 * The module item model.
 */
@Tag(Tag.LI)
open class ModuleItem : Component(), HasComponents, HasStyle {
  var itemId: String? = null
  var caption: String? = null
  var description: String? = null
  var isLeaf: Boolean? = null

  private val tab = Span()
  protected val anchor = VAnchorPanel().also { it.setHref("#") }
  private var icon: Icon? = null

  var root = false
  var shortcutNavigation = false
  var parentMenu: ModuleListMenu? = null

  /**
   * The sub-menu associated with this item.
   */
  var subMenu: ModuleListMenu? = null
    set(value) {
      field = value
      if (parentMenu != null) {
        value!!.autoOpen = true
        value.isAnimationEnabled = parentMenu!!.isAnimationEnabled
        value.focusOnHover = parentMenu!!.focusOnHover
      }
    }

  /**
   * Builds the item content.
   */
  open fun buildContent() {
    super.removeAll()
    if (root && !shortcutNavigation) {
      // add all tabs
      add(tab)
      tab.className = "root"
      tab.add(anchor)
    } else if (shortcutNavigation) {
      anchor.removeAll()
      val strong = VStrongPanel()
      icon = Icon()
      strong.add(anchor)
      anchor.add(icon)
      add(strong)
    } else {
      // only add central anchor
      add(anchor)
    }
  }

  /**
   * Sets the selection style ability.
   * @param selected The selection style ability.
   */
  open fun setSelectionStyle(selected: Boolean) {
    if (root) {
      if (!selected) {
        tab.classNames.remove("selected")
      } else {
        tab.className = "selected"
      }
    } else {
      if (selected) {
        className = "selected"
      } else {
        classNames.remove("selected")
      }
    }
  }

  /**
   * Sets the icon name.
   * @param name The icon name.
   */
  open fun setIcon(name: String?) {
    if (shortcutNavigation && icon != null) {
      icon = Icon(name)
    }
  }

  /**
   * A simple component that wraps a strong element inside.
   */
  private class VStrongPanel : Component(ElementFactory.createStrong()), HasComponents
}
