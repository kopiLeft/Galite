/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.field.ObjectFieldListener
import org.kopi.galite.visual.visual.Action

/**
 * The `DObjectField` is the vaadin implementation
 * of the object field.
 *
 * @param model The row controller.
 * @param label The field label.
 * @param align The field alignment.
 * @param options The field options.
 * @param detail Does the field belongs to the detail view ?
 */
abstract class DObjectField(model: VFieldUI,
                            label: DLabel?,
                            align: Int,
                            options: Int,
                            detail: Boolean) : DField(model, label, align, options, detail), ObjectFieldListener {

  // --------------------------------------------------
  // UI MANAGEMENT
  // --------------------------------------------------
  override fun enter(refresh: Boolean) {
    super.enter(refresh)
    if (blinkOnFocus()) {
      setBlink(true)
    }
  }

  override fun leave() {
    if (blinkOnFocus()) {
      setBlink(false)
    }
    super.leave()
  }

  //---------------------------------------------------
  // OBJECT FIELD LISTENER IMPLEMENTATION
  //---------------------------------------------------
  override fun gotoNextField() {
    performAction(object : Action("key: next field") {
      override fun execute() {
        getModel().getForm().getActiveBlock()!!.gotoNextField()
      }
    })
  }

  override fun gotoPrevField() {
    performAction(object : Action("key: previous field") {
      override fun execute() {
        getModel().getForm().getActiveBlock()!!.gotoPrevField()
      }
    })
  }

  override fun gotoNextBlock() {
    performAction(object : Action("key: next block") {
      override fun execute() {
        getModel().getForm().gotoNextBlock()
      }
    })
  }

  override fun gotoPrevRecord() {
    performAction(object : Action("key: previous record") {
      override fun execute() {
        getModel().getForm().getActiveBlock()!!.gotoPrevRecord()
      }
    })
  }

  override fun gotoNextRecord() {
    performAction(object : Action("key: next record") {
      override fun execute() {
        getModel().getForm().getActiveBlock()!!.gotoNextRecord()
      }
    })
  }

  override fun gotoFirstRecord() {
    performAction(object : Action("key: first record") {
      override fun execute() {
        getModel().getForm().getActiveBlock()!!.gotoFirstRecord()
      }
    })
  }

  override fun gotoLastRecord() {
    performAction(object : Action("key: last record") {
      override fun execute() {
        getModel().getForm().getActiveBlock()!!.gotoLastRecord()
      }
    })
  }

  /**
   * Executes the given action in the event dispatch handler.
   * @param action The action to be executed.
   */
  protected fun performAction(action: Action?) {
    if (action != null && model != null) {
      getModel().getForm().performAsyncAction(action)
    }
  }

  /**
   * Returns true if the field should be blinked when a focus event is triggered.
   * @return True if the field should be blinked when a focus event is triggered.
   */
  protected open fun blinkOnFocus(): Boolean {
    return true
  }

  override fun valueChanged() {
    // Nothing to do
  }
}
