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
package org.kopi.galite.ui.vaadin.field

import java.io.Serializable
import java.util.Locale

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.shared.Registration

/**
 * A rich text field implementation based on CKEditor
 * TODO: implement with appropriate component, not abstract
 */
class RichTextField(
        var col: Int,
        var rows: Int,
        visibleRows: Int,
        var noEdit: Boolean,
        locale: Locale
) : Component(),
        HasValue<AbstractField.ComponentValueChangeEvent<RichTextField, String>, String>,
        Focusable<RichTextField> {

  private val navigationListeners = ArrayList<NavigationListener>()

  override fun setValue(value: String?) {
    TODO("Not yet implemented")
  }

  override fun getValue(): String {
    TODO("Not yet implemented")
  }

  override fun addValueChangeListener(
          listener: HasValue.ValueChangeListener<in AbstractField.ComponentValueChangeEvent<RichTextField, String>>?,
  ): Registration {
    TODO("Not yet implemented")
  }

  override fun setReadOnly(readOnly: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isReadOnly(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setRequiredIndicatorVisible(requiredIndicatorVisible: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isRequiredIndicatorVisible(): Boolean {
    TODO("Not yet implemented")
  }

  //---------------------------------------------------
  // NAVGATION
  //---------------------------------------------------
  /**
   * Registers a navigation listener on this rich text.
   * @param l The listener to be registered.
   */
  fun addNavigationListener(l: NavigationListener) {
    navigationListeners.add(l)
  }

  /**
   * The grid editor field navigation listener
   */
  interface NavigationListener : Serializable {
    /**
     * Fired when a goto next field event is called by the user.
     */
    fun onGotoNextField()

    /**
     * Fired when a goto previous field event is called by the user.
     */
    fun onGotoPrevField()

    /**
     * Fired when a goto next block event is called by the user.
     */
    fun onGotoNextBlock()

    /**
     * Fired when a goto previous record event is called by the user.
     */
    fun onGotoPrevRecord()

    /**
     * Fired when a goto next field event is called by the user.
     */
    fun onGotoNextRecord()

    /**
     * Fired when a goto first record event is called by the user.
     */
    fun onGotoFirstRecord()

    /**
     * Fired when a goto last record event is called by the user.
     */
    fun onGotoLastRecord()

    /**
     * Fired when a goto next empty mandatory field event is called by the user.
     */
    fun onGotoNextEmptyMustfill()
  }
}
