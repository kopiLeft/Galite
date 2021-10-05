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
package org.kopi.galite.visual.ui.vaadin.common

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div

@CssImport.Container(value = [
  CssImport("./styles/galite/dialog.css" , themeFor = "vaadin-dialog-overlay"),
  CssImport("./styles/galite/dialog.css")
])
open class VDialog(withHeader: Boolean, withFooter: Boolean) : Dialog() {

  protected val dialogHeader = Div()
  protected val dialogContent = Div()
  protected val dialogFooter = Div()

  init {
    setId("v-dialog")
    super.getElement().setAttribute("theme", "v-dialog")

    if(withHeader) {
      super.add(dialogHeader)
      dialogHeader.setId("v-dialog-header")
    }

    super.add(dialogContent)

    if(withFooter) {
      super.add(dialogFooter)
      dialogFooter.setId("v-dialog-footer")
    }

    addAttachListener {
      dialogContent.element.parent.classList.remove("draggable-leaf-only")
    }
  }

  /**
   * Replaces the header with given components
   *
   * @param components  components to add to the header
   */
  open fun setHeader(vararg components: Component) {
    dialogHeader.removeAll()
    for (component in components) {
      addToHeader(component)
    }
  }

  /**
   * Adds given components to the header
   *
   * @param components  components to add to the header
   */
  open fun addToHeader(vararg components: Component) {
    for (component in components) {
      dialogHeader.add(component)
    }
  }

  /**
   * Replaces the footer with given components
   *
   * @param components  components to add to the footer
   */
  open fun setFooter(vararg components: Component) {
    dialogFooter.removeAll()
    for (component in components) {
      addToFooter(component)
    }
  }

  /**
   * Adds given components to footer
   *
   * @param components  components to add to the footer
   */
  open fun addToFooter(vararg components: Component) {
    for (component in components) {
      dialogFooter.add(component)
    }
  }

  /**
   * Replaces the content of dialog with given components
   *
   * @param components  components to add to the content
   */
  open fun setContent(vararg components: Component?) {
    dialogContent.removeAll()
    add(*components)
  }

  /**
   * Adds given components to the dialog content
   *
   * @param components  components to add to the content
   */
  override fun add(vararg components: Component?) {
    dialogContent.add(*components)
  }

  /**
   * remove given components from the dialog content
   *
   * @param components  components to remove from the content
   */
  override fun remove(vararg components: Component) {
    super.remove(*components)
  }

  /**
   * remove all components from the dialog content
   */
  override fun removeAll() {
    dialogContent.removeAll()
  }
}
