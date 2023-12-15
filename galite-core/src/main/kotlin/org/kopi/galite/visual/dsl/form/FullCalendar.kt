/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.dsl.form

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.form.*

/**
 * A block is a set of data which are stocked in the database and shown on a [Form].
 * A full calendar block is created in order to view the content of a database in form of full calendar,
 * to insert new data in the database or to update existing data in the database.
 *
 * @param        title                 the title of the block
 */
open class FullCalendar(title: String) : Block(title, 1, 1) {

  var dateField: FormField<*>? = null
  var fromTimeField: FormField<*>? = null
  var toTimeField: FormField<*>? = null
  var fromField: FormField<*>? = null
  var toField: FormField<*>? = null

  /**
   * Creates and returns a date mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a date mustfill field.
   */
  @Deprecated("use from() and to() fields instead")
  fun date(position: FormPosition, init: FormField<LocalDate>.() -> Unit): FormField<LocalDate> =
    date(Domain(), position, init)

  /**
   * Creates and returns a date mustfill field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  @Deprecated("use from() and to() fields instead")
  inline fun date(domain: Domain<LocalDate>,
                  position: FormPosition,
                  init: FormField<LocalDate>.() -> Unit): FormField<LocalDate>
  {
    return mustFill(domain, position, init).also { field ->
      dateField = field
      block.dateField = field.vField as VDateField
    }
  }

  /**
   * Creates and returns a time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  @Deprecated("use from() and to() fields instead")
  fun fromTime(position: FormPosition, init: FormField<LocalTime>.() -> Unit): FormField<LocalTime> =
    fromTime(Domain(), position, init)

  /**
   * Creates and returns a time mustfill field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  @Deprecated("use from() and to() fields instead")
  inline fun  fromTime(domain: Domain<LocalTime>,
                      position: FormPosition,
                      init: FormField<LocalTime>.() -> Unit): FormField<LocalTime> {
    return mustFill(domain, position, init).also { field ->
      fromTimeField = field
      block.fromTimeField = field.vField as VTimeField
    }
  }

  /**
   * Creates and returns a time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  @Deprecated("use from() and to() fields instead")
  fun toTime(position: FormPosition, init: FormField<LocalTime>.() -> Unit): FormField<LocalTime> =
    toTime(Domain(), position, init)

  /**
   * Creates and returns a time mustfill field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  @Deprecated("use from() and to() fields instead")
  inline fun toTime(domain: Domain<LocalTime>,
                    position: FormPosition,
                    init: FormField<LocalTime>.() -> Unit): FormField<LocalTime> {

    return mustFill(domain, position, init).also { field ->
      toTimeField = field
      block.toTimeField = field.vField as VTimeField
    }
  }

  /**
   * Creates and returns a Time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  fun from(position: FormPosition, init: FormField<Instant>.() -> Unit): FormField<Instant> =
    from(Domain(), position, init)

  /**
   * Creates and returns a MUSTFILL field.
   *
   * MUSTFILL fields are accessible fields that the user must fill with a value.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a MUSTFILL field.
   */
  inline fun from(domain: Domain<Instant>,
                  position: FormPosition,
                  init: FormField<Instant>.() -> Unit): FormField<Instant> {
    return mustFill(domain, position, init).also { field ->
      fromField = field
      block.fromField = field.vField as VTimestampField
    }
  }

  /**
   * Creates and returns a Time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  fun to(position: FormPosition, init: FormField<Instant>.() -> Unit): FormField<Instant> =
    to(Domain(), position, init)

  /**
   * Creates and returns a MUSTFILL field.
   *
   * MUSTFILL fields are accessible fields that the user must fill with a value.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a MUSTFILL field.
   */
  inline fun to(domain: Domain<Instant>,
                position: FormPosition,
                init: FormField<Instant>.() -> Unit): FormField<Instant> {
    return mustFill(domain, position, init).also { field ->
      toField = field
      block.toField = field.vField as VTimestampField
    }
  }

  /**
   * Sets the block into insert mode.
   * @exception        VException        an exception may occur during DB access
   */
  override fun insertMode() {
    block.insertMode()
  }

  fun goToDate(date: LocalDate) {
    block.goToDate(date)
  }

  fun getSelectedDate(): LocalDate? = block.getSelectedDate()

  /**
   * Refreshes the full calendar block data.
   */
  fun refreshEntries() {
    block.refreshEntries()
  }

  // ----------------------------------------------------------------------
  // BLOCK MODEL
  // ----------------------------------------------------------------------

  override val block = FullCalendarBlockModel(this@FullCalendar)

  override fun getBlockModel(vForm: VForm): VBlock {
    val model = super.getBlockModel(vForm)
    block.buildFullCalendarForm()
    block.source = if (this::class.isInner && vForm.source != null) vForm.source!! else sourceFile

    return model
  }
}
