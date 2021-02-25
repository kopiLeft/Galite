/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: RichTextField.java 35230 2017-09-13 18:27:19Z hacheni $
 */
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.AbstractField
import java.util.Locale

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.HasValueAndElement
import com.vaadin.flow.shared.Registration

/**
 * A rich text field implementation based on CKEditor
 * TODO: implement with appropriate component, not abstract
 */
class RichTextField(
        col: Int,
        rows: Int,
        visibleRows: Int,
        noEdit: Boolean,
        locale: Locale
) : Component(),
        HasValue<AbstractField.ComponentValueChangeEvent<RichTextField, String>, String>,
        Focusable<RichTextField> {
  override fun setValue(value: String?) {
    TODO("Not yet implemented")
  }

  override fun getValue(): String {
    TODO("Not yet implemented")
  }

  override fun addValueChangeListener(
          listener: HasValue.ValueChangeListener<in AbstractField.ComponentValueChangeEvent<RichTextField, String>>?,
  ): Registration {
    TODO("Not yet implemented")
  }

  override fun setReadOnly(readOnly: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isReadOnly(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setRequiredIndicatorVisible(requiredIndicatorVisible: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isRequiredIndicatorVisible(): Boolean {
    TODO("Not yet implemented")
  }
}
