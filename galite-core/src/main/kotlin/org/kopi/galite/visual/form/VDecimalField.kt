/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.kopi.galite.type.format
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.list.VDecimalColumn
import org.kopi.galite.visual.list.VListColumn
import java.math.BigDecimal
import kotlin.math.max
import kotlin.reflect.KClass

/**
 *
 * @param    digits      The digits after dot
 * @param    maxScale    The maximum scale to be used for this field
 * @param    isFraction    is true if its is a isFraction field
 * @param    minval      The min permitted value
 * @param    maxval      The max permitted value
 *
 */
class VDecimalField(val bufferSize: Int,
                    private val digits: Int,
                    maxScale: Int,
                    val isFraction: Boolean,
                    minValue: BigDecimal?,
                    maxValue: BigDecimal?)
  : VField(computeWidth(digits, maxScale, minValue, maxValue), 1) {
  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  constructor(bufferSize: Int,
              digits: Int,
              maxScale: Int,
              minval: String?,
              maxval: String?,
              fraction: Boolean)
          : this(bufferSize,
                 digits,
                 maxScale,
                 fraction,
                 minval?.let { BigDecimal(it) },
                 maxval?.let { BigDecimal(it) }) {
  }

  /**
   * just after loading, construct record
   */
  override fun build() {
    super.build()
    currentScale = IntArray(2 * bufferSize) { maxScale }
  }

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String {

    var nines: Long = 1

    var min = BigDecimal(Int.MIN_VALUE.toDouble())
    var max = BigDecimal(Int.MAX_VALUE.toDouble())
    for (i in width downTo 2) {
      if (i % 3 != 0) {
        nines *= 10
      }
    }
    var big = BigDecimal((nines - 1).toDouble())

    big = big.setScale(height)
    var mbig = BigDecimal(-(nines / 10 - 1).toDouble())

    mbig = mbig.setScale(height)
    max = if (max > big) max else big
    min = if (min < mbig) min else mbig
    return VlibProperties.getString("decimal-type-field", arrayOf(min, max))
  }

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Decimal")

  override fun isNumeric(): Boolean = true

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn {
    return VDecimalColumn(getHeader(),
                          null,
                          null,
                          align,
                          width,
                          maxScale,
                          getPriority() >= 0)
  }

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    if (s.length > width) {
      return false
    }
    s.forEach {
      if (!((it in '0'..'9')
                      || (it == '.') || (it == '-') || (it == ' ')
                      || (it == ',') || (it == '/'))) {
        return false
      }
    }
    return true
  }

  /**
   * verify that value is valid (on exit)
   * @exception         org.kopi.galite.visual.VException       an exception may be raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {
    val modifiedS = s as? String
    val scale: Int = currentScale[rec]

    if ((modifiedS == "")) {
      setNull(rec)
    } else {
      val v: BigDecimal?
      try {
        v = scanDecimal(modifiedS)
      } catch (e: NumberFormatException) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00006"))
      }
      if (v != null) {
        if (v.scale() > scale) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00011", arrayOf(scale)))
        }
        if (v.compareTo(minValue) == -1) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00012", arrayOf(minValue)))
        }
        if (v.compareTo(maxValue) == 1) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00009", arrayOf(maxValue)))
        }
        if (toText(v.setScale(maxScale)).length > width) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00010"))
        }

        checkConstraint(v)
      }
      setDecimal(rec, v)
    }
  }

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = BigDecimal::class

  // ----------------------------------------------------------------------
  // FIELD VALUE ACCESS
  // ----------------------------------------------------------------------

  /**
   * Returns the sum of the field values of all records.
   *
   * @param     exclude         exclude the current record
   * @return    the sum of the field values, null if none is filled.
   */
  fun computeSum(exclude: Boolean): BigDecimal? {
    var sum: BigDecimal? = null

    for (i in 0 until block!!.bufferSize) {
      if ((!isNullImpl(i)
                      && block!!.isRecordFilled(i)
                      && (!exclude || i != block!!.activeRecord))) {
        if (sum == null) {
          sum = BigDecimal(0.0)
        }
        sum += getDecimal(i)!!
      }
    }
    return sum
  }

  /**
   * Returns the sum of the field values of all records.
   *
   * @param     exclude         exclude the current record
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  fun computeSum(exclude: Boolean, coalesceValue: BigDecimal): BigDecimal =
          computeSum(exclude)?.let { computeSum(exclude) } ?: coalesceValue

  /**
   * Returns the sum of the field values of all records.
   *
   * @return    the sum of the field values, null if none is filled.
   */
  fun computeSum(): BigDecimal? = computeSum(false)

  /**
   * Returns the sum of every filled records in block
   *
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  fun computeSum(coalesceValue: BigDecimal): BigDecimal = computeSum(false, coalesceValue)

  /**
   * Returns the current scale for the specified record.
   *
   * @param     record          the record value.
   * @return    the scale value.
   */
  fun getScale(record: Int): Int = currentScale[record]

  /**
   * Returns the current scale for the current record.
   *
   * @return    the scale value.
   */
  fun getScale(): Int = getScale(block!!.activeRecord)

  /**
   * Sets the scale value for the specified record.
   *
   * @param     scale           the scale value.
   * @param     record          the record value.
   */
  fun setScale(record: Int, scale: Int) {
    if (scale > maxScale) {
      throw InconsistencyException(MessageCode.getMessage("VIS-00060", scale.toString(), maxScale.toString()))
    }
    currentScale[record] = scale
  }

  /**
   * Sets the scale value for the current record.
   *
   * @param     scale           the scale value.
   */
  fun setScale(scale: Int) {
    setScale(block!!.currentRecord, scale)
  }

  /**
   * Clears the field.
   *
   * @param     r       the record number.
   */
  override fun clear(r: Int) {
    super.clear(r)

    for (i in currentScale.indices) {
      currentScale[i] = maxScale
    }
  }

  /*
   * ----------------------------------------------------------------------
   * Interface bd/Triggers
   * ----------------------------------------------------------------------
   */

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setDecimal(r, null)
  }

  /**
   * Sets the field value of given record to a Decimal value.
   */
  override fun setDecimal(r: Int, v: BigDecimal?) {
    // trails (backup) the record if necessary
    var modifiedV = v

    if ((isChangedUI
                    || (value[r] == null && modifiedV != null)
                    || (value[r] != null && value[r] != modifiedV))) {
      trail(r)
      if (modifiedV != null) {
        if (modifiedV.scale() != currentScale[r]) {
          modifiedV = modifiedV.setScale(currentScale[r])
        }
        if (modifiedV!!.compareTo(minValue) == -1) {
          modifiedV = minValue
        } else if (modifiedV.compareTo(maxValue) == 1) {
          modifiedV = maxValue
        }
      }

      // set value in the defined row
      value[r] = modifiedV
      // inform that value has changed
      setChanged(r)
    }
    checkCriticalValue()
  }

  /**
   * Sets the field value of given record.
   *
   * Warning:   This method will become inaccessible to kopi users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    // !!! HACK for Oracle
    if (v != null && (v is Int)) {
      setDecimal(r, BigDecimal(v.toDouble()))
    } else {
      setDecimal(r, v as BigDecimal?)
    }
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   *
   * @param    result       the result row
   * @param    column       the column in the tuple
   */
  override fun retrieveQuery(result: ResultRow, column: Column<*>): Any? = result[column] as? BigDecimal

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a Decimal value.
   */
  override fun getDecimal(r: Int): BigDecimal? = getObject(r) as? BigDecimal

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String {
    if (o == null) {
      return ""
    }
    return toText((o as BigDecimal).setScale(currentScale[0]))
  }

  override fun toObject(s: String): Any? {
    val scale: Int = currentScale[0]

    if ((s == "")) {
      return null
    } else {
      val v: BigDecimal?

      try {
        v = scanDecimal(s)
      } catch (e: NumberFormatException) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00006"))
      }
      if (v != null) {
        if (v.scale() > scale) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00011", arrayOf(scale)))
        }
        if (v.compareTo(minValue) == -1) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00012", arrayOf(minValue)))
        }
        if (v.compareTo(maxValue) == 1) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00009", arrayOf(maxValue)))
        }
        if (toText(v.setScale(maxScale)).length > width) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00010"))
        }
      }
      return v
    }
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String {
    if (value[r] == null) {
      return ""
    }
    var res: String = toText(value[r]!!.setScale(currentScale[r]))

    // append spaces until the max scale is reached to make commas aligned.
    // append an extra space to replace the missing comma if the current scale is zero.
    // FIXME!! The added spaces are actually being deleted in Vaadin implementation
    // FIXME!! because the decimal field doesn't defines a pattern that doesn't accept spaces.
    // FIXME!! Do we really need to do this?
    if (block!!.isMulti()) {
      for (i in (if (currentScale[r] == 0) -1 else currentScale[r]) until maxScale) {
        res += " "
      }
    }
    return res
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): BigDecimal? {
    return value[r]
  }

  /**
   * Copies the value of a record to another
   */
  override fun copyRecord(f: Int, t: Int) {
    val oldValue = value[t]

    value[t] = value[f]
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < block!!.bufferSize
            && (((oldValue != null && value[t] == null)
                    || (oldValue == null && value[t] != null)
                    || (oldValue != null && oldValue != value[t])))) {
      fireValueChanged(t)
    }
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a big decimal value wrt the field type.
   */
  fun formatDecimal(value: BigDecimal): String {
    return toText(value.setScale(currentScale[block!!.activeRecord]))
  }

  /**
   * Returns the string representation in human-readable format.
   */
  fun toText(v: BigDecimal): String {
    return if (!isFraction) {
      v.format()
    } else {
      toFraction(v.format())
    }
  }

  private fun toFraction(str: String): String {
    val dot: Int

    if ((str.indexOf(',').also { dot = it }) == -1) {
      return str
    }
    val precomma = str.substring(0, dot)
    val fract = Integer.valueOf(str.substring(dot + 1, str.length)).toInt()

    when {
      fract * 64 % 1000000 != 0 -> return str
      fract == 0 -> return precomma
      else -> {
        var den = 64
        var num = (fract * den) / 1000000

        while (num % 2 == 0) {
          num /= 2
          den /= 2
        }
        return when (precomma) {
          "0" -> "$num/$den"
          "-0" -> "-$num/$den"
          else -> "$precomma $num/$den"
        }
      }
    }
  }

  private fun checkCriticalValue() {
    if (value[0] != null && value[0]!! < criticalMinValue) {
      setHasCriticalValue(true)
      return
    }
    if (value[0] != null && value[0]!! > criticalMaxValue) {
      setHasCriticalValue(true)
      return
    }
    setHasCriticalValue(false)
  }

  private fun setHasCriticalValue(critical: Boolean) {
    getDisplay()?.let {
      (it as UTextField).setHasCriticalValue(critical)
    }
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private var fieldMaxScale = maxScale

  // dynamic data
  var minValue = minValue?.setScale(maxScale) ?: - calculateUpperBound(digits, maxScale)
    private set
  var maxValue = maxValue?.setScale(maxScale) ?: calculateUpperBound(digits, maxScale)
    private set

  // number of digits after dot
  private lateinit var currentScale: IntArray


  /**
   * The maxScale value for the current record.
   */
  var maxScale: Int = maxScale
    set(scale) {
      // dynamic maxScale mustn't exceed the maxScale defined in the field declaration (fieldMaxScale).
      if (scale > fieldMaxScale) {
        throw InconsistencyException(
          MessageCode.getMessage("VIS-00060",
                                 scale,
                                 fieldMaxScale.toString())
        )
      }
      field = scale

      if (minValue.scale() > field) {
        minValue = minValue.setScale(field)
      }

      if (maxValue.scale() > field) {
        maxValue = maxValue.setScale(field)
      }

      //records scale must be <= maxScale
      for (i in currentScale.indices) {
        if (currentScale[i] > field) {
          currentScale[i] = field
        }
      }
    }

  private var value: Array<BigDecimal?> = arrayOfNulls(2 * bufferSize)

  protected var criticalMinValue = this.minValue

  protected var criticalMaxValue = this.maxValue

  companion object {

    /**
     * Parses the string argument as a decimal number in human-readable format.
     */
    private fun scanDecimal(str: String?): BigDecimal? {
      var negative = false
      var state = 0
      var scale = 0
      var value: Long = 0
      var num: Long = 0
      var den: Long = 0

      if ((str == "")) {
        return null
      }
      for (i in str!!.indices) {
        // skip dots
        if (str[i] == '.') {
          continue
        }
        when (state) {
          0 -> when {
            str[i] == ' ' -> {
              state = 0
            }
            str[i] == '+' -> {
              state = 1
            }
            str[i] == '-' -> {
              negative = true
              state = 1
            }
            str[i] == ',' -> {
              state = 3
            }
            Character.isDigit(str[i]) -> {
              value = Character.digit(str[i], 10).toLong()
              state = 2
            }
            else -> {
              throw NumberFormatException()
            }
          }
          1 -> when {
            str[i] == ' ' -> {
              state = 1
            }
            str[i] == ',' -> {
              state = 3
            }
            Character.isDigit(str[i]) -> {
              value = Character.digit(str[i], 10).toLong()
              state = 2
            }
            else -> {
              throw NumberFormatException()
            }
          }
          2 -> when {
            str[i] == ',' -> {
              state = 3
            }
            str[i] == ' ' -> {
              state = 4
            }
            str[i] == '/' -> {
              num = value
              value = 0
              state = 6
            }
            Character.isDigit(str[i]) -> {
              value = 10 * value + Character.digit(str[i], 10)
              state = 2
            }
            else -> {
              throw NumberFormatException()
            }
          }
          3 -> if (Character.isDigit(str[i])) {
            value = 10 * value + Character.digit(str[i], 10)
            scale += 1
            state = 3
          } else {
            throw NumberFormatException()
          }
          4 -> when {
            str[i] == ' ' -> {
              state = 4
            }
            Character.isDigit(str[i]) -> {
              num = Character.digit(str[i], 10).toLong()
              state = 5
            }
            else -> {
              throw NumberFormatException()
            }
          }
          5 -> when {
            str[i] == '/' -> {
              state = 6
            }
            Character.isDigit(str[i]) -> {
              num = 10 * num + Character.digit(str[i], 10)
              state = 5
            }
            else -> {
              throw NumberFormatException()
            }
          }
          6 -> when {
            str[i] == '0' -> {
              state = 6
            }
            Character.isDigit(str[i]) -> {
              den = Character.digit(str[i], 10).toLong()
              state = 7
            }
            else -> {
              throw NumberFormatException()
            }
          }
          7 -> if (Character.isDigit(str[i])) {
            den = 10 * den + Character.digit(str[i], 10)
            state = 7
          } else {
            throw NumberFormatException()
          }
          else -> throw InconsistencyException("Invalid state reached during parsing. Unexpected input encountered.")
        }
      }
      when (state) {
        0 -> return null
        2 -> {
        }
        3 ->       // remove trailing zeroes after comma
          while (scale > 0 && value % 10 == 0L) {
            value /= 10
            scale -= 1
          }
        7 -> {
          if ((num > den) || (num % 2 == 0L) || (den > 64)) {
            throw NumberFormatException()
          }
          when (den.toInt()) {
            2 -> {
              value = 10 * value + 5 * num
              scale = 1
            }
            4 -> {
              value = 100 * value + 25 * num
              scale = 2
            }
            8 -> {
              value = 1000 * value + 125 * num
              scale = 3
            }
            16 -> {
              value = 10000 * value + 625 * num
              scale = 4
            }
            32 -> {
              value = 100000 * value + 3125 * num
              scale = 5
            }
            64 -> {
              value = 1000000 * value + 15625 * num
              scale = 6
            }
            else -> throw NumberFormatException()
          }
        }
        else -> throw NumberFormatException()
      }
      return if (value == 0L) {
        BigDecimal(0.0)
      } else {
        if (negative) {
          value = -value
        }
        BigDecimal.valueOf(value, scale)
      }
    }

    /**
     * Calculates the upper bound of a decimal field : DECIMAL(digits, scale)
     *
     * @param     digits          the number of total digits.
     * @param     scale           the number of digits representing the fractional part.
     */
    fun calculateUpperBound(digits: Int, scale: Int): BigDecimal {
      val asciiBound: CharArray

      if (scale == 0) {
        asciiBound = CharArray(digits)
        for (i in 0 until digits) {
          asciiBound[i] = '9'
        }
      } else {
        asciiBound = CharArray(digits + 1)
        for (i in 0 until digits + 1) {
          asciiBound[i] = '9'
        }
        asciiBound[digits - scale] = '.'
      }
      return BigDecimal(String(asciiBound))
    }

    /**
     * Computes the the width of a decimal field : DECIMAL(digits, scale)
     *
     * @param     digits          the number of total digits.
     * @param     scale           the number of digits representing the fractional part.
     * @param     minVal          the minimal value the fixnum field can get.
     * @param     maxVal          the maximal value the fixnum field can get.
     */
    fun computeWidth(digits: Int, scale: Int, minVal: BigDecimal?, maxVal: BigDecimal?): Int {
      var upperBound = calculateUpperBound(digits, scale)
      var lowerBound = - upperBound
      if (minVal != null && minVal > lowerBound) {
        lowerBound = minVal.setScale(scale)
      }
      if (maxVal != null && maxVal < upperBound) {
        upperBound = maxVal.setScale(scale)
      }
      return max(upperBound.format().length, lowerBound.format().length)
    }

    /**
     * Computes the number of digits of a decimal field : Decimal(width, scale)
     *
     * @param     width           the width of the decimal field.
     * @param     scale           the number of digits representing the fractional part.
     */
    fun computeDigits(width: Int, scale: Int): Int {
      return when {
        scale == 0 -> {
          width - width / 4
        }
        width == scale || width == scale + 1 -> {
          scale
        }
        else -> {
          width - 1 - ((width - scale - 1) / 4)
        }
      }
    }
  }
}
