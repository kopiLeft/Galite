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
package org.kopi.galite.demo.bill

import java.util.Locale

import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.tablepivot.PivotTable

class PivotTableExample : PivotTable(title = "Form to test Blocks", locale = Locale.UK) {

  val file = menu("File")
  val quit = actor(menu = file, label = "Quit", help = "Close Report.", ident = "quit") {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }
  val cmdQuit = command(item = quit) {
    model.close()
  }

  val city = field(STRING(15)) {
    label = "Country"
    help = "The country"
  }

  val delegation = field(STRING(15)) {
    label = "Delegation"
    help = "The delegation"
  }

  val gender = field(STRING(6)) {
    label = "Gender"
    help = "The gender"
  }

  val age = field(INT(3)) {
    label = "Age"
    help = "The age"
  }

  init {

    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Rades"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Tunis"
      this[delegation] = "Notre damme"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Rades"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Tunis"
      this[delegation] = "Notre damme"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Tunis"
      this[delegation] = "Notre damme"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Green"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Tunis"
      this[delegation] = "Notre damme"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Female"
      this[age] = 22
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Mourouj"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
    add {
      this[city] = "Ben Arous"
      this[delegation] = "Yassminet"
      this[gender]  = "Male"
      this[age] = 26
    }
  }
}
