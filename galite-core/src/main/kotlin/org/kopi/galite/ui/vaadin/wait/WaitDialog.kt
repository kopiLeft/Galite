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
package org.kopi.galite.ui.vaadin.wait

import java.util.Timer
import java.util.TimerTask

import org.kopi.galite.ui.vaadin.progress.ProgressDialog

/**
 * The wait dialog window is a progress bar controlled
 * by a time end limit.
 * The progress will be handled in client side and all we
 * want from the server is to have the total time to wait.
 *
 * @param title The progress dialog title.
 * @param message The progress dialog message.
 * @param timeout The timeout value.
 */
class WaitDialog(
        title: String = "",
        message: String = "",
        private var timeout: Int = 0)
  : ProgressDialog(title, message) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private var timer: Timer?  = Timer()

  /**
   * The max time to wait
   */
  private var maxTime = 0

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  fun show() {
    super.open()
    startWait()
  }

  fun hide() {
    super.close()
    timer!!.cancel()
    timer = null
  }

  /**
   * Start waiting.
   */
  fun startWait() {
    // wait only if needed
    if (timer == null) {
      val timerTask = object : TimerTask() {
        override fun run() {
          if (getProgress() < timeout) {
            progress()
          } else {
            timer!!.cancel()
          }
        }
      }
      // progress every second.
      timer!!.schedule(timerTask, 1000)
    }
  }

  /**
   * Sets the wait dialog timeout.
   * @param timeout The timeout value.
   */
  fun setMaxTime(timeout: Int) {
    this.timeout = timeout
    this.totalJobs = timeout / 1000
  }
}