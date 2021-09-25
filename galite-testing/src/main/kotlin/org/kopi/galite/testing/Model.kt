/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.testing

import org.kopi.galite.visual.common.Window
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.visual.DWindow
import org.kopi.galite.visual.visual.VWindow

import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get

/**
 * Returns the model from the created Vaadin component.
 *
 * @receiver the window.
 */
fun Window.getModelFromComponent(): VWindow {
  val mainWindow = _get<MainWindow>()
  val windows = mainWindow
    ._find<DWindow>()

  val window = windows.single {
    it.getModel()!! eq this.model
  }

  return window.getModel()!!
}

infix fun VWindow.eq(window: VWindow): Boolean {
  return this.getTitle() == window.getTitle()
          && this.source == window.source
}
