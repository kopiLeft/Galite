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

import com.vaadin.flow.component.textfield.BigDecimalField

/**
 * An fixnum field.
 */
class VFixnumField(col: Int,
                   maxScale: Int,
                   minval: Double?,
                   maxval: Double?,
                   fraction: Boolean) : BigDecimalField(), UTextField {

  override fun hasAutoComplete(): Boolean {
    TODO("Not yet implemented")
  }

  override fun getMaxLength(): Int = maxlengthDouble.toInt()

  override fun getMinLength(): Int = minlengthDouble.toInt()

  override fun setMaxLength(maxLength: Int) {
    super.setMaxlength(maxLength.toDouble())
  }

  override fun setMinLength(minLength: Int) {
    super.setMinlength(minLength.toDouble())
  }

}
