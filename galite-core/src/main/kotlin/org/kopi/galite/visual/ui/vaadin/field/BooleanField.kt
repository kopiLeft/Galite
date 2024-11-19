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

package org.kopi.galite.visual.ui.vaadin.field

import kotlin.streams.toList

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.dependency.CssImport

import org.kopi.galite.visual.ui.vaadin.base.Styles

/**
 * The boolean field
 *
 * @param trueRepresentation The representation of the true value.
 * @param falseRepresentation The representation of the false value
 */
@CssImport.Container(value = [
  CssImport("./styles/galite/checkbox.css"),
  CssImport(value = "./styles/galite/checkbox.css", themeFor = "vaadin-checkbox")
])
class BooleanField(val trueRepresentation: String?, val falseRepresentation: String?) : AbstractField<Boolean?>(),
                                                                                        KeyNotifier,
                                                                                        BlurNotifier<AbstractField<Boolean?>>
{
   // Sets the boolean field to be mandatory.
   // This will remove to choose the null option from the two check boxes
  var mandatory = false
  // Variable to keep track of the last focused checkbox item
  private var focusedIndex = 0
  // Initialize the field checkboxGroup Component
  private val checkboxGroup: FocusableCheckboxGroup<String> = FocusableCheckboxGroup<String>().apply {
    label = null
    // Define the items for true, false, and optionally an empty state
    setItems(trueRepresentation.orEmpty(), falseRepresentation.orEmpty())
    value = setOf() // Initialize with no selection
    addValueChangeListener { event ->
      // Ensure only one item is selected, or none at all
      if (event.value.size > 1) {
        // Keep only the last selected item
        val lastSelected = event.value
        lastSelected.remove(event.oldValue.iterator().next())
        value = setOf(lastSelected.iterator().next())
      } else if (event.value.isEmpty() && mandatory) {
        // If mandatory, remove the null option choice
        value = event.oldValue
      }
      // Update internal model and fire change event
      setModelValue(getBooleanValue(value), true)
    }
  }

  init {
    // Remove the "Yes" and "No" labels
    checkboxGroup.setItemLabelGenerator { "" }
    checkboxGroup.addClassName(Styles.BOOLEAN_FIELD)

    add(checkboxGroup)
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

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
   * Function to check if the last item is currently focused
   */
  private fun isLastItemFocused(currentIndex: Int, itemCount: Int = 2): Boolean {
    return currentIndex == itemCount
  }

  /**
   * Sets the component value from a boolean value
   */
  override fun setValue(value: Boolean?) {
    checkboxGroup.value = when (value) {
      true -> setOf(trueRepresentation)
      false -> setOf(falseRepresentation)
      else -> emptySet()
    }
  }

  /**
   * Updates the presentation of this field to display the provided value.
   */
  override fun setPresentationValue(newPresentationValue: Boolean?) {
    setValue(newPresentationValue)
  }

  /**
   * Checks if the component value is null
   */
  override val isNull: Boolean
    get() = checkboxGroup.value.isEmpty()

  /**
   * @return the field's checkbox group component
   */
  override fun getContent(): Component = checkboxGroup

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
   * Checks the boolean field value : No specific actions to execute
   */
  override fun checkValue(rec: Int) {}

  /**
   * Adds Custom focus listener for BooleanField
   */
  override fun addFocusListener(focusFunction: () -> Unit) {
    checkboxGroup.element.addEventListener("focus") {
      focusedIndex = 0
      focusFunction()
    }
  }

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
            if (focusedIndex <= 1) { gotoPrevious() } else { focusedIndex-- }
          } else {
            if (isLastItemFocused(focusedIndex)) { gotoNext() } else { focusedIndex++ }
          }
        }
        Key.ENTER, Key.SPACE -> { // Change the value of the currently focused checkbox
          val checkbox = items.getOrNull(focusedIndex - 1) as? Checkbox

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
