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
package org.kopi.galite.ui.vaadin.grid

import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.StreamResourceWriter
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * The implementation of an image grid editor.
 */
class GridEditorImageField() : GridEditorField<Any?>() {
  override fun setPresentationValue(newPresentationValue: Any?) {
    TODO("Not yet implemented")
  }

  override fun generateModelValue(): Any? {
    TODO("Not yet implemented")
  }


  /**
   * Creates a VAADIN resource from a binary stream.
   */
  class ImageResource(streamSource: StreamResourceWriter?) : StreamResource(generateFileName(), streamSource) {
    val mIMEType: String
      get() = "image/*"

    /**
     * Returns the image byte stream.
     * @return The image byte stream.
     */
    val image: ByteArray
      get() = (writer as ImageStreamSource).image

    companion object {
      /**
       * Generates a dummy file name.
       * @return The generated file name.
       */
      private fun generateFileName(): String {
        return "image" + System.currentTimeMillis()
      }
    }

    init {
      setCacheTime(0L)
    }
  }

  /**
   * The stream provider for the image editor.
   */
  class ImageStreamSource(val image: ByteArray)  {
    val stream: InputStream
      get() = ByteArrayInputStream(image)

  }
}
