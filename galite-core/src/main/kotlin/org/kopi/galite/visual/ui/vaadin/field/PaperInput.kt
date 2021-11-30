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

import com.vaadin.flow.component.HasValidation
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.html.Input
import com.vaadin.flow.component.textfield.Autocapitalize
import com.vaadin.flow.component.textfield.Autocomplete
import com.vaadin.flow.component.textfield.HasAutocapitalize
import com.vaadin.flow.component.textfield.HasAutocomplete
import com.vaadin.flow.component.textfield.HasAutocorrect
import com.vaadin.flow.data.value.ValueChangeMode
import org.kopi.galite.visual.util.base.InconsistencyException

@Tag("paper-input")
@NpmPackage(value = "@polymer/paper-input", version = "3.2.1")
@JsModule("@polymer/paper-input/paper-input.js")
open class PaperInput : Input, HasValidation, HasAutocomplete, HasAutocapitalize, HasAutocorrect {

  fun getDisabled(): Boolean {
    return element.getProperty("disabled", false)
  }

  fun setDisabled(disabled: Boolean) {
    element.setProperty("disabled", disabled)
  }

  fun getLabel(): String {
    return element.getProperty("label", "")
  }

  fun setLabel(label: String) {
    element.setProperty("label", label)
  }

  fun getCharCounter(): Boolean {
    return element.getProperty("charCounter", false)
  }

  fun setCharCounter(charCounter: Boolean) {
    element.setProperty("charCounter", charCounter)
  }

  fun getAlwaysFloatLabel(): Boolean {
    return element.getProperty("alwaysFloatLabel", false)
  }

  fun setAlwaysFloatLabel(alwaysFloatLabel: Boolean) {
    element.setProperty("alwaysFloatLabel", alwaysFloatLabel)
  }

  fun getNoLabelFloat(): Boolean {
    return element.getProperty("noLabelFloat", false)
  }

  fun setNoLabelFloat(noLabelFloat: Boolean) {
    element.setProperty("noLabelFloat", noLabelFloat)
  }

  fun getAutofocus(): Boolean {
    return element.getProperty("autofocus", false)
  }

  fun setAutofocus(autofocus: Boolean) {
    element.setProperty("autofocus", autofocus)
  }

  fun getMaxlength(): Int {
    return element.getProperty("maxlength", -1)
  }

  fun setMaxlength(maxlength: Int) {
    element.setProperty("maxlength", maxlength.toDouble())
  }

  fun getMinlength(): Int {
    return element.getProperty("minlength", -1)
  }

  fun setMinlength(minlength: Int) {
    element.setProperty("minlength", minlength.toDouble())
  }

  fun getReadonly(): Boolean {
    return element.getProperty("readonly", false)
  }

  fun setReadonly(readonly: Boolean) {
    element.setProperty("readonly", readonly)
  }

  fun getRequired(): Boolean {
    return element.getProperty("required", false)
  }

  fun setRequired(required: Boolean) {
    element.setProperty("required", required)
  }

  fun getPattern(): String {
    return element.getProperty("pattern", "")
  }

  fun setPattern(pattern: String) {
    element.setProperty("pattern", pattern)
  }

  fun getName(): String {
    return element.getProperty("name", "")
  }

  fun setName(name: String) {
    element.setProperty("name", name)
  }

  fun getTitle(): String {
    return element.getProperty("title", "")
  }

  fun setTitle(title: String) {
    element.setProperty("title", title)
  }

  override fun getErrorMessage(): String {
    return element.getProperty("errorMessage", "")
  }

  override fun setErrorMessage(errorMessage: String) {
    element.setProperty("errorMessage", errorMessage)
  }

  override fun isInvalid(): Boolean {
    return element.getProperty("invalid", false)
  }

  override fun setInvalid(invalid: Boolean) {
    element.setProperty("invalid", invalid)
  }

  constructor() : super(ValueChangeMode.ON_CHANGE)
  constructor(disabled: Boolean = false, label: String = "", charCounter: Boolean = false, alwaysFloatLabel: Boolean = false,
              noLabelFloat: Boolean = false, autofocus: Boolean = false, pattern: String = "", name: String = "input",
              minLength: Int = -1, maxLength: Int = -1, isReadonly: Boolean = false, isRequired: Boolean = false,
              autocapitalize: Autocapitalize = Autocapitalize.NONE, autocorrect: Boolean = false,
              autocomplete: Autocomplete = Autocomplete.OFF) : super(ValueChangeMode.ON_CHANGE) {
    setDisabled(disabled)
    if (alwaysFloatLabel && noLabelFloat) {
      throw InconsistencyException("Both alwaysFloatLabel and noLabelFloat can not be enabled together")
    }
    setAlwaysFloatLabel(alwaysFloatLabel)
    setNoLabelFloat(noLabelFloat)
    setCharCounter(charCounter)
    setLabel(label)
    setAutofocus(autofocus)
    setMinlength(minLength)
    setMaxlength(maxLength)
    setReadonly(isReadonly)
    setRequired(isRequired)
    setPattern(pattern)
    setName(name)
    setAutocapitalize(autocapitalize)
    setAutocorrect(autocorrect)
    setAutocomplete(autocomplete)
  }
}
