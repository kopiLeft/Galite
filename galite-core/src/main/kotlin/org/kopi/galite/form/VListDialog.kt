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

package org.kopi.galite.form

import kotlin.math.max

import org.kopi.galite.base.UComponent
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.type.Date
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.Module
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.VModel
import org.kopi.galite.visual.VWindow

class VListDialog(list: Array<VListColumn>,
                  val data: Array<Array<Any>>,
                  val idents: IntArray,
                  val rows: Int,
                  skipFirstLine: Boolean) : VModel {

  /**
   * Creates a dialog with specified data
   */
  constructor(list: Array<VListColumn>,
              data: Array<Array<Any>>,
              idents: IntArray,
              rows: Int) : this(list, data, idents, rows, true)

  /**
   * Creates a dialog with specified data
   */
  constructor(list: Array<VListColumn>,
              data: Array<Array<Any>>,
              rows: Int,
              newForm: VDictionary) : this(list, data, makeIdentArray(rows), rows, false) {
    this.newForm = newForm
  }

  /**
   * Creates a dialog with specified data
   */
  constructor(list: Array<VListColumn>,
              data: Array<Array<Any>>,
              rows: Int,
              newForm: String) : this(list, data, rows, Module.Executable(newForm) as VDictionary)

  /**
   * Creates a dialog with specified data
   */
  constructor(list: Array<VListColumn>,
              data: Array<Array<Any>>,
              rows: Int) : this(list, data, makeIdentArray(rows), rows, false)

  /**
   * Creates a dialog with specified data and title bar.
   */
  constructor(list: Array<VListColumn>, data: Array<Array<Any>>) : this(list, data, data[0].size)

  /**
   * Creates a dialog with specified data and title bar.
   */
  constructor(title: String,
              data: Array<String>,
              rows: Int = data.size) : this(arrayOf<VListColumn>(VStringColumn(title,
                                                                 null,
                                                                 VConstants.ALG_LEFT,
                                                                 getMaxLength(data),
                                                                 true)),
                                            arrayOf<Array<String>>(data) as Array<Array<Any>>,
                                            rows)

  /**
   * Displays a dialog box returning position of selected element.
   */
  fun selectFromDialog(window: VWindow, field: VField): Int = selectFromDialog(window, field, true)

  /**
   * Displays a dialog box returning position of selected element.
   */
  fun selectFromDialog(form: VForm, window: VWindow, field: VField): Int {
    this.form = form
    return selectFromDialog(window, field, true)
  }

  /**
   * Displays a dialog box returning position of selected element.
   * @exception org.kopi.galite.visual.VException        an exception may be raised by string formatter
   */
  fun selectFromDialog(showSingleEntry: Boolean): Int = selectFromDialog(null, showSingleEntry)

  /**
   * Displays a dialog box returning position of selected element.
   */
  fun selectFromDialog(window: VWindow?, field: VField?, showSingleEntry: Boolean): Int =
    display.selectFromDialog((window?.getDisplay())!!,
                             field?.getDisplay()!!,
                             showSingleEntry)

  /**
   * Displays a dialog box returning position of selected element.
   * @exception org.kopi.galite.visual.VException       an exception may be raised by string formatter
   */
  fun selectFromDialog(window: VWindow?, showSingleEntry: Boolean): Int =
          display.selectFromDialog((window?.getDisplay())!!, showSingleEntry)

  /**
   * Sorts the model
   */
  fun sort(left: Int = 0) {
    if (data.isEmpty()) { // one element
      return
    }
    for (i in 0 until count) {
      translatedIdents[i] = i + if (isSkipFirstLine) 1 else 0 // reinit
    }
    var i = count

    while (--i >= 0) {
      for (j in 0 until i) {
        val value1 = data[left][translatedIdents[j]]
        val value2 = data[left][translatedIdents[j + 1]]
        var swap = if (value1 != null && value2 != null) {
          when (value1) {
            is String -> {
              value1 > (value2 as String?)!!
            }
            is Number -> {
              value1.toDouble() > (value2 as Number).toDouble()
            }
            is Boolean -> {
              value1 && !(value2 as Boolean)
            }
            is Date -> {
              (value1 as Date) > value2 as Date?
            }
            else -> {
              false
            }
          }
        } else {
          value1 == null && value2 != null
        }
        if (swap) {
          val tmp = translatedIdents[j]

          translatedIdents[j] = translatedIdents[j + 1]
          translatedIdents[j + 1] = tmp
        }
      }
    }
    if (columns[left].isSortAscending) {
      // reverse sorting
      for (i in 0 until count / 2) {
        val tmp = translatedIdents[i]
        translatedIdents[i] = translatedIdents[count - 1 - i]
        translatedIdents[count - 1 - i] = tmp
      }
    }
  }

  // --------------------------------------------------------------------
  // VMODEL IMPLEMENTATION
  // --------------------------------------------------------------------
  override fun setDisplay(display: UComponent) {
    assert(display is UListDialog) { "Display must be UListDialog" }
    this.display = display as UListDialog
  }

  override fun getDisplay(): UListDialog = display

  /**
   * Enables the insertion of a new record
   */
  fun setForceNew() {
    isForceNew = true
  }

  /**
   * Enables the too many rows message
   */
  fun setTooManyRows() {
    isTooManyRows = true
  }

  /**
   * Returns a value at a given position
   */
  fun getValueAt(row: Int, col: Int): Any = data[col][row]

  fun convert(pos: Int): Int = if (pos == -1) -1 else idents[translatedIdents[pos]]

  fun getColumnCount(): Int = data.size

  fun getColumnName(column: Int): String? = titles[column]

  /**
   * @return the skipFirstLine
   */
  var isSkipFirstLine = true
    private set

  var form: VForm? = null

  /**
   * @return the forceNew
   */
  var isForceNew = false
    private set

  /**
   * @return the tooManyRows
   */
  var isTooManyRows = false
    private set

  /**
   * @return the sizes
   */
  val sizes: IntArray

  /**
   * @return the columns
   */
  val columns: Array<VListColumn>

  /**
   * @return the titles
   */
  val titles: Array<String?>

  /**
   * @return the count
   */
  val count: Int

  /**
   * @return the newForm
   */
  var newForm: VDictionary? = null
    private set

  val translatedIdents: IntArray

  private var display: UListDialog

  companion object {

    /**
     * Displays a dialog box returning position of selected element.
     */
    fun selectFromDialog(window: VWindow, str: Array<String>): Int {
      var size = 0

      str.forEach {
        size = max(size, it.length)
      }
      return VListDialog(arrayOf(VStringColumn("Auswahl", null, 0, size, true)),
              arrayOf(str as Array<Any>)).selectFromDialog(window, null, true)
    }

    /**
     * Makes an identifiers array
     */
    private fun makeIdentArray(rows: Int): IntArray {
      val idents = IntArray(rows)

      for (i in 0 until rows) {
        idents[i] = i
      }
      return idents
    }

    /**
     * Returns the max length in a given String array.
     */
    private fun getMaxLength(values: Array<String>): Int {
      var result = 0

      values.forEach {
        if (it != null) {
          result = max(result, it.length)
        }
      }
      return result
    }

    // returned value if a user click on a forced new button and there
    // is no form to create a record
    var NEW_CLICKED = -2
  }

  /**
   * Creates a dialog with specified data
   */
  init {
    if (list.size != data.size) {
      throw InconsistencyException("WRONG NUMBER OF COLUMN OR TITLES: list.length = ${list.size}" +
              " does not match data.length = ${data.size}")
    }
    isSkipFirstLine = skipFirstLine
    count = rows
    columns = list
    sizes = IntArray(list.size)
    titles = arrayOfNulls(list.size)

    if (idents[0] != 0) {
      isSkipFirstLine = false
    }
    translatedIdents = IntArray(idents.size - if (isSkipFirstLine) 1 else 0)
    for (i in sizes.indices) {
      sizes[i] = max(list[i].width, list[i].title.length)
      titles[i] = list[i].title
    }
    for (i in translatedIdents.indices) {
      translatedIdents[i] = i + if (isSkipFirstLine) 1 else 0
    }
    display = UIFactory.uiFactory.createView(this) as UListDialog
  }
}
