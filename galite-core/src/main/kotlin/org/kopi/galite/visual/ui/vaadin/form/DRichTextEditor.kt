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
package org.kopi.galite.visual.ui.vaadin.form

import org.kopi.galite.visual.form.UTextField
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.form.VStringField
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.field.RichTextField
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
) : DField(model, label, align, options, detail), UTextField {

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
                           ApplicationContext.getDefaultLocale(),
                           this)
    editor.addTextValueChangeListener {
      if (it.isFromClient && !(it.oldValue == "" && it.value == "<p><br></p>")) {

        println("------DRichTextField-----it.value   -- :: "+it.value   +it.oldValue)
        valueChanged()
      }
    }
    println("----DRichTextField------editor --"+editor)
    setFieldContent(editor)
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


  override fun valueChanged() {
    // value change event is fired when the field is blurred.
    getModel().isChangedUI = true
    getModel().setChanged(true)
    println("-----------DRichTextEditor----------::"+text)
    getModel().checkType(text)
  }

  override fun updateText() {
    val newModelTxt = getModel().getText(rowController.blockView.getRecordFromDisplayLine(position))
    access {
      editor.value = newModelTxt
    }
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

  override fun getText(): String? {
    return editor.value
  }

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}

  /**
   * Gets the focus to this editor.
   */
  private fun enterMe() {
    access {
      editor.focus()
    }
  }

  // ----------------------------------------------------------------------
  // NAVIGATION
  // ----------------------------------------------------------------------

  fun onGotoNextBlock() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_BLOCK") {
      override fun execute() {
        getModel().block!!.form.gotoNextBlock()
      }
    })
  }
}
