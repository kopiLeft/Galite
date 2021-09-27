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
package org.kopi.galite.visual.ui.vaadin.preview

import java.io.File
import java.io.FileInputStream
import java.io.IOException

import org.kopi.galite.visual.preview.VPreviewWindow
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.Unit
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource

/**
 * The `DPreviewWindow` is the ui implementation of the [VPreviewWindow].
 *
 * @param model The preview window model.
 */
open class DPreviewWindow(val model: VPreviewWindow) : DWindow(model) {

  private var embedded: EmbeddedDocument = EmbeddedDocument(EmbeddedDocument.Type.BROWSER)

  init {
    setSizeUndefined()
    embedded.setSizeFull()
    UI.getCurrent().page.retrieveExtendedClientDetails {
      embedded.setHeight(it.windowInnerHeight - 100f, Unit.PIXELS)
    }
    setContent(embedded)
  }

  /**
   * Customized initializations.
   */
  open fun init() {
    // to be overridden in children classes.
  }

  override fun run() {
    try {
      model.setActorEnabled(VPreviewWindow.CMD_QUIT, true) // force to enable the quit actor
      access(currentUI) {
        setEmbeddedContent(model.printJob!!.dataFile)
      }
    } catch (e: IOException) {
      e.printStackTrace(System.err)
    }
  }

  /**
   * Sets the embedded content.
   * @param file The file content.
   */
  private fun setEmbeddedContent(file: File) {
    val resource = StreamResource(file.name, InputStreamFactory {
      try {
        FileInputStream(file.absolutePath)
      } catch (e: IOException) {
        throw RuntimeException(e)
      } catch (e: InterruptedException) {
        throw RuntimeException(e)
      }
    })
    embedded.resource = resource
    embedded.mimeType = "application/pdf"
  }
}
