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
import com.vaadin.flow.component.Component
import org.kopi.galite.ui.vaadin.form.DField

/**
 * The simple block layout component.
 *
 * @param col The column number.
 * @param line The row number.
 */
class SimpleBlockLayout(col: Int, line: Int) : AbstractBlockLayout(col, line) {
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

  override fun onAttach(attachEvent: AttachEvent?) {
    layout()
  }

  fun initSize() {
    initSize(col, line)
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
    val constraints = ComponentConstraint(x,
                                          y,
                                          width,
                                          height,
                                          alignRight,
                                          useAll)
    if (align == null) {
      if (width < 0) {
        follows!!.add(component!!)
        followsAligns!!.add(constraints)
      } else {
        if (component is DField) {
          val formItem = object : FormItem(component) {
            init {
              addToLabel(component.label)
            }
          }
          aligns!![x][y] = constraints
          components!![x][y] = formItem
        }
      }
    } else {
      if (component == null) {
        return
      }

      // add to the original block as extra components.
      val newConstraint = ComponentConstraint(align!!.getTargetPos(x),
                                              y,
                                              width,
                                              height,
                                              alignRight,
                                              useAll)
      // adds an extra component to the block.
      addAlignedComponent(component, newConstraint)
    }
  }

  override fun layout() {
    // Responsive steps
    /*setResponsiveSteps(
            *Array(col/2) {
              ResponsiveStep("" + (25 + 7 * it) + "em", it + 1, ResponsiveStep.LabelsPosition.TOP)
            }
    )*/

    if (align != null) {
      // aligned blocks will be handled differently
      return
    } else {
      val manager = LayoutManager(this)
      for (y in components!![0].indices) {
        for (x in components!!.indices) {
          if (components!![x][y] != null && aligns!![x][y] != null) {
            // TODO
            /*manager.setComponent(components!![x][y],
                                 aligns!![x][y]!!,
                                 aligns!![x][y]!!.width.coerceAtMost(getAllocatedWidth(x, y)),
                                 aligns!![x][y]!!.height.coerceAtMost(getAllocatedHeight(x, y)))
            setAlignment(aligns!![x][y]!!.y, aligns!![x][y]!!.x, aligns!![x][y]!!.alignRight)*/
            add(components!![x][y])
          }
        }
      }
      manager.layout()
      // add follows
      for (i in follows!!.indices) {
        val align = followsAligns!![i]
        val comp: Component = follows!![i]
        // addInfoComponentdAt(comp, align.x, align.y) TODO
      }
    }
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
   * @param ori The original block to align with.
   * @param targets The alignment targets.
   * @param isChart Is the original block chart ?
   */
  fun setBlockAlignment(ori: Component, targets: IntArray, isChart: Boolean) {
    align = BlockAlignment()

    align!!.isChart = isChart
    align!!.targets = targets
    align!!.ori = ori

    // alignPane = VAlignPanel(align) TODO
  }
}
