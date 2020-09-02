/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

package org.kopi.util.base

import org.kopi.galite.exceptions.InconsistencyException
import java.util.*

abstract class Utils {

  /*
   * Returns a string representation of an integer, padding it
   * with leading zeroes to the specified length.
   */
  open fun formatInteger(value: Int, length: Int): String? {
    val buffer: StringBuffer = StringBuffer(length)
    for (i in length - ("" + value).length downTo 1) {
      buffer.append("0")
    }
    buffer.append(value)
    return buffer.toString()
  }

  /**
   * Check if an assertion is valid
   *
   * @exception        RuntimeException        the entire token reference
   */
  fun verify(b: Boolean) {
    if (!b) {
      throw InconsistencyException()
    }
  }

  /**
   * Creates a list and fills it with the elements of the specified array.
   *
   * @param        array                the array of elements
   */
  open fun toList(array: Array<Any?>?): List<*>? {
    return if (array == null) {
      Vector<Any?>()
    } else {
      array.toList()
    }
  }

  /**
   * Creates a typed array from a list.
   *
   * @param        list                the list containing the elements
   * @param        type                the type of the elements
   */
  open fun toArray(list: List<*>?, type: Class<*>?): Array<Any>? {
    return if (list != null && list.size > 0) {
      var array: Array<Any> = emptyArray()
      try {
        array = list.toTypedArray() as Array<Any>
      } catch (e: ArrayStoreException) {
        System.err.println("Array was:" + list[0])
        System.err.println("New type :" + array.javaClass)
        throw e
      }
      array
    } else {
      java.lang.reflect.Array.newInstance(type, 0) as Array<Any>
    }
  }

  /**
   * Creates a int array from a list.
   *
   * @param        list                the list containing the elements
   * @param        type                the type of the elements
   */
  open fun toIntArray(list: List<*>?): IntArray? {
    return if (list != null && list.size > 0) {
      val array = IntArray(list.size)
      for (i in array.indices.reversed()) {
        array[i] = (list[i] as Int).toInt()
      }
      array
    } else {
      IntArray(0) // $$$ static ?
    }
  }

}
