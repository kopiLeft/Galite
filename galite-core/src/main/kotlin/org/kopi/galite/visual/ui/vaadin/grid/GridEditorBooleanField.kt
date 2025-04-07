/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

import kotlin.streams.toList

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.checkbox.CheckboxGroup

import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.VColor

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
class GridEditorBooleanField(val trueRepresentation: String?,val falseRepresentation: String?) : GridEditorField<Boolean?>() {
  // Sets the boolean field to be mandatory.
  // This will remove to choose the null option from the two check boxes
  var mandatory = false
  // Variable to keep track of the last focused checkbox item
  private var focusedIndex = 0
  private var focusOnFirst = true
  // Initialize the field checkboxGroup Component
  private val checkboxGroup: FocusableCheckboxGroup<String> = FocusableCheckboxGroup<String>().apply {
    label = null
    // Define the items for true, false, and optionally an empty state
    setItems(trueRepresentation.orEmpty(), falseRepresentation.orEmpty())
    value = setOf() // Initialize with no selection
  }

  init {
    // Remove the "Yes" and "No" labels
    checkboxGroup.setItemLabelGenerator { "" }
    checkboxGroup.addClassNames(Styles.BOOLEAN_FIELD, "editor-field")
    checkboxGroup.addValueChangeListener(::onValueChange)

    setWidthFull()
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  /**
   * Sets the field focus.
   * @param focus The field focus
   */
  fun setFocus(focus: Boolean, focusOnFirst: Boolean) {
    if (focus) {
      this.focusOnFirst = focusOnFirst
      focus()
    } else {
      blur()
    }
  }

  /**
   * Ensure only one item is selected, or none at all when value changed
   */
  private fun onValueChange(event: com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent<CheckboxGroup<String>, MutableSet<String>>) {
    if (event.isFromClient) {
      // Ensure only one item is selected, or none at all
      if (event.value.size > 1) {
        // Keep only the last selected item
        val lastSelected = event.value
        lastSelected.remove(event.oldValue.iterator().next())
        checkboxGroup.value = setOf(lastSelected.iterator().next())
      } else if (event.value.isEmpty() && mandatory) {
        // If mandatory, remove the null option choice
        checkboxGroup.value = event.oldValue
      }
    }
    // Update internal model and fire change event
    setModelValue(getBooleanValue(checkboxGroup.value), event.isFromClient)
    (checkboxGroup.getChildren().toList().getOrNull(focusedIndex) as? Checkbox)?.focus()
  }

  /**
   * Gets the field's boolean value
   */
  private fun getBooleanValue(selectedValues: Set<String>): Boolean? {
    return when {
      selectedValues.isEmpty()                     -> null
      selectedValues.contains(trueRepresentation)  -> true
      selectedValues.contains(falseRepresentation) -> false
      else                                         -> null
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

  override fun setColor(align: Int, foreground: VColor?, background: VColor?) {
    // NOT SUPPORTED
  }

  /**
   * Sets the component value from a boolean value
   */
  override fun setValue(value: Boolean?) {
    when (value) {
      true  -> checkboxGroup.setValue(setOf(trueRepresentation))
      false -> checkboxGroup.setValue(setOf(falseRepresentation))
      else  -> { checkboxGroup.deselectAll() ; checkboxGroup.clear() }
    }
  }

  /**
   * Updates the presentation of this field to display the provided value.
   */
  override fun setPresentationValue(newPresentationValue: Boolean?) {
    value = newPresentationValue
  }

  /**
   * @return the field's checkbox group component
   */
  override fun initContent(): Component {
    return checkboxGroup
  }
  /**
   * Enables the checkbox group component
   */
  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    checkboxGroup.isEnabled = enabled
  }

  /**
   * @return the boolean value represented by the field
   */
  override fun getValue(): Boolean? = getBooleanValue(checkboxGroup.value)

  /**
   * Focus on the appropriate checkbox element
   */
  override fun doFocus() {
    focusedIndex = if (focusOnFirst) 0 else 1
    val focusedCheckbox = checkboxGroup.getChildren().toList()[focusedIndex] as? Checkbox
    focusedCheckbox?.focus()
  }

  /**
   * Adds Custom focus listener for BooleanField
   */
  override fun addFocusListener(focusFunction: () -> Unit) {}

  /**
   * Adds custom Key Down listener for BooleanField.
   */
  fun addKeyDownListener(gotoNext: () -> Unit, gotoPrevious: () -> Unit) {
    checkboxGroup.addKeyDownListener { event ->
      val items = checkboxGroup.getChildren().toList() // Retrieve child components (checkboxes)

      when (event.key) {
        Key.TAB -> {
          val modifier = event.modifiers.singleOrNull()

          if (modifier != null && modifier.name == "SHIFT") {
            if (focusedIndex <= 0) { gotoPrevious() } else { focusedIndex-- }
          } else {
            if (focusedIndex >= 1) { gotoNext() } else { focusedIndex++ }
          }
        }
        Key.ENTER, Key.SPACE -> { // Change the value of the currently focused checkbox
          val checkbox = items.getOrNull(focusedIndex) as? Checkbox

          checkbox?.value = !(checkbox?.value ?: false)
        }
      }
    }
  }

  // Inner class to encapsulate CheckboxGroup component and make it focusable
  inner class FocusableCheckboxGroup<T> : CheckboxGroup<T>(), Focusable<CheckboxGroup<T>>, KeyNotifier {
    init {
      // Make the component part of the tab order by setting tab index
      element.setAttribute("tabindex", "0")
    }
  }
}
