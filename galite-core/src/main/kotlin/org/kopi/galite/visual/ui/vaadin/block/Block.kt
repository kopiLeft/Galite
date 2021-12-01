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
package org.kopi.galite.visual.ui.vaadin.block

import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.form.DBlock
import org.kopi.galite.visual.ui.vaadin.form.DBlockDropHandler
import org.kopi.galite.visual.ui.vaadin.form.Form
import org.kopi.galite.visual.ui.vaadin.form.Page

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * The component of a simple block.
 * This UI component supports only laying components for simple
 * layout view.
 */
abstract class Block(private val dropHandler: DBlockDropHandler?,
                     val form: Form) : VerticalLayout(), HasEnabled {

  /** The block layout. */
  var layout: BlockLayout? = null
  var caption: H4? = null
  private var dropWrapper: BlockDropWrapper? = null
  //cached infos
  private var doNotUpdateScrollPosition = false

  /**
   * Is the animation enabled for block view switch?
   */
  var isAnimationEnabled = false // TODO

  /**
   * The scroll page size.
   */
  var scrollPageSize = 0

  /**
   * The max scroll value.
   */
  var maxScrollValue = 0

  /**
   * Should we enable scroll bar ?
   */
  var enableScroll = false

  /**
   * The scroll value
   */
  var scrollValue = 0

  init {
    className = Styles.BLOCK
  }

  /**
   * Sets the block title.
   * @param title The block title.
   */
  fun setTitle(title: String?) {
    element.setAttribute("title", title)
    // TODO
  }

  /**
   * Switches the block view.
   * Switch is only performed when it is a multi block.
   * @param detail Should be switch to the detail view ?
   */
  fun switchView(detail: Boolean) {
    if (layout is MultiBlockLayout) {
      (layout as MultiBlockLayout).switchView(detail)
    }
  }

  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enable is the scroll bar enabled ?
   * @param value The scroll position.
   */
  fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int) {
    // TODO
  }

  /**
   * Adds a component to this block.
   * @param component The component to be added.
   * @param x the x position.
   * @param y The y position.
   * @param width the column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll Use all available area ?
   */
  fun addComponent(
          component: Component?,
          x: Int,
          y: Int,
          width: Int,
          height: Int,
          alignRight: Boolean,
          useAll: Boolean,
  ) {
    buildLayout()
    layout!!.addComponent(component, x, y, width, height, alignRight, useAll)
  }

  /**
   * Builds the block layout.
   */
  private fun buildLayout() {
    if (layout == null) {
      layout = createLayout()
      if (dropHandler != null) {
        dropWrapper = BlockDropWrapper(layout as Component, dropHandler)
        setContent(dropWrapper!!)
      } else {
        setContent(layout as Component)
      }
    }
  }

  /**
   * Fired when the scroll position has changed.
   * @param value The new scroll position.
   */
  protected fun fireOnScroll(value: Int) {
    // TODO
  }

  /**
   * Sends the new records info to client side.
   * @param rec The record number.
   * @param info The record info value.
   */
  protected open fun fireRecordInfoChanged(rec: Int, info: Int) {
    // TODO
    // forces the display after a record info update
    refresh(true)
  }

  /**
   * Notify the client side that the value of the record has changed.
   * @param col The column index.
   * @param rec The record number.
   * @param value The new record value.
   */
  internal open fun fireValueChanged(col: Int, rec: Int, value: String?) {
    // no client side cache
  }

  /**
   * Sets the block content.
   * @param content The block content.
   */
  open fun setContent(content: Component) {
    removeAll()
    add(content)
  }

  /**
   * Sets the block caption.
   * @param caption The block caption.
   */
  protected open fun setCaption(caption: String?, page: Page<*>?) {
    if (caption == null || caption.isEmpty()) {
      return
    }
    this.caption = H4(caption)
    this.caption!!.className = "block-title"
    page?.setCaption(this)
  }

  /**
   * Layout components Creates the content of the block.
   */
  fun layout() {
    // create detail block view.
    layout?.layout()
  }

  fun layoutAlignedComponents() {
    layout?.layoutAlignedComponents()
  }

  open fun clear() {
    super.removeAll()
    if (layout != null) {
      try {
        layout!!.removeAll()
      } catch (e: IndexOutOfBoundsException) {
        // ignore cause it can be cleared before
      }
      layout = null
    }
    caption = null
  }

  open fun delegateCaptionHandling(): Boolean {
    return false
  }

  open fun updateCaption(connector: Component?) {
    // not handled.
  }

  open fun  updateScroll() {
    if (layout != null) {
      layout!!.updateScroll(scrollPageSize,
                            maxScrollValue,
                            enableScroll,
                            scrollValue)
    }
  }

  /**
   * Refreshes the display of this block.
   */
  private fun refresh(force: Boolean) {
    (this as DBlock).refresh(force)
  }

  abstract fun inDetailMode(): Boolean

  fun updateScrollPos(value: Int) {
    fireOnScroll(value)
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  /**
   * Creates the block layout.
   * @return The created block layout.
   */
  abstract fun createLayout(): BlockLayout?

  /**
   * A color pair composed of a foreground and a background color.
   *
   * @param foreground The foreground color.
   * @param background The background color.
   */
  class ColorPair(var foreground: String?, var background: String?)

  /**
   * A record info structure.
   *
   * @param rec The record number.
   * @param value The record info value.
   */
  class RecordInfo(var rec: Int, var value: Int) {

    override fun equals(other: Any?): Boolean {
      return if (other is RecordInfo) {
        rec == other.rec && value == other.value
      } else {
        super.equals(other)
      }
    }

    override fun hashCode(): Int {
      return rec + value
    }
  }
}
