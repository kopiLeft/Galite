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

import org.kopi.galite.form.UActorField
import org.kopi.galite.base.UComponent
import org.kopi.galite.form.VFieldUI

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ClickNotifier
import org.kopi.galite.form.VConstants
import org.kopi.galite.ui.vaadin.base.Utils

/**
 * UI Implementation of an actor field.
 */
class DActorField(model: VFieldUI,
                  label: DLabel?,
                  align: Int,
                  options: Int,
                  detail: Boolean)
  : DField(model, label, align, options, detail), UActorField {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private val field: ActorField

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  init {
    field = ActorField()
    field.setCaption(getModel().label)
    if (getModel().toolTip != null) {
      field.setDescription(Utils.createTooltip(getModel().toolTip))
    }
    field.setEnabled(getModel().getDefaultAccess() >= VConstants.ACS_VISIT)
    field.addClickListener(this)
    setContent(field)
  }

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  fun onClick(event: ClickEvent<DActorField>?) {
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
      field.setVisible(access != VConstants.ACS_HIDDEN)
      field.setEnabled(access >= VConstants.ACS_VISIT)
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

  override fun getText(): String? {
    return null
  }

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}
}
