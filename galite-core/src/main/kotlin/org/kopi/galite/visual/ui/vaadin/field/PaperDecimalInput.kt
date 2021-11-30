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
package org.kopi.galite.visual.ui.vaadin.field

import java.math.BigDecimal

class PaperDecimalInput : PaperInput {

  var minValue: Double = Double.MIN_VALUE
  var maxValue: Double = Double.MAX_VALUE
  var stepValue: Double = 1.0

  var stepSetByUser: Boolean = false
  var minSetByUser: Boolean = false
  var maxSetByUser: Boolean = false

  fun getMax(): Double {
    return getElement().getProperty("max", Double.MAX_VALUE);
  }

  fun setMax(max: Double) {
    maxValue = max
    maxSetByUser = true
    getElement().setProperty("max", max.toDouble());
  }

  fun getMin(): Double {
    return getElement().getProperty("min", Double.MIN_VALUE);
  }

  fun setMin(min: Double) {
    minValue = min
    minSetByUser = true
    getElement().setProperty("min", min.toDouble());
  }

  fun getStep(): Double {
    return getElement().getProperty("step", 1.0);
  }

  fun setStep(step: Double) {
    stepValue = step
    stepSetByUser = true
    getElement().setProperty("step", step.toDouble());
  }

  constructor() : super()

  constructor(label: String = "", min: Double = Double.MIN_VALUE, max: Double = Double.MIN_VALUE, step: Double = 1.0, errorMessage: String = "") {
    setLabel(label)
    setMin(min)
    setMax(max)
    setStep(step)
    setErrorMessage(errorMessage)
  }

  init {
    setType("number")
    addValueChangeListener { e -> validate() }
  }

  protected fun validate() {
    val value = getValue().toDouble()
    val isRequiredButEmpty = (getRequired()
      && null == value)
    val isGreaterThanMax = (value != null
      && value > maxValue)
    val isSmallerThanMin = (value != null
      && value < minValue)
    setInvalid(isRequiredButEmpty || isGreaterThanMax || isSmallerThanMin
      || !isValidByStep(value))
  }

  private fun isValidByStep(value: Double): Boolean {
    if (!stepSetByUser // Don't use step in validation if it's not explicitly
            // set by user. This follows the web component logic.
        || value == null || stepValue.toDouble() == 0.0) {
      return true
    }

    // When min is not defined by user, its value is the absoluteMin
    // provided in constructor. In this case, min should not be considered
    // in the step validation.
    val stepBasis = if (minSetByUser) minValue.toDouble() else 0.0

    return BigDecimal(value.toString())
      .subtract(BigDecimal.valueOf(stepBasis))
      .remainder(BigDecimal.valueOf(stepValue))
      .compareTo(BigDecimal.ZERO) == 0
  }
}
