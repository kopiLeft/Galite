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
package org.kopi.galite.visual.ui.vaadin.block

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.data.provider.ListDataProvider

import org.kopi.galite.visual.ui.vaadin.base.Utils
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.ui.vaadin.form.DField
import org.kopi.galite.visual.ui.vaadin.form.DGridBlock
import org.kopi.galite.visual.ui.vaadin.label.Label

/**
 * An absolute panel component.
 *
 * @param align The alignment info
 */
class AlignPanel(var align: BlockAlignment?, private val targetBlockName: String) : Div() {

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
    } else if (ori is SingleComponentBlockLayout) {
      if(ori.block is DGridBlock) {

        // block contains a VAADIN grid inside
        // -> we align according to grid column position
        val gridBlock = ori.block.grid
        val grid = Grid<Array<Component?>>()
        val rowsSize = aligns!!.maxOf { it.y } + 1
        val columnsSize = gridBlock.columns.size
        val alignedGridComponents = Array(rowsSize) {
          arrayOfNulls<Component>(columnsSize)
        }

        val gridBlockWidth = gridBlock.columns.joinToString(" + ") { it.width }

        grid.width = "calc($gridBlockWidth)"
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS)
        grid.setSelectionMode(Grid.SelectionMode.NONE)
        gridBlock.columns.forEachIndexed { index, column ->
          grid.addComponentColumn {
            (it[index] ?: Div()).also { component ->
              if (component is DField && component.wrappedField is TextField) {
                (component.wrappedField as TextField).inputField.content.getElement().style["width"] ="100%"
              }
            }
          }.setWidth(column.width)
        }
        (grid.dataProvider as ListDataProvider).addFilter { it != null }

        for (i in aligns!!.indices) {
          val align = aligns!![i]

          if (align.x != -1) {
            val overlap = getOverlappingComponent(i, align.x, align.y)
            if (overlap != null) {
              val component = components!![i]
              if (component is Label) {
                val field = getFieldOf(component)
                field?.let {
                  addTooltipToField(component, it)
                }
              } else if (component is DField && component.label == overlap) {
                component.label?.let {
                  addTooltipToField(it, component)
                }
                alignedGridComponents[align.y][align.x] = component
              } else {
                Exception("Block $targetBlockName : Overlapping components at position ${align.x}, ${align.y}").printStackTrace()
              }
            } else {
              alignedGridComponents[align.y][align.x] = components!![i]
            }
          }
        }

        grid.setItems(alignedGridComponents.toList())
        add(grid)
      } else {
        TODO("Not supported yet")
      }
    } else {
      val oriLayout = ori as AbstractBlockLayout

      for (i in aligns!!.indices) {
        val align = aligns!![i]
        if (align.x != -1) {
          val cell = oriLayout.getCellAtOrNull(oriLayout.rowCount - 1, align.x)

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

  private fun setComponentPosition(component: Component, left: Double, top: Int) {
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
  private fun getOverlappingComponent(end: Int, x: Int, y: Int): Component? {
    for (i in 0 until end) {
      if (aligns!![i].x == x && aligns!![i].y == y) {
        return components!![i]
      }
    }
    return null
  }

  /**
   * Returns the field that has the label [label].
   *
   */
  private fun getFieldOf(label: Label): Component? {
    for (component in components!!) {
      if(component is DField && component.label == label) {
        return component
      }
    }
    return null
  }

  private fun addTooltipToField(label: Label, field: Component) {
    field.element.setAttribute("title", label.text)
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    layout()
    setPanelSize()
  }

  /**
   * Calculates the size of the content panel.
   */
  protected fun setPanelSize() {
    // TODO
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
