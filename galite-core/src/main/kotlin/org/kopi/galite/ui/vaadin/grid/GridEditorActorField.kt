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
 * $Id: GridEditorActorField.java 35283 2018-01-05 09:00:51Z hacheni $
 */
package org.kopi.galite.ui.vaadin.grid

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

  override var value: Any? = TODO()

  /*override fun addNavigationListener(listener: NavigationListener?) {
    // NOT SUPPORTED
  }

  override fun removeNavigationListener(listener: NavigationListener?) {
    // NOT SUPPORTED
  }*/
}