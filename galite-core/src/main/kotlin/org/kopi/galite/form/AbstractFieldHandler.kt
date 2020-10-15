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

import org.kopi.galite.type.Utils
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.Module

abstract class AbstractFieldHandler protected constructor(private val rowController: VFieldUI)
  : FieldHandler {

  // ----------------------------------------------------------------------
  // MODEL ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * Returns the row controller field model.
   * @return The row controller field model.
   */
  fun getModel(): VField = rowController.getModel()

  //-----------------------------------------------------------------------
  // FIELDHANDLER IMPLEMENTATION
  // ----------------------------------------------------------------------

  override fun getRowController(): VFieldUI = rowController

  // ----------------------------------------------------------------------
  // FIELDLISTENER IMPLEMENTATION
  // ----------------------------------------------------------------------

  override fun updateModel() {
    if (getModel().changed && getModel().hasFocus()) {
      getModel().checkType(getDisplayedValue(true))
    }
  }

  override fun getDisplayedValue(trim: Boolean): Any {
    return when (val field = getCurrentDisplay()) {
      null -> {
        "" // having null pointer exception when display is not defined
      }
      is UTextField -> {
        val text = field.getText()

        if (!trim) {
          text
        } else if (getModel().height == 1) {
          Utils.trimString(text)
        } else {
          Utils.trailString(text)
        }
      }
      else -> {
        field.getObject()
      }
    }
  }

  override fun getCurrentDisplay(): UField? = rowController.getDisplay()

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
      getModel().list != null && getModel().list!!.newForm != null -> {
        // OLD SYNTAX
        Module.getExecutable(getModel().list!!.newForm) as VDictionary
      }
      getModel().list != null && getModel().list!!.action != -1 -> {
        // NEW SYNTAX
        getModel().block.executeObjectTrigger(getModel().list!!.action) as VDictionary
      }
      else -> {
        null
      }
    }) ?: return false

    when (mode) {
      VForm.CMD_NEWITEM -> {
        id = dictionary.add(getModel().getForm())
      }
      else -> {
        if (mode == VForm.CMD_EDITITEM) {
          try {
            updateModel()
            if (!getModel().isNull(rowController.getBlock().activeRecord)) {
              val value: Int = getModel().getListID()
              if (value != -1) {
                id = dictionary.edit(getModel().getForm(), value)
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
          id = dictionary.search(getModel().getForm())
        }
        if (id == -1) {
          if (mode == VForm.CMD_EDITITEM || mode == VForm.CMD_EDITITEM_S) {
            getModel().setNull(rowController.getBlock().activeRecord)
          }
          throw VExecFailedException() // no message needed
        }
      }
    }
    getModel().setValueID(id)
    getModel().block.gotoNextField()
    return true
  }
}
