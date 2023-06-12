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
package org.kopi.galite.visual.ui.vaadin.form

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.AttachEvent
import org.kopi.galite.visual.form.ModelTransformer
import org.kopi.galite.visual.form.UTextField
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties

import com.vaadin.flow.component.contextmenu.ContextMenu

/**
 * The `DTextField` is the vaadin implementation
 * of the [UTextField] specifications.
 *
 * @param model The row controller.
 * @param label The field label.
 * @param align The field alignment.
 * @param options The field options.
 * @param detail Does the field belongs to the detail view ?
 */
open class DTextField(
        model: VFieldUI,
        label: DLabel?,
        align: Int,
        options: Int,
        detail: Boolean,
) : DField(model, label, align, options, detail), UTextField {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private val field: TextField // the text component
  protected var inside = false
  protected var noEdit = options and VConstants.FDO_NOEDIT != 0
  protected var scanner = options and VConstants.FDO_NOECHO != 0 && getModel().height > 1
  private var selectionAfterUpdateDisabled = false
  protected var transformer: ModelTransformer? = null

  init {
    transformer = if (getModel().height == 1
            || !scanner && getModel().getTypeOptions() and VConstants.FDO_DYNAMIC_NL > 0) {
      DefaultTransformer(getModel().width,
                         getModel().height)
    } else if (!scanner) {
      NewlineTransformer(
        getModel().width,
        getModel().height
      )
    } else {
      ScannerTransformer(this)
    }
    field = createFieldGUI(options and VConstants.FDO_NOECHO != 0, scanner, align)

    field.inputField.addTextValueChangeListener {
      if (it.isFromClient) {
        valueChanged()
      }
    }

    createContextMenu()
    setFieldContent(field)
  }

  override fun valueChanged() {
    val value = text
    println("--DTextF----valueChanged-text- ::"+text)
    println("isChanged(getModel().getText(), value) ===== "+isChanged(getModel().getText(), value))
    println("getModel().getText() ===== "+getModel().getText())
    println("value ===== "+value)
    checkText(value, true) // isChanged(getModel().getText(), value))

//    if (isChanged(getModel().getText(), value)) {
//      checkText(value)
//    }
  }

  /**
   * Returns `true` if there is a difference between the old and the new text.
   * @param oldText The old text value.
   * @param newText The new text value.
   * @return `true` if there is a difference between the old and the new text.
   */
  protected fun isChanged(oldText: String?, newText: String?): Boolean {
    return oldText != newText
  }

  // --------------------------------------------------
  // CREATE FIELD UI
  // --------------------------------------------------
  /**
   * Creates the field UI component.
   * @param noEcho Password field ?
   * @param scanner Scanner field ?
   * @param align The field alignment.
   * @return The [TextField] object.
   */
  private fun createFieldGUI(noEcho: Boolean,
                             scanner: Boolean,
                             align: Int): TextField {

    return TextField(getModel(),
                     noEcho,
                     scanner,
                     noEdit,
                     align,
                     model.hasAutofill(),
                     this)
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------7
  override fun updateAccess() {
    super.updateAccess()
    label!!.update(model, getBlockView().getRecordFromDisplayLine(position))
    access(currentUI) {
      field.isEnabled = access >= VConstants.ACS_VISIT
      isEnabled = access >= VConstants.ACS_VISIT
    }
  }

  override fun updateText() {
    val newModelTxt = getModel().getText(getBlockView().getRecordFromDisplayLine(position))
    access(currentUI) {
      println("transformer!!.toGui(newModelTxt)?.trim() :: "+transformer!!.toGui(newModelTxt)?.trim())
      field.value = transformer!!.toGui(newModelTxt)?.trim() // FIXME
    }
  //  super.updateText()
    if (modelHasFocus() && !selectionAfterUpdateDisabled) {
      selectionAfterUpdateDisabled = false
    }
  }

  override fun updateColor() {
    styleManager.createAndApplyStyle(field.inputField, getModel().align, foreground, background)
  }

  override fun updateFocus() {
    label!!.update(model, position)
    if (!modelHasFocus()) {
      if (inside) {
        inside = false
       // leaveMe()
      }
    } else {
      if (!inside) {
        inside = true
        enterMe()
      }
    }
    super.updateFocus()
  }

  override fun forceFocus() {
    enterMe()
  }

  /**
   * Gets the focus to this field.
   */
  @Synchronized
  private fun enterMe() {
    access(currentUI) {
      if (scanner) {
        println("transformer!!.toGui() :: "+transformer!!.toGui(""))
        field.value = transformer!!.toGui("")
      }
      field.focus()
    }
  }

  /**
   * Leaves the field.
   */
  @Synchronized
  private fun leaveMe() {
    reInstallSelectionFocusListener()
    // update GUI: for
    // scanner nescessary
    if (scanner) {
      // trick: it is now displayed on a different way
      access(currentUI) {
        println("transformer?.toModel(getText()) :: "+transformer?.toModel(text))
        field.value = transformer?.toModel(getText())
      }
    }
  }

  /**
   * Check the given text against model definition.
   *
   * @param s The text to be verified.
   * @throws VException Errors occurs during check.
   */
  private fun checkText(s: String?, changed: Boolean) {
    println("------DTextField---checkText----s- :: "+s)
    println("------DTextField---checkText---transformer!!.toModel()- :: "+transformer!!.toModel(s ?: ""))
    val text = transformer!!.toModel(s ?: "")
    if (!transformer!!.checkFormat(text)) {
      return
    }
  //  getModel().onTextChange(text!!)

    if (getModel().checkText(text!!) && changed) {
      getModel().isChangedUI = true
    }
    getModel().setChanged(changed)
  }

  //---------------------------------------------------
  // TEXTFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getText(): String? {
//    return when (val value: Any? = this.field.value) {
//      is LocalDate -> value.format()
//      is LocalTime -> value.format()
//      is Instant -> value.format()
//      is LocalDateTime -> value.format()
//      else -> value?.toString()
//    }

    return getModel().toText(field.value)
    //return transformer!!.toModel(field.value.orEmpty())
  }

  override fun setHasCriticalValue(b: Boolean) {
    // ignore
  }

  override fun addSelectionFocusListener() {
    // ignore
  }

  override fun removeSelectionFocusListener() {
    // ignore
  }

  /**
   * Reinstalls the focus listener.
   */
  open fun reInstallSelectionFocusListener() {
    removeSelectionFocusListener()
    addSelectionFocusListener()
  }

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {
    selectionAfterUpdateDisabled = disable
  }

  //---------------------------------------------------
  // DFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getObject(): Any? {
    return wrappedField.value
  }

  override fun setBlink(blink: Boolean) {
    access(currentUI) {
      field.setBlink(blink)
    }
  }

  /**
   * Default implementation of the [ModelTransformer]
   *
   * @param col The column index.
   * @param row The row index.
   */
  inner class DefaultTransformer(var col: Int, var row: Int) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toGui(modelTxt: String?): String? {
      return modelTxt
    }

    override fun toModel(guiTxt: String?): String? {
      return guiTxt
    }

    override fun checkFormat(guiTxt: String?): Boolean {
      return if (row == 1) true else convertToSingleLine(guiTxt, col, row).length <= row * col
    }
  }

  /**
   * A scanner model transformer.
   */
  internal class ScannerTransformer(private val field: DTextField) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toGui(modelTxt: String?): String {
      return if (modelTxt == null || "" == modelTxt) {
        VlibProperties.getString("scan-ready")
      } else if (!field.field.isReadOnly) {
        VlibProperties.getString("scan-read") + " " + modelTxt
      } else {
        VlibProperties.getString("scan-finished")
      }
    }

    override fun toModel(guiTxt: String?): String? {
      return guiTxt
    }

    override fun checkFormat(guiTxt: String?): Boolean {
      return true
    }
  }

  /**
   * New line model transformer.
   *
   * @param col The column index.
   * @param row The row index.
   */
  inner class NewlineTransformer(private val col: Int, private val row: Int) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toModel(guiTxt: String?): String {
      return convertFixedTextToSingleLine(guiTxt, col, row)
    }

    override fun toGui(modelTxt: String?): String =
      buildString {
        val length = modelTxt!!.length
        var usedRows = 1
        var start = 0
        while (start < length) {
          val line = modelTxt.substring(start, (start + col).coerceAtMost(length))
          var last = -1
          var i = line.length - 1
          while (last == -1 && i >= 0) {
            if (!Character.isWhitespace(line[i])) {
              last = i
            }
            --i
          }
          if (last != -1) {
            append(line.substring(0, last + 1))
          }
          if (usedRows < row) {
            if (start + col < length) {
              append('\n')
            }
            usedRows++
          }
          start += col
        }
      }

    override fun checkFormat(guiTxt: String?): Boolean = guiTxt!!.length <= row * col
  }

  /**
   * Add the field context menu.
   */
  protected fun createContextMenu() {
    if (model.hasAutofill() && getModel().getDefaultAccess() > VConstants.ACS_SKIPPED) {
      val contextMenu = ContextMenu()
      contextMenu.addItem(VlibProperties.getString("item-index")) {
        performAutoFillAction()
      }
      //.setData(VlibProperties.getString("item-index")) TODO

      println("ooooooooooooooo  :: "+field.value)
      contextMenu.target = field
    }
  }

  companion object {
    /**
     * Converts a given string to a line string.
     * @param source The source text.
     * @param col The column index.
     * @param row The row index.
     * @return The converted string.
     */
    private fun convertToSingleLine(source: String?, col: Int, row: Int): String =
            buildString {
              val length = source!!.length
              var start = 0
              while (start < length) {
                var index = source.indexOf('\n', start)
                if (index - start < col && index != -1) {
                  append(source.substring(start, index))
                  for (j in index - start until col) {
                    append(' ')
                  }
                  start = index + 1
                  if (start == length) {
                    // last line ends with a "new line" -> add an empty line
                    for (j in 0 until col) {
                      append(' ')
                    }
                  }
                } else {
                  if (start + col >= length) {
                    append(source.substring(start, length))
                    for (j in length until start + col) {
                      append(' ')
                    }
                    start = length
                  } else {
                    // find white space to break line
                    var i = start + col - 1
                    while (i > start) {
                      if (Character.isWhitespace(source[i])) {
                        break
                      }
                      i--
                    }
                    index = if (i == start) {
                      start + col
                    } else {
                      i + 1
                    }
                    append(source.substring(start, index))
                    var j = (index - start) % col
                    while (j != 0 && j < col) {
                      append(' ')
                      j++
                    }
                    start = index
                  }
                }
              }
            }

    /**
     * Converts a given string to a fixed line string.
     * @param source The source text.
     * @param col The column index.
     * @param row The row index.
     * @return The converted string.
     */
    private fun convertFixedTextToSingleLine(source: String?, col: Int, row: Int): String =
            buildString {
              val length = source!!.length
              var start = 0
              while (start < length) {
                var index = source.indexOf('\n', start)
                if (index - start < col && index != -1) {
                  append(source.substring(start, index))
                  for (j in index - start until col) {
                    append(' ')
                  }
                  start = index + 1
                  if (start == length) {
                    // last line ends with a "new line" -> add an empty line
                    for (j in 0 until col) {
                      append(' ')
                    }
                  }
                } else {
                  if (start + col >= length) {
                    append(source.substring(start, length))
                    for (j in length until start + col) {
                      append(' ')
                    }
                    start = length
                  } else {
                    // find white space to break line
                    var i = start + col
                    while (i > start) {
                      if (Character.isWhitespace(source[i])) {
                        break
                      }
                      i--
                    }
                    index = if (i == start) {
                      start + col
                    } else {
                      i
                    }
                    append(source.substring(start, index))
                    for (j in index - start until col) {
                      append(' ')
                    }
                    start = index + 1
                  }
                }
              }
            }
  }
}
