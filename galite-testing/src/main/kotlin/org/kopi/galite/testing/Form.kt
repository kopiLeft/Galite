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
package org.kopi.galite.testing

import java.util.function.Predicate

import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.ui.vaadin.form.DForm
import org.kopi.galite.visual.ui.vaadin.main.MainWindow

import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get

/**
 * Finds the Vaadin form component of this form.
 */
fun Form.findForm(): DForm {
  val forms = findForms()

  if (forms.isEmpty()) {
    throw Exception("Form '$title' not found")
  } else if (forms.size > 1) {
    throw Exception("Form '$title' is found many times")
  }

  return forms.single()
}

/**
 * Finds the Vaadin form components of this form.
 */
fun Form.findForms(): List<DForm> {
  val mainWindow = _get<MainWindow>()

  return mainWindow
    ._find {
      predicates = mutableListOf(
        Predicate {
          it.getModel()!! eq model
        }
      )
    }
}

infix fun VForm.eq(form: VForm): Boolean {
  return this.getName()  == form.getName()
          && this.source == form.source
}
