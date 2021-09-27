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
package org.kopi.galite.visual.ui.vaadin.welcome

import com.vaadin.flow.component.ComponentEvent

/**
 * A welcome screen event that encapsulates the login information.
 *
 * @param source The source component.
 * @param username The user name.
 * @param password The password.
 * @param locale The locale.
 */
class WelcomeViewEvent(source: WelcomeView,
                       val username: String,
                       val password: String,
                       val locale: String)
  : ComponentEvent<WelcomeView?>(source, false) {

  /**
   * Returns the welcome screen component.
   */
  val welcomeView: WelcomeView
    get() = super.getSource() as WelcomeView
}
