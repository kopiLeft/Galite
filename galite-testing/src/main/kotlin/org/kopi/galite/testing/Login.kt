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

import java.util.Locale

import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.base.VInputText
import org.kopi.galite.ui.vaadin.menu.ModuleItem
import org.kopi.galite.ui.vaadin.menu.ModuleList
import org.kopi.galite.ui.vaadin.notif.ConfirmNotification
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.RootMenu.Companion.ROOT_MENU_LOCALIZATION_RESOURCE

import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.menubar.MenuBar
import com.vaadin.flow.component.textfield.PasswordField

/**
 * Login to the application.
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

/**
 * Logout of the galite application.
 *
 * @param confirm confirm want to logout?
 */
fun logout(confirm: Boolean = true, duration: Long = 50) {
  val modulesMenu = _get<ModuleList> { id = "user_menu" }._get<MenuBar>()
  val manager = LocalizationManager(ApplicationContext.getDefaultLocale(), Locale.getDefault())
  val moduleLocalizer = manager.getModuleLocalizer(ROOT_MENU_LOCALIZATION_RESOURCE, "logout")

  val moduleItem = modulesMenu
    ._get<ModuleItem> { text = moduleLocalizer.getLabel()!! }
    .parent.get() as MenuItem

  modulesMenu._click(moduleItem)

  waitAndRunUIQueue(20)
  Thread.sleep(duration)

  val notificationFooter = _get<ConfirmNotification>().footer
  val button = if(confirm) {
    notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "OK") }
  } else {
    notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "NO") }
  }

  button._click()
}
