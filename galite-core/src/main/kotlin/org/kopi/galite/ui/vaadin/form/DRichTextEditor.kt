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

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.Focusable
import org.kopi.galite.form.UTextField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.form.VStringField
import org.kopi.galite.ui.vaadin.field.RichTextField
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ApplicationContext

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
) : DField(model, label, align, options, detail),
        UTextField
        /*Focusable<DRichTextEditor> , ValueChangeListener, NavigationListener TODO*/ {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val editor: RichTextField
  private var inside = false

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    editor = RichTextField(getModel().width,
                           getModel().height,
                           if (getModel().height == 1) 1 else (getModel() as VStringField).getVisibleHeight(),
                           model.model.isNoEdit(),
                           ApplicationContext.getDefaultLocale())
    editor.addValueChangeListener(::valueChanged)
    //editor.addNavigationListener(this) TODO
    //setContent(editor) TODO
  }
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateColor() {}

  override fun getObject(): Any? {
    return editor.value
  }

  override fun setBlink(blink: Boolean) {
    /*BackgroundThreadHandler.access(Runnable { TODO
      if (blink) {
        editor.addStyleName("blink")
      } else {
        editor.removeStyleName("blink")
      }
    })*/
  }

  override fun focus() {
    // model transfer focus is performed
    // when the field is focused and not when
    // the field is clicked like text field
    // because CK editor does not provide a way
    // to capture click event on the editable area
    onClick()
  }


  fun valueChanged(event: AbstractField.ComponentValueChangeEvent<RichTextField, String>?) {
    // value change event is fired when the field is blurred.
    getModel().isChangedUI = true
    getModel().setChanged(true)
  }

  override fun updateText() {
    val newModelTxt = getModel().getText(getRowController().blockView.getRecordFromDisplayLine(position))
    //BackgroundThreadHandler.access(Runnable { TODO
    editor.setValue(newModelTxt)
    //})
  }

  override fun updateFocus() {
    label!!.update(model, position)
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
    label!!.update(model, getBlockView().getRecordFromDisplayLine(position))
    /*BackgroundThreadHandler.access(Runnable { TODO
      editor.setEnabled(access >= VConstants.ACS_VISIT)
      isEnabled = access >= VConstants.ACS_VISIT
    })*/
  }

  override fun forceFocus() {
    enterMe()
  }

  override fun getText(): String {
    return editor.getValue()
  }

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}

  /**
   * Gets the focus to this editor.
   */
  private fun enterMe() {
    //BackgroundThreadHandler.access(Runnable { TODO
    editor.focus()
    //})
  }

  // ----------------------------------------------------------------------
  // NAVIGATION
  // ----------------------------------------------------------------------
  fun onGotoNextField() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_TAB") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoNextField()
        }
      }
    })
  }

  fun onGotoPrevField() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_STAB") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoPrevField()
        }
      }
    })
  }

  fun onGotoNextBlock() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_BLOCK") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.gotoNextBlock()
        }
      }
    })
  }

  fun onGotoPrevRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_UP") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoPrevRecord()
        }
      }
    })
  }

  fun onGotoNextRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_DOWN") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoNextRecord()
        }
      }
    })
  }

  fun onGotoFirstRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_FIRST") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoFirstRecord()
        }
      }
    })
  }

  fun onGotoLastRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_LAST") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoLastRecord()
        }
      }
    })
  }

  fun onGotoNextEmptyMustfill() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_ALTENTER") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoNextEmptyMustfill()
        }
      }
    })
  }
}
