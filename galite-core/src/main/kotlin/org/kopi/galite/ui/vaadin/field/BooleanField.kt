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
package org.kopi.galite.ui.vaadin.field

import org.kopi.galite.ui.vaadin.base.Styles

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * The boolean field
 *
 * @param trueRepresentation The representation of the true value.
 * @param falseRepresentation The representation of the false false
 */
@CssImport("./styles/galite/checkbox.css")
class BooleanField(trueRepresentation: String?, falseRepresentation: String?) : ObjectField<Boolean?>() {

  /**
   * Sets the boolean field to be mandatory
   * This will remove to choose the null option
   * from the two check boxes
   */
  var mandatory = false

  private val content: HorizontalLayout = HorizontalLayout()

  private val yes: Checkbox = Checkbox()

  private val no: Checkbox = Checkbox()

  private var forceHiddenVisibility = false

  init {
    className = Styles.BOOLEAN_FIELD
    content.className = "k-boolean-field-content"
    yes.classNames.add("true")
    no.classNames.add("false")
    setLabel(trueRepresentation, falseRepresentation)
    content.add(yes)
    content.add(no)
    // content.setCellVerticalAlignment(yes, HasVerticalAlignment.ALIGN_BOTTOM) TODO
    // content.setCellVerticalAlignment(no, HasVerticalAlignment.ALIGN_BOTTOM) TODO
    add(content)
    yes.addValueChangeListener(::onYesChange)
    no.addValueChangeListener(::onNoChange)
    yes.element.style["visibility"] = "hidden"
    no.element.style["visibility"] = "hidden"
    content.element.addEventListener("mouseover") {
      isVisible = true
    }

    content.element.addEventListener("mouseout") {
      if (value == null) {
        isVisible = false
      }
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  fun beforeClientResponse(initial: Boolean) {
    // super.beforeClientResponse(initial) TODO
    // state.value = value TODO
  }

  /**
   * Sets the field focus.
   * @param focus The field focus
   */
  fun setFocus(focus: Boolean) {
    if (focus) {
      focus()
    } else {
      blur()
    }
  }

  /**
   * Sets the blink state of the boolean field.
   * @param blink The blink state.
   */
  fun setBlink(blink: Boolean) {
    if (blink) {
      element.classList.add(Styles.BOOLEAN_FIELD + "-blink")
    } else {
      element.classList.remove(Styles.BOOLEAN_FIELD + "-blink")
    }
  }

  override fun onBlur(event: BlurNotifier.BlurEvent<AbstractField<Boolean?>>) {
    super.onBlur(event)
    if (value == null) {
      isVisible = false
    }
  }

  override fun onFocus(event: FocusNotifier.FocusEvent<AbstractField<Boolean?>>) {
    super.onFocus(event)
    isVisible = true
  }

  override fun setParentVisibility(visible: Boolean) {
    if (value == null) {
      yes.element.style["visibility"] = "hidden"
      no.element.style["visibility"] = "hidden"
      element.classList.remove(Styles.BOOLEAN_FIELD + "-visible")
    } else {
      isVisible = visible
    }
    forceHiddenVisibility = !visible
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

  override val isNull: Boolean
    get() = !yes.value && !no.value

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

  override fun getContent(): Component = content

  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    yes.isEnabled = enabled
    no.isEnabled = enabled
  }

  override fun setColor(foreground: String?, background: String?) {
    // NOT SUPPORTED FOR BOOLEAN FIELDS
  }

  override fun getValue(): Boolean? =
          if (!yes.value && !no.value) {
            null
          } else {
            yes.value
          }

  override fun checkValue(rec: Int) {}

  private fun onYesChange(event: HasValue.ValueChangeEvent<Boolean>) {
    if (event.isFromClient) {
      if (event.value) {
        no.value = false
      } else if (mandatory && !no.value) {
        yes.value = true
      }
    }
    setModelValue(value, event.isFromClient)
    handleComponentVisiblity()
  }

  private fun onNoChange(event: HasValue.ValueChangeEvent<Boolean>) {
    if (event.isFromClient) {
      if (event.value) {
        yes.value = false
      } else if (mandatory && !yes.value) {
        no.value = true
      }
    }
    setModelValue(value, event.isFromClient)
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
}
