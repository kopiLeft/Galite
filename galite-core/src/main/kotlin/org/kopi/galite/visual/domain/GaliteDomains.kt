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

import org.joda.time.DateTime
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.type.Image
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.type.Week

/**
 * Integer field type is LONG is used to insert integers.
 * Only the text width is to be defined.
 */
class LONG(width: Int) : Domain<Long>(width)

class INT(width: Int) : Domain<Int>(width)

/**
 * A STRING is used to enter characters which can be either letters, numbers or both.
 * The width has always to be given.
 * Moreover, you can optionally indicate how many rows it will contain and how many will finally be displayed
 * on the form.
 * If these optional arguments are used, you have to indicate the carriage return method by specifying either
 * the FIXED ON or the FIXED OFF option.
 */
class STRING(width: Int,
             height: Int,
             visibleHeight: Int,
             fixed: Fixed,
             convert: Convert = Convert.NONE,
             styled: Boolean = false) :
  Domain<String>(width, height, visibleHeight) {

  constructor(width: Int,
              convert: Convert = Convert.NONE,
              styled: Boolean = false) :
          this(width, 1, 0, Fixed.UNDEFINED, convert, styled)

  constructor(width: Int,
              height: Int,
              fixed: Fixed,
              convert: Convert = Convert.NONE,
              styled: Boolean = false) :
          this(width, height, 0, fixed, convert, styled)

  init {
    this.styled = styled
    this.fixed = fixed
    this.convert = convert
  }
}

/**
 * A Text and a [STRING] are similar apart from the fact that in a text,
 * two parameters have always to be given: namely the width and the height of the field
 * whereas you only need to determine the width in a string.
 */
class TEXT(width: Int,
           height: Int,
           visibleHeight: Int,
           fixed: Fixed = Fixed.UNDEFINED,
           styled: Boolean = false) :
  Domain<String>(width, height, visibleHeight) {

  constructor(width: Int,
              height: Int,
              fixed: Fixed = Fixed.UNDEFINED,
              styled: Boolean = false) :
          this(width, height, 0, fixed, styled)

  init {
    this.styled = styled
    this.fixed = fixed
  }
}

/**
 * A DECIMAL is used to insert numbers, integers, decimal numbers.
 * The maximal width has to be determined for all them.
 * The maximal scale i.e the number of characters standing after the comma has also to be defined.
 * Also the comma has to be counted as a character.
 */
class DECIMAL(width: Int, scale: Int) : Domain<Decimal>(width, scale)

/**
 * Fraction numbers are [DECIMAL] numbers.
 * The maximal width has to be determined for all them.
 * Also the comma has to be counted as a character.
 * Only the width is to be defined in a FRACTION.
 */
class FRACTION(width: Int) : Domain<Decimal>(width)

/**
 * In a BOOL you have to assign a Boolean value to the item you have entered.
 * Boolean values are values like "True" or "False" and "Yes" or "No".
 */
object BOOL : Domain<Boolean>()

object DATE : Domain<Date>()

object MONTH : Domain<Month>()

object WEEK : Domain<Week>()

object TIMESTAMP : Domain<Timestamp>()

object DATETIME : Domain<DateTime>()

object TIME : Domain<Time>()

/**
 * This field type is used to insert an illustration or a picture.
 * When introducing an IMAGE, you have to determine its width and height.
 * These values have to be integers and are measured in pixel.
 * In this case, the two attributes are compulsory.
 * The field will look like a file chooser that lets you choose and image file to show in the field.
 */
class IMAGE(width: Int, height: Int) : Domain<Image>(width, height)
