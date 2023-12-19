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

import java.math.BigDecimal

import org.kopi.galite.visual.chart.VBooleanCodeDimension
import org.kopi.galite.visual.chart.VColumnFormat
import org.kopi.galite.visual.chart.VDimension
import org.kopi.galite.visual.chart.VDecimalCodeDimension
import org.kopi.galite.visual.chart.VDecimalCodeMeasure
import org.kopi.galite.visual.chart.VIntegerCodeDimension
import org.kopi.galite.visual.chart.VIntegerCodeMeasure
import org.kopi.galite.visual.chart.VMeasure
import org.kopi.galite.visual.chart.VStringCodeDimension
import org.kopi.galite.visual.dsl.chart.ChartDimension
import org.kopi.galite.visual.dsl.chart.ChartMeasure
import org.kopi.galite.visual.dsl.common.CodeDescription
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.form.FormField
import org.kopi.galite.visual.dsl.pivottable.PivotTableField
import org.kopi.galite.visual.dsl.report.ReportField
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.form.VBooleanCodeField
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VDecimalCodeField
import org.kopi.galite.visual.form.VIntegerCodeField
import org.kopi.galite.visual.form.VStringCodeField
import org.kopi.galite.visual.pivottable.VPivotTableColumn
import org.kopi.galite.visual.report.VBooleanCodeColumn
import org.kopi.galite.visual.report.VCalculateColumn
import org.kopi.galite.visual.report.VCellFormat
import org.kopi.galite.visual.report.VDecimalCodeColumn
import org.kopi.galite.visual.report.VIntegerCodeColumn
import org.kopi.galite.visual.report.VReportColumn
import org.kopi.galite.visual.report.VStringCodeColumn
import org.kopi.galite.visual.VColor

/**
 * Represents a code domain.
 */
open class CodeDomain<T : Comparable<T>?> : Domain<T>() {

  /**
   * Contains all values that a domain can take
   */
  val codes: MutableList<CodeDescription<T>> = mutableListOf()

  /**
   * Builds the form field model
   */
  override fun buildFormFieldModel(formField: FormField<T>): VField {
    return with(formField) {
      when (kClass) {
        Boolean::class -> VBooleanCodeField(block.buffer,
                                            this@CodeDomain.ident.ifEmpty { ident },
                                            block.sourceFile,
                                            codes.map { it.ident }.toTypedArray(),
                                            codes.map { it.value as? Boolean }.toTypedArray())
        BigDecimal::class -> VDecimalCodeField(block.buffer,
                                               this@CodeDomain.ident.ifEmpty { ident },
                                               block.sourceFile,
                                               codes.map { it.ident }.toTypedArray(),
                                               codes.map { it.value as? BigDecimal }.toTypedArray())
        Int::class, Long::class -> VIntegerCodeField(block.buffer,
                                                     this@CodeDomain.ident.ifEmpty { ident },
                                                     block.sourceFile,
                                                     codes.map { it.ident }.toTypedArray(),
                                                     codes.map { it.value as? Int }.toTypedArray())
        String::class -> VStringCodeField(block.buffer,
                                          this@CodeDomain.ident.ifEmpty { ident },
                                          block.sourceFile,
                                          codes.map { it.ident }.toTypedArray(),
                                          codes.map { it.value as? String }.toTypedArray())
        else -> throw RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }.also { field ->
        field.initLabels(codes.map { it.label }.toTypedArray())
      }
    }
  }

  /**
   * Builds the chart dimension model
   */
  override fun buildDimensionModel(dimension: ChartDimension<*>, format: VColumnFormat?): VDimension {
    return with(dimension) {
      val source = dimension.source!!

      when (kClass) {
        Boolean::class -> VBooleanCodeDimension(ident,
                                                format,
                                                this@CodeDomain.ident.ifEmpty { ident },
                                                source,
                                                codes.map { it.ident }.toTypedArray(),
                                                codes.map { it.value as? Boolean }.toTypedArray())
        BigDecimal::class -> VDecimalCodeDimension(ident,
                                                   format,
                                                   this@CodeDomain.ident.ifEmpty { ident },
                                                   source,
                                                   codes.map { it.ident }.toTypedArray(),
                                                   codes.map { it.value as? BigDecimal }.toTypedArray())
        Int::class, Long::class -> VIntegerCodeDimension(ident,
                                                         format,
                                                         this@CodeDomain.ident.ifEmpty { ident },
                                                         source,
                                                         codes.map { it.ident }.toTypedArray(),
                                                         codes.map { it.value as? Int }.toTypedArray())
        String::class -> VStringCodeDimension(ident,
                                              format,
                                              this@CodeDomain.ident.ifEmpty { ident },
                                              source,
                                              codes.map { it.ident }.toTypedArray(),
                                              codes.map { it.value as? String }.toTypedArray())
        else -> throw RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }.also {
        it.initLabels(codes.map { it.label }.toTypedArray())
      }
    }
  }

