/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.dsl.pivotTable

import org.jdom2.Element
import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.Menu

/**
 * This class implements an  XML localization file generator
 */
class ReportLocalizationWriter : LocalizationWriter() {
  fun genReport(title: String?,
                help: String?,
                fields: MutableList<ReportField<*>>,
                menus: MutableList<Menu>,
                actors: MutableList<Actor>) {
    val self = Element("report")

    self.setAttribute("title", title)
    if (help != null) {
      self.setAttribute("help", help)
    }
    pushNode(self)
    // coll.genLocalization(this) TODO

    // Menus
    menus.forEach { menu ->
      menu.genLocalization(this)
    }

    // Actors
    actors.forEach { actor ->
      actor.genLocalization(this)
    }

    fields.forEach { field ->
      field.genLocalization(this)
    }
    // do not pop: this is the root element
  }

  fun genField(ident: String, label: String?, help: String?) {
    val self = Element("field")
    self.setAttribute("ident", ident)
    if (label != null) {
      self.setAttribute("label", label)
    }
    if (help != null) {
      self.setAttribute("help", help)
    }
    peekNode(null).addContent(self)
  }
}
