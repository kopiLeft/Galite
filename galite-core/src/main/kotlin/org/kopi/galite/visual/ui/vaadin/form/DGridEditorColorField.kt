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

import java.awt.Color

import org.kopi.galite.visual.form.VColorField
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorColorField

import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer

class DGridEditorColorField(columnView: VFieldUI,
                            label: DGridEditorLabel?,
                            align: Int,
                            options: Int) : DGridEditorField<Any?>(columnView, label, align, options) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private var color: String? = null

  init {
    (editor as GridEditorColorField).addValueChangeListener { this }
    (editor as GridEditorColorField).getContent().element.addEventListener("change") {
      setColortoVField(((editor as GridEditorColorField).value as String?) ?: "#000000")
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateText() {
    setObject((getModel() as VColorField).getColor(columnView.blockView.getRecordFromDisplayLine(position)))
  }

  override fun getObject(): Any? = color

  fun setObject(s: Color?) {
    val stringColor = s?.let { colorToRgbString(s) }
    access(currentUI) {
      (editor as GridEditorColorField).setData(stringColor)
    }
    color = stringColor
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

  private fun setColortoVField(value: String) {
    getModel().isChangedUI = true
    getModel().setColor(rgbStringToColor(value))
  }

  /**
   * Convert RGB String to Java.awt.Color.
   */
  private fun rgbStringToColor(hexString: String): Color {
    // Remove the '#' from the beginning of the RGB string
    val rgbString = if (hexString.startsWith("#")) hexString.substring(1) else hexString

    // Split the RGB string into red, green, and blue components
    val redHex = rgbString.substring(0, 2)
    val greenHex = rgbString.substring(2, 4)
    val blueHex = rgbString.substring(4, 6)

    // Convert hex to decimal and create a Color object
    val red = redHex.toInt(16)
    val green = greenHex.toInt(16)
    val blue = blueHex.toInt(16)

    return Color(red, green, blue)
  }

  /**
   * Convert a Java.awt.Color object to Hexadecimal String.
   */
  private fun colorToRgbString(color: Color): String {
    val redHex = String.format("%02x", color.red)
    val greenHex = String.format("%02x", color.green)
    val blueHex = String.format("%02x", color.blue)

    return "$redHex$greenHex$blueHex"
  }
}
