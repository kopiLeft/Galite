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
package org.kopi.galite.form.dsl

import org.jdom2.Element
import org.kopi.galite.common.Actor
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Menu
import org.kopi.galite.domain.Domain

/**
 * This class implements an  XML localization file generator
 */
class FormLocalizationWriter : LocalizationWriter() {

  fun genForm(title: String?,
              types: List<Domain<*>>,
              menus: List<Menu>,
              actors: List<Actor>,
              pages: List<FormPage>,
              blocks: List<FormElement>) {
    val self = Element("form")
    self.setAttribute("title", title)
    pushNode(self)

    // types
    types.forEach {
      it.genLocalization(this)
    }

    // menus
    menus.forEach {
      it.genLocalization(this)
    }

    // actors
    actors.forEach {
      it.genLocalization(this)
    }

    // pages
    pages.forEach {
      it.genLocalization(this)
    }

    blocks.forEach {
      it.genLocalization(this)
    }
    // do not pop: this is the root element
  }

  fun genBlockInsert(block: FormBlock) {
    val self = Element("blockinsert")
    pushNode(self)
    // coll.genLocalization(this) TODO
    block.genLocalization(this)
    // do not pop: this is the root element
  }

  fun genPage(ident: String?, title: String?) {
    val self = Element("page")
    self.setAttribute("ident", ident)
    self.setAttribute("title", title)
    peekNode(null).addContent(self)
  }

  /**
   * !!!FIXME handle NEW PAGE
   */
  fun genBlock(name: String?,
               title: String?,
               help: String?,
               indices: List<FormBlockIndex>,
               fields: List<FormField<*>>) {
    val self = Element("block")
    self.setAttribute("name", name)
    if (title != null) {
      self.setAttribute("title", title)
    }
    if (help != null) {
      self.setAttribute("help", help)
    }
    pushNode(self)
    indices.forEach { indice ->
      indice.genLocalization(this)
    }
    fields.forEach { field ->
      field.genLocalization(this)
    }
    popNode(self)
    peekNode(null).addContent(self)
  }

  fun genBlockIndex(ident: String?, message: String?) {
    val self = Element("index")
    self.setAttribute("ident", ident)
    self.setAttribute("message", message)
    peekNode(null).addContent(self)
  }

  fun genField(ident: String?, label: String?, help: String?) {
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
