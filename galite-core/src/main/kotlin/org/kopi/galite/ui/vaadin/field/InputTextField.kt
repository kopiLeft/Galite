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

import java.text.DecimalFormatSymbols
import java.util.Locale

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.block.Block
import org.kopi.galite.ui.vaadin.field.TextField.ConvertType
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.window.Window

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

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.textfield.HasAutocomplete
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.shared.Registration

/**
 * A text field component that can support many validation
 * strategies to restrict field input.
 *
 * Protected constructor to use to create other types of fields.
 */
open class InputTextField<C: AbstractField<C, out Any>> internal constructor(protected val field: C)
  : HasSize, AbstractCompositeField<C, InputTextField<C>, String>(null),
      KeyNotifier, HasStyle, BlurNotifier<InputTextField<C>>, Focusable<InputTextField<C>>,
      HasAutocomplete, HasPrefixAndSuffix
      /*, HasSelectionHandlers<Suggestion?>, SuggestionHandler, HasValue<String?> TODO*/ {

  /**
   * Returns the parent window of this text field.
   * @return The parent window of this text field.
   */
  val parentWindow: Window?
    get() = fieldConnector.getWindow()

  // used while checking if FF has set input prompt as value
  private var validationStrategy: TextValidator? = null
  private var periodPressed = false
  private var currentText: String? = null
  //private var oracle: SuggestOracle? = null
  //private var display: SuggestionDisplay? = null

  // not really used.
  private var limit = 20
  /**
   * Whether or not the first suggestion will be automatically selected.
   * This behavior is on by default.
   */
  var isAutoSelectEnabled = true
  private var hasAutocomplete = false
  private var valueBeforeEdit: String? = ""
  private var align: String? = null
  private var isCheckingValue = false
  /**
   * `true` if the state of this field is not synchronized with server side.
   */
  var isAlreadySynchronized = false
  private var recordNumber = -1 // The record number corresponding to this text input

  init {
    className = Styles.TEXT_INPUT
    addKeyPressListener(::onKeyPress)
    addKeyUpListener(::onKeyUp)
    element.addEventListener("paste", ::onPasteEvent)
    //sinkEvents(Event.ONCONTEXTMENU) TODO
    addKeyDownListener(::onKeyDown)
    addFocusListener(::onFocus)
    //addBlurListener(::onBlur)
    // TODO : disable context menu from showing up.
    autocomplete = if (hasAutoComplete()) {
      Autocomplete.ON
    } else {
      Autocomplete.OFF
    }
  }

  companion object {
    var focusedTextField: InputTextField<*>? = null

    /**
     * Returns the last focused text field.
     * @return the last focused text field.
     */
    var lastFocusedTextField: InputTextField<*>? = null
      private set

    /**
     * Returns the last focused field connector.
     * @return the last focused text connector.
     */
    val lastFocusedField: Field?
      get() {
        return if (lastFocusedTextField != null) {
          lastFocusedTextField!!.fieldConnector
        } else {
          null
        }
      }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override fun setPresentationValue(newPresentationValue: String?) {
    content.value = newPresentationValue
  }

  fun addTextValueChangeListener(listener: HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<*, *>>): Registration {
    return field.addValueChangeListener(listener)
  }

  override fun initContent(): C = field

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
        if (!validationStrategy!!.validate(java.lang.String.valueOf(it))) {
          cancelKey()
        }
      }
      return
    }
    // validate the whole text input.
    if (validationStrategy != null) {
      event.key.keys.forEach {
        if (!validationStrategy!!.validate(value + java.lang.String.valueOf(it))) {
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
      val before: String = value

      if (!validationStrategy!!.validate(value)) {
        value = before
      } else {
        // even if it is not really correct, we mark the field as dirty after
        // a paste event.
        fieldConnector.isChanged = true
        fieldConnector.markAsDirty(record)
      }
    }
  }

  override fun setValue(text: String?) {
    var text = text

    // set record to synchronize view and model even field is not focused
    setRecord()
    // set only valid inputs
    if (validationStrategy is NoeditValidator
      || validationStrategy!!.validate(text)
    ) {
      if (text == null) {
        text = "" // avoid NullPointerException
      }
      if (text != value) {
        fieldConnector.isChanged = true
      }
      super.setValue(text)
    }
    if (text != null) {
      valueBeforeEdit = text
    }
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
  var size: Int
    get() = element.getProperty("size").toInt()
    set(value) { element.setProperty("size", value.toString()) }

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
    fieldConnector.unsetDirty()
  }

  /**
   * Sets the record number from the display line
   */
  protected fun setRecord() {
    val position = fieldConnector.position

    recordNumber = fieldConnector.columnView!!.getRecordFromDisplayLine(position)
  }

  /**
   * Returns the record number of this input widget.
   * @return The record number of this input widget.
   */
  protected val record: Int
    get() {
      if (recordNumber == -1) {
        // get the record from the display line when it is not set
        setRecord()
      }
      return recordNumber
    }

  /**
   * Called when the field value might have changed and/or the field was
   * blurred. These are combined so the blur event is sent in the same batch
   * as a possible value change event (these are often connected).
   *
   * @param blurred true if the field was blurred
   */
  fun valueChange(blurred: Boolean) {
    // if field is not changed give up
    // this can happen when the dirty values
    // are sent before blurring this field
    if (!fieldConnector.isChanged) {
      return
    }
    val rec = record
    // mark the field as dirty only when really changed
    if (!value.equals(fieldConnector.getCachedValueAt(rec))) {
      fieldConnector.markAsDirty(rec, value)
    }
    // field is left and a showing suggestions are
    // displayed ==> restore the old value of the field
    if (blurred) {
      maybeRestoreOldValue()
      if (fieldConnector.isDirty) {
        maybeSynchronizeWithServerSide()
      }
    } else {
      handleEnumerationFields(rec)
      maybeCheckValue(rec)
    }
  }

  /**
   * Restores the old value of the field when it is needed.
   * This can happen when the field is blurred and the suggestions
   * popup is showing.
   */
  protected fun maybeRestoreOldValue() {
    /*if (isShowingSuggestions) { TODO
      setText(fieldConnector.columnView!!.getValueAt(fieldConnector.position))
    }*/
  }

  /**
   * When the field is blurred and the navigation of this
   * field is delegated to server side, the state of this component
   * should be synchronized with the server side to have all necessary
   * triggers executed
   */
  protected fun maybeSynchronizeWithServerSide() {
    if (delegateNavigationToServer()
      && connector.needsSynchronization()
      && !isAlreadySynchronized
    ) {
      sendDirtyValuesToServerSide()
    }
  }

  /**
   * Checks the value of this input field when it is needed.
   * @param rec The record number of the field.
   */
  protected fun maybeCheckValue(rec: Int) {
    if (!isCheckingValue && !delegateNavigationToServer()) {
      try {
        checkValue(rec)
      } catch (e: CheckTypeException) {
        e.displayError()
      }
    }
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
   * Sends all dirty values to the server side.
   * This will send all pending dirty values to be sure
   * that all necessary values are sent to the server model.
   * TODO
   */
  internal fun sendDirtyValuesToServerSide() {
    val window = parentWindow
    val block = connector.parent.get() as? Block

    if (block != null) {
      window!!.cleanDirtyValues(block)
    }
    // now the field is synchronized with server side.
    isAlreadySynchronized = true
  }

  /**
   * Should the navigation be delegated to server side ?
   * @return `true` if the navigation is delegated to serevr side.
   */
  internal fun delegateNavigationToServer(): Boolean = fieldConnector.delegateNavigationToServer()

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
    // check if the field has really changed.
    if (isChanged) {
      fieldConnector.isChanged = true
    }
    valueBeforeEdit = value
  }// look to the lower and upper convert type to detect if the field value has really changed

  /**
   * Returns `true` when the field context is changed.
   * @return `true` when the field context is changed.
   */
  protected val isChanged: Boolean
    get() = if (validationStrategy is StringValidator) {
      val strategy = validationStrategy as StringValidator

      // look to the lower and upper convert type to detect if the field value has really changed
      when (strategy.getConvertType()) {
        ConvertType.UPPER -> value.toUpperCase() != valueBeforeEdit!!.toUpperCase()
        ConvertType.LOWER -> value.toLowerCase() != valueBeforeEdit!!.toLowerCase()
        else -> !value.equals(valueBeforeEdit)
      }
    } else {
      !value.equals(valueBeforeEdit)
    }

  /**
   * Checks if the decimal separator must be changed.
   */
  protected fun maybeReplaceDecimalSeparator() {
    if (validationStrategy is DecimalValidator && value.contains(".")) {
      val dfs: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale(MainWindow.locale)) // TODO
      if (dfs.decimalSeparator != '.') {
        value = value.replace('.', dfs.decimalSeparator)
      }
    }
  }

  fun onValueChange(event: HasValue.ValueChangeEvent<String?>?) {
    // FIXME : this causes the block of the UI after suggestion selection
    // delegateEvent(this, event);
    valueChange(false)
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

  /**
   * Fires a goto next field event.
   */
  fun gotoNextBlockTextInput() {
    // first send dirty values to server side.
    sendDirtyValuesToServerSide()
    fieldConnector.columnView!!.gotoNextField()
  }

  protected open fun onLoad() {
    //super.onLoad() TODO
    //Scheduler.get().scheduleFinally(object : ScheduledCommand() {
    //  fun execute() {
    //    parent = WidgetUtils.getParent(this@VInputTextField, VWindow::class.java)
    //  }
    //})
  }

  open fun onBlur(event: BlurNotifier.BlurEvent<InputTextField<C>>) {
    // this is called twice on Chrome when e.g. changing tab while prompting
    // field focused - do not change settings on the second time
    if (focusedTextField !== this || focusedTextField == null) {
      return
    }
    removeStyleDependentName("focus")
    focusedTextField = null
    valueChange(true)
    lazyHideSuggestions()
    recordNumber = -1 // set this field is not related to any record
  }

  open fun onFocus(event: FocusNotifier.FocusEvent<InputTextField<C>>) {
    if (focusedTextField == this) {
      // already got the focus. give up
      return
    }
    focusedTextField = this
    setRecord()
    fieldConnector.columnView!!.disableAllBlocksActors()
    addStyleDependentName("focus")
    setLastFocusedInput()
    // cancel the fetch of suggestions list on field focus
    // when the field is not empty
    maybeCancelSuggestions()
    fieldConnector.columnView!!.setAsActiveField(-1)
    fieldConnector.isChanged = false
    isAlreadySynchronized = false
    valueBeforeEdit = value
    // activate all actors related to this field.
    fieldConnector.setActorsEnabled(true)
    // ensure the selection of the field content.
    maybeSelectAll()
  }

  fun selectAll() {
    //Scheduler.get().scheduleFinally(object : ScheduledCommand() {
    //  fun execute() {
    //    if (focusedTextField == this@VInputTextField) {
    //      super.selectAll()
    //    }
    //  }
    //})
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
   * Selects the content of this text input
   */
  private fun maybeSelectAll() {
    if (value != null && value.isNotEmpty()) {
      selectAll()
    }
  }

  /**
   * Sets the last focused input field.
   */
  private fun setLastFocusedInput() {
    if (parent != null) {
      lastFocusedTextField = this
      //parent.setLastFocusedTextBox(lastFocusedTextField)
    }
  }

  /**
   * Cancel suggestions query if needed.
   */
  private fun maybeCancelSuggestions() {
    if (value == null || value.isEmpty()) {
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
      validationStrategy!!.checkType(this, if (value == null) "" else value.trim())
      if (!value.equals(fieldConnector.getCachedValueAt(rec))) {
        connector.markAsDirty(rec, value)
      }
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
    val text: String = value

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
  internal val fieldConnector: Field
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

  *//**
   * Sets the suggestion oracle used to create suggestions.
   *
   * @param oracle the oracle
   *//*
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
  /*package*/
  fun showSuggestions(query: String?) {
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
   * Sets the limit to the number of suggestions the oracle should provide. It
   * is up to the oracle to enforce this limit.
   *
   * @param limit the limit to the number of suggestions provided
   */
  fun setLimit(limit: Int) {
    this.limit = limit
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
    valueBeforeEdit = null
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
