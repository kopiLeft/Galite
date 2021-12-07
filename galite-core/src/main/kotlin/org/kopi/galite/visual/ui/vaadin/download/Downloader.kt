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

import com.vaadin.flow.component.HasComponents

/**
 * A download component that allows a user to download a file produced in the server.
 *
 * @param file the file to download.
 * @param name the file name.
 * @param parent component that will trigger this download.
 */
class Downloader(file: File, name: String, val parent: HasComponents): DownloadAnchor(file, name) {

  init {
    style["visibility"] = "hidden"
  }

  /**
   * Downloads the file attached to this downloader.
   */
  fun download() {
    parent.add(this)

    element.callJsFunction("click")
  }
}
