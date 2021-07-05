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

import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.ui.vaadin.base.Utils

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.data.provider.ListDataProvider

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
  private val ui = UI.getCurrent() // TODO

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
      val gridBlock = ori.block.grid
      val  grid = Grid<Array<Component?>>()
      val rowsSize = aligns!!.maxOf { it.y } + 1
      val columnsSize = gridBlock.columns.size
      val alignedGridComponents = Array(rowsSize) {
        arrayOfNulls<Component>(columnsSize)
      }

      grid.width = gridBlock.width
      grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
      grid.setSelectionMode(Grid.SelectionMode.NONE)
      gridBlock.columns.forEachIndexed { index, column ->
        grid.addComponentColumn { it[index] ?: Div() }
          .setWidth(column.width)
      }
      (grid.dataProvider as ListDataProvider).addFilter { it != null }

      for (i in aligns!!.indices) {
        val align = aligns!![i]

        if (align.x != -1) {
          alignedGridComponents[align.y][align.x] = components!![i]
        }
      }

      grid.setItems(alignedGridComponents.toList())
      add(grid)

    } else {
      val ori = ori as AbstractBlockLayout

      for (i in aligns!!.indices) {
        val align = aligns!![i]
        if (align.x != -1) {
          val cell = ori.getCellAtOrNull(ori.rowCount - 1, align.x)

          if (cell != null) {
            add(components!![i])
            Thread {
              setComponentPosition(
                components!![i],
                Utils.getOffsetLeft(cell, ui),
                align.y * 21) // text fields height is 15px
            }.start()
          }
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
  protected fun getOverlappingComponent(end: Int, x: Int, y: Int): Component? {
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
    //setWidth("${width}px") TODO
    //setHeight("${height}px")
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
