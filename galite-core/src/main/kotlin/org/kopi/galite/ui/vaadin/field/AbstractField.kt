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
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.Tag

@Tag(Tag.DIV)
abstract class AbstractField<T>: com.vaadin.flow.component.AbstractField<AbstractField<T>, T>(null),
  HasComponents, ClickNotifier<AbstractField<T>>, Focusable<AbstractField<T>>, HasSize {

  abstract fun addFocusListener(function: () -> Unit)

  internal abstract val isNull: Boolean

  internal abstract fun checkValue(rec: Int)

  abstract fun getContent(): Component

  abstract override fun getValue(): T

  abstract override fun setValue(value: T)

  override fun focus() {
    val content = getContent()

    if (content is Focusable<*>) {
      content.focus()
    }
  }
}
