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
package org.kopi.galite.visual.ui.vaadin.grid

/**
 * The grid editor actor field server side implementation
 */
class GridEditorActorField(caption: String?) : GridEditorField<String?>() {

  /**
   * The actor field icon name.
   */
  var icon: String? = null

  init {
    //setCaption(caption) TODO
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  val type: Class<out String>
    get() = String::class.java

  override fun setPresentationValue(newPresentationValue: String?) {
    TODO("Not yet implemented")
  }

  override fun getValue(): String? {
    TODO("Not yet implemented")
  }

  override fun doFocus() {
    TODO("Not yet implemented")
  }

  override fun addFocusListener(focusFunction: () -> Unit) {
    TODO("Not yet implemented")
  }

  override fun setBlink(blink: Boolean) {}

  /*override fun addNavigationListener(listener: NavigationListener?) {
    // NOT SUPPORTED
  }

  override fun removeNavigationListener(listener: NavigationListener?) {
    // NOT SUPPORTED
  }*/
}
