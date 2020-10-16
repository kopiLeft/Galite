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

import org.kopi.galite.db.Query
import org.kopi.galite.base.UComponent
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VListColumn
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VModel


abstract class VField(val width: Int, val height: Int) : VConstants, VModel {

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------
  /**
   * Returns the field label.
   */
  open fun getHeader(): String {
    TODO()
  }

  open fun predefinedFill(): Any {
    TODO()
  }

  open fun isNull(r: Int): Boolean {
    TODO()
  }

  open fun loadItem(item: Int): Any = TODO()

  open fun autofill() {
    TODO()
  }

  open fun getText(r: Int): String {
    TODO()
  }

  open fun build() {
    TODO()
  }

  open fun enumerateValue(desc: Boolean) {
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

  /**
   *
   */
  protected open fun helpOnType(help: VHelpGenerator) {
    helpOnType(help, null)
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

  open fun hasAutocomplete(): Boolean {
    TODO()
  }

  open fun getAutocompleteLength(): Int {
    TODO()
  }

  open fun getAutocompleteType(): Int {
    TODO()
  }

  open fun hasAutofill(): Boolean {
    TODO()
  }

  open fun fillField(handler: PredefinedValueHandler?): Boolean {
    TODO()
  }

  open fun getDisplayedValue(trim: Boolean): Any {
    TODO()
  }

  override fun setDisplay(display: UComponent) {
    TODO()
  }

  override fun getDisplay(): UField = TODO()

  open fun checkType(rec: Int, s: Any) {
    TODO()
  }

  open fun checkType(s: Any) {
    TODO()
  }

  open fun getObject(r: Int): Any {
    TODO()
  }

  fun setDimension(width: Int, height: Int) {
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

  open fun getObjectImpl(r: Int): Any? {
    TODO()
  }

  open fun getType(): Int {
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

  fun getForm(): VForm {
    TODO()
  }

  open fun helpOnField(help: VHelpGenerator) {
    TODO()
  }

  fun getSearchCondition(): String? = TODO()

  fun hasFocus(): Boolean = TODO()

   fun getListID(): Int = TODO()

   fun setValueID(id: Int) {
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
  private val access // access in each mode
          : IntArray
    get() {
      TODO()
    }
  private val priority // order in select results
          = 0
  private val indices // bitset of unique indices
          = 0
  val name // field name (for dumps)
          : String? = null
  val label // field label
          : String? = null
  private val options // options
          = 0
  private val help // help text
          : String? = null
  private val index // The position in parent field array
          = 0
  private val align // field alignment
          = 0
  private val posInArray // position in array of fields
          = 0

  var list: VList? = null // list

  lateinit var columns // columns in block's tables
          : Array<VColumn>

  //  private   VFieldUI        ui;             // The UI manager
  lateinit var alias // The alias field
          : VField

  // changed?
  val changed // changed by user / changes are done in the model
          = false
  val changedUI // changed by user / changes are in the ui -> update model
          = false

  // UPDATE model before doing anything
  private val border = 0

  // dynamic data
  private val searchOperator // search operator
          = 0
  private val dynAccess // dynamic access
          : IntArray = TODO()

  // ####
  private val fieldListener: EventListenerList? = null

  // if there is only the model and no gui
  // all the job use less memory and are faster
  private val hasListener = false

  private val pos: VPosition? = null
  private val cmd: Array<VCommand>

  private val foreground // foreground colors for this field.
          : Array<VColor>
  private val background // background colors for this field.
          : Array<VColor>
}
