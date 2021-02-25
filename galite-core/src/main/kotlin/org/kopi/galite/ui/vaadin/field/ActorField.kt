/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: ActorField.java 35283 2018-01-05 09:00:51Z hacheni $
 */
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.customfield.CustomField
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.shared.Registration
import org.kopi.galite.ui.vaadin.base.Styles

/**
 * The actor field server side implementation
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
    //setWidget(button) TODO
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

  /**
   * Sets the actor field icon name.
   * @param icon The actor field icon name.
   */
  fun setIcon(icon: Icon) {
    button.icon = icon
  }

  override fun onFocus(event: FocusNotifier.FocusEvent<CustomField<Any>>?) {}

  override fun onBlur(event: BlurNotifier.BlurEvent<CustomField<Any>>?) {}

  override val isNull: Boolean = true

  override fun setValue(o: Any?) {
    // Do nothing as this is an actor field with no value
  }

  /**
   * Sets the field foreground and background colors
   * @param foreground The foreground color
   * @param background The background color
   */
  override fun setColor(foreground: String?, background: String?) {
    this.foreground = foreground
    this.background = background
    if (foreground != null && foreground.isNotEmpty()) {
      // button.element.style.setColor(foreground) TODO
    } else {
      // button.element.style.setColor("inherit") TODO
    }
    if (background != null && background.isNotEmpty()) {
      // button.element.style.setBackgroundColor(foreground) TODO
    } else {
      // button.element.style.setBackgroundColor("inherit") TODO
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

  override fun generateModelValue(): Any? {
    return null
  }
}
