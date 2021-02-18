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
package org.kopi.galite.ui.vaadin.label

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import java.util.*

/**
 * The sortable label component.
 */
open class SortableLabel(text: String?) : Label(), HasComponents {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the label to be sortable.
   * @param sortable is it a sortable label ?
   */
  open fun setSortable(sortable: Boolean) {
    this.sortable = sortable
    if (sortable) {
      element.style.set("styleSuffix", "sortable")
      add(deck)
    }
  }

  /**
   * Returns `true` if the sort is activated on this label.
   * @return `true` if the sort is activated on this label.
   */
  open fun isSortable(): Boolean {
    return sortable
  }

  /**
   * Registers a sortable label listener.
   * @param l The listener to be registered.
   */
  open fun addSortableLabelListener(l: SortableLabelListener) {
    listeners!!.add(l)
  }

  /**
   * Removes a sortable label listener.
   * @param l The listener to be removed.
   */
  open fun removeSortableLabelListener(l: SortableLabelListener) {
    listeners!!.remove(l)
  }

  /**
   * Fires on sort event.
   */
  protected open fun fireOnSort() {
    listeners!!.forEach {
      if (it != null) {
        it.onSort(sortMode)
      }
    }
  }

  /**
   * Switches the sort image.
   */
  protected open fun switchSortImage() {
    sortMode++
    if (sortMode > SORT_DESC) {
      sortMode = SORT_NONE
    }
    addComponentAtIndex(sortMode, deck)
  }

  /**
   * Sets the blank image.
   * @param src The blank image
   */
  open fun setNoneImage(src: String?) {
    none!!.src = src
  }

  /**
   * Sets the ASC image.
   * @param src The ASC image
   */
  open fun setAscImage(src: String?) {
    asc!!.src = src
  }

  /**
   * Sets the DESC image.
   * @param src The DESC image
   */
  open fun setDescImage(src: String?) {
    desc!!.src = src
  }

  /**
   * Ensures that sort icons are visible
   * by applying a negative margin when
   * label width is exactly the label
   * container width.
   * @param margin The margin amount to be applied.
   */
  open fun setSortIconsMargin(margin: Double) {
    deck!!.element.style.set("STYLE_PADDING_LEFT", "0 px")
    deck!!.element.style.set("STYLE_MARGIN_LEFT", "$margin px")
  }

  override fun clear() {
    super.clear()
    asc = null
    desc = null
    none = null
    deck = null
    listeners!!.clear()
    listeners = null
  }

  open fun onClick(event: ShortcutEvent?) {
    // switch sort
    switchSortImage()
    // fire event.
    fireOnSort()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var asc: Image? = null
  private var desc: Image? = null
  private var none: Image? = null
  private var deck: Div? = null
  private var sortMode = 0
  private var sortable = false
  private var listeners: MutableList<SortableLabelListener>? = null

  /**
   * Constants defining the current direction of the sort.
   */
  var SORT_NONE = 0
  var SORT_ASC = 1
  var SORT_DESC = 2

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new sortable label widget.
   */
  init {
    listeners = ArrayList<SortableLabelListener>()
    deck = Div()
    deck!!.element.style.set("name", "sort-icons")
    asc = Image()
    desc = Image()
    none = Image()
    asc!!.height = "10px"
    asc!!.width = "8px"
    asc!!.element.setAttribute("align", "absmiddle")
    asc!!.element.setProperty("border", 0.0)
    none!!.height = "10px"
    none!!.width = "8px"
    asc!!.element.setAttribute("align", "absmiddle")
    none!!.element.setProperty("border", 0.0)
    desc!!.height = "10px"
    desc!!.width = "8px"
    desc!!.element.setAttribute("align", "absmiddle")
    desc!!.element.setProperty("border", 0.0)
    deck!!.add(none)
    deck!!.add(asc)
    deck!!.add(desc)
    add(deck)
    Shortcuts.addShortcutListener(deck,
                                  { keyDownEvent: ShortcutEvent? -> onClick(keyDownEvent) },
                                  Key.ON_DEMAND)
            .listenOn(this)
    sortMode = SORT_NONE
  }
}