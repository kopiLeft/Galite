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
package org.kopi.galite.domain

import org.kopi.galite.form.VConstants

/**
 * The convert option used in order to modify the String's case.
 */
enum class Convert(val value: Int) {

  /**
   * Converts the first letter of each word to capital letter.
   */
  NAME(VConstants.FDO_CONVERT_NAME),

  /**
   * Convert the whole text to capital letters.
   */
  UPPER(VConstants.FDO_CONVERT_UPPER),

  /**
   * Converts the whole text to normal letters.
   */
  LOWER(VConstants.FDO_CONVERT_NAME),

  /**
   * No conversion.
   */
  NONE(VConstants.FDO_CONVERT_NONE)
}
