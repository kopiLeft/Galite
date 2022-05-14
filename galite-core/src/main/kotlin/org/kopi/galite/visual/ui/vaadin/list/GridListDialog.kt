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
package org.kopi.galite.visual.ui.vaadin.list

import org.kopi.galite.visual.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.base.VInputButton
import org.kopi.galite.visual.ApplicationContext

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * A list dialog
 */
@CssImport.Container(value = [
  CssImport("./styles/galite/grid.css" , themeFor = "vaadin-grid"),
  CssImport("./styles/galite/list.css" , themeFor = "vaadin-grid"),
  CssImport("./styles/galite/list.css" , themeFor = "vcf-enhanced-dialog-overlay")
])
open class GridListDialog : EnhancedDialog(), HasEnabled, KeyNotifier, HasStyle {

  protected var newForm: VInputButton? = null
  protected val close = Button(LocalizedProperties.getString(locale, "CLOSE"))
  private var content: VerticalLayout = VerticalLayout()
  protected var pattern: String? = null

  init {
    className = Styles.LIST_DIALOG_CONTAINER
    element.themeList.add(Styles.LIST_DIALOG_CONTAINER)
    content.className = Styles.LIST_DIALOG
    content.element.setAttribute("hideFocus", "true")
    content.element.style["outline"] = "0px"
    isResizable = true
    isCloseOnOutsideClick = false
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------


  fun showListDialog() {
    // now show the list dialog
    super.open()
  }

  override fun open() {
    showListDialog()
  }

  /**
   * Sets the table component associated with this list dialog.
   */
  protected var table: ListTable? = null
    set(table) {
      field = table
      field!!.className = Styles.LIST_DIALOG_TABLE
      field!!.addThemeName(Styles.LIST_DIALOG_TABLE)
      content.add(field) // put table inside the focus panel
      if (newForm != null) {
        content.add(newForm)
      }
      add(field!!.widthStyler, content)
      addToFooter(close)
    }

  private val locale get() = ApplicationContext.applicationContext.getApplication().defaultLocale.toString()
}