  /**
   * Builds the chart measure model
   */
  override fun buildMeasureModel(measure: ChartMeasure<*>, color: VColor?): VMeasure {
    return with(measure) {
      when (kClass) {
        BigDecimal::class -> VDecimalCodeMeasure(ident,
                                                 color,
                                                 this@CodeDomain.ident.ifEmpty { ident },
                                                 measure.source,
                                                 codes.map { it.ident }.toTypedArray(),
                                                 codes.map { it.value as? BigDecimal }.toTypedArray())
        Int::class, Long::class -> VIntegerCodeMeasure(ident,
                                                       color,
                                                       this@CodeDomain.ident.ifEmpty { ident },
                                                       measure.source,
                                                       codes.map { it.ident }.toTypedArray(),
                                                       codes.map { it.value as? Int }.toTypedArray())
        else -> throw RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }.also {
        it.initLabels(codes.map { it.label }.toTypedArray())
      }
    }
  }

  /**
   * Builds the report column model
   */
  override fun buildReportFieldModel(
    field: ReportField<*>,
    function: VCalculateColumn?,
    format: VCellFormat?
  ): VReportColumn {
    return with(field) {
      when (kClass) {
        Boolean::class -> VBooleanCodeColumn(
          ident,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source,
          options,
          align.value,
          groupID,
          function,
          width ?: 0,
          format,
          codes.map { it.ident }.toTypedArray(),
          codes.map { it.value as Boolean }.toBooleanArray()
        )
        BigDecimal::class -> VDecimalCodeColumn(
          ident,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source,
          options,
          align.value,
          groupID,
          function,
          width ?: 0,
          format,
          codes.map { it.ident }.toTypedArray(),
          codes.map { it.value as? BigDecimal }.toTypedArray()
        )
        Int::class, Long::class -> VIntegerCodeColumn(
          ident,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source!!,
          options,
          align.value,
          groupID,
          function,
          width ?: 0,
          format,
          codes.map { it.ident }.toTypedArray(),
          codes.map { it.value as Int }.toIntArray()
        )
        String::class -> VStringCodeColumn(
          ident,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source,
          options,
          align.value,
          groupID,
          function,
          width ?: 0,
          format,
          codes.map { it.ident }.toTypedArray(),
          codes.map { it.value as? String }.toTypedArray()
        )
        else -> throw RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }.also {
        it.initLabels(codes.map { it.label }.toTypedArray())
      }
    }
  }

  /**
   * Builds the pivot table column model
   */
  override fun buildPivotTableFieldModel(
    field: PivotTableField<*>,
    position: Dimension.Position?,
    format: VCellFormat?
  ): VPivotTableColumn {
    return with(field) {
      when (kClass) {
        Boolean::class -> org.kopi.galite.visual.pivottable.VBooleanCodeColumn(
          ident,
          position,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source,
          codes.map { it.ident }.toTypedArray(),
          format,
          codes.map { it.value as Boolean }.toBooleanArray()
        )
        BigDecimal::class -> org.kopi.galite.visual.pivottable.VDecimalCodeColumn(
          ident,
          position,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source,
          codes.map { it.ident }.toTypedArray(),
          format,
          codes.map { it.value as? BigDecimal }.toTypedArray()
        )
        Int::class, Long::class -> org.kopi.galite.visual.pivottable.VIntegerCodeColumn(
          ident,
          position,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source!!,
          codes.map { it.ident }.toTypedArray(),
          format,
          codes.map { it.value as Int }.toIntArray()
        )
        String::class -> org.kopi.galite.visual.pivottable.VStringCodeColumn(
          ident,
          position,
          this@CodeDomain.ident.ifEmpty { ident },
          field.source,
          codes.map { it.ident }.toTypedArray(),
          format,
          codes.map { it.value as? String }.toTypedArray()
        )
        else -> throw RuntimeException("Type ${kClass!!.qualifiedName} is not supported")
      }.also {
        it.initLabels(codes.map { it.label }.toTypedArray())
      }
    }
  }

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a field.
   *
   * @param value the value
   * @receiver the text
   */
  infix fun String.keyOf(value: T) {
    val codeDescription = CodeDescription("id$${codes.size}", this, value)
    codes.add(codeDescription)
  }

  /**
   * Returns the label assigned to a value.
   *
   * @param value the value
   * @return the text label
   */
  fun labelOf(value: T): String = codes.single { it.value == value }.label

  /**
   * Returns the value assigned to a label.
   *
   * @param label the label
   * @return the value
   */
  fun keyOf(label: String): T = codes.single { it.label == label }.value

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * Generates localization.
   */
  override fun genTypeLocalization(writer: LocalizationWriter) {
    writer.genCodeType(codes)
  }
}
