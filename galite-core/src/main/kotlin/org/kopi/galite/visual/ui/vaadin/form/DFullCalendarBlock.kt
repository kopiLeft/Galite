/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.ui.vaadin.form

import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.ui.vaadin.block.BlockLayout
import org.kopi.galite.visual.ui.vaadin.block.SingleComponentBlockLayout
import org.kopi.galite.visual.ui.vaadin.calendar.DTimeGridCalendar

/**
 * Full Calendar based block implementation.
 */
class DFullCalendarBlock(parent: DForm, model: VFullCalendarBlock) : DBlock(parent, model) {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  lateinit var timeGridCalendar: DTimeGridCalendar

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  /**
   * Differently create fields for this block
   */
  override fun createFields() {
    // No fields in calendar block
    columnViews = arrayOfNulls(0)
    timeGridCalendar = DTimeGridCalendar(model as VFullCalendarBlock)
    addComponent(timeGridCalendar, 0, 0, 1, 1, false, false)
  }

  override fun refresh(force: Boolean) {
    super.refresh(force)
    // refreshEntries() TODO
  }

  override fun refreshEntries() {
    timeGridCalendar.refreshEntries()
  }

  override fun getSelectedDate(): Date = timeGridCalendar.getSelectedDate()

  override fun goToDate(date: Date) {
    timeGridCalendar.goToDate(date)
  }

  override fun enter() {
    timeGridCalendar.enter()
  }

  override fun createLayout(): BlockLayout {
    return SingleComponentBlockLayout(this)
  }
}
