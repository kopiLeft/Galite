/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.dsl.form

import org.kopi.galite.visual.form.VConstants

enum class Access(val value: Int) {
  /**
   * hidden access
   */
  HIDDEN(VConstants.ACS_HIDDEN),

  /**
   * Skipped access
   */
  SKIPPED(VConstants.ACS_SKIPPED),

  /**
   * Visit access
   */
  VISIT(VConstants.ACS_VISIT),

  /**
   * Mustfill access
   */
  MUSTFILL(VConstants.ACS_MUSTFILL)
}
