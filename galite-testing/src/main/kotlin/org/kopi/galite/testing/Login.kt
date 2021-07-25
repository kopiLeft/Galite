/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.testing

import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.textfield.PasswordField
import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.base.VInputText

/**
 * Logins to the application.
 *
 * @param testUser the user name.
 * @param testPassword the password.
 * @param duration how much time it takes to login to the application.
 */
fun login(testUser: String, testPassword: String, duration: Long = 100) {
  // Fill to username and password fields
  _get<VInputText> { id = "user_name" }._value = testUser
  _get<PasswordField> { id = "user_password" }._value = testPassword

  //  Click on the login button
  _get<VInputButton> { id = "login_button" }._clickAndWait(duration)
}
