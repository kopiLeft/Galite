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
package org.kopi.galite.visual.ui.vaadin.visual

import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.chart.VChart
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.VListDialog
import org.kopi.galite.visual.preview.VPreviewWindow
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.ui.vaadin.chart.DChart
import org.kopi.galite.visual.ui.vaadin.form.DForm
import org.kopi.galite.visual.ui.vaadin.form.DListDialog
import org.kopi.galite.visual.ui.vaadin.preview.DPreviewWindow
import org.kopi.galite.visual.ui.vaadin.report.DReport
import org.kopi.galite.visual.visual.UIFactory
import org.kopi.galite.visual.visual.VHelpViewer
import org.kopi.galite.visual.visual.VItemTree
import org.kopi.galite.visual.visual.VMenuTree
import org.kopi.galite.visual.visual.VModel

/**
 * The `VUIFactory` is a vaadin implementation of the [UIFactory].
 */
class VUIFactory : UIFactory() {
  /*
   * The only way to do here is to use the compiler javac with -sourcepath option to handle non yet
   * compiler UForm and UListDialog classes
   */
  override fun createView(model: VModel): UComponent {
    val view = when (model) {
      is VMenuTree -> {
        createMenuTree(model)
      }
      is VItemTree -> {
        createItemTree(model)
      }
      is VForm -> {
        createForm(model)
      }
      is VPreviewWindow -> {
        createPreviewWindow(model)
      }
      is VReport -> {
        createReport(model)
      }
      is VChart -> {
        createChart(model)
      }
      is VHelpViewer -> {
        createHelpViewer(model)
      }
      is VListDialog -> {
        createListDialog(model)
      }
      else -> {
        throw IllegalArgumentException("NO UI IMPLEMENTATION FOR " + model.javaClass)
      }
    }
    model.setDisplay(view)
    return view
  }
  //-----------------------------------------------------------
  // UI COMPONENTS CREATION
  //-----------------------------------------------------------
  /**
   * Creates the [DMenuTree] from a given model.
   * @param model The menu tree model
   * @return The  [DMenuTree] view.
   */
  internal fun createMenuTree(model: VMenuTree): DMenuTree {
    return DMenuTree(model)
  }

  /**
   * Creates the [DItemTree] from a given model.
   * @param model The item tree model
   * @return The  [DItemTree] view.
   */
  internal fun createItemTree(model: VItemTree): DItemTree {
    return DItemTree(model)
  }

  /**
   * Creates the [DForm] from a given model.
   * @param model The form model
   * @return The  [DForm] view.
   */
  internal fun createForm(model: VForm): DForm {
    return DForm(model)
  }

  /**
   * Creates the [DPreviewWindow] from a given model.
   * @param model The preview model
   * @return The  [DPreviewWindow] view.
   */
  internal fun createPreviewWindow(model: VPreviewWindow): DPreviewWindow {
    return DPreviewWindow(model)
  }

  /**
   * Creates the [DReport] from a given model.
   * @param model The report model
   * @return The  [DReport] view.
   */
  internal fun createReport(model: VReport): DReport {
    return DReport(model)
  }

  /**
   * Creates the [DReport] from a given model.
   * @param model The report model
   * @return The  [DReport] view.
   */
  internal fun createChart(model: VChart): DChart {
    return DChart(model)
  }

  /**
   * Creates the [DHelpViewer] from a given model.
   * @param model The help viewer model
   * @return The  [DHelpViewer] view.
   */
  internal fun createHelpViewer(model: VHelpViewer): DHelpViewer {
    return DHelpViewer(model)
  }

  /**
   * Creates the [DListDialog] from a given model.
   * @param model The list dialog model
   * @return The  [DListDialog] view.
   */
  internal fun createListDialog(model: VListDialog): DListDialog {
    return DListDialog(model)
  }
}
