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
package org.kopi.galite.ui.vaadin.block

import org.kopi.galite.ui.vaadin.base.Utils

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div

/**
 * An absolute panel component.
 *
 * @param align The alignment info
 */
class AlignPanel(var align: BlockAlignment?) : Div() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var components: MutableList<Component>? = mutableListOf()
  private var aligns: MutableList<ComponentConstraint>? = mutableListOf()
  private val ui = UI.getCurrent()

  init {
    className = "k-align-pane"
    element.style["overflow"] = "visible"
    element.style["position"] = "relative"
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds a constrained component.
   * @param w The component.
   * @param align The component constraint.
   */
  fun addComponent(w: Component, align: ComponentConstraint) {
    add(w)
    components!!.add(w)
    aligns!!.add(align)
  }

  /**
   * Layouts the block content.
   */
  protected fun layout() {
    if (align == null) {
      return
    }
    val ori = align!!.block.layout
    if (ori == null) {
      return
    } else if (ori is SingleComponentBlockLayout) { // FIXME
      // block contains a VAADIN grid inside
      // -> we align according to grid column position
      val gridBlock = ori.block

      Thread {
      for (i in aligns!!.indices) {
        val align = aligns!![i]
        if (align.x != -1) {
          val column = gridBlock.headers[align.x]

          if (column != null) {
            var offsetWidth = 0
            val overlap: Component? = getOverlappingWidget(i, align.x, align.y)
            if (overlap != null) {
              offsetWidth = (overlap as HasSize).width.toInt() + 10 // horizontal gap
            }
              setComponentPosition(
                components!![i],
                Utils.getOffsetLeft(column.element, ui),
                align.y * 21) // text fields height is 15px
          }
        }
      }
      }.start()
    } else {
      for (i in aligns!!.indices) {
        val align = aligns!![i]
        if (align.x != -1) {
          /*try {
            val cell: Element = ori.getCellFormatter().getElement(ori.rowCount - 1, align.x)
            if (cell != null) {
              var offsetWidth = 0
              val overlap: Component? = getOverlappingWidget(i, align.x, align.y)
              if (overlap != null) {
                offsetWidth = overlap.element.getClientWidth() + 10 // horizontal gap
              }
              setWidgetPosition(
                getWidget(i),
                cell.getOffsetLeft() + offsetWidth - if (align.x == 0) 0 else 0.coerceAtLeast(
                  getWidget(i).getElement()
                    .getClientWidth() - cell.getClientWidth()
                ),
                align.y * 21
              ) // text fields height is 15px
            }
          } catch (e: IndexOutOfBoundsException) {
            getWidget(i).setVisible(false)
          }*/
        }
      }
    }
  }

  fun setComponentPosition(component: Component, left: Double, top: Int) {
    ui.access {
      component.element.style["position"] = "absolute"
      component.element.style["left"] = left.toString() + "px"
      component.element.style["top"] = top.toString() + "px"
    }
  }

  /**
   * Returns The overlapping component if it exists.
   * @param end: The end searching index.
   * @param x The column number.
   * @param y The row number.
   * @return The overlapping component. `null` otherwise.
   */
  protected fun getOverlappingWidget(end: Int, x: Int, y: Int): Component? {
    for (i in 0 until end) {
      if (aligns!![i].x == x && aligns!![i].y == y) {
        return components!![i]
      }
    }
    return null
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    layout()
    setPanelSize()
  }

  /**
   * Calculates the size of the content panel.
   */
  protected fun setPanelSize() {
    if (aligns == null) {
      return
    }
    var width = 0
    var height = 0
    var i = 0
    for (child in children) {
      val align = aligns!![i]
      if (align.x != -1) {
        //width = width.coerceAtLeast(child.element.getAbsoluteRight())
        //height = height.coerceAtLeast(child.element.getOffsetTop() + child.element.getClientHeight())
      }
      i++
    }
    setWidth("${width}px")
    setHeight("${height}px")
  }

  /**
   * Releases this align block.
   */
  fun release() {
    components = null
    aligns = null
    align = null
  }
}
