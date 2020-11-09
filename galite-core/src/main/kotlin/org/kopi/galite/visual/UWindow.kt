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

import org.kopi.galite.base.UComponent

/**
 * `UWindow` is the top-level interface that must be implemented
 * by all windows. It is the visual component of the [VWindow] model.
 */
interface UWindow : UComponent, VActionListener, ModelCloseListener, WaitDialogListener,
        WaitInfoListener, ProgressDialogListener, FileProductionListener {
  /**
   * Starts the window view
   */
  @Throws(VException::class)
  open fun run()

  /**
   * Returns the [VWindow] model of this UI component.
   * @return the `UWindow` model.
   */
  fun getModel(): VWindow

  /**
   * Sets the `UWindow` title.
   * @param title the window title.
   */
  fun setTitle(title: String)

  /**
   * Sets the `UWindow` information text.
   * @param text The window information text.
   */
  fun setInformationText(text: String?)

  /**
   * Sets the `UWindow` total jobs.
   * @param totalJobs The window total jobs.
   */
  fun setTotalJobs(totalJobs: Int)

  /**
   * Sets the `UWindow` current job.
   * @param currentJob The window current job.
   */
  fun setCurrentJob(currentJob: Int)

  /**
   * Sets the wait message.
   * @param message The wait message
   */
  fun updateWaitDialogMessage(message: String)

  /**
   * Closes the `UWindow`
   */
  fun closeWindow()

  /**
   * Sets the `UWindow` focus enable state
   * @param enabled  boolean value specifying if the window focus should be enabled or not.
   */
  fun setWindowFocusEnabled(enabled: Boolean)

  /**
   * Performs the appropriate action synchronously.
   * @param action The [Action] to be executed
   * @see VActionListener.performAsyncAction
   */
  fun performBasicAction(action: Action)
}
