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

class PaperIntegerInput : PaperInput {

  var minValue: Int = Int.MIN_VALUE
  var maxValue: Int = Int.MAX_VALUE
  var stepValue: Int = 1

  var stepSetByUser: Boolean = false
  var minSetByUser: Boolean = false
  var maxSetByUser: Boolean = false

  fun getMax(): Int {
    return getElement().getProperty("max", Int.MAX_VALUE);
  }

  fun setMax(max: Int) {
    maxValue = max
    maxSetByUser = true
    getElement().setProperty("max", max.toDouble());
  }

  fun getMin(): Int {
    return getElement().getProperty("min", Int.MIN_VALUE);
  }

  fun setMin(min: Int) {
    minValue = min
    minSetByUser = true
    getElement().setProperty("min", min.toDouble());
  }

  fun getStep(): Int {
    return getElement().getProperty("step", 1);
  }

  fun setStep(step: Int) {
    stepValue = step
    stepSetByUser = true
    getElement().setProperty("step", step.toDouble());
  }

  constructor() : super()

  constructor(label: String = "", min: Int = Int.MIN_VALUE, max: Int = Int.MIN_VALUE, step: Int = 1, errorMessage: String = "") {
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
    val value = getValue().toInt()
    val isRequiredButEmpty = (getRequired()
      && null == value)
    val isGreaterThanMax = (value != null
      && value > maxValue)
    val isSmallerThanMin = (value != null
      && value < minValue)
    setInvalid(isRequiredButEmpty || isGreaterThanMax || isSmallerThanMin
      || !isValidByStep(value))
  }

  private fun isValidByStep(value: Int): Boolean {
    if (!stepSetByUser // Don't use step in validation if it's not explicitly
            // set by user. This follows the web component logic.
      || value == null || stepValue == 0) {
      return true
    }

    // When min is not defined by user, its value is the absoluteMin
    // provided in constructor. In this case, min should not be considered
    // in the step validation.
    val stepBasis = if (minSetByUser) minValue else 0

    return value - stepBasis % stepValue == 0
  }
}
