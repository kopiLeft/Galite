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

import java.io.Serializable

/**
 * A class to specify alignment in Layout
 *
 * @param          x               position in x
 * @param          y               position in y
 * @param          width           number of column
 * @param          height          number of lines
 * @param          alignRight      position in alignRight
 * @param          useAll          use the whole possible width of the column
 */
open class Alignment @JvmOverloads constructor(var x: Int,
                     var y: Int,
                     var width: Int,
                     var height: Int,
                     var alignRight: Boolean,
                     var useAll: Boolean = false) : Serializable {
  var ALG_LEFT = false
  var ALG_RIGHT = true
}
