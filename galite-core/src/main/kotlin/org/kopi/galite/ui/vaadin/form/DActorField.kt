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

import org.kopi.galite.base.UComponent
import org.kopi.galite.form.UActorField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.base.Utils
import org.kopi.galite.ui.vaadin.field.ActorField

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div

/**
 * UI Implementation of an actor field.
 */
class DActorField(model: VFieldUI,
                  label: DLabel?,
                  align: Int,
                  options: Int,
                  detail: Boolean)
  : DField(model, label, align, options, detail),
        UActorField,
        ComponentEventListener<ClickEvent<Button>> {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private val field = ActorField()

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  init {
    //field.setCaption(getModel().label) TODO
    if (getModel().toolTip != null) {
      //field.setDescription(Utils.createTooltip(getModel().toolTip)) TODO
    }
    field.isEnabled = getModel().getDefaultAccess() >= VConstants.ACS_VISIT
    field.addClickHandler(this)
    //setContent(field) TODO
  }

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun onComponentEvent(event: ClickEvent<Button>?) {
    // field action is performed in the window action queue
    // it is not like the other fields trigger
    model.executeAction()
  }

  override fun setBlink(blink: Boolean) {}

  override fun getAutofillButton(): UComponent? = null

  override fun updateText() {}

  override fun updateAccess() {
    super.updateAccess()
    //BackgroundThreadHandler.access(Runnable { TODO
    field.isVisible = access != VConstants.ACS_HIDDEN
    field.isEnabled = access >= VConstants.ACS_VISIT
    //})
  }

  override fun updateFocus() {}

  override fun forceFocus() {}

  override fun updateColor() {
    //BackgroundThreadHandler.access(Runnable {  TODO
    field.setColor(Utils.toString(foreground), Utils.toString(background))
    //})
  }

  override fun getObject(): Any? {
    return null
  }

  override fun getText(): String? = null

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}
}
