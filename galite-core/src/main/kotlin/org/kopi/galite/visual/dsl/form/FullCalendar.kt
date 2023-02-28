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
import java.util.*
import org.kopi.galite.visual.DefaultActor
import org.kopi.galite.visual.cross.VFullCalendarForm

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VDateField
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.VTimeField
import org.kopi.galite.visual.form.VTimestampField
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock

/**
 * A block is a set of data which are stocked in the database and shown on a [Form].
 * A full calendar block is created in order to view the content of a database in form of full calendar,
 * to insert new data in the database or to update existing data in the database.
 *
 * @param        title                 the title of the block
 */
open class FullCalendar(title: String) : VFullCalendarBlock(title, 1, 1) {

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
  inline fun <reified T: LocalDate> date(domain: Domain<T>,
                                    position: FormPosition,
                                    init: FormField<T>.() -> Unit): FormField<T> {

    return mustFill(domain, position, init).also { field ->
      dateField = field
      dateFieldModel = field.vField as VDateField
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
  inline fun <reified T: LocalTime> fromTime(domain: Domain<T>,
                                        position: FormPosition,
                                        init: FormField<T>.() -> Unit): FormField<T> {

    return mustFill(domain, position, init).also { field ->
      fromTimeField = field
      fromTimeFieldModel = field.vField as VTimeField
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
  inline fun <reified T: LocalTime> toTime(domain: Domain<T>,
                                      position: FormPosition,
                                      init: FormField<T>.() -> Unit): FormField<T> {

    return mustFill(domain, position, init).also { field ->
      toTimeField = field
      toTimeFieldModel = field.vField as VTimeField
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
  inline fun <reified T: Instant> from(domain: Domain<T>,
                                         position: FormPosition,
                                         init: FormField<T>.() -> Unit): FormField<T> {
    return mustFill(domain, position, init).also { field ->
      fromField = field
      fromFieldModel = field.vField as VTimestampField
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
  inline fun <reified T: Instant> to(domain: Domain<T>,
                                       position: FormPosition,
                                       init: FormField<T>.() -> Unit): FormField<T> {
    return mustFill(domain, position, init).also { field ->
      toField = field
      toFieldModel = field.vField as VTimestampField
    }
  }

  override fun setInfo(form: VForm) {
    fields.forEach {
      it.setInfo(super.source)
    }
  }

  fun buildFullCalendarForm() {
    fullCalendarForm = object : VFullCalendarForm() {

      init {
        init()
        initDefaultActors()
        initDefaultCommands()
      }

      override val locale: Locale?
        get() = form.locale
      override val fullCalendarBlock: VFullCalendarBlock
        get() = this@FullCalendar

      fun init() {
        val vSimpleBlock = BlockModel(this, this@FullCalendar, source)

        vSimpleBlock.setInfo(pageNumber, this)
        vSimpleBlock.initIntern()

        val defaultActors = form.actors.filter { actor ->
          actor is DefaultActor &&
                  (actor.code == CMD_AUTOFILL
                          || actor.code == CMD_EDITITEM
                          || actor.code == CMD_EDITITEM_S
                          || actor.code == CMD_NEWITEM)

        }.toTypedArray()
        addActors(defaultActors)

        addBlock(vSimpleBlock)
        source = vSimpleBlock.source
        setTitle(vSimpleBlock.title)
      }

      override fun formClassName(): String = block.javaClass.name
    }
  }

  // ----------------------------------------------------------------------
  // BLOCK MODEL
  // ----------------------------------------------------------------------


  override fun getBlockModel(vForm: VForm): VBlock {
    val model = super.getBlockModel(vForm)
    buildFullCalendarForm()
    return model
  }
}
