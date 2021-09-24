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
package org.kopi.galite.ui.vaadin.common

import com.vaadin.flow.component.html.Span

/**
 * A simple span widget.
 */
class VSpan : Span {
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new span widget.
   */
  constructor() : super()

  /**
   * Creates a span with a text.
   * @param text The span text.
   */
  constructor(text: String?) : super(text)

  /**
   * Sets the inner HTML of this span widget.
   * @param html The HTML to be set.
   */
  fun setHtml(html: String?) {
    element.setProperty("innerHTML", html)
  }
}
