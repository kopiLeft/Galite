/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access

import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorColorField

class DGridEditorColorField(columnView: VFieldUI,
                            label: DGridEditorLabel?,
                            align: Int,
                            options: Int) : DGridEditorField<Any?>(columnView, label, align, options) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private var color: String? = null

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateText() {
    // TODO
  }

  override fun getObject(): Any? = color

  fun setObject(s: Any?) {
    access(currentUI) {
      if (s != null) {
        //editor.setImage(s as ByteArray?) TODO
        setBlink(false)
        setBlink(true)
      }
    }
    color = s as String?
  }

  override fun createEditor(): GridEditorColorField = GridEditorColorField()

  override fun createConverter(): Converter<Any?, Any?> {
    return object : Converter<Any?, Any?> {

      override fun convertToModel(value: Any?, context: ValueContext?): Result<Any?>? = Result.ok(value)

      override fun convertToPresentation(value: Any?, context: ValueContext?): Any? = value
    }
  }

  override fun createRenderer(): Renderer<Any?> {
    TODO()
  }

  override fun valueChanged(oldValue: String?) {
    // Nothing to do
  }

  /*override fun getEditor(): GridEditorImageField { TODO
    return super.getEditor() as GridEditorImageField
  }*/
}
