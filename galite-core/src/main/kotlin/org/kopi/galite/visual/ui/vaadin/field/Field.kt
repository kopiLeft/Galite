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
package org.kopi.galite.visual.ui.vaadin.field

import org.kopi.galite.visual.ui.vaadin.form.DBlock
import org.kopi.galite.visual.ui.vaadin.form.DField
import org.kopi.galite.visual.ui.vaadin.window.Window

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.html.Div

/**
 * The field component. Contains one text input field or other component
 * like image field.
 *
 * @param hasIncrement has increment button ?
 * @param hasDecrement has decrement button ?
 */
abstract class Field(val hasIncrement: Boolean, val hasDecrement: Boolean) : Div(), FieldListener, HasStyle {

  lateinit var wrappedField: AbstractField<*>

  //---------------------------------------------------
  // IMPLMENTATIONS
  //---------------------------------------------------
  /**
   * Initializes the field widget.
   * @param hasIncrement Has increment button ?
   * @param hasDecrement Has decrement button ?
   */
  open fun init(hasIncrement: Boolean, hasDecrement: Boolean) {
    /* TODO
    addDomHandler(object : ClickHandler() {
      fun onClick(event: ClickEvent?) {
        fireClicked()
      }
    }, ClickEvent.getType())*/
  }

  /**
   * Gains the focus on this field.
   */
  open fun focus() {
    wrappedField.focus()
  }

  open fun iniComponent() {
    init(hasIncrement, hasDecrement)
  }

  open fun delegateCaptionHandling(): Boolean {
    // do not delegate caption handling
    return false
  }

  open fun updateCaption(connector: Component?) {
    // not handled.
  }

  /**
   * Sets the field background and foreground colors.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  open fun setColor(foreground: String?, background: String?) {
    if (wrappedField is TextField) {
      (wrappedField as TextField).setColor(foreground, background)
    } else if (wrappedField is ObjectField<*>) {
      (wrappedField as ObjectField<*>).setColor(foreground, background)
    }
  }

  /**
   * Returns the parent window of this field.
   * @return The parent window of this field.
   */
  internal open fun getWindow(): Window? = ((this as DField).model.blockView as? DBlock)?.parent
}
