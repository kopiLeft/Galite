/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.progress

import org.kopi.galite.visual.ui.vaadin.base.Styles

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.progressbar.ProgressBar

/**
 * A custom progress bar widget
 *
 * @param jobs total jobs
 */
@CssImport("./styles/galite/progress.css")
class ProgressBar(val parent: ProgressDialog, var jobs: Int) : ProgressBar() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  internal var progress = 0.0

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    className = Styles.PROGRESS_BAR
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Progress with one step.
   */
  fun progress() {
    setProgress(progress + 1)
  }

  /**
   * Set the progress bar to the given job.
   * @param job progress to set
   */
  fun setProgress(job: Double) {
    progress = job
    setProgressPercentage(job / jobs)
  }

  /**
   * Sets the progress percentage.
   * @param percentage The percentage of the progress.
   */
  fun setProgressPercentage(percentage: Double) {
    value = percentage
    parent.setPercentageText(percentage * 100)
  }

  /**
   * Returns the progress percentage.
   * @return The progress percentage.
   */
  fun getProgress(): Double {
    return value
  }
}
