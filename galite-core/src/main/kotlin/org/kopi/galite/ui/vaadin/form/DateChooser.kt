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
package org.kopi.galite.ui.vaadin.form

import org.kopi.galite.base.UComponent
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.type.Date

import com.vaadin.flow.component.Component

/**
 * The `DateChooser` is date selection component.
 * @param date The initial date.
 *
 * TODO: Implement this with appropriate componenet
 */
class DateChooser(date: Date?, reference: Component) : Component(), UComponent, DateChooserListener {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Launches the date chooser as a modal window.
   * @param date The initial date.
   * @return The selected date.
   */
  private fun doModal(date: Date?): Date {
    TODO()
  }

  /**
   * Disposes the date chooser.
   */
  protected fun dispose() {
    TODO()
  }

  /**
   * Destroys the date chooser.
   */
  protected fun destroy() {
    selectedDate = null
  }

  /**
   * Returns the application instance.
   * @return The application instance.
   */
  protected val application: VApplication
    get() = ApplicationContext.applicationContext.getApplication() as VApplication

  /**
   * Returns The current selected date (can be `null`)
   * @return The current selected date (can be `null`)
   */
  fun getSelectedDate(): Date? {
    return selectedDate
  }

  /**
   * Sets the selected date.
   * @param selectedDate The selected date.
   */
  fun setSelectedDate(selectedDate: Date?) {
    this.selectedDate = selectedDate
  }

  //-------------------------------------------------
  // DATE CHOOSER LISTENER IMPLEMENTATION
  //-------------------------------------------------
  override fun onClose(selected: java.util.Date?) {
    TODO()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var selectedDate: Date? = date

  /**
   * Returns the first day of the selected month.
   * @return The first day of the selected month.
   */
  val firstDay = 0

  companion object {
    //---------------------------------------------------
    // UTILS
    //---------------------------------------------------
    /**
     * Static date selection.
     * @param date The initial date.
     * @return The selected date.
     */
    fun selectDate(date: Date, reference: Component): Date {
      val chooser = DateChooser(date, reference)
      return chooser.doModal(date)
    }

    /**
     * Returns the number of days in the specified month
     * @return The number of days in the specified month
     */
    /*package*/
    fun getDaysInMonth(d: Date?): Int {
      TODO()
    }
  }

  override fun isEnabled(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }
}
