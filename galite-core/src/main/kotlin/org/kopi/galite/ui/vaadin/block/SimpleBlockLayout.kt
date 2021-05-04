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

import org.kopi.galite.ui.vaadin.field.ActorField
import org.kopi.galite.ui.vaadin.form.DBlock
import org.kopi.galite.ui.vaadin.form.DField
import org.kopi.galite.ui.vaadin.form.DGridMultiBlock
import org.kopi.galite.form.VField

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.formlayout.FormLayout.FormItem
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * The simple block layout component.
 *
 * @param col The column number.
 * @param line The row number.
 */
open class SimpleBlockLayout(col: Int, line: Int) : AbstractBlockLayout(col, line) {
  var align: BlockAlignment? = null
  private var follows: MutableList<Component>? = null
  private var followsAligns: MutableList<ComponentConstraint>? = null

  init {
    className = "simple"
    initSize()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override fun initSize() {
    initSize(col, line)
    // setAlignment(align) TODO
  }

  override fun initSize(columns: Int, rows: Int) {
    super.initSize(columns, rows)
    follows = ArrayList()
    followsAligns = ArrayList()
  }

  override fun addComponent(
          component: Component?, x: Int, y: Int, width: Int, height: Int, alignRight: Boolean,
          useAll: Boolean,
  ) {
    val constraints = ComponentConstraint(x, y, width, height, alignRight, useAll)

    if (parent.get() is DGridMultiBlock) { // TODO
      getBlock().isLayoutBelongsToGridDetail = true
      if (component != null) {
        if (component is DField) {
          val columnView: ColumnView = if (constraints.width < 0 || component.wrappedField is ActorField) {
            ColumnView(getBlock()).also { columnView ->
              columnView.label = null
              columnView.addField(component)
              if (blockInDetailMode()) {
                columnView.detailLabel = null
                columnView.setDetailDisplay(component)
              }
            }
          } else {
            ColumnView(getBlock()).also { columnView ->
              // Label
              columnView.label = component.label
              if (blockInDetailMode()) {
                columnView.detailLabel = component.label
              }

              // Field
              columnView.addField(component)
              if (blockInDetailMode()) {
                columnView.setDetailDisplay(component)
              }
            }
          }

          getBlock().addField(columnView)
        }
      }
    } else if (component != null) {
      if (component is FormItem) {
        components!![x][y] = component
      } else if (component is DField) {
        if(constraints.width < 0 ) {
          val formItem = object : FormItem(component) {}

          add(formItem, constraints)
        } else {
          val formItem = object : FormItem(component) {
            init {
              addToLabel(component.label)
            }
          }
          add(formItem, constraints)
        }

        // a follow field has no label
        // an actor field has no label too.
        // we treat this cases separately
        val columnView: ColumnView = if (constraints.width < 0 || component.wrappedField is ActorField) {
          ColumnView(getBlock()).also { columnView ->
            columnView.label = null
            columnView.addField(component)
            if (blockInDetailMode()) {
              columnView.detailLabel = null
              columnView.setDetailDisplay(component)
            }
          }
        } else {
          ColumnView(getBlock()).also { columnView ->
            // Label
            columnView.label = component.label
            if (blockInDetailMode()) {
              columnView.detailLabel = component.label
            }

            // Field
            columnView.addField(component)
            if (blockInDetailMode()) {
              columnView.setDetailDisplay(component)
            }
          }
        }

        getBlock().addField(columnView)
      }
    }

    if(component is Grid<*>) { // TODO
      add(component, constraints)
    }
  }

  override fun add(component: Component?, constraints: ComponentConstraint) {
    if (align == null) {
      if (constraints.width < 0) {
        follows!!.add(component!!)
        followsAligns!!.add(constraints)
      } else {
        aligns!![constraints.x][constraints.y] = constraints
        components!![constraints.x][constraints.y] = component
      }
    } else {
      if (component == null) {
        return
      }

      // add to the original block as extra components.
      val newConstraint = ComponentConstraint(align!!.getTargetPos(constraints.x),
                                              constraints.y,
                                              constraints.width,
                                              constraints.height,
                                              constraints.alignRight,
                                              constraints.useAll)
      // adds an extra component to the block.
      addAlignedComponent(component, newConstraint)
    }
  }

  override fun layout() {
    if (align != null) {
      // aligned blocks will be handled differently
      return
    } else {
      val manager = LayoutManager(this)

      for (y in components!![0].indices) {
        for (x in components!!.indices) {
          if (components!![x][y] != null && aligns!![x][y] != null) {
            // TODO
            manager.setComponent(components!![x][y]!!,
                                 aligns!![x][y]!!,
                                 aligns!![x][y]!!.width.coerceAtMost(getAllocatedWidth(x, y)),
                                 aligns!![x][y]!!.height.coerceAtMost(getAllocatedHeight(x, y)))
            setAlignment(aligns!![x][y]!!.y, aligns!![x][y]!!.x, aligns!![x][y]!!.alignRight)
          }
        }
      }
      manager.layout()
      // add follows
      for (i in follows!!.indices) {
        val align = followsAligns!![i]
        val comp: Component = follows!![i]
        addInfoComponentAt(comp, align.x, align.y)
      }
    }
  }

  /**
   * Sets an info component in the given cell.
   * @param info The info component.
   * @param x The cell column.
   * @param y The cell row.
   */
  protected fun addInfoComponentAt(info: Component?, x: Int, y: Int) {
    for(field in components!![x][y]!!.children) {
      if(field is DField) {
        val content = HorizontalLayout(field, info)

        content.className = "info-content"
        val formItem = object : FormItem(content) {
          init {
            addToLabel(field.label)
          }
        }
        setComponent(formItem,
                     aligns!![x][y]!!.x,
                     aligns!![x][y]!!.y,
                     aligns!![x][y]!!.width.coerceAtMost(getAllocatedWidth(x, y)),
                     getComponentHeight(components!![x][y]!!)
                       .coerceAtLeast(getComponentHeight(info!!))
                       .coerceAtMost(getAllocatedHeight(x, y))
        )
        break
      }
    }
  }

  /**
   * Returns the component height.
   * @return The component height.
   */
  protected fun getComponentHeight(comp: Component) = if (comp is VField) comp.height else 1

  /**
   * Returns the allocated height for the given column and row.
   * @return The allocated height for the given column and row.
   */
  protected fun getAllocatedHeight(col: Int, row: Int): Int {
    var allocatedHeight = 1
    for (y in row + 1 until components!![col].size) {
      if (components!![col][y] != null) {
        break
      }
      allocatedHeight++
    }
    return allocatedHeight
  }

  /**
   * Returns the allocated width for the given column and row
   * @return The allocated width for the given column and row
   */
  private fun getAllocatedWidth(col: Int, row: Int): Int {
    var allocatedWidth = 1
    for (x in col + 1 until components!!.size) {
      if (components!![x][row] != null) {
        break
      }
      allocatedWidth++
    }
    return allocatedWidth
  }

  fun getBlock(): DBlock = parent.get() as DBlock

  /**
   * Returns if the block is in detail mode
   *
   * @return True if the block is in detail mode.
   */
  fun blockInDetailMode(): Boolean {
    return getBlock().noChart
  }

  override fun clear() {
    TODO("Not yet implemented")
  }

  override fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int) {
    TODO("Not yet implemented")
  }


  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the alignment information for this simple layout.
   * @param original The original block to align with.
   * @param targets The alignment targets.
   * @param isChart Is the original block chart ?
   */
  open fun setBlockAlignment(original: Component, targets: IntArray, isChart: Boolean) {
    align = BlockAlignment()

    align!!.isChart = isChart
    align!!.targets = targets
    align!!.ori = original

    // alignPane = VAlignPanel(align) TODO
  }
}
