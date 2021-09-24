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
package org.kopi.galite.ui.vaadin.main

import org.kopi.galite.ui.vaadin.base.Utils.findMainWindow
import org.kopi.galite.ui.vaadin.common.VCaption

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div

/**
 * The main window container component.
 * This component will be responsible of displaying only one window.
 * The control of the displayed component will be from outside.
 */
class VWindowContainer(private val menu: VWindowsMenu) : Div() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val windowToCaptionMap = WindowMap()
  private var currentWindow : Component? = null
  private val pane = Div()
  private val caption: VCaption

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    val captionWrapper = Div()
    className = "k-window-container"
    pane.className = "k-container-pane"
    caption = VCaption(false)
    caption.className = "k-window-caption"
    captionWrapper.setId("session")
    captionWrapper.add(caption)
    add(captionWrapper)
    add(pane)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds the specified window to this container.
   * @param window The window component.
   * @param title the window caption.
   */
  fun addWindow(window: Component, title: String) {
    // first add the window to the container
    pane.add(window)
    // now map the window component to its index
    windowToCaptionMap[window] = title
  }

  /**
   * Removes the given window from the container.
   * @param window The window to be removed.
   * @return The new shown component or `null` if no window is shown.
   */
  fun removeWindow(window: Component): Component? {
    val previousWindow = windowToCaptionMap.previousWindowOf(window)
    // look for internal map.
    val caption = windowToCaptionMap.remove(window)
    if (caption != null) {
      this.caption.setCaption("") // reset window caption
      pane.remove(window)
      if (windowToCaptionMap.isNotEmpty() && previousWindow != null) {
        // show previous window in the list
        return showWindow(previousWindow)
      }
    }

    return null
  }

  /**
   * Updates the given window title.
   * @param window The concerned window.
   * @param title The new title.
   */
  fun updateWindowTitle(window: Component, title: String) {
    windowToCaptionMap[window] = title
    caption.setCaption(title)
  }

  /**
   * Shows the given window in this container. This causes the currently- visible
   * window to be hidden
   *
   * @param window The window to be shown.
   * @return The new shown widget or `null` if no window is shown.
   */
  fun showWindow(window: Component?): Component? {
    // show only if we find a mapping or the component in the pane dom
    if (windowToCaptionMap.contains(window) || pane.children.toArray().contains(window)) {
      if(currentWindow != window) {
        menu.getItemFor(window)?.addClassName("item-selected")
        menu.getItemFor(currentWindow)?.removeClassName("item-selected")
        currentWindow?.isVisible = false
        currentWindow = window

        window!!.isVisible = true

        caption.setCaption(windowToCaptionMap[window])
        findMainWindow()!!.application.setPageTitle(caption.getCaption())
      }
      return window
    }

    return null
  }

  /**
   * Shows the next window in the list.
   */
  fun showNextWindow(): Component? = showWindow(windowToCaptionMap.nextWindowOf(currentWindow))

  /**
   * Shows the previous window in the list.
   */
  fun showPreviousWindow(): Component? = showWindow(windowToCaptionMap.previousWindowOf(currentWindow))

  val isEmpty: Boolean get() = windowToCaptionMap.isEmpty()

  inner class WindowMap(private val windowMap: MutableMap<Component, String> = mutableMapOf()): MutableMap<Component, String> by windowMap {
    private val windowList = mutableListOf<Component>()

    override fun put(key: Component, value: String): String? {
      windowList.add(key)
      return windowMap.put(key, value)
    }

    override fun putAll(from: Map<out Component, String>) {
      windowList.addAll(from.keys)
      windowMap.putAll(from)
    }

    override fun putIfAbsent(key: Component, value: String): String? {
      return super.putIfAbsent(key, value).also {
        if(it != null) {
          windowList.add(key)
        }
      }
    }

    override fun remove(key: Component): String? {
      return windowMap.remove(key).also {
        if(it != null) {
          windowList.remove(key)
        }
      }
    }

    override fun remove(key: Component, value: String): Boolean {
      return windowMap.remove(key, value).also {
        if (it) {
          windowList.remove(key)
        }
      }
    }

    fun nextWindowOf(window: Component?): Component? {
      var componentIndex  = windowList.indexOf(window)

      componentIndex += 1
      if (componentIndex >= windowList.size) {
        componentIndex = 0
      }

      return windowList.getOrNull(componentIndex)
    }

    fun previousWindowOf(window: Component?): Component? {
      var componentIndex  = windowList.indexOf(window)

      componentIndex -= 1
      if (componentIndex < 0) {
        componentIndex = windowToCaptionMap.count() - 1
      }

      return windowList.getOrNull(componentIndex)
    }
  }
}
