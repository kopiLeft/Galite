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
package org.kopi.galite.ui.vaadin.form

import java.time.LocalDate
import java.util.Calendar

import org.kopi.galite.base.UComponent
import org.kopi.galite.type.Date
import org.kopi.galite.type.Month
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.accessAndAwait
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ApplicationContext

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.datepicker.DatePicker

/**
 * The `DateChooser` is date selection component.
 *
 * @param selectedDate The initial date.
 */
class DateChooser(private var selectedDate: Date?,
                  reference: Component?)
  : DatePicker(),
        UComponent,
        HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>> {

  /**
   * Returns the first day of the selected month.
   * @return The first day of the selected month.
   */
  val firstDay = 0

  init {
    // showRelativeTo(reference) TODO
    // setToDay(VlibProperties.getString("today")) TODO
    if (selectedDate != null) {
      //super.setSelectedDate(date.toCalendar().time) TODO
    }
    addValueChangeListener(this)
    locale = application.defaultLocale
    //setOffset(Date().timezoneOffset) TODO
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Launches the date chooser as a modal window.
   * @param date The initial date.
   * @return The selected date.
   */
  private fun doModal(date: Date): Date? {
    //BackgroundThreadHandler.startAndWait(Runnable { TODO
    application.attachComponent(this@DateChooser)
    //}, this)
    return selectedDate
  }

  /**
   * Disposes the date chooser.
   */
  protected fun dispose() {
    application.detachComponent(this)
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
  fun setSelectedDate(selectedDate: Date) {
    this.selectedDate = selectedDate
  }

  //-------------------------------------------------
  // DATE CHOOSER LISTENER IMPLEMENTATION
  //-------------------------------------------------

  override fun valueChanged(event: ComponentValueChangeEvent<DatePicker, LocalDate>) {
    val selected = event.value
    if (selected != null) {
      val cal: Calendar = Calendar.getInstance(ApplicationContext.getDefaultLocale())
      //cal.time = selected TODO
      setSelectedDate(Date(cal))
    }
    dispose()
    //BackgroundThreadHandler.releaseLock(this) TODO
  }

  companion object {
    //---------------------------------------------------
    // UTILS
    //---------------------------------------------------
    /**
     * Static date selection.
     * @param date The initial date.
     * @return The selected date.
     */
    fun selectDate(date: Date, reference: Component): Date? {
      lateinit var chooser: DateChooser

      accessAndAwait {
        chooser = DateChooser(date, reference)
      }

      return chooser.doModal(date)
    }

    /**
     * Returns the number of days in the specified month
     * @return The number of days in the specified month
     */
    fun getDaysInMonth(d: Date): Int {
      return Month(d).getLastDay().day
    }
  }
}
