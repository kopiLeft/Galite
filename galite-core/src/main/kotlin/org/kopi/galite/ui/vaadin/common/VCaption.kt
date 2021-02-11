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
package org.kopi.galite.ui.vaadin.common

import org.kopi.galite.ui.vaadin.base.VAnchorPanel

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.html.Span

/**
 * A tab caption widget. Used to display tab titles.
 *
 * @param onlyAnchor Should the caption include only an anchor ?
 */
class VCaption(val onlyAnchor: Boolean) : Composite<Component>(), HasStyle {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val anchor = VAnchorPanel()
  private val caption = VSpan()

  /**
   * Creates a new `VCaption` instance.
   */
  init {
    className = "k-caption"
    anchor.href = "#" // to have the hand cursor.
    if (!onlyAnchor) {
      anchor.add(caption)
    }
  }

  override fun initContent(): Component {
    return if (onlyAnchor) {
      anchor
    } else {
      Span().also { it.add(anchor) }
    }
  }
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the inner caption.
   * @param caption The inner caption.
   */
  fun setCaption(caption: String?) {
    if (anchor.element.childCount == 0) {
      anchor.text = caption
    } else {
      this.caption.text = caption
    }
  }

  /**
   * Returns the caption text.
   * @return The caption text.
   */
  fun getCaption(): String {
    return if (anchor.element.childCount == 0) {
      anchor.text
    } else {
      caption.text
    }
  }

  /**
   * Sets this caption to be active or not.
   * @param active The active state.
   */
  fun setActive(active: Boolean) {
    if (active) {
      anchor.classNames.add("active")
    } else {
      anchor.classNames.remove("active")
    }
  }
}
