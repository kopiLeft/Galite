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

package org.kopi.galite.visual

import org.kopi.galite.base.DBContext
import org.kopi.galite.l10n.LocalizationManager
import java.io.File

abstract class VWindow {
  open fun getType(): Int {
    return Constants.MDL_UNKOWN;
  }

  /**
   * Destroy this class (break all references to help java to GC the form)
   */
  open fun destroyModel() {}

  fun localizeActors(manager: LocalizationManager?): Void = TODO()

  fun setWaitInfo(message: String): Void = TODO()

  protected var display: UWindow = TODO()

  fun unsetWaitInfo(): Void = TODO()

  fun fireFileProduced(file: File?, name: String): Void = TODO()

  open fun setDBContext(context: DBContext?): Void = TODO()

}
