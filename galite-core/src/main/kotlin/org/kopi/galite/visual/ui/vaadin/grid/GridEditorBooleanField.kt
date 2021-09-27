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
package org.kopi.galite.visual.ui.vaadin.grid

import org.kopi.galite.visual.ui.vaadin.base.Styles

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * An editor for boolean field.
 *
 * The component is an association of two check buttons
 * working exclusively to handle the three possible values handled
 * by a boolean field.
 *
 * yes is checked & no is not checked --> true
 * no is checked & yes is not checked --> false
 * yes and no are both unchecked --> null
 *
 * yes and no cannot be checked at the same time
 */
class GridEditorBooleanField(trueRepresentation: String?, falseRepresentation: String?) : GridEditorField<Boolean?>() {
  /**
   * Sets the boolean field to be mandatory
   * This will remove to choose the null option
   * from the two check boxes
   */
  var mandatory = false

  private var content: HorizontalLayout = HorizontalLayout()

  private var yes: Checkbox = Checkbox()

  private var no: Checkbox = Checkbox()

  private var forceHiddenVisibility = false

  init {
    addClassNames(Styles.BOOLEAN_FIELD, "editor-field", "editor-booleanfield", "k-boolean-field-content")
    yes.classNames.add("true")
    no.classNames.add("false")
    setWidthFull()
    setLabel(trueRepresentation, falseRepresentation)
    content.add(yes)
    content.add(no)
    // content.setCellVerticalAlignment(yes, HasVerticalAlignment.ALIGN_BOTTOM) TODO
    // content.setCellVerticalAlignment(no, HasVerticalAlignment.ALIGN_BOTTOM) TODO
    yes.addValueChangeListener(::onYesChange)
    no.addValueChangeListener(::onNoChange)
    yes.element.style["visibility"] = "hidden"
    no.element.style["visibility"] = "hidden"
    content.element.addEventListener("mouseover") {
      isVisible = true
    }

    content.element.addEventListener("mouseout") {
      if(value == null) {
        isVisible = false
      }
    }

    addFocusListener {
      onFocus()
    }
    addBlurListener {
      onBlur()
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  /**
   * Sets the field focus.
   * @param focus The field focus
   */
  fun setFocus(focus: Boolean) {
    if(focus) {
      focus()
    } else {
      blur()
    }
  }

  /**
   * Sets the blink state of the boolean field.
   * @param blink The blink state.
   */
  override fun setBlink(blink: Boolean) {
    if (blink) {
      element.classList.add(Styles.BOOLEAN_FIELD + "-blink")
    } else {
      element.classList.remove(Styles.BOOLEAN_FIELD + "-blink")
    }
  }

  fun onBlur() {
    if (value == null) {
      isVisible = false
    }
  }

  fun onFocus() {
    isVisible = true
  }

  override fun setVisible(visible: Boolean) {
    if (!forceHiddenVisibility && visible) {
      yes.element.style["visibility"] = "visible"
      no.element.style["visibility"] = "visible"
      element.classList.add(Styles.BOOLEAN_FIELD + "-visible")
    } else {
      yes.element.style["visibility"] = "hidden"
      no.element.style["visibility"] = "hidden"
      element.classList.remove(Styles.BOOLEAN_FIELD + "-visible")
    }
  }

  override fun isVisible(): Boolean =
    yes.element.style["visibility"].equals("visible")
            && no.element.style["visibility"].equals("visible")

  /**
   * Sets the value of this boolean field.
   * @param value The field value.
   */
  override fun setValue(value: Boolean?) {
    when {
      value == null -> {
        yes.value = false
        no.value = false
      }
      value -> {
        yes.value = true
        no.value = false
      }
      else -> {
        yes.value = false
        no.value = true
      }
    }
    handleComponentVisiblity()
  }

  override fun setPresentationValue(newPresentationValue: Boolean?) {
    value = newPresentationValue
  }

  override fun addFocusListener(function: () -> Unit) {
    yes.addFocusListener {
      function()
    }
    no.addFocusListener {
      function()
    }
  }

  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    yes.isEnabled = enabled
    no.isEnabled = enabled
  }

  override fun getValue(): Boolean? =
    if (!yes.value && !no.value) {
      null
    } else {
      yes.value
    }

  private fun onYesChange(event: HasValue.ValueChangeEvent<Boolean>) {
    if (event.value) {
      no.value = false
    } else if (mandatory && !no.value) {
      yes.value = true
    }
    if (value == true || value == null) {
      setModelValue(value, event.isFromClient)
    }
    handleComponentVisiblity()
  }

  private fun onNoChange(event: HasValue.ValueChangeEvent<Boolean>) {
    if (event.value) {
      yes.value = false
    } else if (mandatory && !yes.value) {
      no.value = true
    }
    if (value == false || value == null) {
      setModelValue(value, event.isFromClient)
    }
    handleComponentVisiblity()
  }

  /**
   * Handles the component visibility according to its value.
   */
  protected fun handleComponentVisiblity() {
    isVisible = value != null
  }

  /**
   * Sets the tooltip of the checkbox buttons inside the boolean field.
   *
   * @param yes The localized label for true value.
   * @param no The localized label for false value.
   */
  fun setLabel(yes: String?, no: String?) {
    this.yes.element.setProperty("title", yes)
    this.no.element.setProperty("title", no)
  }

  override fun initContent(): Component {
    return content
  }

  override fun doFocus() {
    // DO NOTHING
  }
}
