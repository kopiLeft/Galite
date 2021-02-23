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

import java.util.*

/**
 * A grid text editor based on custom components.
 */
class DGridTextEditorField(
        columnView: VFieldUI,
        label: DGridEditorLabel?,
        align: Int,
        options: Int
) : DGridEditorField<String?>(columnView, label, align, options), UTextField {
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  val `object`: Any
    get() = getEditor().getValue()

  fun updateText() {
    val newModelTxt: String = model.getText(blockView.getRecordFromDisplayLine(position))
    BackgroundThreadHandler.access(
            Runnable { getEditor().setValue(transformer.toGui(newModelTxt).trim()) })
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

  protected override val nullRepresentation: T?
    protected get() = ""

  override fun reset() {
    inside = false
    selectionAfterUpdateDisabled = false
    super.reset()
  }

  val text: String
    get() = getEditor().getDisplayedValue()

  fun setHasCriticalValue(b: Boolean) {}
  fun addSelectionFocusListener() {}
  fun removeSelectionFocusListener() {}
  fun setSelectionAfterUpdateDisabled(disable: Boolean) {
    selectionAfterUpdateDisabled = disable
  }

  override fun getEditor(): GridEditorTextField {
    return super.getEditor() as GridEditorTextField
  }

  override fun createEditor(): GridEditorTextField {
    val editor: GridEditorTextField
    editor = createEditorField()
    editor.setAlignment(columnView.getModel().getAlign())
    editor.setAutocompleteLength(columnView.getModel().getAutocompleteLength())
    editor.setHasAutocomplete(columnView.getModel().hasAutocomplete())
    editor.setNavigationDelegationMode(navigationDelegationMode)
    editor.setHasAutofill(columnView.hasAutofill())
    editor.setHasPreFieldTrigger(columnView.getModel().hasTrigger(VConstants.TRG_PREFLD))
    editor.addActors(actors)
    editor.setConvertType(getConvertType(columnView.getModel()))
    return editor
  }

  override fun createConverter(): Converter<String, Any> {
    return object : Converter<String?, Any?>() {
      val presentationType: Class<String>
        get() = String::class.java
      val modelType: Class<Any>
        get() = Any::class.java

      @Throws(ConversionException::class)
      fun convertToPresentation(value: Any?, targetType: Class<out String?>?, locale: Locale?): String {
        return transformer.toGui(model.toText(value))
      }

      @Throws(ConversionException::class)
      fun convertToModel(value: String?, targetType: Class<out Any?>?, locale: Locale?): Any {
        return try {
          model.toObject(transformer.toModel(value))
        } catch (e: VException) {
          throw ConversionException(e)
        }
      }
    }
  }

  override fun createRenderer(): Renderer<String> {
    return TextRenderer()
  }

  /**
   * Creates an editor field according to the field model.
   * @return The created editor field.
   */
  protected fun createEditorField(): GridEditorTextField {
    val editor: GridEditorTextField
    editor = if (model is VStringField) {
      // string field & text area
      if (model.getHeight() > 1) {
        createTextEditorField()
      } else {
        createStringEditorField()
      }
    } else if (model is VIntegerField) {
      // integer fields
      createIntegerEditorField()
    } else if (model is VMonthField) {
      // month field
      createMonthEditorField()
    } else if (model is VDateField) {
      // date field
      createDateEditorField()
    } else if (model is VWeekField) {
      // week field
      createWeekEditorField()
    } else if (model is VTimeField) {
      // time field
      createTimeEditorField()
    } else if (model is VCodeField) {
      // code field
      createEnumEditorField()
    } else if (model is VFixnumField) {
      createFixnumEditorField()
    } else if (model is VTimestampField) {
      createTimestampEditorField()
    } else {
      throw IllegalArgumentException("unknown field model : " + model.getClass().getName())
    }
    return editor
  }

  /**
   * Creates a string editor for the grid block.
   * @return The created editor
   */
  protected fun createStringEditorField(): GridEditorTextField {
    return GridEditorTextField(model.getWidth())
  }

  /**
   * Creates a text editor for the grid block.
   * @return The created editor
   */
  protected fun createTextEditorField(): GridEditorTextAreaField {
    val scanner: Boolean
    scanner = options and VConstants.FDO_NOECHO !== 0 && model.getHeight() > 1
    return GridEditorTextAreaField(if (scanner) 40 else model.getWidth(),
                                   model.getHeight(),
                                   (model as VStringField).getVisibleHeight(),
                                   !scanner && options and VConstants.FDO_DYNAMIC_NL > 0)
  }

  /**
   * Creates an integer editor for the grid block.
   * @return The created editor
   */
  protected fun createIntegerEditorField(): GridEditorIntegerField {
    val model: VIntegerField
    model = getModel() as VIntegerField
    return GridEditorIntegerField(model.getWidth(),
                                  model.getMaxValue(),
                                  model.getMaxValue())
  }

  /**
   * Creates a deciaml editor for the grid block.
   * @return The created editor
   */
  protected fun createFixnumEditorField(): GridEditorFixnumField {
    val model: VFixnumField
    model = getModel() as VFixnumField
    return GridEditorFixnumField(model.getWidth(),
                                 model.getMaxValue().doubleValue(),
                                 model.getMaxValue().doubleValue(),
                                 model.getMaxScale(),
                                 model.isFraction())
  }

  /**
   * Creates an month editor for the grid block.
   * @return The created editor
   */
  protected fun createEnumEditorField(): GridEditorEnumField {
    return GridEditorEnumField(model.getWidth(), (model as VCodeField).getLabels())
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
      BackgroundThreadHandler.access(Runnable { getEditor().setValue(transformer.toModel(text)) })
    }
  }

  /**
   * Gets the focus to this field.
   */
  @Synchronized
  private fun enterMe() {
    BackgroundThreadHandler.access(Runnable {
      if (scanner) {
        getEditor().setValue(transformer.toGui(""))
      }
      getEditor().focus()
    })
  }

  /**
   * Checks the given text.
   * @param s The text to be checked.
   * @param changed Is value changed ?
   */
  private fun checkText(s: String, changed: Boolean) {
    val text: String = transformer.toModel(s)
    if (!transformer.checkFormat(text)) {
      return
    }
    if (model.checkText(text) && changed) {
      model.setChangedUI(true)
    }
    model.setChanged(changed)
  }
  // ----------------------------------------------------------------------
  // TRANSFORMERS IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Default implementation of the [ModelTransformer]
   */
  /*package*/
  internal class DefaultTransformer
  /**
   * Creates a new `DefaultTransformer` instance.
   * @param col The column index.
   * @param row The row index.
   */(
//---------------------------------------
          // DATA MEMBERS
          //---------------------------------------
          var col: Int, var row: Int
  ) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    fun toGui(modelTxt: String): String {
      return modelTxt
    }

    fun toModel(guiTxt: String): String {
      return guiTxt
    }

    fun checkFormat(source: String): Boolean {
      return if (row == 1) true else convertToSingleLine(source, col, row).length <= row * col
    }

    companion object {
      /**
       * Converts a given string to a line string.
       * @param source The source text.
       * @param col The column index.
       * @param row The row index.
       * @return The converted string.
       */
      private fun convertToSingleLine(source: String, col: Int, row: Int): String {
        val target = StringBuffer()
        val length = source.length
        var start = 0
        while (start < length) {
          var index = source.indexOf('\n', start)
          if (index - start < col && index != -1) {
            target.append(source.substring(start, index))
            for (j in index - start until col) {
              target.append(' ')
            }
            start = index + 1
            if (start == length) {
              // last line ends with a "new line" -> add an empty line
              for (j in 0 until col) {
                target.append(' ')
              }
            }
          } else {
            if (start + col >= length) {
              target.append(source.substring(start, length))
              for (j in length until start + col) {
                target.append(' ')
              }
              start = length
            } else {
              // find white space to break line
              var i: Int
              i = start + col - 1
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
              target.append(source.substring(start, index))
              var j = (index - start) % col
              while (j != 0 && j < col) {
                target.append(' ')
                j++
              }
              start = index
            }
          }
        }
        return target.toString()
      }
    }
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
  }

  /**
   * A scanner model transformer.
   */
  /*package*/
  internal class ScannerTransformer(field: GridEditorField<String?>) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    fun toGui(modelTxt: String?): String {
      return if (modelTxt == null || "" == modelTxt) {
        VlibProperties.getString("scan-ready")
      } else if (!field.isReadOnly()) {
        VlibProperties.getString("scan-read").toString() + " " + modelTxt
      } else {
        VlibProperties.getString("scan-finished")
      }
    }

    fun toModel(guiTxt: String): String {
      return guiTxt
    }

    fun checkFormat(software: String?): Boolean {
      return true
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    private val field: GridEditorField<String>
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    /**
     * Creates a new `ScannerTransformer` instance.
     * @param field The field view.
     */
    init {
      this.field = field
    }
  }

  /**
   * New line model transformer.
   */
  /*package*/
  internal class NewlineTransformer
  /**
   * Creates a new `NewlineTransformer` instance.
   * @param col The column index.
   * @param row The row index.
   */(
//---------------------------------------
          // DATA MEMBERS
          //---------------------------------------
          private val col: Int, private val row: Int
  ) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    fun toModel(source: String): String {
      return convertFixedTextToSingleLine(source, col, row)
    }

    fun toGui(source: String): String {
      val target = StringBuffer()
      val length = source.length
      var usedRows = 1
      var start = 0
      while (start < length) {
        val line = source.substring(start, Math.min(start + col, length))
        var last = -1
        var i = line.length - 1
        while (last == -1 && i >= 0) {
          if (!Character.isWhitespace(line[i])) {
            last = i
          }
          --i
        }
        if (last != -1) {
          target.append(line.substring(0, last + 1))
        }
        if (usedRows < row) {
          if (start + col < length) {
            target.append('\n')
          }
          usedRows++
        }
        start += col
      }
      return target.toString()
    }

    fun checkFormat(source: String): Boolean {
      return source.length <= row * col
    }

    companion object {
      /**
       * Converts a given string to a fixed line string.
       * @param source The source text.
       * @param col The column index.
       * @param row The row index.
       * @return The converted string.
       */
      private fun convertFixedTextToSingleLine(source: String, col: Int, row: Int): String {
        val target = StringBuffer()
        val length = source.length
        var start = 0
        while (start < length) {
          var index = source.indexOf('\n', start)
          if (index - start < col && index != -1) {
            target.append(source.substring(start, index))
            for (j in index - start until col) {
              target.append(' ')
            }
            start = index + 1
            if (start == length) {
              // last line ends with a "new line" -> add an empty line
              for (j in 0 until col) {
                target.append(' ')
              }
            }
          } else {
            if (start + col >= length) {
              target.append(source.substring(start, length))
              for (j in length until start + col) {
                target.append(' ')
              }
              start = length
            } else {
              // find white space to break line
              var i: Int
              i = start + col
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
              target.append(source.substring(start, index))
              for (j in index - start until col) {
                target.append(' ')
              }
              start = index + 1
            }
          }
        }
        return target.toString()
      }
    }
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected var inside = false
  protected var scanner: Boolean
  private var selectionAfterUpdateDisabled = false
  protected var transformer: ModelTransformer? = null

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  init {
    scanner = options and VConstants.FDO_NOECHO !== 0 && model.getHeight() > 1
    transformer = if (model.getHeight() === 1 || !scanner && model.getTypeOptions() and VConstants.FDO_DYNAMIC_NL > 0) {
      DefaultTransformer(model.getWidth(), model.getHeight())
    } else if (!scanner) {
      NewlineTransformer(model.getWidth(), model.getHeight())
    } else {
      ScannerTransformer(getEditor())
    }
    getEditor().addTextChangeListener(object : TextChangeListener() {
      fun onTextChange(event: TextChangeEvent) {
        checkText(event.getNewText(), isChanged(event.getOldText(), event.getNewText()))
      }

      /**
       * Returns `true` if there is a difference between the old and the new text.
       * @param oldText The old text value.
       * @param newText The new text value.
       * @return `true` if there is a difference between the old and the new text.
       */
      private fun isChanged(oldText: String, newText: String): Boolean {
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
    })
    getEditor().addSuggestionsQueryListener(object : SuggestionsQueryListener() {
      fun onSuggestionsQuery(event: SuggestionsQueryEvent) {
        model.getForm().performAsyncAction(object : KopiAction("SUGGESTIONS_QUERY") {
          @Throws(VException::class)
          fun execute() {
            val suggestions: Array<Array<String>>?
            suggestions = model.getSuggestions(event.getQuery())
            if (suggestions != null) {
              BackgroundThreadHandler.access(
                      Runnable { getEditor().setSuggestions(suggestions, event.getQuery()) })
            }
          }
        })
      }
    })
  }
}