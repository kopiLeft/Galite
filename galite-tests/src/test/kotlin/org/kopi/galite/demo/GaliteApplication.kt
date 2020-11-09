package org.kopi.galite.demo

import org.kopi.galite.db.DBContext
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.Registry
import java.util.*

class GaliteRegistry: Registry("Galite", null)

class GaliteApplication: VApplication(GaliteRegistry()) {
  override val supportedLocales get() =
    arrayOf(Locale.FRANCE,
            Locale("de", "AT"),
            Locale("ar", "TN"))
  override val sologanImage get() = "resource/slogan.png"
  override val logoImage get() = "resource/logo_kopi.png"
  override val logoHref get() = "http://"
  override val alternateLocale get() = Locale("de", "AT")
  override fun login(
          database: String,
          driver: String,
          username: String,
          password: String,
          schema: String
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
