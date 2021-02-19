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
package org.kopi.galite.ui.vaadin.base

import com.vaadin.flow.component.Component
import com.vaadin.flow.server.Command

/**
 * Collects some utilities for background threads in a vaadin application.
 *
 *
 * Note that all performed background tasks are followed by an client UI update
 * using the push mechanism incorporated with vaadin.
 *
 */
object BackgroundThreadHandler {

  /**
   * Excluse access to the UI from a background thread to perform some updates.
   * @param command the command which accesses the UI.
   */
  fun Component.access(command: Command) {
    ui.ifPresent { myUi ->
      myUi.access(command)
    }
  }
}
