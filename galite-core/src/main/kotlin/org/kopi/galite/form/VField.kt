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

import javax.swing.event.EventListenerList

import kotlin.reflect.KClass

import org.kopi.galite.base.Query
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VListColumn
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VCommand

abstract class VField(val width: Int, val height: Int) {

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns the field label.
   */
  open fun getHeader(): String {
    TODO()
  }

  open fun build() {
    TODO()
  }

  /**
   * Sets the field value of given record.
   * Warning:   This method will become inaccessible to kopi users in next release
   */
  open fun setObject(r: Int, v: Any) {
    TODO()
  }

  open fun toText(o: Any?): String? {
    TODO()
  }

  fun fireValueChanged(t: Int) {
    TODO()
  }

  /**
   *
   */
  protected open fun helpOnType(help: VHelpGenerator, names: Array<String>?) {
    TODO()
  }
  fun getPriority(): Int {
    TODO()
  }

  /**
   * return the name of this field
   */
  open fun getTypeName(): String {
    TODO()
  }

  open fun fillField(handler: PredefinedValueHandler): Boolean {
    TODO()
  }

  open fun getDisplayedValue(trim: Boolean): Any {
    TODO()
  }

  open fun checkType(rec: Int, s: Any) {
    TODO()
  }

  open fun getObject(r: Int): Any {
    TODO()
  }

  open fun getListColumn(): VListColumn? {
    TODO()
  }

  open fun getTextImpl(r: Int): String? {
    TODO()
  }

  open fun copyRecord(f: Int, t: Int) {
    TODO()
  }

  open fun getTypeInformation(): String {
    TODO()
  }

  fun trail(r: Int) {
    TODO()
  }

  open fun getSqlImpl(r: Int): String? {
    TODO()
  }

  open fun toObject(s: String): Any? {
    TODO()
  }

  open fun getDisplay(): UField? {
    TODO()
  }

  open fun getObjectImpl(r: Int): Any? {
    TODO()
  }

  open fun isNullImpl(r: Int): Boolean {
    TODO()
  }

  open fun retrieveQuery(query: Query, column: Int): Any? {
    TODO()
  }

  open fun setNull(r: Int) {
    TODO()
  }

  open fun getDataType(): KClass<*> {
    TODO()
  }

  open fun checkText(s: String): Boolean {
    TODO()
  }

  fun setChanged(r: Int) {
    TODO()
  }

  open fun clear(r: Int) {
    TODO()
  }

  companion object {
    const val MDL_FLD_COLOR = 1
    const val MDL_FLD_IMAGE = 2
    const val MDL_FLD_EDITOR = 3
    const val MDL_FLD_TEXT = 4
    const val MDL_FLD_ACTOR = 5
  }

  lateinit var block: VBlock

  // access in each mode
  private val access : IntArray
    get() {
      TODO()
    }

  // order in select results
  private val priority = 0

  // bitset of unique indices
  private val indices = 0

  // field name (for dumps)
  val name : String? = null

   // field label
  private val label : String? = null

  // options
  private val options = 0

   // help text
  private val help : String? = null

  // The position in parent field array
  private val index = 0

  // field alignment
  val align = 0

  // position in array of fields
  private val posInArray = 0

  // list
  lateinit var list: VList

  // columns in block's tables
  lateinit var columns : Array<VColumn>

  //  private   VFieldUI        ui;
  // The UI manager
  // The alias field
  lateinit var alias : VField

  // changed by user / changes are done in the model
  private val changed = false

  // changed by user / changes are in the ui -> update model
  val changedUI = false

  // UPDATE model before doing anything
  private val border = 0

  // dynamic data
  // search operator
  private val searchOperator = 0

  // dynamic access
  private val dynAccess : IntArray = TODO()

  // ####
  private val fieldListener: EventListenerList? = null

  // if there is only the model and no gui
  // all the job use less memory and are faster
  private val hasListener = false

  private val pos: VPosition? = null
  private val cmd: Array<VCommand>

  // foreground colors for this field.
  private val foreground : Array<VColor>

  // background colors for this field.
  private val background : Array<VColor>
}
