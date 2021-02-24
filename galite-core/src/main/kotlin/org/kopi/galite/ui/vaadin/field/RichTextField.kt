/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: RichTextField.java 35230 2017-09-13 18:27:19Z hacheni $
 */
package org.kopi.galite.ui.vaadin.field

import com.vaadin.server.PaintException
import java.io.Serializable
import java.util.*

/**
 * A rich text field implementation based on CKEditor
 */
class RichTextField(
        col: Int,
        rows: Int,
        visibleRows: Int,
        noEdit: Boolean,
        locale: Locale
) : CKEditorTextField() {
  protected val state: RichTextFieldState
    protected get() = super.getState() as RichTextFieldState

  @Throws(PaintException::class)
  fun paintContent(target: PaintTarget) {
    super.paintContent(target)
    target.addAttribute(VCKEditorTextField.ATTR_IMMEDIATE, isImmediate())
  }

  protected val explicitImmediateValue: Boolean
    protected get() = true

  /**
   * Creates the configuration to be used for this rich text.
   * @return the configuration to be used for this rich text.
   */
  protected fun createConfiguration(locale: Locale, visibleRows: Int) {
    BackgroundThreadHandler.access(Runnable {
      val configuration: CKEditorConfig
      configuration = CKEditorConfig()
      configuration.useCompactTags()
      configuration.disableElementsPath()
      configuration.setLanguage(locale.language)
      configuration.disableSpellChecker()
      configuration.setHeight(LINE_HEIGHT * visibleRows.toString() + "px")
      configuration.addExtraConfig("toolbarGroups", createEditorToolbarGroups())
      configuration.addExtraConfig("removeButtons", removedToolbarButtons)
      configuration.addExtraConfig("contentsCss", "'" + Utils.getThemeResourceURL("ckeditor.css").toString() + "'")
      setConfig(configuration)
    })
  }

  /**
   * Creates the editor toolbar groups configurations
   * @return The editor toolbar groups configurations
   */
  protected fun createEditorToolbarGroups(): String {
    val toolbarGroups: StringBuffer
    toolbarGroups = StringBuffer()
    toolbarGroups.append("[")
    toolbarGroups.append("{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },")
    toolbarGroups.append("{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },")
    toolbarGroups.append("{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },")
    toolbarGroups.append("'/',")
    toolbarGroups.append("{ name: 'styles', groups: [ 'styles' ] },")
    toolbarGroups.append("{ name: 'links', groups: [ 'links' ] },")
    toolbarGroups.append("{ name: 'colors', groups: [ 'colors' ] },")
    toolbarGroups.append("{ name: 'insert', groups: [ 'insert' ] },")
    toolbarGroups.append("{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },")
    toolbarGroups.append("{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },")
    toolbarGroups.append("{ name: 'forms', groups: [ 'forms' ] },")
    toolbarGroups.append("{ name: 'tools', groups: [ 'tools' ] },")
    toolbarGroups.append("'/',")
    toolbarGroups.append("{ name: 'others', groups: [ 'others' ] },")
    toolbarGroups.append("{ name: 'about', groups: [ 'about' ] }")
    toolbarGroups.append("]")
    return toolbarGroups.toString()
  }

  protected val removedToolbarButtons: String
    protected get() = ("'Blockquote,"
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
  //---------------------------------------------------
  // NAVGATION
  //---------------------------------------------------
  /**
   * Registers a navigation listener on this rich text.
   * @param l The listener to be registered.
   */
  fun addNavigationListener(l: NavigationListener) {
    navigationListeners.add(l)
  }

  /**
   * RPC navigation handler
   */
  inner class NavigationHandler : RichTextFieldNavigationServerRpc {
    fun gotoNextField() {
      println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx NEXT FIELD")
      for (l in navigationListeners) {
        l.onGotoNextField()
      }
    }

    fun gotoPrevField() {
      println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx PREV FIELD")
      for (l in navigationListeners) {
        l.onGotoPrevField()
      }
    }

    fun gotoNextBlock() {
      for (l in navigationListeners) {
        l.onGotoNextBlock()
      }
    }

    fun gotoPrevRecord() {
      for (l in navigationListeners) {
        l.onGotoPrevRecord()
      }
    }

    fun gotoNextRecord() {
      for (l in navigationListeners) {
        l.onGotoNextRecord()
      }
    }

    fun gotoFirstRecord() {
      for (l in navigationListeners) {
        l.onGotoFirstRecord()
      }
    }

    fun gotoLastRecord() {
      for (l in navigationListeners) {
        l.onGotoLastRecord()
      }
    }

    fun gotoNextEmptyMustfill() {
      for (l in navigationListeners) {
        l.onGotoNextEmptyMustfill()
      }
    }
  }

  /**
   * The grid editor field navigation listener
   */
  interface NavigationListener : Serializable {
    /**
     * Fired when a goto next field event is called by the user.
     */
    fun onGotoNextField()

    /**
     * Fired when a goto previous field event is called by the user.
     */
    fun onGotoPrevField()

    /**
     * Fired when a goto next block event is called by the user.
     */
    fun onGotoNextBlock()

    /**
     * Fired when a goto previous record event is called by the user.
     */
    fun onGotoPrevRecord()

    /**
     * Fired when a goto next field event is called by the user.
     */
    fun onGotoNextRecord()

    /**
     * Fired when a goto first record event is called by the user.
     */
    fun onGotoFirstRecord()

    /**
     * Fired when a goto last record event is called by the user.
     */
    fun onGotoLastRecord()

    /**
     * Fired when a goto next empty mandatory field event is called by the user.
     */
    fun onGotoNextEmptyMustfill()
  }

  private val navigationListeners: MutableList<NavigationListener>

  companion object {
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    /**
     * Minimal field width to see the toolbar in 56 px height (2 lines)
     */
    private const val MIN_WIDTH = 540
    private const val LINE_HEIGHT = 20
    private const val TOOLBAR_HEIGHT = 66
  }
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates the rich text field server component.
   * @param col The column number.
   * @param rows The rows number.
   * @param visibleRows The visible rows number.
   * @param noEdit Is it no edit field ?
   * @param locale The locale to be used for this rich text
   */
  init {
    state.col = col
    state.rows = rows
    state.noEdit = noEdit
    createConfiguration(locale, visibleRows)
    setHeight(TOOLBAR_HEIGHT + LINE_HEIGHT * visibleRows, Unit.PIXELS)
    setReadOnly(noEdit)
    if (FontMetrics.LETTER.getWidth() * col < MIN_WIDTH) {
      setWidth(MIN_WIDTH, Unit.PIXELS)
    } else {
      setWidth(FontMetrics.LETTER.getWidth() * col, Unit.PIXELS)
    }
    navigationListeners = ArrayList()
    registerRpc(NavigationHandler())
  }
}