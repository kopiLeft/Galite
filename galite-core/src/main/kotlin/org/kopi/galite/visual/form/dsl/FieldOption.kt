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
package org.kopi.galite.visual.form.dsl

import org.kopi.galite.visual.form.VConstants

enum class FieldOption(val value: Int) {
  /**
   * If this option is used, characters typed in the field will not be displayed and a star(*) will be
   * displayed instead, this option is useful for password fields.
   */
  NOECHO(VConstants.FDO_NOECHO),

  /**
   * This option makes it impossible to change the data of the field or to overwrite it.
   */
  NOEDIT(VConstants.FDO_NOEDIT),

  /**
   * This option adds two opposed arrows icons(up and down) just before the field,
   * clicking on the icon changes the way data are sorted in the field,
   * you can click the icon three times to have ascending sort, descending sort and default sort.
   */
  SORTABLE(VConstants.FDO_SORT),

  /**
   * This option make the field transient, meaning that the system can no trail it,
   * if a transaction  calls this field and then this transaction is aborted,
   * the field will not be able to backup or roll-back to its original value,
   * besides this option makes changes ignored for this field.
   */
  TRANSIENT(VConstants.FDO_TRANSIENT),

  /**
   * If the field is a lookup is a column of a lookup table, using this option prevent
   * the system to clear the field when inserting new rows or updating rows.
   */
  NO_DELETE_ON_UPDATE(VConstants.FDO_DO_NOT_ERASE_ON_LOOKUP),

  /**
   * If the block is in detailed, using this option on a field make it invisible in the detail.
   */
  NO_DETAIL(VConstants.FDO_NODETAIL),

  /**
   * If the block is multiple, using this option on a field exclude it from the chart.
   */
  NO_CHART(VConstants.FDO_NOCHART),

  /**
   * Whatever the string you input, this option will make kopi transform it to capital letters.
   */
  QUERY_UPPER(VConstants.FDO_SEARCH_UPPER),

  /**
   * the opposite of the previous option it transform strings to lower case.
   */
  QUERY_LOWER(VConstants.FDO_SEARCH_LOWER)
}
