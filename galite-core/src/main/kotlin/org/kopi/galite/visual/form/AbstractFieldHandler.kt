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

package org.kopi.galite.visual.form

import org.kopi.galite.database.Utils
import org.kopi.galite.visual.Module
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException

abstract class AbstractFieldHandler protected constructor(private val rowController: VFieldUI) : FieldHandler {

  // ----------------------------------------------------------------------
  // MODEL ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * Returns the row controller field model.
   * @return The row controller field model.
   */
  val model: VField get() = rowController.model

  //-----------------------------------------------------------------------
  // FIELDHANDLER IMPLEMENTATION
  // ----------------------------------------------------------------------

  override fun getRowController(): VFieldUI = rowController

  // ----------------------------------------------------------------------
  // FIELDLISTENER IMPLEMENTATION
  // ----------------------------------------------------------------------

  override fun updateModel() {
    if (model.isChanged && model.hasFocus()) {
      model.checkType(getDisplayedValue(true))
    }
  }

  override fun getDisplayedValue(trim: Boolean): Any? {
    return when (val field = getCurrentDisplay()) {
      null -> {
        "" // having null pointer exception when display is not defined
      }
      is UTextField -> {
        println("-------------AbstractFieldHandler-------UTextField----")
        val text = field.getText()

        if (!trim) {
          println("-------------AbstractFieldHandler-------UTextField---if !trim--- :: " +text)

          text
        } else if (model.height == 1) {
          println("-------------AbstractFieldHandler-------UTextField---else if---")

          Utils.trimString(text!!)
        } else {
          println("-------------AbstractFieldHandler-------UTextField---else---")

          Utils.trailString(text!!)
        }
      }
      else -> {
        field.getObject()
        println("-------------AbstractFieldHandler-------else field.getObject()----" + field.getObject())

        println("-------------AbstractFieldHandler-------else----")
      }
    }
  }

  override fun getCurrentDisplay(): UField? = rowController.display

  override fun fieldError(message: String) {
    rowController.displayFieldError(message)
  }

  override fun requestFocus(): Boolean {
    rowController.transferFocus(getCurrentDisplay()!!)
    return true
  }

  override fun loadItem(mode: Int): Boolean {
    var mode = mode
    var id = -1
    val dictionary = (when {
      model.list != null && model.list!!.newForm != null -> {
        // OLD SYNTAX
        Module.getExecutable(model.list!!.newForm!!) as VDictionary
      }
      model.list != null && model.list!!.action != null -> {
        // NEW SYNTAX
        model.list!!.action!!().model
      }
      else -> {
        null
      }
    }) ?: return false

    if (mode == VForm.CMD_NEWITEM) {
      id = dictionary.add()
    } else if (mode == VForm.CMD_EDITITEM) {
      try {
        updateModel()
        if (!model.isNull(rowController.getBlock().activeRecord)) {
          val value: Int = model.getListID()
          if (value != -1) {
            id = dictionary.edit(value)
          } else {
            mode = VForm.CMD_EDITITEM_S
          }
        } else {
          mode = VForm.CMD_EDITITEM_S
        }
      } catch (e: VException) {
        mode = VForm.CMD_EDITITEM_S
      }
    }
    if (mode == VForm.CMD_EDITITEM_S) {
      id = dictionary.search()
    }
    if (id == -1) {
      if (mode == VForm.CMD_EDITITEM || mode == VForm.CMD_EDITITEM_S) {
        model.setNull(rowController.getBlock().activeRecord)
      }
      throw VExecFailedException() // no message needed
    }
    model.setValueID(id)
    model.block!!.gotoNextField()
    return true
  }
}
