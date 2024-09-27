/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

import org.kopi.galite.type.format
import org.kopi.galite.visual.ui.vaadin.base.JSKeyDownHandler
import org.kopi.galite.visual.ui.vaadin.base.ShortcutAction
import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.form.DField
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.window.Window
import org.kopi.galite.visual.ui.vaadin.base.DecimalFormatSymbols
import org.kopi.galite.visual.form.VConstants

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyDownEvent
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.KeyPressEvent
import com.vaadin.flow.component.KeyUpEvent
import com.vaadin.flow.component.textfield.Autocomplete
import com.vaadin.flow.component.textfield.HasAutocomplete
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.dom.DomEvent

/**
 * A text field component that can support many validation
 * strategies to restrict field input.
 *
 * Protected constructor to use to create other types of fields.
 */
open class InputTextField<C> internal constructor(protected val internalField: C)
  : HasSize, AbstractCompositeField<C, InputTextField<C>, String>(null),
      KeyNotifier, HasStyle, BlurNotifier<InputTextField<C>>, Focusable<InputTextField<C>>,
      HasAutocomplete, HasPrefixAndSuffix, JSKeyDownHandler
        where C: AbstractField<*, out Any>, C: Focusable<*>
      /*, HasSelectionHandlers<Suggestion?>, SuggestionHandler TODO*/ {

  /**
   * Returns the parent window of this text field.
   * @return The parent window of this text field.
   */
  val parentWindow: Window? get() = fieldConnector.getWindow()
  // used while checking if FF has set input prompt as value
  private var validationStrategy: TextValidator? = null
  private var periodPressed = false
  private var currentText: String? = null
  //private var oracle: SuggestOracle? = null
  //private var display: SuggestionDisplay? = null
  override val keyNavigators: MutableMap<String, ShortcutAction<*>> = mutableMapOf()
  private var hasAutocomplete = false
  private var align: String? = null
  private var isCheckingValue = false

  init {
    className = Styles.TEXT_INPUT
    //element.addEventListener("paste", ::onPasteEvent) // TODO
    //sinkEvents(Event.ONCONTEXTMENU) TODO
    // addKeyDownListener(::onKeyDown) TODO
    //addKeyPressListener(::onKeyPress)
    //addKeyUpListener(::onKeyUp)
    addFocusListener(::onFocus)
    //addBlurListener(::onBlur)
    // TODO : disable context menu from showing up.
    // Autoselection on focus
    element.setProperty("autoselect", true)
  }

  var focusedTextField: InputTextField<*>? = null

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override fun setPresentationValue(newPresentationValue: String?) {
      // Cast content to AbstractField that can accept a String
      (content as? AbstractField<*, String>)?.value = newPresentationValue
  }

  open fun addTextValueChangeListener(listener: HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<*, *>>) {
    internalField.addValueChangeListener(listener)
  }

  override fun getValue(): String? {
    return format(internalField.value)
  }

  private fun format(s: Any?): String? =
    when (s) {
      is LocalDate -> s.format()
      is BigDecimal -> s.format()
      is LocalTime -> s.format()
      is Instant -> s.format()
      else -> s?.toString()
    }

  override fun initContent(): C = internalField

  fun onKeyPress(event: KeyPressEvent) {
    // block any key when a suggestions query is launched.
    //if (connector.isQueryingForSuggestions()) { TODO
    //  cancelKey()
    //  return
    //}
    if (event.key == Key.CONTROL
            || event.key == Key.ALT || event.key == Key.META
            || (java.lang.String.valueOf(event.key.keys.first()).trim { it <= ' ' }.isEmpty()
                    && event.key !== Key.SPACE)
    ) {
      return
    }
    //if (event.key == Key.SPACE && getSelectedText() != null && getSelectedText().equals(value)) { TODO
    //  setText(null) // clear text
    //  cancelKey()
    //  return
    //}

    // if the content if the input is selected, validate only the typed character.
    // this is typically used for enumeration fields to allow the content to be overwritten
    if (getSelectedText() != null && getSelectedText().equals(value)
      && validationStrategy != null && validationStrategy is EnumValidator
    ) {
      event.key.keys.forEach {
        if (!validationStrategy!!.validate(it)) {
          cancelKey()
        }
      }
      return
    }
    // validate the whole text input.
    if (validationStrategy != null) {
      event.key.keys.forEach {
        if (!validationStrategy!!.validate(value + it)) {
          cancelKey()
        }
      }
    }
    if (event.key.matches(".")) {
      periodPressed = true
    }
  }

  fun getSelectedText(): String? {
    // TODO
    return null
  }

  fun cancelKey() {
    // TODO
  }

  private fun onPasteEvent(event: DomEvent) {
    // should validate text content
    if (validationStrategy != null) {
      val before = value

      if (!validationStrategy!!.validate(value)) {
        value = before
      }
    }
  }

  @JvmName("setAnyValue")
  fun setValue(text: Any?) {
    value = format(text)
  }

  override fun setValue(text: String?) {
    var text = text

    // set only valid inputs
    //if (validationStrategy is NoeditValidator TODO
    //  || validationStrategy!!.validate(text)
    //) {
    if (text == null) {
      text = "" // avoid NullPointerException
    }
    setPresentationValue(text)
    //}
  }

  /**
   * Sets the input text foreground and background colors.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(foreground: String?, background: String?) {
    // clear server color styles if necessary
    if ((foreground == null || foreground.isEmpty())
      && (background == null || background.isEmpty())
    ) {
      clearServerStyles()
    }
    style["text-align"] = align // TODO: test this carefully
    if (foreground != null && foreground.isNotEmpty()) {
      // set color directly on element style
      style["color"] = "$foreground !important"
    }
    if (background != null && background.isNotEmpty()) {
      // set color directly on element style
      style["background-color"] = "$background !important"
    }
  }

  /**
   * Removes the color styles generated by the server
   */
  protected fun clearServerStyles() {
    for (style in className.split("\\s")) {
      if (style.endsWith("-" + fieldConnector.position)) {
        removeClassName(style)
      }
    }
  }

  /*fun setAlignment(align: TextAlignment) { TODO
    super.setAlignment(align)
    this.align = align.toString().toLowerCase()
  }*/

  /**
   * Sets the alignment of a text field.
   * @param align The text field alignment.
   */
  fun setAlign(align: Int) {
    if(internalField is com.vaadin.flow.component.textfield.TextField) {
      when (align) {
        VConstants.ALG_RIGHT -> internalField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        VConstants.ALG_CENTER -> internalField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER)
      }
    }
  }

  /**
   * Returns `true` if the auto complete function should be used.
   * @return `true` if the auto complete function should be used.
   */
  open fun hasAutoComplete(): Boolean = hasAutocomplete

  /**
   * Sets the text validation strategy.
   * @param validationStrategy The text validation strategy.
   */
  fun setTextValidator(validationStrategy: TextValidator?) {
    this.validationStrategy = validationStrategy
  }

  /**
   * Sets the fields width.
   * @param width The field width.
   */
  fun setWidth(width: Int) {
    setWidth(width.toString() + "ex")
  }

  /**
   * Returns the field max length.
   * @return The field max length.
   */
  open fun getMaxLength(): Double = element.getProperty("maxlength", 0.0)

  /**
   * Sets the text zone max length.
   * @param maxLength The max length.
   */
  open fun setMaxLength(maxLength: Int) {
    updateMaxLength(maxLength)
  }

  /**
   * Sets the text size.
   */
  open var size: Int
    get() = internalField.getElement().getProperty("size").toInt()
    set(value) { this.element.setProperty("size", value.toString()) }

  /**
   * This method is responsible for updating the DOM or otherwise ensuring
   * that the given max length is enforced. Called when the max length for the
   * field has changed.
   *
   * @param maxLength The new max length
   */
  protected fun updateMaxLength(maxLength: Int) {
    // TODO: do we need this?
    if (maxLength >= 0) {
      element.setProperty("maxlength", maxLength.toDouble())
    } else {
      element.removeAttribute("maxLength")
    }
  }

  /**
   * Sets the the blink state of the field.
   * @param blink The blink state.
   */
  fun setBlink(blink: Boolean) {
    if (blink) {
      addStyleDependentName("blink")
    } else {
      removeStyleDependentName("blink")
    }
  }

  /**
   * Updates the field content.
   * @param text The new text field content.
   */
  fun updateFieldContent(text: String?) {
    value = text
  }

  /**
   * Special handling for enumeration fields.
   * @param rec The field record number
   */
  protected fun handleEnumerationFields(rec: Int) {
    //if (!isShowingSuggestions && validationStrategy is EnumValidator) {
    //  fieldConnector.markAsDirty(rec, value)
    //}
  }

  /**
   * Returns `true` if word wrap is used.
   * @return `true` if word wrap is used.
   */
  protected val isWordwrap: Boolean
    get() {
      val wrap: String = element.getAttribute("wrap")
      return "off" != wrap
    }

  /**
   * Sets this field to be checking it value.
   * @param isCheckingValue Are we checking the field value ?
   */
  fun setCheckingValue(isCheckingValue: Boolean) {
    this.isCheckingValue = isCheckingValue
  }

  fun onKeyUp(event: KeyUpEvent) {
    // After every user key input, refresh the popup's suggestions.
    if (hasAutocomplete /*&& !connector.isQueryingForSuggestions() TODO*/) {
      when (event.key) {
        Key.ARROW_DOWN,
            Key.PAGE_DOWN,
            Key.ARROW_UP,
            Key.PAGE_UP,
            Key.ARROW_LEFT,
            Key.ARROW_RIGHT,
            Key.ENTER,
            Key.TAB,
            Key.SHIFT,
            Key.CONTROL,
            Key.ALT,
            Key.ESCAPE -> {
        }
        else -> refreshSuggestions()
      }
    }
    // if period press is detected in key press event
    // we try to replace the decimal separator.
    if (periodPressed) {
      // look if the decimal separator must be changed.
      maybeReplaceDecimalSeparator()
      periodPressed = false
    }
  }

  /**
   * Checks if the decimal separator must be changed.
   */
  protected fun maybeReplaceDecimalSeparator() {
    if (validationStrategy is DecimalValidator && value!!.contains(".")) {
      val dfs = DecimalFormatSymbols.get(MainWindow.locale)

      if (dfs!!.decimalSeparator != '.') {
        value = value?.replace('.', dfs.decimalSeparator)
      }
    }
  }

  fun onKeyDown(event: KeyDownEvent) {
    /*if (hasAutocomplete && !connector.isQueryingForSuggestions()) { TODO
      if (isShowingSuggestions) {
        // stop the propagation to parent elements when
        // the suggestions display is showing.
        event.stopPropagation()
      }
      when (event.key) {
        Key.ARROW_DOWN -> display.moveSelectionDown()
        Key.ARROW_UP -> display.moveSelectionUp()
        Key.ESCAPE -> display.hideSuggestions()
        Key.ENTER, Key.TAB -> {
          val suggestion = display.getCurrentSelection()
          if (suggestion == null) {
            display.hideSuggestions()
          } else {
            setNewSelection(suggestion)
          }
        }
      }
    }
    if (BrowserInfo.get().isIE() && event.key == Key.ENTER) {
      // IE does not send change events when pressing enter in a text
      // input so we handle it using a key listener instead
      valueChange(false)
    }*/
  }

  /**
   * Request that this field get the focus in the window.
   * @param focused The focus ability
   * @return `true` if the focused input is this field
   */
  fun requestFocusInWindow(focused: Boolean): Boolean {
    // we need to check here if the text is already focused
    // to not get the focus twice
    if (focusedTextField == this) {
      return true
    }
    focus()
    setFocus(focused)
    return false
  }

  open fun setFocus(focused: Boolean) {
    if (focused) {
      focus()
    } else {
      blur()
    }
  }

  override fun focus() {
    internalField.focus()
  }

  open fun onBlur(event: BlurNotifier.BlurEvent<InputTextField<C>>) {
    // this is called twice on Chrome when e.g. changing tab while prompting
    // field focused - do not change settings on the second time
    if (focusedTextField !== this || focusedTextField == null) {
      return
    }
    removeStyleDependentName("focus")
    focusedTextField = null
    lazyHideSuggestions()
  }

  open fun onFocus(event: FocusNotifier.FocusEvent<InputTextField<C>>) {
    if (focusedTextField == this) {
      // already got the focus. give up
      return
    }
    focusedTextField = this
    addStyleDependentName("focus")
    // cancel the fetch of suggestions list on field focus
    // when the field is not empty
    maybeCancelSuggestions()
  }

  override fun onDetach(detachEvent: DetachEvent?) {
    super.onDetach(detachEvent)
    if (focusedTextField == this) {
      focusedTextField = null
    }
  }

  override fun setReadOnly(readOnly: Boolean) {
    val wasReadOnly: Boolean = isReadOnly

    if (readOnly) {
      tabIndex = -1
    } else if (wasReadOnly && !readOnly && tabIndex == -1) {
      /*
       * Need to manually set tab index to 0 since server will not send
       * the tab index if it is 0.
       */
      tabIndex = 0
    }
    super.setReadOnly(readOnly)
  }
  //---------------------------------------------------
  // PRIVATE MEMBERS
  //---------------------------------------------------

  /**
   * Cancel suggestions query if needed.
   */
  private fun maybeCancelSuggestions() {
    if (value == null || value!!.isEmpty()) {
      //cancelSuggestions()
      // restore the suggestions to be fetched
      // before GWT returns control to event browser
      // loop
      //allowSuggestions()
    }
  }

  /**
   * Returns the text field container.
   * @return The the text field container.
   */
  internal val connector: TextField
    get() = super.getParent().get() as TextField

  /**
   * Checks the value of this text field.
   * @param rec The active record.
   * @throws CheckTypeException When field content is not valid
   */
  fun checkValue(rec: Int) {
    isCheckingValue = true //!!! don't check twice on field blur
    if (validationStrategy != null) {
      validationStrategy!!.checkType(this, if (value == null) "" else value!!.trim())
    }
    isCheckingValue = false
  }
  //---------------------------------------------------
  // AUTO COMPLETION UTILS
  //---------------------------------------------------
  /**
   * Returns `true` if the suggestions list is showing.
   * @return `true` if the suggestions list is showing.
   */
  //val isShowingSuggestions: Boolean
  //  get() = display != null && display.isSuggestionListShowingImpl()

  /**
   * Refreshes the suggestions list.
   */
  private fun refreshSuggestions() {
    // Get the raw text.
    val text = value

    if (text == null || text.isEmpty() || text.length.toDouble() == getMaxLength()) {
      hideSuggestions()
    } else {
      currentText = text
    }
    showSuggestions(text)
  }

  /**
   * Set the new suggestion in the text box.
   *
   * @param curSuggestion the new suggestion
   */
  //private fun setNewSelection(curSuggestion: Suggestion?) {
  //  if (curSuggestion != null && currentText != curSuggestion.getReplacementString()) {
  //    currentText = curSuggestion.getReplacementString()
  //    setText(currentText)
  //    fireSuggestionEvent(curSuggestion)
  //    hideSuggestions()
  //    gotoNextBlockTextInput()
  //  }
  //}

  /**
   * Hides auto complete suggestions.
   */
  fun hideSuggestions() {
    //if (display != null && display.isSuggestionListShowingImpl()) {
    //  display.hideSuggestions()
    //}
  }

  /**
   * Hides auto complete suggestions with a delay.
   */
  fun lazyHideSuggestions() {
    //if (display.isSuggestionListShowingImpl()) {
    //  lazySuggestionsHider.schedule(200)
    //}
  }

  /**
   * Returns `true` if we are waiting for suggestions.
   * @return `true` if we are waiting for suggestions.
   */
  /*val isAboutShowingSuggestions: Boolean
    get() = display != null && display.isAboutShowingSuggestions()

  /**
   * Gives up showing suggestions
   */
  fun cancelSuggestions() {
    connector.cancelSuggestionsQuery()
  }

  /**
   * Allows to query suggestions from server side.
   */
  fun allowSuggestions() {
    connector.allowSuggestionsQuery()
  }*/

  /**
   * Returns the parent field connector.
   * @return The parent field connector.
   */
  internal val fieldConnector: DField
    get() = connector.fieldParent

  /**
   * Checks if the content of this field is empty.
   * @return `true` if this field is empty.
   */
  val isNull: Boolean
    get() = value == null || ("" == value)

  /**
   * Returns the suggest oracle.
   * @return The suggest oracle.
   */
  /*fun getOracle(): SuggestOracle? {
    return oracle
  }

  /**
   * Sets the suggestion oracle used to create suggestions.
   *
   * @param oracle the oracle
   */
  fun setOracle(oracle: SuggestOracle?) {
    this.oracle = oracle
  }

  /**
   * Fires a suggestion selection event.
   * @param selectedSuggestion The selected suggestion.
   */
  private fun fireSuggestionEvent(selectedSuggestion: Suggestion) {
    SelectionEvent.fire(this, selectedSuggestion)
  }

  fun addSelectionHandler(handler: SelectionHandler<Suggestion?>?): HandlerRegistration {
    return addHandler(handler, SelectionEvent.getType())
  }

  protected fun onEnsureDebugId(baseID: String?) {
    super.onEnsureDebugId(baseID)
    if (display != null) {
      display.onEnsureDebugId(baseID)
    }
  }*/

  /**
   * Shows the suggestions beginning with the given query string.
   * @param query The searched text.
   */
  internal fun showSuggestions(query: String?) {
    if (!hasAutocomplete) {
      return
    }
    //if (oracle == null) {
    //  return
    //}
    //if (query!!.isEmpty()) {
    //  oracle.requestDefaultSuggestions(Request(null, limit), callback)
    //} else {
    //  oracle.requestSuggestions(Request(query, limit), callback)
    //}
  }

  /**
   * Check if the [DefaultSuggestionDisplay] is showing. Note that this
   * method only has a meaningful return value when the
   * [DefaultSuggestionDisplay] is used.
   *
   * @return true if the list of suggestions is currently showing, false if not
   */
  //@get:Deprecated("use {@link DefaultSuggestionDisplay#isSuggestionListShowing()}")
  //val isSuggestionListShowing: Boolean
  //  get() = display != null && display.isSuggestionListShowingImpl()

  /**
   * Refreshes the current list of suggestions.
   */
  fun refreshSuggestionList() {
    if (isAttached && hasAutocomplete) {
      refreshSuggestions()
    }
  }

  /**
   * Show the current list of suggestions.
   */
  fun showSuggestionList() {
    if (isAttached && hasAutocomplete) {
      currentText = null
      refreshSuggestions()
    }
  }

  /**
   * Sets the suggestion display.
   * @param display The suggestion display.
   */
  //fun setDisplay(display: SuggestionDisplay?) {
  //  this.display = display
  //}

  /**
   * Sets the suggestion display modality.
   * @param modal The display modality.
   */
  fun setDisplayModality(modal: Boolean) {
    //if (display != null && display is DefaultSuggestionDisplay) {
    //  (display as DefaultSuggestionDisplay).setModal(modal)
    //}
  }

  /**
   * Sets whether this widget is enabled.
   *
   * @param enabled `true` to enable the widget, `false` to disable it
   */
  override fun setEnabled(enabled: Boolean) {
    super<AbstractCompositeField>.setEnabled(enabled)
    //if (!enabled && display != null) {
    //  display.hideSuggestions()
    //}
  }

  /**
   * Releases the content of this input field.
   */
  open fun release() {
    validationStrategy = null
    currentText = null
    //oracle = null
    //display = null
    align = null
    //callback = null
    //suggestionCallback = null
  }

  /**
   * Enable or disable animations in the [DefaultSuggestionDisplay]. Note
   * that this method is a no-op unless the [DefaultSuggestionDisplay] is
   * used.
   *
   */
  @Deprecated("use {@link DefaultSuggestionDisplay#setAnimationEnabled(boolean)} instead")
  fun setAnimationEnabled(enable: Boolean) {
    //if (display != null) {
    //  display.setAnimationEnabledImpl(enable)
    //}
  }

  /**
   * Sets the auto completion feature.
   * @param hasAutocomplete the auto completion feature.
   */
  fun setHasAutocomplete(hasAutocomplete: Boolean) {
    this.hasAutocomplete = hasAutocomplete

    autocomplete = if (hasAutoComplete()) {
      Autocomplete.ON
    } else {
      Autocomplete.OFF
    }
  }

  /**
   * Sets the input field type attribute to [type]
   */
  fun setInputType(type: String) {
    element.node.runWhenAttached { ui ->
      ui.page.executeJs("$0.focusElement.type=$1", this, type)
    }
  }

  /*private var callback: Callback? = object : Callback() {
    fun onSuggestionsReady(request: Request?, response: Response) {
      // If disabled while request was in-flight, drop it
      if (!isEnabled() || !hasAutocomplete) {
        return
      }
      if (display != null) {
        display.setMoreSuggestions(response.hasMoreSuggestions(), response.getMoreSuggestionsCount())
        if (response is org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response) {
          display.showSuggestions(
            this@VInputTextField,
            (response as org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response).getAutocompleteSuggestion(),
            oracle.isDisplayStringHTML(),
            isAutoSelectEnabled,
            suggestionCallback
          )
        }
      }
    }
  }
  private var suggestionCallback: SuggestionCallback? = object : SuggestionCallback() {
    fun onSuggestionSelected(suggestion: Suggestion?) {
      setNewSelection(suggestion)
    }
  }*/

  fun addStyleDependentName(dependentClassName: String) {
    if(className != null) {
      element.classList.add("${Styles.TEXT_INPUT}-$dependentClassName")
    }
  }

  fun removeStyleDependentName(dependentClassName: String) {
    if(className != null) {
      element.classList.remove("${Styles.TEXT_INPUT}-$dependentClassName")
    }
  }
}
