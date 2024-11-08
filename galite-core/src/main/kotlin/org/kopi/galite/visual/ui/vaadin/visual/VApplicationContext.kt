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

package org.kopi.galite.visual.ui.vaadin.visual

import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.visual.Application
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.PreviewRunner

class VApplicationContext : ApplicationContext() {

  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------
  private var previewRunner: VPreviewRunner? = null

  override fun getApplication(): Application {
    val ui = BackgroundThreadHandler.locateUI()

    return if (ui == null) {
      VApplication.instance
    } else {
      ui.children
        .filter { component -> component is VApplication }
        .findFirst()
        .orElse(null) as? VApplication ?: VApplication.instance
    }
  }

  /**
   * Returns the current PreviewRunner.
   * @return The current PreviewRunner.
   */
  override fun getPreviewRunner(): PreviewRunner {
    return previewRunner ?: VPreviewRunner().also { previewRunner = it }
  }

  /**
   * Returns `true` if we are in a web application context.
   * @return `true` if we are in a web application context.
   */
  override fun isWebApplicationContext(): Boolean {
    return true
  }
}
