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

package org.kopi.galite.report

import java.io.OutputStream

class PExport2CSV(table: UReport.UTable, model: MReport, printOptions: PConfig, pageTitle: String) : PExport(table,
        model, printOptions, pageTitle), Constants {
  override fun exportHeader(data: Array<String?>) {
    TODO("Not yet implemented")
  }

  override fun exportRow(level: Int, data: Array<String?>, orig: Array<Any?>, alignment: IntArray?) {
    TODO("Not yet implemented")
  }

  override fun export(stream: OutputStream) {
    TODO("Not yet implemented")
  }

  override fun startGroup(subTitle: String?) {
    TODO("Not yet implemented")
  }

}
