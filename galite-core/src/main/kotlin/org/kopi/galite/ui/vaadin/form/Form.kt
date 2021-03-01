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
package org.kopi.galite.ui.vaadin.form

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import org.kopi.galite.ui.vaadin.base.Styles

/**
 * The form component.
 *
 * @param pageCount The form page count.
 * @param titles The pages title.
 */
@CssImport("./styles/galite/Form.css")
class Form(val pageCount: Int, val titles: Array<String>) : Div() {

  /**
   * The form locale.
   */
  var locale: String? = null

  /**
   * The current position
   */
  var currentPosition = 0

  /**
   * Total positions
   */
  var totalPositions = 0

  /**
   * The blocks components data.
   */
  //var blocksData: Map<Connector, BlockComponentData> = HashMap<Connector, BlockComponentData>() TODO

  private var currentPage = -1
  private val pages: Array<Page<*>?> = arrayOfNulls(if (pageCount == 0) 1 else pageCount)
  private val tabsToPages: MutableMap<Tab, Component> = mutableMapOf()
  private var tabPanel: Tabs = Tabs()

  init {
    for (i in pages.indices) {
      if (pageCount != 0) {
        if (titles[i].endsWith("<CENTER>")) {
          pages[i] = Page(HorizontalLayout())
        } else {
          pages[i] = Page(VerticalLayout())
        }
      } else {
        pages[i] = Page(VerticalLayout())
      }
    }
    // setPages content.
    setContent(pageCount, titles)
  }

  /**
   * Sets the form content.
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  private fun setContent(pageCount: Int, titles: Array<String>) {
    if (pageCount == 0) {
      setContent(pages[0]!!)
    } else {
      tabPanel.className = Styles.FORM_TAB_PANEL
      for (i in pages.indices) {
        val tab = createTabLabel(titles[i])
        tabsToPages[tab] = pages[i]!!
        tabPanel.add(tab)
        tab.isEnabled = false
      }

      tabPanel.addSelectedChangeListener {
        tabsToPages.values.forEach { page -> page.isVisible = false }
        tabsToPages[tabPanel.selectedTab]!!.isVisible = true
      }
      setContent(tabPanel, Div(*pages))
    }
  }

  /**
   * Sets the given components as children of this form.
   *
   * @param components the components to add.
   */
  private fun setContent(vararg components: Component) {
    removeAll()
    add(*components)
  }

  /**
   * Selects the given page
   * @param page The page index.
   */
  private fun selectPage(page: Int) {
    tabPanel.selectedIndex = page
    tabPanel.selectedTab.isEnabled = true
  }

  /**
   * Creates a page label.
   * @param title The page title.
   * @return The page tab
   */
  private fun createTabLabel(title: String): Tab = Tab(
          if (title.endsWith("<CENTER>")) title.substring(0, title.length - 8) else title
  )

  /**
   * Adds a block to this form.
   * @param block The block to be added.
   * @param page The page index.
   * @param isFollow Is it a follow block ?
   * @param isChart Is it a chart block ?
   */
  fun addBlock(block: Component, page: Int, isFollow: Boolean, isChart: Boolean) {
    val hAlign = if (isChart) {
      JustifyContentMode.CENTER
    } else {
      JustifyContentMode.START
    }
    if (isFollow) {
      pages[page]!!.addFollow(block, hAlign)
    } else {
      pages[page]!!.add(block, hAlign)
    }
  }

  /**
   * Goes to the page with index = i
   * @param i The page index.
   */
  fun gotoPage(i: Int) {
    currentPage = i
    selectPage(i)
  }

  /**
   * Sets the block info position.
   * @param current The current record.
   * @param total The total records.
   */
  fun setPosition(current: Int, total: Int) {
    currentPosition = current
    totalPositions = total
  }
}
