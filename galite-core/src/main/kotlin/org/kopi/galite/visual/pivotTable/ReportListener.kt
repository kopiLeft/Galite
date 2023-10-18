/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.pivotTable

import java.io.Serializable
import java.util.EventListener

/**
 * `TableModelListener` defines the interface for an object that listens
 * to changes in [MPivotTable] content.
 *
 * @see javax.swing.table.TableModel
 */
interface ReportListener : EventListener, Serializable {
  /**
   * This fine grain notification tells listeners that
   * the report model has changed and the display should
   * update it self
   */
  fun contentChanged()
}
