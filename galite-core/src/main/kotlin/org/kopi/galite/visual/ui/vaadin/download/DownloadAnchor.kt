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
package org.kopi.galite.visual.ui.vaadin.download

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource

/**
 * An anchor component that allows to download a file produced in the server.
 *
 * @param file the file to download.
 * @param name the file name
 */
open class DownloadAnchor(file: File, name: String): Anchor() {

  init {
    val href = StreamResource(name, InputStreamFactory {
      createFileInputStream(file.absolutePath)
    })

    element.setAttribute("download", true)

    setHref(href)
  }

  private fun createFileInputStream(path: String): InputStream? {
    return try {
      Files.newInputStream(Paths.get(path))
    } catch (e: IOException) {
      throw RuntimeException(e)
    } catch (e: InterruptedException) {
      throw RuntimeException(e)
    }
  }
}
