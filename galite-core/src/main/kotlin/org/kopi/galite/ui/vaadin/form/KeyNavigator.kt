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

import java.io.IOException

import org.kopi.galite.form.VField
import org.kopi.galite.ui.vaadin.event.TextFieldListener
import org.kopi.galite.ui.vaadin.field.InputTextField
import org.kopi.galite.util.PrintException
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.PrinterManager
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VWindow

/**
 * Text field key navigation.
 */
class KeyNavigator(private val model: VField?, private val box: InputTextField<*>?) : TextFieldListener {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun gotoNextField() {
    performAction(object : Action("keyKEY_TAB") {
      override fun execute() {
        if (model != null) {
          model.block!!.form.getActiveBlock()!!.gotoNextField()
        }
      }
    })
  }

  override fun gotoPrevField() {
    performAction(object : Action("keyKEY_STAB") {
      override fun execute() {
        if (model != null) {
          model.block!!.form.getActiveBlock()!!.gotoPrevField()
        }
      }
    })
  }

  override fun gotoNextBlock() {
    performAction(object : Action("keyKEY_BLOCK") {
      override fun execute() {
        if (model != null) {
          model.block!!.form.gotoNextBlock()
        }
      }
    })
  }

  override fun gotoPrevRecord() {
    performAction(object : Action("keyKEY_REC_UP") {
      override fun execute() {
        if (model != null) {
          model.block!!.gotoPrevRecord()
        }
      }
    })
  }

  override fun gotoNextRecord() {
    performAction(object : Action("keyKEY_REC_DOWN") {
      override fun execute() {
        if (model != null) {
          model.block!!.gotoNextRecord()
        }
      }
    })
  }

  override fun gotoFirstRecord() {
    performAction(object : Action("keyKEY_REC_FIRST") {
      override fun execute() {
        if (model != null) {
          model.block!!.form.getActiveBlock()!!.gotoFirstRecord()
        }
      }
    })
  }

  override fun gotoLastRecord() {
    performAction(object : Action("keyKEY_REC_LAST") {
      override fun execute() {
        if (model != null) {
          model.block!!.form.getActiveBlock()!!.gotoLastRecord()
        }
      }
    })
  }

  override fun gotoNextEmptyMustfill() {
    performAction(object : Action("keyKEY_ALTENTER") {
      override fun execute() {
        if (model != null) {
          model.block!!.form.getActiveBlock()!!.gotoNextEmptyMustfill()
        }
      }
    })
  }

  override fun closeWindow() {
    performAction(object : Action("keyKEY_ESCAPE") {
      override fun execute() {
        if (model != null) {
          model.block!!.form.close(VWindow.CDE_QUIT)
        }
      }
    })
  }

  override fun printForm() {
    performAction(object : Action("keyKEY_ALTENTER") {
      override fun execute() {
        if (model != null) {
          try {
            val job = model.getDisplay()!!.getBlockView().getFormView().printForm()
            PrinterManager.getPrinterManager().getCurrentPrinter().print(job!!)
          } catch (e: PrintException) {
            throw VExecFailedException(e.message)
          } catch (e: IOException) {
            throw VExecFailedException(e.message)
          }
        }
      }
    })
  }

  override fun previousEntry() {
    performAction(object : Action("keyKEY_LIST_UP") {
      override fun execute() {
        if (model != null) {
          (model.getDisplay() as DField).rowController.previousEntry()
        }
      }
    })
  }

  override fun nextEntry() {
    performAction(object : Action("keyKEY_LIST_UP") {
      override fun execute() {
        if (model != null) {
          (model.getDisplay() as DField).rowController.nextEntry()
        }
      }
    })
  }

  override fun onQuery(query: String?) {
    model!!.getForm().performAsyncAction(object : Action() {
      override fun execute() {
        val suggestions = model.getSuggestions(query)
        if (box != null && suggestions != null) {
          /*BackgroundThreadHandler.access(Runnable { TODO
            box.setSuggestions(suggestions, query)
          })*/
        }
      }
    })
  }

  /*fun onSuggestion(suggestion: AutocompleteSuggestion?) { TODO
    // not working cause objects passed in the the shared state are not properly
    // serialized. We will set the field value at the client side and fire a go to next field
    // here to convert the field displayed value to model value.
    // model.setObject(model.block.getActiveRecord(), suggestion.getValue());
  }*/

  override fun autofill() {
    (model!!.getDisplay() as DField).performAutoFillAction()
  }

  /**
   * Executes the given action in the event dispatch handler.
   * @param action The action to be executed.
   */
  protected fun performAction(action: Action?) {
    if (action != null && model != null) {
      model.getForm().performAsyncAction(action)
    }
  }
}
