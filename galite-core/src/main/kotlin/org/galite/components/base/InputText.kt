/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.galite.components.base

import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.html.Input

/**
 * An input text component.
 */
class InputText(input: String?) : Input(), KeyNotifier {

  init {
    value = input
  }

  fun setSize(size: Int) {
    element.setAttribute("size", size.toString() + "")
  }

  fun setName(name: String?) {
    element.setAttribute("name", name)
  }

}
