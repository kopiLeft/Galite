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

import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.grid.GridEditorImageField

import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer

class DGridEditorImageField(
        columnView: VFieldUI,
        label: DGridEditorLabel?,
        align: Int,
        width: Int,
        height: Int,
        options: Int
) : DGridEditorField<Any?>(columnView, label, align, options) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var image: ByteArray? = null

  init {
    (editor as GridEditorImageField).setImageWidth(width)
    (editor as GridEditorImageField).setImageHeight(height)
  }

  companion object {
    private var keyCounter = 0
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateText() {
    // TODO
  }

  override fun getObject(): Any? = image

  fun setObject(s: Any?) {
    //BackgroundThreadHandler.access(Runnable { TODO
    if (s != null) {
      //editor.setImage(s as ByteArray?) TODO
      setBlink(false)
      setBlink(true)
    }
    //})
    image = s as ByteArray?
  }

  override fun createEditor(): GridEditorImageField = GridEditorImageField()

  override fun createConverter(): Converter<Any?, Any?> {
    TODO()
  }

  override fun createRenderer(): Renderer<Any?> {
    TODO()
  }

  /*override fun getEditor(): GridEditorImageField { TODO
    return super.getEditor() as GridEditorImageField
  }*/
}
