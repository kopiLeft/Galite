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

import org.kopi.galite.ui.vaadin.block.Block
import org.kopi.galite.ui.vaadin.common.VCaption
import org.kopi.galite.ui.vaadin.event.PositionPanelListener

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
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
class Form(val pageCount: Int, val titles: Array<String>) : Div(), FormListener, PositionPanelListener {

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
  var blocksData: MutableMap<Component, BlockComponentData> = mutableMapOf()

  private var currentPage = -1
  private var pages: Array<Page<*>?> = arrayOfNulls(if (pageCount == 0) 1 else pageCount)
  private val tabsToPages: MutableMap<Tab, Component> = mutableMapOf()
  private var tabPanel: Tabs = Tabs()
  private var listeners: MutableList<FormListener> = mutableListOf()
  private var lastSelected = -1
  private var fireSelectionEvent = true
  private var blockInfo: PositionPanel = PositionPanel()

  init {
    className = Styles.FORM
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
    if (blockInfo != null) {
      blockInfo.setPosition(current, total)
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Initializes the form component content.
   * @param locale The application locale.
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  fun init(
          pageCount: Int,
          titles: Array<String>,
          separator: String?,
  ) {
    // not used any more but we keep it may be we will used again
    pages = arrayOfNulls(if (pageCount == 0) 1 else pageCount)
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
    setContent(pageCount, titles, separator)
  }

  /**
   * Creates the block info widget.
   * @param connection The application connection.
   * @param locale The application locale.
   */
  fun showBlockInfo(block: Block) {
    blockInfo.isVisible = false // hide it initially
    blockInfo.show(block)
  }

  /**
   * Sets the form content.
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  protected fun setContent(pageCount: Int, titles: Array<String>, separator: String?) {
    // TODO
  }

  /**
   * Creates a page caption.
   * @param title The page title.
   * @return The page caption
   */
  protected fun createCaption(title: String): VCaption =
          VCaption(true).also { caption ->
            caption.setCaption(if (title.endsWith("<CENTER>")) title.substring(0, title.length - 8) else title)
          }

  /**
   * Sets the page enabled or disabled.
   * @param enabled the page ability.
   * @param page The page index.
   */
  fun setEnabled(enabled: Boolean, page: Int) {
    // TODO
  }

  /**
   * Registers a form listener.
   * @param l The listener to be registered.
   */
  fun addFormListener(l: FormListener) {
    listeners.add(l)
  }

  /**
   * Removes a form listener.
   * @param l The listener to be removed.
   */
  fun removeFormListener(l: FormListener) {
    listeners.remove(l)
  }

  /**
   * Registers a position panel listener.
   * @param l The listener to be registered.
   */
  fun addPositionPanelListener(l: PositionPanelListener) {
    if (blockInfo != null) {
      blockInfo.addPositionPanelListener(l)
    }
  }

  /**
   * Removes a position panel listener.
   * @param l The listener to be removed.
   */
  fun removePositionPanelListener(l: PositionPanelListener) {
    if (blockInfo != null) {
      blockInfo.removePositionPanelListener(l)
    }
  }

  /**
   * Fires a page selection event.
   * @param page The page index.
   */
  protected fun firePageSelected(page: Int) {
    for (l in listeners) {
      l.onPageSelection(page)
    }
  }

  /**
   * Requests to go to the next position.
   */
  protected fun fireGotoNextPosition() {
    for (l in listeners) {
      l.gotoNextPosition()
    }
  }

  /**
   * Requests to go to the previous position.
   */
  protected fun fireGotoPrevPosition() {
    for (l in listeners) {
      l.gotoPrevPosition()
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected fun fireGotoLastPosition() {
    for (l in listeners) {
      l.gotoLastPosition()
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected fun fireGotoFirstPosition() {
    for (l in listeners) {
      l.gotoFirstPosition()
    }
  }

  /**
   * Requests to go to the specified position.
   * @param posno The position number.
   */
  protected fun fireGotoPosition(posno: Int) {
    for (l in listeners) {
      l.gotoPosition(posno)
    }
  }

  protected fun init() {
    addFormListener(this)
    addPositionPanelListener(this)
  }

  fun delegateCaptionHandling(): Boolean {
    // do not delegate caption handling
    return false
  }

  override fun onAttach(event: AttachEvent) {
    if (event.isInitialAttach) {
      init(pageCount, titles,"single.gif") // TODO: full path to image?
      // look for blocks
      for (child in children) {
        if (child is Block) {
          val data = blocksData[child]
          if (data != null) {
            addBlock(child, data.page, data.isFollow, data.isChart)
          }
        } else if (child is Grid<*>) {
          val data = blocksData[child]
          if (data != null) {
            addBlock(child, data.page, data.isFollow, data.isChart)
          }
        }
      }
    }
  }

  override fun onPageSelection(page: Int) {
    // communicates the dirty values before leaving page
    cleanDirtyValues(null)
    disableAllBlocksActors()
    firePageSelected(page)
  }

  /*fun onUnregister() { TODO
    removeFormListener(this)
    removePositionPanelListener(this)
    if (getParent() is VWindow) {
      (getParent() as VWindow).clearFooter()
    }
    clear()
    super.onUnregister()
  }*/

  override fun gotoNextPosition() {
    fireGotoNextPosition()
  }

  override fun gotoPrevPosition() {
    fireGotoPrevPosition()
  }

  override fun gotoLastPosition() {
    fireGotoLastPosition()
  }

  override fun gotoFirstPosition() {
    fireGotoFirstPosition()
  }

  override fun gotoPosition(posno: Int) {
    fireGotoPosition(posno)
  }

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  /**
   * Sets the position in the position panel.
   * @param current The current position.
   * @param total The total number of positions.
   */
  fun setCurrentPosition(current: Int, total: Int) {
    setPosition(current, total)
  }

  /**
   * Cleans the dirty values of this form.
   */
  fun cleanDirtyValues(active: Block?, transferFocus: Boolean = true) {
    for (child in children) {
      if (child is Block) {
        child.cleanDirtyValues(active, transferFocus)
      }
    }
  }

  /**
   * Disables all block actors
   */
  fun disableAllBlocksActors() {
    for (child in children) {
      if (child is Block) {
        child.setColumnViewsActorsEnabled(false)
      }
    }
  }
}
