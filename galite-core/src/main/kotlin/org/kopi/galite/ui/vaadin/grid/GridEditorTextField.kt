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
package org.kopi.galite.ui.vaadin.grid

import java.io.Serializable
import java.lang.reflect.Method
import java.util.EventListener

import kotlin.collections.ArrayList

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.textfield.TextField

/**
 * A text field used as editor
 */
open class GridEditorTextField(width: Int) : GridEditorField<String?>() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  val wrappedField = TextField()

  init {
    add(wrappedField)
  }

  override fun setPresentationValue(newPresentationValue: Any?) {
    wrappedField.value = newPresentationValue.toString()
  }

  override fun generateModelValue(): Any? = wrappedField.value

  /**
   * Sets the input field type attribute to [type]
   */
  fun setInputType(type: String) {
    element.node.runWhenAttached { ui ->
      ui.page.executeJs("$0.focusElement.type=$1", this, type)
    }
  }

  /**
   * The column number.
   */
  var col = width

  /**
   * The field text alignment.
   */
  var align = 0

  /**
   * The auto complete minimum length to begin querying for suggestions
   */
  var autocompleteLength = 0

  /**
   * If the field has the auto complete feature.
   */
  var hasAutocomplete = false

  /**
   * Sets the field to have the autofill option.
   */
  var hasAutofill = false

  /**
   * The convert type to be applied to the field.
   */
  var convertType: ConvertType = ConvertType.NONE

  /**
   * Returns the displayed value of the text editor.
   * @return The displayed value of the text editor.
   */
  open fun getDisplayedValue(): String? {
    return displayedValue ?: ""
  }

  /**
   * Sets the field suggestions.
   * @param suggestions The displayed strings.
   * @param values The encapsulated values.
   */
  open fun setSuggestions(suggestions: Array<Array<String>>, query: String) {
    val newSuggestions: MutableList<AutocompleteSuggestion>
    newSuggestions = ArrayList<AutocompleteSuggestion>()
    for (i in suggestions.indices) {
      val suggestion = AutocompleteSuggestion()
      suggestion.id = i
      suggestion.query = query
      suggestion.displayStrings = suggestions[i]
      newSuggestions.add(suggestion)
    }
  }

  /**
   * The text change listener.
   */
  interface SuggestionsQueryListener : EventListener, Serializable {
    /**
     * Notifies registered objects that should query suggestions .
     * @param event The suggestions query event providing the query string.
     */
    fun onSuggestionsQuery(event: SuggestionsQueryEvent?)
  }

  /**
   * The suggestions query event object.
   * @param source The source component.
   * @param query The suggestions query
   */
  class SuggestionsQueryEvent(source: Component?, val query: String) {

    companion object {
      val QUERY_METHOD: Method? = null

      init {
        TODO()
      }
    }
  }

  //---------------------------------------------------
  // TEXT CHANGE
  //---------------------------------------------------
  /**
   * Fires a text change event object.
   * @param newText The new text value.
   * @param oldText The old text value.
   */
  protected open fun fireTextChangeEvent(newText: String, oldText: String) {
    fireEvent(TextChangeEvent(this, newText, oldText))
  }

  /**
   * The text change listener.
   */
  interface TextChangeListener : EventListener {
    /**
     * Notifies registered objects that text has change.
     * @param event The text change event providing new and old texts.
     */
    fun onTextChange(event: TextChangeEvent?)
  }

  /**
   * The text change event object.
   *
   * @param source The source component.
   * @param newText The new text value.
   * @param oldText The old text value.
   */
  class TextChangeEvent(source: Component?, val newText: String, val oldText: String)
    : ComponentEvent<Component>(source, true) {

    companion object {
      val TEXT_CHANGE: Method? = null

      init {
        TODO()
      }
    }
  }

  /**
   * The convert type to be applied to this text field.
   * The convert type can be to upper case, to lower case or to name.
   */
  enum class ConvertType {
    /**
     * no conversion.
     */
    NONE,

    /**
     * upper case conversion.
     */
    UPPER,

    /**
     * lower case conversion.
     */
    LOWER,

    /**
     * name conversion.
     */
    NAME
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  // used to store an internal state of this field
  private val internalValue: String? = null
  private val displayedValue: String? = null
}
