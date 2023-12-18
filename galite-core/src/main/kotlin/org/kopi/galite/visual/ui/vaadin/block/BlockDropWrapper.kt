/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.ui.vaadin.block

import java.io.ByteArrayOutputStream

import org.kopi.galite.visual.ui.vaadin.form.DBlockDropHandler

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer

import elemental.json.Json

@CssImport(value = "./styles/galite/dropwrapper.css", themeFor = "vaadin-upload")
class BlockDropWrapper(val layout: Component, dropHandler: DBlockDropHandler) : VerticalLayout() {
  private val buffer = if (dropHandler.isChartBlockContext) MultiFileMemoryBuffer() else MemoryBuffer()
  private val upload = Upload(buffer)

  init {
    val dropArea = VerticalLayout()
    upload.setAcceptedFileTypes(*dropHandler.acceptedFlavors.map { ".${it.lowercase()}" }.toTypedArray())
    upload.element.themeList.add("drop-wrapper-upload")
    dropArea.add(layout)
    upload.element.appendChild(dropArea.element)
    add(upload)

    upload.addStartedListener {
      dropHandler.onStart()
      dropHandler.streamHandler.streamingStarted(it.contentLength)
    }

    upload.addProgressListener {
      dropHandler.streamHandler.onProgress(it.readBytes, it.contentLength)
    }

    upload.addFinishedListener {
      val outputStream = if (buffer is MultiFileMemoryBuffer) {
        buffer.getOutputBuffer(it.fileName)
      } else {
        (buffer as MemoryBuffer).fileData.outputBuffer
      }

      dropHandler.streamHandler.streamingFinished(it.fileName,
                                                  it.contentLength,
                                                  outputStream as ByteArrayOutputStream)
    }

    upload.addFailedListener {
      dropHandler.streamHandler.streamingFailed(it.reason)
    }

    upload.addAllFinishedListener {
      dropHandler.onFinish(buffer)
      maybeReset()
    }
  }

  private fun maybeReset() {
    if (buffer !is MultiFileMemoryBuffer) {
      upload.element.setPropertyJson("files", Json.createArray())
    }
  }
}
