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
package org.kopi.galite.ui.vaadin.progress

import org.kopi.galite.ui.vaadin.base.Styles

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Paragraph

/**
 * The progress dialog component.
 *
 * @param title       The progress dialog title.
 * @param message     The progress dialog message.
 */
open class ProgressDialog(title: String = "", message: String = "") : EnhancedDialog(), HasStyle {

  /**
   * The polling interval to fetch current job value.
   */
  var pollingInterval = 1000

  /**
   * The dialog content.
   */
  private var content = Div()

  /**
   * The progress dialog total number of operations.
   */
  var totalJobs: Int = 0
    set(value) {
      bar.jobs = value
      field = value
    }

  /**
   * The dialog title displayed in the header.
   */
  private var title = H4(title)

  /**
   * The dialog message displayed in the [content].
   */
  private var message = Paragraph(message)

  /**
   * The dialog progress bar.
   */
  private var bar = ProgressBar(this, totalJobs)

  private val percentageLabel = Paragraph()

  init {
    className = Styles.PROGRESS_DIALOG
    isCloseOnEsc = false
    isCloseOnOutsideClick = false
    isDraggable = true
    content.add(this.message)
    content.add(bar)
    content.add(percentageLabel)
    setHeader(this.title)
    setContent(content)
  }

  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------
  /**
   * Sets the progress bar title.
   * @param text The progress title.
   */
  fun setTitle(text: String?) {
    title.text = text
  }

  /**
   * Sets the progress bar message.
   * @param text The message.
   */
  fun setMessage(text: String?) {
    message.text = text
  }

  /**
   * Returns the bar progress percentage.
   * @return The bar progress percentage.
   */
  fun getProgress() = (bar.progress).toInt()

  /**
   * Sets the progress bar current job.
   * @param job The current job.
   */
  fun setProgress(job: Double) {
    bar.setProgress(job)
  }

  /**
   * Sets the progress bar current job.
   * @param job The current job.
   */
  fun setProgress(job: Int) {
    bar.setProgress(job.toDouble())
  }

  /**
   * Updates the bar progress.
   */
  fun progress() {
    bar.progress()
  }

  /**
   * Sets the percentage text.
   * @param percentage The percentage text.
   */
  open fun setPercentageText(percentage: Double) {
    percentageLabel.text = percentage.toInt().toString() + "%"
  }
}
