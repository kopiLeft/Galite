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

import org.kopi.galite.visual.dsl.common.CodeDescription
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.form.FormField
import org.kopi.galite.visual.form.VBooleanCodeField
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VFixnumCodeField
import org.kopi.galite.visual.form.VIntegerCodeField
import org.kopi.galite.visual.form.VStringCodeField
import org.kopi.galite.visual.type.Decimal

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
  override fun buildFieldModel(formField: FormField<T>): VField {
    return with(formField) {
      when (kClass) {
        Boolean::class -> VBooleanCodeField(block.buffer,
                                            ident,
                                            block.sourceFile,
                                            codes.map { it.ident }.toTypedArray(),
                                            codes.map { it.value as? Boolean }.toTypedArray())
        Decimal::class -> VFixnumCodeField(block.buffer,
                                           ident,
                                           block.sourceFile,
                                           codes.map { it.ident }.toTypedArray(),
                                           codes.map { it.value as? Decimal }.toTypedArray())
        Int::class, Long::class -> VIntegerCodeField(block.buffer,
                                                     ident,
                                                     block.sourceFile,
                                                     codes.map { it.ident }.toTypedArray(),
                                                     codes.map { it.value as? Int }.toTypedArray())
        String::class -> VStringCodeField(block.buffer,
                                          ident,
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
