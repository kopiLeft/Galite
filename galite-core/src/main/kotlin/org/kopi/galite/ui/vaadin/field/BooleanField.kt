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

import org.kopi.galite.ui.vaadin.base.Styles

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.customfield.CustomField
import com.vaadin.flow.component.orderedlayout.HorizontalLayout


/**
 * Server side implementation of the boolean field
 *
 * @param trueRepresentation The representation of the true value.
 * @param falseRepresentation The representation of the false false
 */
class BooleanField(trueRepresentation: String?, falseRepresentation: String?) : ObjectField<Boolean>() {

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
    /*registerRpc(object : BooleanFieldServerRpc() { TODO
      fun valueChanged(value: Boolean) {
        setValue(value)
        fireValueChangeEvent(value)
      }
    })*/

    className = Styles.BOOLEAN_FIELD
    yes.classNames.add("true")
    no.classNames.add("false")
    content.add(yes)
    content.add(no)
    // content.setCellVerticalAlignment(yes, HasVerticalAlignment.ALIGN_BOTTOM) TODO
    // content.setCellVerticalAlignment(no, HasVerticalAlignment.ALIGN_BOTTOM) TODO
    // setWidget(content) TODO
    yes.addValueChangeListener(::onYesChange)
    no.addValueChangeListener(::onNoChange)
    // addKeyPressHandler(this) TODO
    // addKeyDownHandler(this) TODO
    // sinkEvents(Event.ONMOUSEOVER or Event.ONMOUSEOUT) TODO
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
    // getRpcProxy(BooleanFieldClientRpc::class.java).setFocus(focus) TODO
  }

  /**
   * Sets the blink state of the boolean field.
   * @param blink The blink state.
   */
  fun setBlink(blink: Boolean) {
    // if (blink) { TODO
    //   addStyleDependentName("blink")
    // } else {
    //   removeStyleDependentName("blink")
    // }
  }

  override fun onBlur(event: BlurNotifier.BlurEvent<CustomField<Any>>?) {
    super.onBlur(event)
    if (value == null) {
      isVisible = false
    }
  }

  override fun onFocus(event: FocusNotifier.FocusEvent<CustomField<Any>>?) {
    super.onFocus(event)
    isVisible = true
  }

  override fun setParentVisibility(visible: Boolean) {
    if (value == null) {
      yes.element.style["visibility"] = "hidden"
      no.element.style["visibility"] = "hidden"
      // removeStyleDependentName("visible") TODO
    } else {
      isVisible = visible
    }
    forceHiddenVisibility = !visible
  }

  override fun setVisible(visible: Boolean) {
    if (!forceHiddenVisibility && visible) {
      yes.element.style["visibility"] = "visible"
      no.element.style["visibility"] = "visible"
      //addStyleDependentName("visible") TODO
    } else {
      yes.element.style["visibility"] = "hidden"
      no.element.style["visibility"] = "hidden"
      //removeStyleDependentName("visible") TODO
    }
  }

  override fun isVisible(): Boolean {
    TODO()
  }

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

  override fun generateModelValue(): Boolean? = value

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

  fun onYesChange(event: HasValue.ValueChangeEvent<Boolean>) {
    if (event.value) {
      no.value = false
    } else if (mandatory && !no.value) {
      yes.value = true
    }
    handleComponentVisiblity()
    // ValueChangeEvent.fire(this, value) TODO
  }

  fun onNoChange(event: HasValue.ValueChangeEvent<Boolean>) {
    if (event.value) {
      yes.value = false
    } else if (mandatory && !yes.value) {
      no.value = true
    }
    handleComponentVisiblity()
    // ValueChangeEvent.fire(this, value) TODO
  }

  /**
   * Handles the component visibility according to its value.
   */
  protected fun handleComponentVisiblity() {
    TODO()
  }

  /**
   * Sets the name of the radio button inside the boolean field.
   *
   * The label attached with this field. TODO: doc?
   * Needed to set the for attribute of the input radio
   * in the field widget.
   *
   * @param label The name of the radio buttons.
   * @param yes The localized label for true value.
   * @param no The localized label for false value.
   */
  fun setLabel(label: String, yes: String?, no: String?) {
    var label = label
    label = label.replace("\\s".toRegex(), "_")
    // this.yes.setName(label) TODO
    // this.no.setName(label) TODO
    // this.yes.setTitle(yes) TODO
    // this.no.setTitle(no) TODO
  }
}
