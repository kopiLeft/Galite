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
package org.kopi.galite.ui.vaadin.visual

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

import org.kopi.galite.ui.vaadin.base.LocalizedProperties

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource

/**
 * A Dialog that allows a user to download a file produced in the server.
 *
 * @param file the file to download.
 * @param name the file name
 */
class DownloaderDialog(file: File, name: String, locale: String): Dialog() {
  private val downloadButton =
    Button(LocalizedProperties.getString(locale, "downloadLabel"), Icon(VaadinIcon.DOWNLOAD_ALT))
  private val closeButton = Button(LocalizedProperties.getString(locale, "CLOSE"), Icon(VaadinIcon.CLOSE_CIRCLE))

  init {
    val href = StreamResource(name, InputStreamFactory {
      createFileInputStream(file.absolutePath)
    })
    val download = Anchor(href, "")
    val buttons = Div()
    val title = Div()

    title.className ="download-file-title"
    buttons.className = "download-file-buttons"
    download.element.setAttribute("download", true)
    downloadButton.isDisableOnClick = true

    title.text = LocalizedProperties.getString(locale, "downloadText") + ": $name"
    closeButton.addClickListener {
      close()
    }

    download.add(downloadButton)
    buttons.add(download, closeButton)
    add(VerticalLayout(title, buttons))
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
