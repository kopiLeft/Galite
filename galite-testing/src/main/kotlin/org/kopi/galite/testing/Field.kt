/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.testing

import org.kopi.galite.form.UField
import org.kopi.galite.form.VField
import org.kopi.galite.form.dsl.FormField
import org.kopi.galite.type.Timestamp
import org.kopi.galite.ui.vaadin.form.DField
import org.kopi.galite.ui.vaadin.grid.GridEditorBooleanField
import org.kopi.galite.ui.vaadin.grid.GridEditorField
import org.kopi.galite.ui.vaadin.grid.GridEditorTextField
import org.kopi.galite.ui.vaadin.grid.GridEditorTimestampField
import org.kopi.galite.ui.vaadin.main.MainWindow

import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._fireDomEvent
import com.github.mvysny.kaributesting.v10._fireEvent
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.HasValueAndElement
import com.vaadin.flow.component.grid.Grid

/**
 * Edit a form field.
 *
 * @param value the value to set to this field.
 */
fun <T> FormField<T>.edit(value: T): UField {
  val mainWindow = _get<MainWindow>()
  lateinit var field: UField

  val editorField  = if (this.block.vBlock.isMulti()) {
    val column = mainWindow
      ._find<Grid.Column<*>>()
      .single { (it.editorComponent as GridEditorField<*>).dGridEditorField.getModel() eq this.vField }
    val gridEditorField = (column.editorComponent as GridEditorField<*>)

    field = gridEditorField.dGridEditorField
    gridEditorField
  } else {
    val fields = mainWindow._find<DField>()

    field = fields.single { it.getModel() eq this.vField }
    field.wrappedField
  }

  if(editorField is GridEditorBooleanField) {
    editorField._fireDomEvent("mouseover")
  }

  (editorField as ClickNotifier<*>)._clickAndWait(100)

  val oldValue = editorField._value

  editorField as HasValueAndElement<ComponentValueChangeEvent<*, Any?>, Any?>

  when (editorField) {
    is GridEditorTimestampField -> {
      editorField._value = (value as Timestamp).format("yyyy-MM-dd HH:mm:ss")
    }
    is GridEditorTextField -> {
      editorField._value = value.toString()
    }
    else -> {
      editorField._value = value
    }
  }
  editorField._fireEvent(ComponentValueChangeEvent<Component, Any?>(editorField, editorField, oldValue, true))

  return field
}

/**
 * Finds the Vaadin field component of this form field.
 */
fun <T> FormField<T>.findField(): HasValue<HasValue.ValueChangeEvent<*>, *> {
  val mainWindow = _get<MainWindow>()

  return if (block.vBlock.isMulti()) {
    mainWindow
      ._find<Grid.Column<*>>()
      .single { (it.editorComponent as GridEditorField<*>).dGridEditorField.getModel() eq vField }
      .editorComponent as GridEditorField<*>
  } else {
    mainWindow._find<DField>().single { it.getModel() eq vField }.wrappedField
  } as HasValue<HasValue.ValueChangeEvent<*>, *>
}

/**
 * Click on a field.
 */
fun <T> FormField<T>.click(): UField {
  val mainWindow = _get<MainWindow>()
  lateinit var field: UField

  val editorField  = if (this.block.vBlock.isMulti()) {
    val column = mainWindow
      ._find<Grid.Column<*>>()
      .single { (it.editorComponent as GridEditorField<*>).dGridEditorField.getModel() eq this.vField }
    val gridEditorField = (column.editorComponent as GridEditorField<*>)

    field = gridEditorField.dGridEditorField
    gridEditorField
  } else {
    val fields = mainWindow._find<DField>()

    field = fields.single { it.getModel() eq this.vField }
    field.wrappedField
  }

  (editorField as ClickNotifier<*>)._clickAndWait(50)

  return field
}


infix fun VField.eq(block: VField): Boolean {
  return this.name == block.name
          && this.label == block.label
          && this.block!! eq block.block!!
}
