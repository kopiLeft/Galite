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
package org.kopi.galite.demo.web.vaadin

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import java.util.Locale

import org.kopi.galite.db.DBContext
import org.kopi.galite.tests.VApplicationTestBase
import org.kopi.galite.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.ui.vaadin.notif.ConfirmNotification
import org.kopi.galite.ui.vaadin.notif.InformationNotification
import org.kopi.galite.ui.vaadin.notif.WarningNotification
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
open class Application : SpringBootServletInitializer()

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}

class GaliteApplication : VApplication(VApplicationTestBase.GaliteRegistry()) {
  override val sologanImage get() = "slogan.png"
  override val logoImage get() = "logo_kopi.png"
  override val logoHref get() = "http://"
  override val alternateLocale get() = Locale("de", "AT")
  override val supportedLocales
    get() =
      arrayOf(Locale.FRANCE,
              Locale("de", "AT"),
              Locale("ar", "TN"))

  override fun login(
          database: String,
          driver: String,
          username: String,
          password: String,
          schema: String?
  ): DBContext? {
    return try {
      DBContext().apply {
        this.defaultConnection = this.createConnection(driver, database, username, password, true, schema)
      }
    } catch (exception: Throwable) {
      null
    }
  }
}

@Route("confirm")
class ConfirmNotificationUI : VerticalLayout() {
  val confirmationDialog = ConfirmNotification("Question", "Quitter : Êtes-vous sûr ?")
  init {
    confirmationDialog.locale = "en_GB"
    val button = Button("Open Dialog") { _ -> confirmationDialog.open() }
    add(button)
  }
}

@Route("warning")
class WarningNotificationUI : VerticalLayout() {
  val warningDialog = WarningNotification("Warning", "Message warning")

  init {
    warningDialog.locale = "en_GB"
    val button = Button("Open Dialog") { _ -> warningDialog.open() }
    add(button)
  }
}

@Route("error")
class ErrorNotificationUI : VerticalLayout() {
  val errorDialog = ErrorNotification("Erreur", "Message d'erreur")

  init {
    errorDialog.locale = "en_GB"
    val button = Button("Open Dialog") { _ -> errorDialog.open() }
    add(button)
  }
}

@Route("information")
class InformationNotificationUI : VerticalLayout() {
  val infoDialog = InformationNotification("Info", "Message d'information")

  init {
    infoDialog.locale = "en_GB"
    val button = Button("Open Dialog") { _ -> infoDialog.open() }
    add(button)
  }
}
