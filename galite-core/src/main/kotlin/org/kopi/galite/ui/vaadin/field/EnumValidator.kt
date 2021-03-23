/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.ui.vaadin.field

/**
 * Validator for enumeration fields

 * @param enumerations The enumeration values.
 */
class EnumValidator(private val enumerations: Array<String>?,
                    maxLength: Int)
  : AllowAllValidator(maxLength) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun validate(text: String?): Boolean {
    if (enumerations != null && text != null) {
      val s = text.toLowerCase()
      for (i in enumerations.indices) {
        if (enumerations[i].toLowerCase().startsWith(s)) {
          return true
        }
      }
      return false
    }
    return true
  }

  override fun checkType(field: InputTextField, text: String) {
    if ("" == text) {
      field.value = null
    } else {
      /*
       * -1:  no match
       * >=0: one match
       * -2:  two (or more) matches: cannot choose
       */
      var found = -1
      val newText = text.toLowerCase()
      var i = 0
      while (found != -2 && i < enumerations!!.size) {
        if (enumerations[i].toLowerCase().startsWith(newText)) {
          if (enumerations[i].toLowerCase() == newText) {
            found = i
            break
          }
          found = if (found == -1) {
            i
          } else {
            -2
          }
        }
        i++
      }
      when (found) {
        -1 -> throw CheckTypeException(field, "00001")
        -2 -> {
          // show the suggestions list
          Field.doNotLeaveActiveField = true
          // TextField.internalHandleQuery(text, true, true) TODO
        }
        else -> field.value = enumerations!![found]
      }
    }
  }
}
