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

import java.util.Locale

import org.kopi.galite.visual.ui.vaadin.base.ShortcutAction
import org.kopi.galite.visual.ui.vaadin.base.runAfterGetValue
import org.kopi.galite.visual.ui.vaadin.form.DRichTextEditor
import org.vaadin.pekka.WysiwygE

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.dom.Element

/**
 * A rich text field implementation based on wysiwyg-e
 */

@CssImport("./styles/galite/richtext.css")
class RichTextField(
        var col: Int,
        var rows: Int,
        visibleRows: Int,
        var noEdit: Boolean,
        locale: Locale, // TODO
        val parent: DRichTextEditor
) : AbstractField<String?>() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private val editor = FocusableWysiwygE(true)

  /**
   * Minimal field width to see the toolbar in 56 px height (2 lines)
   */
  private val MIN_WIDTH = 540
  private val LINE_HEIGHT = 20
  private val TOOLBAR_HEIGHT = 66

  init {
    editor.className ="richtext"
    editor.placeholder = ""
    add(editor)
    editor.setHeight((TOOLBAR_HEIGHT + LINE_HEIGHT * visibleRows).toFloat(), Unit.PIXELS)
    editor.isReadOnly = noEdit

    if (8 * col < MIN_WIDTH) { // FIXME: FontMetrics.LETTER.width always return 0
      editor.setWidth(MIN_WIDTH.toFloat(), Unit.PIXELS)
    } else {
      editor.setWidth((8 * col).toFloat(), Unit.PIXELS)
    }
    createNavigatorKeys()
  }

  /**
   * Creates the configuration to be used for this rich text.
   * @return the configuration to be used for this rich text.
   */
  protected fun createConfiguration(locale: Locale, visibleRows: Int) {
    // The component CKEditorVaadin contain a Config
    // access {
    /* val configuration: Config = Config()
      // configuration.useCompactTags()
      // configuration.disableElementsPath()
     configuration.setUILanguage(Constants.Language.valueOf(locale.language))
      // configuration.disableSpellChecker()
      // configuration.setHeight(RichTextField.LINE_HEIGHT * visibleRows.toString() + "px")
      /* configuration.addExtraConfig("toolbarGroups", createEditorToolbarGroups())
       configuration.addExtraConfig("removeButtons", getRemovedToolbarButtons())
       configuration.addExtraConfig("contentsCss", "'" + Utils.getThemeResourceURL("ckeditor.css").toString() + "'")*/
       editor!!.config = configuration*/
    // }
  }

  /**
   * Creates the editor toolbar groups configurations
   * @return The editor toolbar groups configurations
   */
  protected fun createEditorToolbarGroups(): String {
    return buildString {
      append("[")
      append("{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },")
      append("{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },")
      append("{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },")
      append("'/',")
      append("{ name: 'styles', groups: [ 'styles' ] },")
      append("{ name: 'links', groups: [ 'links' ] },")
      append("{ name: 'colors', groups: [ 'colors' ] },")
      append("{ name: 'insert', groups: [ 'insert' ] },")
      append("{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },")
      append("{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },")
      append("{ name: 'forms', groups: [ 'forms' ] },")
      append("{ name: 'tools', groups: [ 'tools' ] },")
      append("'/',")
      append("{ name: 'others', groups: [ 'others' ] },")
      append("{ name: 'about', groups: [ 'about' ] }")
      append("]")
    }
  }

  protected fun getRemovedToolbarButtons(): String {
    return ("'Blockquote,"
            + "CreateDiv,"
            + "BidiLtr,"
            + "BidiRtl,"
            + "Language,"
            + "Source,"
            + "Save,"
            + "Templates,"
            + "NewPage,"
            + "Preview,"
            + "Print,"
            + "Anchor,"
            + "Flash,"
            + "Smiley,"
            + "PageBreak,"
            + "Iframe,"
            + "PasteFromWord,"
            + "PasteText,"
            + "Paste,"
            + "ImageButton,"
            + "Button,"
            + "Select,"
            + "Textarea,"
            + "TextField,"
            + "Radio,"
            + "Checkbox,"
            + "HiddenField,"
            + "Form,"
            + "Styles,"
            + "About,"
            + "Replace,"
            + "SelectAll,"
            + "RemoveFormat,"
            + "CopyFormatting,"
            + "ShowBlocks'")
  }


  override fun setReadOnly(readOnly: Boolean) {
    editor.isReadOnly = readOnly
  }

  override fun isReadOnly() = editor.isReadOnly

  override fun setRequiredIndicatorVisible(requiredIndicatorVisible: Boolean) {
    editor.isRequiredIndicatorVisible = requiredIndicatorVisible
  }

  override fun isRequiredIndicatorVisible() = editor.isRequiredIndicatorVisible

  /**
   * Registers a text change listener
   * @param l The text change listener.
   */
  fun addTextValueChangeListener(l: HasValue.ValueChangeListener<ComponentValueChangeEvent<*, *>>) {
    editor.addValueChangeListener(l)
  }

  //---------------------------------------------------
  // NAVGATION
  //---------------------------------------------------

  override fun setPresentationValue(newPresentationValue: String?) {
    editor.value = newPresentationValue
  }

  override fun getValue() = editor.value

  override fun setValue(value: String?) {
    editor.value = value
  }

  /**
   * Checks if the content of this field is empty.
   * @return `true` if this field is empty.
   */
  override val isNull: Boolean
    get() =  value == null

  /**
   * Checks the internal value of this field.
   * @param rec The active record.
   */
  override fun checkValue(rec: Int) {}

  override fun addFocusListener(function: () -> kotlin.Unit) {
    editor.addFocusListener {
      function()
    }
  }

  override fun focus() {
    editor.focus()
  }

  override fun getContent(): Component = editor

  /**
   * Creates the navigation actions.
   */
  fun createNavigatorKeys() {
    addKeyNavigator(Key.ENTER, KeyModifier.of("Control")) { parent.gotoNextEmptyMustfill() }
    addKeyNavigator(Key.ENTER, KeyModifier.of("Shift")) { parent.onGotoNextBlock() }
    addKeyNavigator(Key.PAGE_DOWN, KeyModifier.of("Shift")) { parent.gotoNextRecord() }
    addKeyNavigator(Key.PAGE_UP, KeyModifier.of("Shift")) { parent.gotoPrevRecord() }
    addKeyNavigator(Key.HOME, KeyModifier.of("Shift")) { parent.gotoFirstRecord() }
    addKeyNavigator(Key.END, KeyModifier.of("Shift")) { parent.gotoLastRecord() }
    //addKeyNavigator(Key.ARROW_LEFT, KeyModifier.of("Control")) { parent.gotoPrevField() } // FIXME: WysiwygE defines already this shortcut
    addKeyNavigator(Key.TAB, KeyModifier.of("Shift")) { parent.gotoPrevField() }
    addKeyNavigator(Key.TAB) { parent.gotoNextField() }
    addKeyNavigator(Key.ARROW_UP, KeyModifier.of("Shift")) { parent.gotoPrevField() }
    //addKeyNavigator(Key.ARROW_RIGHT, KeyModifier.of("Control")) { parent.gotoNextField() } // FIXME: WysiwygE defines already this shortcut
    addKeyNavigator(Key.ARROW_DOWN, KeyModifier.of("Shift")) { parent.gotoNextField() }
  }

  /**
   * Adds a key navigator action to this handler.
   * @param key The key code.
   * @param modifiers The modifiers.
   * @param navigationAction lambda representing the action to perform
   */
  private fun addKeyNavigator(key: Key, vararg modifiers: KeyModifier, navigationAction: () -> kotlin.Unit) {
    NavigationAction(this, key, modifiers, navigationAction)
      .registerShortcut()
  }

  /**
   * A navigation action
   */
  inner class NavigationAction(
    field: RichTextField,
    key: Key,
    modifiers: Array<out KeyModifier>,
    navigationAction: () -> kotlin.Unit
  ) : ShortcutAction<RichTextField>(field, key, modifiers, navigationAction) {

    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    override fun performAction() {
      editor.runAfterGetValue {
        // first sends the text value to model if changed
        parent.valueChanged()
        navigationAction()
      }
    }
  }
}

@Tag("wysiwyg-e-rich-text")
@JsModule("./src/wysiwyg-e-rich-text.js")
class FocusableWysiwygE(allToolsVisible: Boolean): WysiwygE(allToolsVisible), Focusable<FocusableWysiwygE> {
  init {
    // Ident and outdent tools not working and they define shortcuts which doesn't
    // allow to define a custom shortcut in server side.
    getTool(Tool.INDENT)?.removeFromParent()
    getTool(Tool.OUTDENT)?.removeFromParent()
  }

  fun getTool(tool: Tool): Element? {
    val tag = tool.name.toLowerCase()

    return element.children.filter { element: Element ->
      element.tag.endsWith(tag)
    }.findAny().orElseGet(null)
  }
}
