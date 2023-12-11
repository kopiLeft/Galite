/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

import com.github.mvysny.kaributesting.v10._clickItemWithCaption
import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.menubar.MenuBar

import org.kopi.galite.database.Modules
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.ui.vaadin.menu.ModuleList
import org.kopi.galite.visual.ApplicationContext

/**
 * Opens a specific form.
 *
 * @param form the form caption.
 * @param duration how much time it takes to open the form.
 */
fun Form.openForm(form: String, duration: Long? = null) {
  val modulesMenu = _get<ModuleList> { id = "module_list" }._get<MenuBar>()
  val existingForms = findForms()

  if (duration != null) {
    modulesMenu._clickItemWithCaptionAndWait(form, duration)
  } else {
    val timeout = 2000
    var time = 0

    modulesMenu._clickItemWithCaption(form)
    while((findForms() - existingForms).isEmpty() && time < timeout) {
      Thread.sleep(100)
      time += 100
    }
  }
}

/**
 * Opens a specific form.
 *
 * @param duration how much time it takes to open the form.
 * @param menu to which menu the form belongs.
 * @receiver the form to open.
 */
fun Form.open(duration: Long? = null, menu: String? = null) {
  val form = lookupFormCaption(menu)

  openForm(form, duration)
}

/**
 * Lookup the form caption from the localized menu.
 *
 * @param menu to which menu the form belongs.
 */
fun Form.lookupFormCaption(menu: String? = null): String {
  val sources = transaction {
    Modules
      .slice(Modules.sourceName, Modules.shortName)
      .select { Modules.objectName eq this@lookupFormCaption::class.qualifiedName }
      .map { it[Modules.sourceName] to it[Modules.shortName] }
  }

  val manager = LocalizationManager(ApplicationContext.getDefaultLocale(), Locale.getDefault())

  val source = when {
    sources.isEmpty() -> throw Exception(this::class.qualifiedName + " was not found in any menu")
    sources.size == 1 -> sources[0]
    menu != null -> sources.first { it.first.contains(menu) }
    else -> sources[0]
  }

  val moduleLocalizer = manager.getModuleLocalizer(source.first, source.second)

  return moduleLocalizer.getLabel()!!
}
