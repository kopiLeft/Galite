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
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.visual.ui.vaadin.field.ColorField

class DColorField(model: VFieldUI,
                  label: DLabel?,
                  align: Int,
                  options: Int,
                  detail: Boolean) : DObjectField(model, label, align, options, detail) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private var color: String? = null
  private val field = ColorField()

  // --------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------

  /**
   * Creates a new `DColorField` instance.
   */
  init {
    field.addObjectFieldListener(this)
    field.getContent().element.addEventListener("change") {
      setColortoVField(field.value as String?)
    }
    setFieldContent(field)
  }

  // --------------------------------------------------
  // IMPLEMENTATION OF ABSTRACTS METHODS
  // --------------------------------------------------

  override fun getObject(): Any? {
    return color
  }

  override fun setBlink(blink: Boolean) {
    // nothing to do for the moment
  }

  override fun updateColor() {
    // not yet implemented
  }

  /**
   * Sets the object associated to current record
   *
   * @param s The object to set in
   */
  fun setObject(s: Color?) {
    val stringColor = s?.let { colorToRgbString(s) }
    BackgroundThreadHandler.access(currentUI) {
      field.setData(stringColor)
    }
    color = stringColor
  }

  override fun updateText() {
    setObject((getModel() as VColorField).getColor(model.blockView.getRecordFromDisplayLine(position)))
    super.updateText()
  }

  fun setColortoVField(value: String?) {
    getModel().isChangedUI = true
    getModel().setColor(rgbStringToColor(value))
  }

  /**
   * Convert RGB String to Java.awt.Color.
   */
  fun rgbStringToColor(hexString: String?): Color {
    // Remove the '#' from the beginning of the RGB string
    val rgbString = if (hexString!!.startsWith("#")) hexString.substring(1) else hexString

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
  fun colorToRgbString(color: Color): String {
    val redHex = String.format("%02x", color.red)
    val greenHex = String.format("%02x", color.green)
    val blueHex = String.format("%02x", color.blue)

    return "$redHex$greenHex$blueHex"
  }
}
