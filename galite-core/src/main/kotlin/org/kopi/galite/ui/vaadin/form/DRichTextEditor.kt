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

import org.kopi.galite.form.UTextField
import org.kopi.galite.form.VFieldUI

/**
 * Rich text editor implementation based on CK editor for vaadin.
 */
class DRichTextEditor(
        model: VFieldUI,
        label: DLabel?,
        align: Int,
        options: Int,
        height: Int,
        detail: Boolean
) : DField(model, label, align, options, detail), UTextField, FocusListener, ValueChangeListener, NavigationListener {
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateColor() {}

  override fun getObject(): Any? {
    return editor.getValue()
  }

  override fun setBlink(blink: Boolean) {
    BackgroundThreadHandler.access(Runnable {
      if (blink) {
        editor.addStyleName("blink")
      } else {
        editor.removeStyleName("blink")
      }
    })
  }

  fun focus(event: FocusEvent?) {
    // model transfer focus is performed
    // when the field is focused and not when
    // the field is clicked like text field
    // because CK editor does not provide a way
    // to capture click event on the editable area
    onClick()
  }

  fun valueChange(event: ValueChangeEvent?) {
    // value change event is fired when the field is blurred.
    getModel().setChangedUI(true)
    getModel().setChanged(true)
  }

  override fun updateText() {
    val newModelTxt = getModel().getText(getRowController().getBlockView().getRecordFromDisplayLine(getPosition()))
    BackgroundThreadHandler.access(Runnable { editor.setValue(newModelTxt) })
  }

  override fun updateFocus() {
    label!!.update(model, getPosition())
    if (!modelHasFocus()) {
      if (inside) {
        inside = false
      }
    } else {
      if (!inside) {
        inside = true
        enterMe()
      }
    }
    super.updateFocus()
  }

  override fun updateAccess() {
    super.updateAccess()
    label!!.update(model, getBlockView().getRecordFromDisplayLine(getPosition()))
    BackgroundThreadHandler.access(Runnable {
      editor.setEnabled(access >= VConstants.ACS_VISIT)
      isEnabled = access >= VConstants.ACS_VISIT
    })
  }

  override fun forceFocus() {
    enterMe()
  }

  override fun getText(): String {
    return editor.getValue()
  }

  fun setHasCriticalValue(b: Boolean) {}
  fun addSelectionFocusListener() {}
  fun removeSelectionFocusListener() {}
  fun setSelectionAfterUpdateDisabled(disable: Boolean) {}

  /**
   * Gets the focus to this editor.
   */
  private fun enterMe() {
    BackgroundThreadHandler.access(Runnable { editor.focus() })
  }

  // ----------------------------------------------------------------------
  // NAVIGATION
  // ----------------------------------------------------------------------
  fun onGotoNextField() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_TAB") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextField()
        }
      }
    })
  }

  fun onGotoPrevField() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_STAB") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoPrevField()
        }
      }
    })
  }

  fun onGotoNextBlock() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_BLOCK") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().gotoNextBlock()
        }
      }
    })
  }

  fun onGotoPrevRecord() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_REC_UP") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoPrevRecord()
        }
      }
    })
  }

  fun onGotoNextRecord() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_REC_DOWN") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextRecord()
        }
      }
    })
  }

  fun onGotoFirstRecord() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_REC_FIRST") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoFirstRecord()
        }
      }
    })
  }

  fun onGotoLastRecord() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_REC_LAST") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoLastRecord()
        }
      }
    })
  }

  fun onGotoNextEmptyMustfill() {
    getModel().getForm().performAsyncAction(object : KopiAction("keyKEY_ALTENTER") {
      @Throws(VException::class)
      fun execute() {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextEmptyMustfill()
        }
      }
    })
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val editor: RichTextField
  private var inside = false

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    editor = RichTextField(getModel().getWidth(),
                           getModel().getHeight(),
                           if (getModel().getHeight() === 1) 1 else (getModel() as VStringField).getVisibleHeight(),
                           model.getModel().isNoEdit(),
                           ApplicationContext.getDefaultLocale())
    editor.addFocusListener(this)
    editor.addValueChangeListener(this)
    editor.addNavigationListener(this)
    setContent(editor)
  }
}
