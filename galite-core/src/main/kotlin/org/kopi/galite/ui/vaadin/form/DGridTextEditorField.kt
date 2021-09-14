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
package org.kopi.galite.ui.vaadin.form

import org.kopi.galite.form.ModelTransformer
import org.kopi.galite.form.UTextField
import org.kopi.galite.form.VCodeField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.form.VFixnumField
import org.kopi.galite.form.VIntegerField
import org.kopi.galite.form.VMonthField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.VTimeField
import org.kopi.galite.form.VTimestampField
import org.kopi.galite.form.VWeekField
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.grid.GridEditorDateField
import org.kopi.galite.ui.vaadin.grid.GridEditorEnumField
import org.kopi.galite.ui.vaadin.grid.GridEditorField
import org.kopi.galite.ui.vaadin.grid.GridEditorFixnumField
import org.kopi.galite.ui.vaadin.grid.GridEditorIntegerField
import org.kopi.galite.ui.vaadin.grid.GridEditorMonthField
import org.kopi.galite.ui.vaadin.grid.GridEditorTextAreaField
import org.kopi.galite.ui.vaadin.grid.GridEditorTextField
import org.kopi.galite.ui.vaadin.grid.GridEditorTimeField
import org.kopi.galite.ui.vaadin.grid.GridEditorTimestampField
import org.kopi.galite.ui.vaadin.grid.GridEditorWeekField
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties

import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TextRenderer
import com.vaadin.flow.component.AbstractField

/**
 * A grid text editor based on custom components.
 */
