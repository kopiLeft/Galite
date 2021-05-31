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

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VScrollablePanel
import org.kopi.galite.ui.vaadin.block.Block
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.AttachEvent

/**
 * A form page, can be either or vertical or horizontal page.
 */
class Page<T>(private var content: T) : Div()  where T: Component, T: FlexComponent {

  private var scrollPanel: VScrollablePanel?
  private var last: Component? = null
  private var width = 0.0

  init {
    this.content.className = Styles.FORM_PAGE_CONTENT
    scrollPanel = VScrollablePanel(this.content)
    add(scrollPanel)
    className = Styles.FORM_PAGE

    access {
      UI.getCurrent().page.addBrowserWindowResizeListener { event ->
        if (event.width < width) {
          this.style["width"] = (event.width - 28).toString() + "px"
          style["overflow"] = "auto"
        } else {
          this.style["width"] = "auto"
        }

      }
    }
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    element.executeJs("return $0.clientWidth", this.element).then { width -> this.width = width.asNumber() }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds a child to this page.
   * @param child The child component.
   * @param hAlign The horizontal alignment.
   */
  fun add(child: Block, hAlign: FlexComponent.JustifyContentMode) {
    content.add(child)
    content.justifyContentMode = hAlign
    last = child
  }

  /**
   * Adds a follow widget.
   * @param child The widget to be added.
   * @param align The alignment.
   */
  fun addFollow(child: Block, align: FlexComponent.JustifyContentMode) {
    if (last != null) {
      val temp = VerticalLayout()
      temp.className = "follow-blocks-container"
      content.remove(last)
      temp.add(last)
      temp.add(child)
      content.add(temp)
    } else {
      add(child, align)
    }
    last = null
  }

  /**
   * Sets the block caption.
   * @param block The block widget.
   */
  fun setCaption(block: Block) {
    setCaption(content, block)
  }

  /**
   * Sets the block caption.
   * @param content The caption container.
   * @param block The block widget.
   */
  protected fun <T> setCaption(content: T, block: Block) where T: Component, T: FlexComponent {
    val caption = block.caption
    if (caption != null) {
      if (content is HorizontalLayout) {
        // wrap it in a vertical content before
        val temp = VerticalLayout()
        val index: Int = content.indexOf(block)
        temp.className = "k-centered-page-wrapper"
        temp.add(caption)
        temp.add(block)
        (content as HorizontalLayout).addComponentAtIndex(index, temp)
      } else if (content is VerticalLayout) {
        val index: Int = content.indexOf(block)
        if (index >= 0) {
          (content as VerticalLayout).addComponentAtIndex(index, caption)
        } else {
          // it is a follow block
          for (i in 0 until content.componentCount) {
            if (content.getComponentAt(i) != null
                    && "follow-blocks-container" == (content.getComponentAt(i) as HasStyle).className) {
              setCaption(content.getComponentAt(i) as T, block)
            }
          }
        }
      } else {
        content.add(caption) // not really suitable.
      }
    }
  }
}
