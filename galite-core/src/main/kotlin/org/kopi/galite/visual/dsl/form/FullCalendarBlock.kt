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
package org.kopi.galite.visual.dsl.form

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp

/**
 * A block is a set of data which are stocked in the database and shown on a [Form].
 * A full calendar block is created in order to view the content of a database in form of full calendar,
 * to insert new data in the database or to update existing data in the database.
 *
 * @param        title                 the title of the block
 */
open class FullCalendarBlock(title: String,
                             ident: String? = null)
  : FormBlock(1, 1, title, ident) {

  var dateField: MustFillFormField<*>? = null
  var fromTimeField: MustFillFormField<*>? = null
  var toTimeField: MustFillFormField<*>? = null
  var fromField: MustFillFormField<*>? = null
  var toField: MustFillFormField<*>? = null

  /**
   * Creates and returns a date mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a date mustfill field.
   */
  fun date(position: FormPosition, init: MustFillFormField<Date>.() -> Unit): MustFillFormField<Date> =
    date(Domain(), position, init)

  /**
   * Creates and returns a date mustfill field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  inline fun <reified T: Date> date(domain: Domain<T>,
                                    position: FormPosition,
                                    init: MustFillFormField<T>.() -> Unit): MustFillFormField<T> {

    return mustFill(domain, position, init).also { field ->
      dateField = field
    }
  }

  /**
   * Creates and returns a time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  fun fromTime(position: FormPosition, init: MustFillFormField<Time>.() -> Unit): MustFillFormField<Time> =
    fromTime(Domain(), position, init)

  /**
   * Creates and returns a time mustfill field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  inline fun <reified T: Time> fromTime(domain: Domain<T>,
                                        position: FormPosition,
                                        init: MustFillFormField<T>.() -> Unit): MustFillFormField<T> {

    return mustFill(domain, position, init).also { field ->
      fromTimeField = field
    }
  }

  /**
   * Creates and returns a time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  fun toTime(position: FormPosition, init: MustFillFormField<Time>.() -> Unit): MustFillFormField<Time> =
    toTime(Domain(), position, init)

  /**
   * Creates and returns a time mustfill field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  inline fun <reified T: Time> toTime(domain: Domain<T>,
                                      position: FormPosition,
                                      init: MustFillFormField<T>.() -> Unit): MustFillFormField<T> {

    return mustFill(domain, position, init).also { field ->
      toTimeField = field
    }
  }

  /**
   * Creates and returns a Time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  fun from(position: FormPosition, init: MustFillFormField<Timestamp>.() -> Unit): FormField<Timestamp> =
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
  inline fun <reified T: Timestamp> from(domain: Domain<T>,
                                         position: FormPosition,
                                         init: MustFillFormField<T>.() -> Unit): FormField<T> {
    return mustFill(domain, position, init).also { field ->
      fromField = field
    }
  }

  /**
   * Creates and returns a Time mustfill field.
   *
   * @param init    initialization method to initialize the field.
   * @return a mustfill field.
   */
  fun to(position: FormPosition, init: MustFillFormField<Timestamp>.() -> Unit): FormField<Timestamp> =
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
  inline fun <reified T: Timestamp> to(domain: Domain<T>,
                                       position: FormPosition,
                                       init: MustFillFormField<T>.() -> Unit): FormField<T> {
    return mustFill(domain, position, init).also { field ->
      toField = field
    }
  }

  /**
   * Sets the block into insert mode.
   * @exception        VException        an exception may occur during DB access
   */
  override fun insertMode() {
    vBlock.insertMode()
  }

  fun goToDate(date: Date) {
    model.goToDate(date)
  }

  fun getSelectedDate(): Date? = model.getSelectedDate()

  /**
   * Refreshes the full calendar block data.
   */
  fun refreshEntries() {
    model.refreshEntries()
  }

  // ----------------------------------------------------------------------
  // BLOCK MODEL
  // ----------------------------------------------------------------------

  val model: VFullCalendarBlock get() = (vBlock as VFullCalendarBlock)

  /** Returns block model */
  override fun getBlockModel(vForm: VForm, source: String?): VBlock {
    val fullCalendarModel = FullCalendarBlockModel(vForm, this, source)

    vBlock = fullCalendarModel

    return fullCalendarModel
  }
}
