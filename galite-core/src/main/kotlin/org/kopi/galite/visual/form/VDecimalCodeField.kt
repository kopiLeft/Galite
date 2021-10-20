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

package org.kopi.galite.visual.form

import java.math.BigDecimal

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.kopi.galite.visual.list.VDecimalCodeColumn
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.util.base.InconsistencyException

/**
 * Constructor
 *
 * @param     ident           the identifier of the type in the source file
 * @param     source          the qualified name of the source file defining the list
 */
open class VDecimalCodeField(bufferSize: Int,
                             ident: String,
                             source: String,
                             names: Array<String>,
                             private val codes: Array<Decimal?>) : VCodeField(bufferSize, ident, source, names) {

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn =
          VDecimalCodeColumn(getHeader(), null, null, labels, codes, getPriority() >= 0)


  /**
   * Returns the array of codes.
   */
  @Suppress("UNCHECKED_CAST")
  override fun getCodes(): Array<Any?> = codes as Array<Any?>

  // ----------------------------------------------------------------------
  // FIELD VALUE ACCESS
  // ----------------------------------------------------------------------
  /**
   * Returns the sum of the field values of all records.
   *
   * @param     exclude         exclude the current record
   * @return    the sum of the field values, null if none is filled.
   */
  fun computeSum(exclude: Boolean): Decimal? {
    var sum: Decimal? = null

    for (i in 0 until block!!.bufferSize) {
      if (block!!.isRecordFilled(i)
              && !isNull(i)
              && (!exclude || i != block!!.activeRecord)) {
        if (sum == null) {
          sum = Decimal(0.0)
        }
        sum = sum + getDecimal(i)
      }
    }
    return sum
  }

  /**
   * Returns the sum of the field values of all records.
   *
   * @return    the sum of the field values, null if none is filled.
   */
  fun computeSum(): Decimal? = computeSum(false)

  /**
   * Returns the sum of every filled records in block.
   */
  open fun getSum(): Decimal {
    val sum = computeSum()

    return sum ?: Decimal(0.0)
  }

  /**
   * Sets the field value of given record to a decimal value.
   */
  override fun setDecimal(r: Int, v: Decimal?) {
    if (v == null) {
      setCode(r, -1)
    } else {
      var code = -1 // cannot be null
      var i = 0
      while (code == -1 && i < codes.size) {
        if (v == codes[i]) {
          code = i
        }
        i++
      }
      if (code == -1) {
        throw InconsistencyException("bad code value $v field $name")
      }
      setCode(r, code)
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setDecimal(r, v as? Decimal)
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param    result       the result row
   * @param    column       the column in the tuple
   */
  override fun retrieveQuery(result: ResultRow, column: Column<*>): Any? {
    val bigDecimal = result[column] as? BigDecimal

    return if (bigDecimal == null) {
      null
    } else {
      Decimal(bigDecimal)
    }
  }

  /**
   * Returns the field value of given record as a int value.
   */
  override fun getDecimal(r: Int): Decimal = getObject(r) as Decimal

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = if (value[r] == -1) null else codes[value[r]]

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): BigDecimal? = if (value[r] == -1) null else codes[value[r]]!!.toSql()

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = Decimal::class

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------
  /**
   * Returns a string representation of a bigdecimal value wrt the field type.
   */
  override fun formatDecimal(value: Decimal): String {
    var code = -1 // cannot be null
    var i = 0

    while (code == -1 && i < codes.size) {
      if (value == codes[i]) {
        code = i
      }
      i++
    }
    if (code == -1) {
      throw InconsistencyException("bad code value $value field $name")
    }
    return formatCode(code)
  }
}
