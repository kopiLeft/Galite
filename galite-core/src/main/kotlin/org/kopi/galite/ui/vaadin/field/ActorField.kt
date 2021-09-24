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
package org.kopi.galite.ui.vaadin.field

import org.kopi.galite.ui.vaadin.base.Styles

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.shared.Registration

/**
 * The actor field
 */
class ActorField : ObjectField<Any?>() {

  /**
   * The foreground color
   */
  var foreground: String? = null

  /**
   * The background color
   */
  var background: String? = null

  private var button: Button = Button()

  init {
    button.className = Styles.ACTOR_FIELD_BUTTON
    element.classList.add(Styles.ACTOR_FIELD)
    add(button)
  }
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  fun addClickHandler(handler: ComponentEventListener<ClickEvent<Button>>?): Registration? {
    return button.addClickListener(handler)
  }

  /**
   * Sets the actor field caption.
   * @param caption The field caption.
   */
  fun setCaption(caption: String?) {
    button.text = caption
  }

  fun setDescription(tooltip: String) {
    element.setProperty("title", tooltip)
  }

  /**
   * Sets the actor field icon name.
   * @param icon The actor field icon name.
   */
  fun setIcon(icon: Icon) {
    button.icon = icon
  }

  override fun onBlur(event: BlurNotifier.BlurEvent<AbstractField<Any?>>) {}

  override fun onFocus(event: FocusNotifier.FocusEvent<AbstractField<Any?>>) {}
  override val isNull: Boolean = true

  override fun setValue(o: Any?) {
    // Do nothing as this is an actor field with no value
  }

  override fun addFocusListener(function: () -> Unit) {
    super.addFocusListener {
      function()
    }
  }

  override fun getContent(): Component = button

  /**
   * Sets the field foreground and background colors
   * @param foreground The foreground color
   * @param background The background color
   */
  override fun setColor(foreground: String?, background: String?) {
    this.foreground = foreground
    this.background = background
    if (foreground != null && foreground.isNotEmpty()) {
      button.element.style["color"] = foreground
    } else {
      button.element.style["color"] = "inherit"
    }
    if (background != null && background.isNotEmpty()) {
      button.element.style["background-color"] = foreground
    } else {
      button.element.style["background-color"] = "inherit"
    }
  }

  override fun getValue(): Any? {
    return null
  }

  override fun checkValue(rec: Int) {}

  override fun setParentVisibility(visible: Boolean) {}

  override fun setPresentationValue(newPresentationValue: Any?) {
    // Do nothing
  }
}
