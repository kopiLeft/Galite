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

import org.kopi.galite.l10n.FieldLocalizer
import org.kopi.galite.list.VList
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VlibProperties

/**
 *
 * @param     type            the identifier of the type in the source file
 * @param     source          the qualified name of the source file defining the list
 * @param     idents          an array of identifiers identifying each code value
 */
abstract class VCodeField(val bufferSize: Int,
                          val type: String,
                          var source: String,
                          val idents: Array<String>)
  : VField(1, 1) {

  override fun hasAutofill(): Boolean = true

  override fun hasAutocomplete(): Boolean = true

  override fun getAutocompleteLength(): Int = 0

  override fun getAutocompleteType(): Int = VList.AUTOCOMPLETE_CONTAINS

  /**
   * just after loading, construct record
   */
  override fun build() {
    super.build()
    for (i in 0 until block!!.bufferSize) {
      value[i] = -1
    }
  }

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("code-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Code")

  /**
   *
   */
  override fun helpOnType(help: VHelpGenerator) {
    super.helpOnType(help, if (this is VBooleanField) null else labels)
  }

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    val s = s.toLowerCase()

    for (i in labels.indices) {
      if (labels[i].toLowerCase().startsWith(s)) {
        return true
      }
    }
    return false
  }

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, o: Any?) {
    var s = o as? String

    if (s == "") {
      setNull(rec)
    } else {
      /*
       * -1:  no match
       * >=0: one match
       * -2:  two (or more) matches: cannot choose
       */
      var found = -1

      s = s!!.toLowerCase()
      var i = 0

      while (found != -2 && i < labels.size) {
        if (labels[i].toLowerCase().startsWith(s)) {
          if (labels[i].toLowerCase() == s) {
            found = i
            break
          }
          found = if (found == -1) {
            i
          } else {
            -2
          }
        }
        i++
      }
      when (found) {
        -1 -> throw VFieldException(this, MessageCode.getMessage("VIS-00001"))
        -2 -> {
          val listDialog: VListDialog
          val selected: Int
          val selectedToModel: IntArray
          val codes: Array<Any?>
          var count: Int = 0
          run {
            var i = 0

            while (i < labels.size) {
              if (labels[i].toLowerCase().startsWith(s)) {
                count++
              }
              i++
            }
          }
          codes = arrayOfNulls(count)
          selectedToModel = IntArray(count)
          var j = 0

          while (i < labels.size) {
            if (labels[i].toLowerCase().startsWith(s)) {
              codes[j] = codes[i]
              selectedToModel[j] = i
              j++
            }
            i++
          }

          listDialog = VListDialog(arrayOf(getListColumn()!!), arrayOf(codes))
          selected = listDialog.selectFromDialog(getForm(), null, this)
          if (selected != -1) {
            setCode(rec, selectedToModel[selected])
          } else {
            throw VFieldException(this, MessageCode.getMessage("VIS-00002"))
          }
        }
        else -> setCode(rec, found)
      }
    }
  }

  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    if (handler != null) {
      val selected = handler.selectFromList(arrayOf(getListColumn()!!), arrayOf(getCodes()),
                                            labels)

      if (selected != null) {
        /*
         * -1:  no match
         * >=0: one match
         * -2:  two (or more) matches: cannot choose
         */
        var found = -1
        var i = 0

        while (found != -2 && i < labels.size) {
          if (labels[i] == selected) {
            found = if (found == -1) {
              i
            } else {
              -2
            }
          }
          i++
        }
        assert(found >= 0)
        setCode(block!!.activeRecord, found)
        return true
      }
    }
    return false
  }

  /**
   * return true if this field implements "enumerateValue"
   */
  override fun hasNextPreviousEntry(): Boolean = true

  /**
   * Checks that field value exists in list
   */
  override fun enumerateValue(desc: Boolean) {
    var desc = desc

    desc = if (!getListColumn()!!.isSortAscending) desc else !desc
    var pos = value[block!!.activeRecord]

    if (pos == -1 && desc) {
      pos = labels.size
    }
    pos += if (desc) -1 else 1
    if (pos < 0 || pos >= labels.size) {
      throw VExecFailedException() // no message to display
    } else {
      setCode(block!!.activeRecord, pos)
    }
  }

  override fun getSuggestions(query: String?): Array<Array<String?>>? {
    return if (query == null) {
      null
    } else {
      val suggestions: MutableList<Array<String?>>
      suggestions = ArrayList()
      for (i in labels.indices) {
        if (labels[i].toLowerCase().contains(query.toLowerCase())) {
          suggestions.add(arrayOf(labels[i]))
        }
      }
      suggestions.toTypedArray()
    }
  }
  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Returns the array of codes.
   */
  abstract fun getCodes(): Array<Any?>

  /*
   *
   */
  protected fun setCode(r: Int, v: Int) {
    if (changedUI || value[r] != v) {
      // trails (backup) the record if necessary
      trail(r)
      // set value in the defined row
      value[r] = v
      // inform that value has changed
      setChanged(r)
    }
    // else nothing to do
  }

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setCode(r, -1)
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean {
    return value[r] == -1
  }

  //deprecation
  /**
   * Returns the field value of given record as a bigdecimal value.
   */
  override fun getFixed(r: Int): org.kopi.galite.type.Decimal {
    throw InconsistencyException()
  }

  //deprecation
  /**
   * Returns the field value of given record as a boolean value.
   */
  override fun getBoolean(r: Int): Boolean? {
    throw InconsistencyException()
  }

  //deprecation
  /**
   * Returns the field value of given record as a int value.
   */
  override fun getInt(r: Int): Int? {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a string value.
   */
  override fun getString(r: Int): String {
    throw InconsistencyException()
  }

  override fun toText(o: Any?): String {
    for (i in getCodes().indices) {
      if (getCodes()[i] == o) {
        return labels[i]
      }
    }
    return ""
  }

  override fun toObject(s: String): Any? {
    for (i in labels.indices) {
      if (labels[i] == s) {
        return getCodes()[i]
      }
    }
    return null
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String = if (value[r] == -1) ""
  else labels[value[r]]

  /**
   * Returns the SQL representation of field value of given record.
   */
  abstract override fun getSqlImpl(r: Int): String

  /**
   * Copies the value of a record to another
   */
  override fun copyRecord(f: Int, t: Int) {
    val oldValue: Int = value[t]

    value[t] = value[f]
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < block!!.bufferSize && oldValue != value[t]) {
      fireValueChanged(t)
    }
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a code value wrt the field type.
   */
  protected fun formatCode(code: Int): String {
    return labels[code]
  }

  /**
   * Returns a string representation of a bigdecimal value wrt the field type.
   */
  protected open fun formatFixed(value: org.kopi.galite.type.Decimal): String {
    throw InconsistencyException()
  }

  /**
   * Returns a string representation of a boolean value wrt the field type.
   */
  protected open fun formatBoolean(value: Boolean): String {
    throw InconsistencyException()
  }

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  protected open fun formatInt(value: Int): String {
    throw InconsistencyException()
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  override fun localize(parent: FieldLocalizer?) {
    val loc = parent!!.manager.getTypeLocalizer(source, type)

    val labels = arrayOfNulls<String>(idents.size)

    for (i in labels.indices) {
      labels[i] = loc.getCodeLabel(idents[i])
    }

    this.labels = labels.requireNoNulls()
    setDimension(getMaxWidth(this.labels), 1)
  }


  /**
   * represents the name of this field
   */
  lateinit var labels: Array<String>
    private set

  // dynamic data
  protected var value: IntArray = IntArray(2 * bufferSize) // -1: null


  companion object {
    // --------------------------------------------------------------------
    // PRIVATE METHODS
    // --------------------------------------------------------------------
    private fun getMaxWidth(labels: Array<String>): Int {
      var res = 0

      for (i in labels.indices) {
        res = Math.max(labels[i].length, res)
      }
      return res
    }
  }
}