class DGridTextEditorField(
        columnView: VFieldUI,
        label: DGridEditorLabel?,
        align: Int,
        options: Int
) : DGridEditorField<String>(columnView, label, align, options), UTextField {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected var inside = false
  protected var scanner = options and VConstants.FDO_NOECHO != 0 && getModel().height > 1
  private var selectionAfterUpdateDisabled = false
  protected var transformer: ModelTransformer

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  init {
    transformer = if (getModel().height == 1 || !scanner && getModel().getTypeOptions() and VConstants.FDO_DYNAMIC_NL > 0) {
      DefaultTransformer(getModel().width, getModel().height)
    } else if (!scanner) {
      NewlineTransformer(getModel().width, getModel().height)
    } else {
      ScannerTransformer(editor)
    }
    editor.addValueChangeListener(::valueChanged)
    // TODO
  }

  fun valueChanged(event: AbstractField.ComponentValueChangeEvent<GridEditorField<String>, String>) {
    if(event.isFromClient) {
      checkText(event.value.toString(), true)
    }
  }

  override fun valueChanged(oldValue: String?) {
    checkText(editor.value, isChanged(oldValue, editor.value))
  }

  override fun getObject(): Any? = editor.value

  override fun updateText() {
    val newModelTxt = getModel().getText(getBlockView().getRecordFromDisplayLine(position))
    access(currentUI) {
      //editor.value = transformer.toGui(newModelTxt)!!.trim() FIXME
      editor.value = transformer.toGui(newModelTxt)
    }
    if (modelHasFocus() && !selectionAfterUpdateDisabled) {
      selectionAfterUpdateDisabled = false
    }
  }

  override fun updateFocus() {
    if (!modelHasFocus()) {
      if (inside) {
        inside = false
        leaveMe()
      }
    } else {
      if (!inside) {
        inside = true
        enterMe()
      }
    }
    super.updateFocus()
  }

  override val nullRepresentation: String?
    get() = ""

  override fun reset() {
    inside = false
    selectionAfterUpdateDisabled = false
    super.reset()
  }

  override fun getText(): String? = editor.value?.toString()

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {
    selectionAfterUpdateDisabled = disable
  }

  override fun createEditor(): GridEditorTextField {

    val editor: GridEditorTextField = createEditorField()
    //editor.setAlignment(columnView.getModel().getAlign()) TODO
    //editor.setAutocompleteLength(columnView.getModel().getAutocompleteLength())
    //editor.setHasAutocomplete(columnView.getModel().hasAutocomplete())
    //editor.setNavigationDelegationMode(getNavigationDelegationMode())
    if (columnView.hasAutofill()) {
      editor.setAutofill()
    }
    //editor.setHasPreFieldTrigger(columnView.getModel().hasTrigger(VConstants.TRG_PREFLD))
    editor.addActors(actors)
    //editor.setConvertType(getConvertType(columnView.model))
    return editor
  }

  override fun createConverter(): Converter<String, Any?> {
    return object : Converter<String, Any?> {
      override fun convertToPresentation(value: Any?, context: ValueContext?): String? {
        return transformer.toGui(getModel().toText(value))
      }

      override fun convertToModel(value: String?, context: ValueContext?): Result<Any?> {
        return try {
          Result.ok(getModel().toObject(transformer.toModel(value)!!))
        } catch (e: VException) {
          Result.error(e.message)
        }
      }
    }
  }

  override fun createRenderer(): Renderer<String> {
    return TextRenderer()
  }

  override fun format(input: Any?): Any? = transformer.toGui(getModel().toText(input))

  /**
   * Creates an editor field according to the field model.
   * @return The created editor field.
   */
  protected fun createEditorField(): GridEditorTextField {
    return if (getModel() is VStringField) {
      // string field & text area
      if (getModel().height > 1) {
        createTextEditorField()
      } else {
        createStringEditorField()
      }
    } else if (getModel() is VIntegerField) {
      // integer fields
      createIntegerEditorField()
    } else if (getModel() is VMonthField) {
      // month field
      createMonthEditorField()
    } else if (getModel() is VDateField) {
      // date field
      createDateEditorField()
    } else if (getModel() is VWeekField) {
      // week field
      createWeekEditorField()
    } else if (getModel() is VTimeField) {
      // time field
      createTimeEditorField()
    } else if (getModel() is VCodeField) {
      // code field
      createEnumEditorField()
    } else if (getModel() is VFixnumField) {
      createFixnumEditorField()
    } else if (getModel() is VTimestampField) {
      createTimestampEditorField()
    } else {
      throw IllegalArgumentException("unknown field model : " + getModel().javaClass.name)
    }
  }

  /**
   * Creates a string editor for the grid block.
   * @return The created editor
   */
  protected fun createStringEditorField(): GridEditorTextField {
    return GridEditorTextField(getModel().width)
  }

  /**
   * Creates a text editor for the grid block.
   * @return The created editor
   */
  protected fun createTextEditorField(): GridEditorTextAreaField {
    val scanner = options and VConstants.FDO_NOECHO != 0 && getModel().height > 1
    return GridEditorTextAreaField(if (scanner) 40 else getModel().width,
                                   getModel().height,
                                   (getModel() as VStringField).getVisibleHeight(),
                                   !scanner && options and VConstants.FDO_DYNAMIC_NL > 0)
  }

  /**
   * Creates an integer editor for the grid block.
   * @return The created editor
   */
  protected fun createIntegerEditorField(): GridEditorIntegerField {
    val model = getModel() as VIntegerField
    return GridEditorIntegerField(model.width,
                                  model.maxValue,
                                  model.maxValue)
  }

  /**
   * Creates a deciaml editor for the grid block.
   * @return The created editor
   */
  protected fun createFixnumEditorField(): GridEditorFixnumField {
    val model = getModel() as VFixnumField
    return GridEditorFixnumField(model.width,
                                 model.maxValue.toDouble(),
                                 model.maxValue.toDouble(),
                                 model.maxScale,
                                 model.isFraction)
  }

  /**
   * Creates an month editor for the grid block.
   * @return The created editor
   */
  protected fun createEnumEditorField(): GridEditorEnumField {
    return GridEditorEnumField(getModel().width, (getModel() as VCodeField).labels)
  }

  /**
   * Creates an month editor for the grid block.
   * @return The created editor
   */
  protected fun createMonthEditorField(): GridEditorMonthField {
    return GridEditorMonthField()
  }

  /**
   * Creates an date editor for the grid block.
   * @return The created editor
   */
  protected fun createDateEditorField(): GridEditorDateField {
    return GridEditorDateField()
  }

  /**
   * Creates an time editor for the grid block.
   * @return The created editor
   */
  protected fun createTimeEditorField(): GridEditorTimeField {
    return GridEditorTimeField()
  }

  /**
   * Creates an time stamp editor for the grid block.
   * @return The created editor
   */
  protected fun createTimestampEditorField(): GridEditorTimestampField {
    return GridEditorTimestampField()
  }

  /**
   * Creates an week editor for the grid block.
   * @return The created editor
   */
  protected fun createWeekEditorField(): GridEditorWeekField {
    return GridEditorWeekField()
  }

  /**
   * Reinstalls the focus listener.
   */
  protected fun reInstallSelectionFocusListener() {
    removeSelectionFocusListener()
    addSelectionFocusListener()
  }

  /**
   * Leaves the field.
   */
  @Synchronized
  private fun leaveMe() {
    reInstallSelectionFocusListener()
    // update GUI: for scanner necessary
    if (scanner) {
      // trick: it is now displayed on a different way
      access(currentUI) {
        editor.value = transformer.toModel(getText()!!)
      }
    }
  }

  /**
   * Gets the focus to this field.
   */
  @Synchronized
  private fun enterMe() {
   access(currentUI) {
      if (scanner) {
        editor.value = transformer.toGui("")
      }
      editor.focus()
    }
  }

  /**
   * Checks the given text.
   * @param s The text to be checked.
   * @param changed Is value changed ?
   */
  private fun checkText(s: String?, changed: Boolean) {
    val text = transformer.toModel(s)
    if (!transformer.checkFormat(text)) {
      return
    }
    if (getModel().checkText(text!!) && changed) {
      getModel().isChangedUI = true
    }
    getModel().setChanged(changed)
  }

  /**
   * Returns `true` if there is a difference between the old and the new text.
   * @param oldText The old text value.
   * @param newText The new text value.
   * @return `true` if there is a difference between the old and the new text.
   */
  private fun isChanged(oldText: String?, newText: String?): Boolean {
    var oldText: String? = oldText
    var newText: String? = newText
    if (oldText == null) {
      oldText = "" // replace null by empty string to avoid null pointer exceptions
    }
    if (newText == null) {
      newText = ""
    }
    return oldText != newText
  }
  // ----------------------------------------------------------------------
  // TRANSFORMERS IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Default implementation of the [ModelTransformer]
   *
   * @param col The column index.
   * @param row The row index.
   */
  internal class DefaultTransformer(var col: Int, var row: Int) : ModelTransformer {
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
    }
  }

  /**
   * A scanner model transformer.
   *
   * @param field The field view.
   */
  internal class ScannerTransformer(private val field: GridEditorField<String>) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toGui(modelTxt: String?): String {
      return if (modelTxt == null || "" == modelTxt) {
        VlibProperties.getString("scan-ready")
      } else if (!field.isReadOnly) {
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
  internal class NewlineTransformer(private val col: Int, private val row: Int) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toModel(guiTxt: String?): String = convertFixedTextToSingleLine(guiTxt, col, row)

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

    companion object {
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
}
