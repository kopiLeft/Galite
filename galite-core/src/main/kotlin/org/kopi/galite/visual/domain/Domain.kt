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

package org.kopi.galite.visual.domain

import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import kotlin.reflect.KClass

import org.joda.time.DateTime
import org.kopi.galite.type.Image
import org.kopi.galite.type.Month
import org.kopi.galite.type.Week
import org.kopi.galite.visual.chart.*
import org.kopi.galite.visual.dsl.chart.ChartDimension
import org.kopi.galite.visual.dsl.chart.ChartMeasure
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.form.FormField
import org.kopi.galite.visual.dsl.report.ReportField
import org.kopi.galite.visual.form.*
import org.kopi.galite.visual.report.*
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.dsl.pivottable.PivotTableField
import org.kopi.galite.visual.pivottable.VPivotTableColumn

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
  private var constraint: Constraint<T>? = null
  val ident: String = if(this::class.qualifiedName == null) "" else this::class.java.simpleName
  val source: String =
    if(this::class.qualifiedName == null) {
      ""
    } else {
      javaClass.`package`.name.replace(".", "/") + File.separatorChar + javaClass.simpleName
    }

  /**
   * Sets the minimum value of a number domain.
   */
  var <U> Domain<U>.min : U? where U : Comparable<U>?, U : Number?
    get() = min
    set(value) {
      min = value
    }

  /**
   * Sets the maximum value of a number domain.
   */
  var <U> Domain<U>.max : U? where U : Comparable<U>?, U : Number?
    get() = max
    set(value) {
      max = value
    }

  /** the minimum value that cannot exceed  */
  private var min : T? = null

  /** the maximum value that cannot exceed  */
  private var max : T? = null

  /**
   * Determines the field data type
   */
  var kClass: KClass<*>? = null

  /**
   * Defines a [constraint] that the field value should verify. Otherwise an error [message] is displayed to the user.
   *
   * @param message the error message to display.
   * @param constraint the constraint that the field value should verify.
   */
  fun check(message: String? = null, constraint: (value: T) -> Boolean) {
    this.constraint = Constraint(message, constraint)
  }

  /**
   * Builds the form field model
   */
  open fun buildFormFieldModel(formField: FormField<T>): VField {
    val model = with(formField) {
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
        BigDecimal::class -> VDecimalField(block.buffer,
                                           width!!,
                                           height ?: 6,
                                           height == null,
                                           min as? BigDecimal,
                                           max as? BigDecimal)
        Boolean::class -> VBooleanField(block.buffer)
        org.joda.time.LocalDate::class, LocalDate::class, java.sql.Date::class, java.util.Date::class ->
          VDateField(block.buffer)
        Month::class -> VMonthField(block.buffer)
        Week::class -> VWeekField(block.buffer)
        org.joda.time.LocalTime::class, LocalTime::class -> VTimeField(block.buffer)
        Instant::class, LocalDateTime::class, DateTime::class -> VTimestampField(block.buffer, kClass)
        Image::class -> VImageField(block.buffer, width!!, height!!)
        else -> {
          if(this@Domain is TEXT) {
            VTextField(block.buffer,
                       width ?: 0,
                       height ?: 1,
                       visibleHeight ?: 0,
                       if (fixed != Fixed.UNDEFINED) fixed.value else VConstants.FDO_CONVERT_NONE,
                       styled)
          }

          throw RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
        }
      }
    }

    if (constraint != null) {
      model.constraint = constraint!!.constraint as (value: Any) -> Boolean
      model.constraintMessage = constraint!!.message
    }

    return model
  }

  /**
   * Builds the chart dimension model
   */
  open fun buildDimensionModel(dimension: ChartDimension<*>, format: VColumnFormat?): VDimension {
    return with(dimension) {
      when (kClass) {
        Int::class, Long::class -> VIntegerDimension(ident, format)
        BigDecimal::class -> VDecimalDimension(ident, format, height ?: 6, true)
        String::class -> VStringDimension(ident, format)
        Boolean::class -> VBooleanDimension(ident, format)
        org.joda.time.LocalDate::class, LocalDate::class, java.sql.Date::class, java.util.Date::class ->
          VDateDimension(ident, format)
        Month::class -> VMonthDimension(ident, format)
        Week::class -> VWeekDimension(ident, format)
        org.joda.time.LocalTime::class, LocalTime::class -> VTimeDimension(ident, format)
        Instant::class, LocalDateTime::class, DateTime::class -> VTimestampDimension(ident, format)
        else -> throw java.lang.RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }
    }
  }

  /**
   * Builds the chart measure model
   */
  open fun buildMeasureModel(measure: ChartMeasure<*>): VMeasure {
    return with(measure) {
      when (kClass) {
        Int::class, Long::class -> VIntegerMeasure(ident)
        BigDecimal::class -> VDecimalMeasure(ident, height!!)
        else -> throw java.lang.RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }
    }
  }

  /**
   * Builds the report column model
   */
  open fun buildReportFieldModel(field: ReportField<*>, function: VCalculateColumn?, format: VCellFormat?): VReportColumn {
    return with(field) {
      when (kClass) {
        Int::class, Long::class ->
          VIntegerColumn(ident, options, align.value, groupID, function, width ?: 0, format)
        String::class ->
          VStringColumn(
            ident, options, align.value, groupID, function, width ?: 0,
            height ?: 0, format
          )
        BigDecimal::class ->
          VDecimalColumn(
            ident, options, align.value, groupID, function, width ?: 0,
            height ?: 0, format
          )
        Boolean::class ->
          VBooleanColumn(ident, options, align.value, groupID, function, format)
        org.joda.time.LocalDate::class, LocalDate::class, java.sql.Date::class, java.util.Date::class ->
          VDateColumn(ident, options, align.value, groupID, function, width ?: 0, format)
        Month::class ->
          VMonthColumn(ident, options, align.value, groupID, function, format)
        Week::class ->
          VWeekColumn(ident, options, align.value, groupID, function, format)
        org.joda.time.LocalTime::class, LocalTime::class ->
          VTimeColumn(ident, options, align.value, groupID, function, format)
        Instant::class, LocalDateTime::class, DateTime::class ->
          VTimestampColumn(ident, options, align.value, groupID, function, format)
        else -> throw java.lang.RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }
    }
  }

  /**
   * Builds the pivot table column model
   */
  open fun buildPivotTableFieldModel(field: PivotTableField<*>, position: Dimension.Position?): VPivotTableColumn {
    return with(field) {
      when (kClass) {
        Int::class, Long::class, String::class, BigDecimal::class, Boolean::class, org.joda.time.LocalDate::class,
        LocalDate::class, java.sql.Date::class, java.util.Date::class, Month::class, Week::class, org.joda.time.LocalTime::class,
        LocalTime::class, Instant::class, LocalDateTime::class, DateTime::class ->
          VPivotTableColumn(ident, position)

        else -> throw java.lang.RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }
    }
  }

  /**
   * Returns the default alignment
   */
  val defaultAlignment: Int
    get() = if (kClass == BigDecimal::class) {
      VConstants.ALG_RIGHT
    } else {
      VConstants.ALG_LEFT
    }

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------
  fun hasSize(): Boolean =
          when (kClass) {
            BigDecimal::class, Int::class, Long::class, String::class -> true
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
