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
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import java.util.Locale

import org.kopi.galite.db.DBContext
import org.kopi.galite.tests.VApplicationTestBase
import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.notification.VConfirmNotification
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

@Route("a")
class A : VerticalLayout() {
  init {
    val dialog = VConfirmNotification("title","test test")
    dialog.dialog = ConfirmDialog("Question",
                               "Quitter : Êtes-vous sûr ?",
                               "Oui",
                               this::onPublish,
                               "Non",
                               this::onCancel)
    dialog.setButtons("")
    dialog.ok = VInputButton("ok")
    dialog.cancel = VInputButton("cancel")
    val button = Button("Open dialog")
    dialog.ok!!.addClickListener { dialog.open() }
    dialog.cancel!!.addClickListener { dialog.close() }
    add(button, dialog)
  }
  private fun onPublish(event: ConfirmDialog.ConfirmEvent) {}
  private fun onCancel(event: ConfirmDialog.CancelEvent) {}
}

