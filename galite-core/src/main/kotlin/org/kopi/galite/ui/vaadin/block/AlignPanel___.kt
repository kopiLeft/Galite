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

import com.vaadin.flow.component.AttachEvent
import java.lang.IndexOutOfBoundsException

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div

/**
 * An absolute panel component.
 *
 * @param align The alignment info
 */
/*
class AlignPanel___(var align: BlockAlignment?) : Div() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var components: MutableList<Component>? = mutableListOf()
  private var aligns: MutableList<ComponentConstraint>? = mutableListOf()

  init {
    className = "k-align-pane"
    element.style["overflow"] = "visible"
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  */
/**
   * Adds a constrained component.
   * @param w The component.
   * @param align The component constraint.
   *//*

  fun addComponent(w: Component, align: ComponentConstraint) {
    add(w)
    components!!.add(w)
    aligns!!.add(align)
  }

  */
/**
   * Layouts the block content.
   *//*

  protected fun layout() {
    if (align == null) {
      return
    }
    val ori = align!!.block.layout as? AbstractBlockLayout
    if (ori == null || ori.getCellFormatter() == null) {
      return
    } else if (ori.getWidget(0, 0) is Grid<*>) {
      // block contains a VAADIN grid inside
      // -> we align according to grid column position
      val grid: Grid<*>
      grid = ori.getWidget(0, 0) as Grid<*>
      for (i in aligns.indices) {
        var body: Element
        val align = aligns[i]
        body = grid.getEscalator().getBody().getElement() as Element
        if (align.x != -1 && body.getLastChild() != null) {
          val cell: Element = body.getLastChild().getChild(align.x) as Element
          if (cell != null) {
            var offsetWidth = 0
            val overlap: Component? = getOverlappingWidget(i, align.x, align.y)
            if (overlap != null) {
              offsetWidth = overlap.getElement().getClientWidth() + 10 // horizontal gap
            }
            setWidgetPosition(
              getWidget(i),
              cell.getOffsetLeft() + offsetWidth - if (align.x == 0) 0 else Math.max(0,
                                                                                     getWidget(i).getElement()
                                                                                       .getClientWidth() - cell.getClientWidth()
              ),
              align.y * 21
            ) // text fields height is 15px
          }
        }
      }
    } else {
      for (i in aligns!!.indices) {
        val align = aligns!![i]
        if (align.x != -1) {
          try {
            val cell: Element = ori.getCellFormatter().getElement(ori.getRowCount() - 1, align.x)
            if (cell != null) {
              var offsetWidth = 0
              val overlap: Component? = getOverlappingWidget(i, align.x, align.y)
              if (overlap != null) {
                offsetWidth = overlap.getElement().getClientWidth() + 10 // horizontal gap
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
          }
        }
      }
    }
  }

  */
/**
   * Returns The overlapping component if it exists.
   * @param end: The end searching index.
   * @param x The column number.
   * @param y The row number.
   * @return The overlapping component. `null` otherwise.
   *//*

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

  */
/**
   * Calculates the size of the content panel.
   *//*

  protected fun setPanelSize() {
    if (aligns == null) {
      return
    }
    var width = 0
    var height = 0
    for (i in 0 until getWidgetCount()) {
      var child: Component
      child = getWidget(i)
      val align = aligns[i]
      if (align.x != -1) {
        width = width.coerceAtLeast(child.getElement().getAbsoluteRight())
        height = height.coerceAtLeast(child.getElement().getOffsetTop() + child.getElement().getClientHeight())
      }
    }
    setPixelSize(width, height)
  }

  */
/**
   * Releases this align block.
   *//*

  fun release() {
    components = null
    aligns = null
    align = null
  }
}
*/
