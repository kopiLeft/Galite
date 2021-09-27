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
import org.kopi.galite.visual.ui.vaadin.grid.ActorRenderer
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorActorField

import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer

/**
 * The grid editor actor field.
 */
class DGridEditorActorField(
        columnView: VFieldUI,
        label: DGridEditorLabel?,
        align: Int,
        options: Int
) : DGridEditorField<String?>(columnView, label, align, options) {

  init {
    if (getModel().toolTip != null) {
      // editor.setDescription(model.getToolTip()) TODO
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  override fun updateText() {}

  override fun getObject(): Any? = null

  override fun createEditor(): GridEditorActorField = GridEditorActorField(getModel().label)

  override fun createConverter(): Converter<String?, Any?> {
    return object : Converter<String?, Any?> {

      override fun convertToModel(value: String?, context: ValueContext?): Result<Any?>? = null

      override fun convertToPresentation(value: Any?, context: ValueContext?): String? = null

      val modelType: Class<Any> // TODO
        get() = Any::class.java

      val presentationType: Class<String> // TODO
        get() = String::class.java
    }
  }

  override fun createRenderer(): Renderer<String?> {
    return object : ActorRenderer(getModel().label) {
      override fun onClick(item: String?) {
        columnView.executeAction()
      }
    }
  }

  override fun onClick() {
    // field action is performed in the window action queue
    // it is not like the other fields trigger
    columnView.executeAction()
  }

  override fun valueChanged(oldValue: String?) {
    // Nothing to do
  }
}
