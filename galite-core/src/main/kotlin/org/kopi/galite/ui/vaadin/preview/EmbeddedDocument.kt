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
package org.kopi.galite.ui.vaadin.preview

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.Tag
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.StreamResource

class EmbeddedDocument(val type: Type = Type.OBJECT) : Component(Element(type.tag)), HasSize {

  init {
    setSizeFull()
    if (type == Type.BROWSER) {
      element.setAttribute("allowTransparency", "true")
      element.setAttribute("frameborder", "0")
    }
  }

  constructor(resource: StreamResource) : this() {
    this.resource = resource
  }

  constructor(url: String) : this() {
    element.setAttribute("data", url)
  }

  var resource: StreamResource? = null
    set(value) {
      field = value
      if (type == Type.OBJECT) {
        element.setAttribute("data", resource)
      } else {
        element.setAttribute("src", resource)
      }
    }

  var mimeType: String
    get() = element.getAttribute("type")
    set(value) {
      element.setAttribute("type", value)
    }

  enum class Type(val tag: String) {
    OBJECT("object"),
    IMG(Tag.IMG),
    BROWSER(Tag.IFRAME)
  }
}
