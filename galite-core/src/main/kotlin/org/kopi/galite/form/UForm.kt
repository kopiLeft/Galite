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

import org.kopi.galite.util.PrintJob
import org.kopi.galite.visual.UWindow

/**
 * `UForm` is the top-level interface that must be implemented
 * by all forms. It is the visual component of the [VForm] model.
 */
interface UForm : UWindow, FormListener {

  /**
   * Returns the block view of a given [UBlock] model.
   * @param block the [VBlock] model.
   * @return The [UBlock] view.
   */
  fun getBlockView(block: VBlock): UBlock

  /**
   * Returns the print job of the form view.
   * @return The [PrintJob] of this `UForm`
   * @throws VException operation may fail
   */
  fun printForm(): PrintJob

  /**
   * Prepares the snapshot.
   */
  fun printSnapshot()

  /**
   * Returns the Debug throwable info
   * @return The [Throwable] debug info.
   */
  fun getRuntimeDebugInfo(): Throwable

  /**
   * Show document preview
   */
  fun launchDocumentPreview(file: String)
}
