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

package org.kopi.galite.form

import org.kopi.galite.visual.VExecFailedException

/**
 * Represents an exception with a message.
 *
 * @param    field        the field that has not a correct value
 * @param    message        the associated message
 * @param    newValue    the new value for the field
 */
class VFieldException(val field: VField,
                      message: String? = null,
                      private val newValue: Any? = null)
  : VExecFailedException(message) {

  /**
   * Returns the field where the error occure
   */
  fun resetValue() {
    if (newValue != null) {
      field.setObject(field.block!!.activeRecord, newValue)
    }
  }

  companion object {
    private const  val serialVersionUID = 0L
  }
}
