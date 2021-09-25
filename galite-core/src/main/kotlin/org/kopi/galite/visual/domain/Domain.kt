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

package org.kopi.galite.visual.domain

import org.joda.time.DateTime
import kotlin.reflect.KClass

import org.kopi.galite.visual.common.LocalizationWriter
import org.kopi.galite.visual.form.VBooleanField
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VDateField
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VFixnumField
import org.kopi.galite.visual.form.VImageField
import org.kopi.galite.visual.form.VIntegerField
import org.kopi.galite.visual.form.VMonthField
import org.kopi.galite.visual.form.VStringField
import org.kopi.galite.visual.form.VTimeField
import org.kopi.galite.visual.form.VTimestampField
import org.kopi.galite.visual.form.VWeekField
import org.kopi.galite.visual.form.dsl.FormField
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.type.Image
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.type.Week

/**
 * A domain is a data type with predefined list of allowed values.
 *
 * @param width             the width in char of this field
 * @param height            the height in char of this field
 * @param visibleHeight     the visible height in char of this field.
 */
open class Domain<T>(val width: Int? = null,
                     val height: Int? = null,
                     val visibleHeight: Int? = null) {

  protected var isFraction = false
  protected var styled: Boolean = false
  protected var fixed: Fixed = Fixed.UNDEFINED
  protected var convert: Convert = Convert.NONE
  val ident: String = this::class.java.simpleName

  /**
   * Determines the field data type
   */
  var kClass: KClass<*>? = null

  /**
   * Builds the form field model
   */
  open fun buildFieldModel(formField: FormField<T>): VField {
    return with(formField) {
      when (kClass) {
        Int::class, Long::class -> VIntegerField(block.buffer,
                                                 width ?: 0,
                                                 min as? Int ?: Int.MIN_VALUE,
                                                 max as? Int ?: Int.MAX_VALUE)
        String::class -> {
          if(visibleHeight != height) {
            VStringField(block.buffer,
                         width ?: 0,
                         height ?: 1,
                         visibleHeight ?: 0,
                         (fixed.value or convert.value) and
                                 (VConstants.FDO_CONVERT_MASK or VConstants.FDO_DYNAMIC_NL),
                         styled)
          } else {
            VStringField(block.buffer,
                         width ?: 0,
                         height ?: 1,
                         (fixed.value or convert.value) and
                                 VConstants.FDO_CONVERT_MASK or
                                 VConstants.FDO_DYNAMIC_NL,
                         styled)
          }
        }
        Decimal::class -> VFixnumField(block.buffer,
                                       width!!,
                                       height ?: 6,
                                       height == null,
                                       min as? Decimal,
                                       max as? Decimal)
        Boolean::class -> VBooleanField(block.buffer)
        Date::class, java.util.Date::class -> VDateField(block.buffer)
        Month::class -> VMonthField(block.buffer)
        Week::class -> VWeekField(block.buffer)
        Time::class -> VTimeField(block.buffer)
        Timestamp::class, DateTime::class -> VTimestampField(block.buffer)
        Image::class -> VImageField(block.buffer, width!!, height!!)
        else -> throw RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }
    }
  }

  /**
   * Returns the default alignment
   */
  val defaultAlignment: Int
    get() = if (kClass == Decimal::class) {
      VConstants.ALG_RIGHT
    } else {
      VConstants.ALG_LEFT
    }

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------
  fun hasSize(): Boolean =
          when (kClass) {
            Decimal::class, Int::class, Long::class, String::class -> true
            else -> false
          }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  fun genLocalization(writer: LocalizationWriter) {
    writer.genTypeDefinition(ident, this)
  }

  open fun genTypeLocalization(writer: LocalizationWriter) {
    // DO NOTHING !
  }
}
