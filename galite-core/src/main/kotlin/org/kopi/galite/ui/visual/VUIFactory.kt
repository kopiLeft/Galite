/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: VUIFactory.java 35325 2018-08-29 11:27:17Z iayadi $
 */
package org.kopi.galite.ui.visual

import org.kopi.galite.base.UComponent
import org.kopi.galite.report.VReport
import org.kopi.galite.ui.report.DReport
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.VModel

/**
 * The `VUIFactory` is a vaadin implementation of the [UIFactory].
 */
class VUIFactory : UIFactory() {
  /*
   * The only way to do here is to use the compiler javac with -sourcepath option to handle non yet
   * compiler UForm and UListDialog classes
   */
  override fun createView(model: VModel): UComponent {

    val view: UComponent = if (model is VReport) {
      createReport(model as VReport)
    } else {
      throw IllegalArgumentException("NO UI IMPLEMENTATION FOR " + model.javaClass)
    }
    //model.setDisplay(view)
    return view
  }
  //-----------------------------------------------------------
  // UI COMPONENTS CREATION
  //-----------------------------------------------------------
  /**
   * Creates the [DReport] from a given model.
   * @param model The report model
   * @return The  [DReport] view.
   */
  protected fun createReport(model: VReport): DReport {
    return DReport(model)
  }
}
