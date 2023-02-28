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
package org.kopi.galite.visual.ui.vaadin.form

import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.block.Block
import org.kopi.galite.visual.ui.vaadin.common.VCaption
import org.kopi.galite.visual.ui.vaadin.common.VTable
import org.kopi.galite.visual.ui.vaadin.event.PositionPanelListener

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs

/**
 * The form component.
 *
 * @param pageCount The form page count.
 * @param titles The pages title.
 */
@CssImport("./styles/galite/form.css")
class Form(val pageCount: Int, val titles: List<String>) : Div(), PositionPanelListener {

  /**
   * The current position
   */
  var currentPosition = 0

  /**
   * Total positions
   */
  var totalPositions = 0

  private var currentPage = -1
  private var pages: Array<Page<*>?> = arrayOfNulls(if (pageCount == 0) 1 else pageCount)
  private val tabsToPages: MutableMap<Tab, Component> = mutableMapOf()
  private val tabs: MutableList<Tab> = mutableListOf()
  private var tabPanel: Tabs? = null
  private var listeners: MutableList<FormListener> = mutableListOf()
  private var lastSelected: Tab? = null
  private var blockInfo = PositionPanel()

  init {
    className = Styles.FORM
    init(pageCount, titles)
  }

  /**
   * Sets the form content.
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  private fun setContent(pageCount: Int, titles: List<String>) {
    if (pageCount == 0) {
      setContent(pages[0]!!)
    } else {
      tabPanel = Tabs()
      tabPanel!!.className = Styles.FORM_TAB_PANEL
      for (i in pages.indices) {
        val tab = createTabLabel(titles[i])
        tabs.add(tab)
        tabsToPages[tab] = pages[i]!!
        tabPanel!!.add(tab)
        tab.isEnabled = false
        tab.addClassName("tab")
      }

      tabPanel!!.addSelectedChangeListener {
        if (it.isFromClient) {
          // This to prevent user from switch tabs. the method firePageSelected() is responsible for changing page.
          // This will keep the previous tab if firePageSelected fails to switch tabs because an error occurred
          // (For example: the used didn't fill a MUSTFILL field)
          lastSelected = it.previousTab
          tabPanel!!.selectedTab = lastSelected

          firePageSelected(pages.indexOf(tabsToPages[it.selectedTab]))
        }
      }
      setContent(tabPanel!!, Div(*pages))
    }
  }

  /**
   * Sets the given components as children of this form.
   *
   * @param components the components to add.
   */
  private fun setContent(vararg components: Component) {
    removeAll()
    val table = VTable(0, 0)

    components.forEach {
      table.addInNewRow(it)
    }
    add(table)
  }

  /**
   * Selects the given page
   * @param page The page index.
   */
  private fun selectPage(page: Int) {
    if (tabPanel != null) {
      tabs[page].isEnabled = true
      tabPanel!!.selectedIndex = page
      lastSelected = tabPanel!!.selectedTab
    }
  }

  /**
   * Creates a page label.
   * @param title The page title.
   * @return The page tab
   */
  private fun createTabLabel(title: String): Tab =
          Tab(if (title.endsWith("<CENTER>")) title.substring(0, title.length - 8) else title)

  /**
   * Adds a block to this form.
   * @param block The block to be added.
   * @param page The page index.
   * @param isFollow Is it a follow block ?
   * @param isChart Is it a chart block ?
   */
  fun addBlock(block: Block, page: Int, isFollow: Boolean, isChart: Boolean) {
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

    block.layout()
    block.layoutAlignedComponents()
  }

  /**
   * Sets the block border.
   * @param block The block.
   */
  internal fun setBorder(block: DBlock, page: Int) {
    block.setBorder(block.model.border.value, block.model.title, pages[page])
  }

  /**
   * Goes to the page with index = i
   * @param i The page index.
   */
  fun gotoPage(i: Int) {
    currentPage = i
    lastSelected?.let {
      tabsToPages[it]!!.isVisible = false
      it.isSelected = false
    }
    lastSelected?.removeClassName("selected-tab")
    pages[i]!!.isVisible = true
    selectPage(i)
    if (tabPanel != null) {
      tabPanel!!.getComponentAt(currentPage).element.classList.add("selected-tab")
    }
  }

  /**
   * Sets the block info position.
   * @param current The current record.
   * @param total The total records.
   */
  fun setPosition(current: Int, total: Int) {
    currentPosition = current
    totalPositions = total
    blockInfo.setPosition(current, total)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Initializes the form component content.
   *
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  fun init(pageCount: Int, titles: List<String>) {
    // not used any more but we keep it may be we will used again
    pages = arrayOfNulls(if (pageCount == 0) 1 else pageCount)
    for (i in pages.indices) {
      pages[i] = if (pageCount != 0) {
        if (titles[i].endsWith("<CENTER>")) {
          Page(HorizontalLayout())
        } else {
          Page(VerticalLayout())
        }
      } else {
        Page(VerticalLayout())
      }
      pages[i]!!.isVisible = false

      if (pageCount == 0) {
        pages[i]!!.style["max-height"] = "calc(100vh - 210px)"
      } else {
        pages[i]!!.style["max-height"] = "calc(100vh - 260px)"
      }
    }
    // setPages content.
    setContent(pageCount, titles)
  }

  /**
   * Creates the block info component.
   */
  fun showBlockInfo() {
    blockInfo.isVisible = false // hide it initially
    blockInfo.show()
  }

  /**
   * Sets the form content.
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  private fun setContent(pageCount: Int, titles: Array<String>, separator: String?) {
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
    tabs.getOrNull(page)?.isEnabled = enabled
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
    blockInfo.removePositionPanelListener(l)
  }

  /**
   * Fires a page selection event.
   * @param page The page index.
   */
  private fun firePageSelected(page: Int) {
    for (l in listeners) {
      l.onPageSelection(page)
    }
  }

  /**
   * Requests to go to the next position.
   */
  private fun fireGotoNextPosition() {
    for (l in listeners) {
      l.gotoNextPosition()
    }
  }

  /**
   * Requests to go to the previous position.
   */
  private fun fireGotoPrevPosition() {
    for (l in listeners) {
      l.gotoPrevPosition()
    }
  }

  /**
   * Requests to go to the last position.
   */
  private fun fireGotoLastPosition() {
    for (l in listeners) {
      l.gotoLastPosition()
    }
  }

  /**
   * Requests to go to the last position.
   */
  private fun fireGotoFirstPosition() {
    for (l in listeners) {
      l.gotoFirstPosition()
    }
  }

  /**
   * Requests to go to the specified position.
   * @param posno The position number.
   */
  private fun fireGotoPosition(posno: Int) {
    for (l in listeners) {
      l.gotoPosition(posno)
    }
  }

  protected fun init() {
    addPositionPanelListener(this)
  }

  fun delegateCaptionHandling(): Boolean {
    // do not delegate caption handling
    return false
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
}
