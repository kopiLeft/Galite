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
package org.kopi.galite.ui.vaadin.form

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VScrollablePanel

/**
 * A form page, can be either or vertical or horizontal page.
 */
class Page<T>(private var content: T) : Div()  where T: Component, T: FlexComponent {

  private var scrollPanel: VScrollablePanel?
  private var last: Component? = null

  init {
    this.content.className = Styles.FORM_PAGE_CONTENT
    scrollPanel = VScrollablePanel(this.content)
    add(scrollPanel)
    className = Styles.FORM_PAGE
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds a child to this page.
   * @param child The child component.
   * @param hAlign The horizontal alignment.
   */
  fun add(child: Component, hAlign: FlexComponent.JustifyContentMode) {
    content.add(child)
    content.justifyContentMode = hAlign
    last = child
  }

  /**
   * Adds a follow widget.
   * @param child The widget to be added.
   * @param align The alignment.
   */
  fun addFollow(child: Component, align: FlexComponent.JustifyContentMode) {
    if (last != null) {
      val temp = VerticalLayout()
      temp.className = "follow-blocks-container"
      content.remove(last)
      temp.add(last)
      temp.add(child)
      content.add(temp)
    } else {
      add(child, align)
    }
    last = null
  }
}
