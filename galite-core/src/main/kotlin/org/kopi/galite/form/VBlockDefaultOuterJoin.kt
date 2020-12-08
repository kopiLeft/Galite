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

import org.jetbrains.exposed.sql.Op

class VBlockDefaultOuterJoin {

  companion object {
    fun getFetchRecordCondition(fields: Array<VField>): Op<Boolean>? {
      var condition : Op<Boolean>? = null

      for (i in fields.indices) {
        val fld = fields[i]
        if (fld.hasNullableCols()) {
          for (j in 1 until fld.getColumnCount()) {
            if (!fld.getColumn(j)!!.nullable) {
              condition =  Op.build { fld.getColumn(j)!!.column eq  fld.getColumn(0)!!.column }
            }
          }
        } else {
          for (j in 1 until fld.getColumnCount()) {
            condition =  Op.build { fld.getColumn(j)!!.column eq  fld.getColumn(j - 1)!!.column }
          }
        }
      }
      return condition
    }
  }
}
