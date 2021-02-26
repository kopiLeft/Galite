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
package org.kopi.galite.ui.vaadin.upload

import java.io.ByteArrayOutputStream
import java.io.OutputStream

import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.upload.ProgressUpdateEvent
import com.vaadin.flow.component.upload.Receiver
import com.vaadin.flow.component.upload.Upload
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ApplicationContext

/**
 * The `FileUploader` handles file upload operations.
 */
class FileUploader : ComponentEventListener<ProgressUpdateEvent>, Receiver {

  //--------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------
  /**
   * The file uploader component.
   */
  val uploader = Upload(this)

  /**
   * The output stream.
   */
  var output: ByteArrayOutputStream? = null

  private var mimeType: String? = null

  /**
   * The uploaded file name.
   */
  var filename: String? = null
    private set

  init {
    uploader.addProgressListener(this)
    //uploader.addStartedListener(this) TODO
    //uploader.addFinishedListener(this)
    //uploader.addFailedListener(this)
    //uploader.setLocale(application.getDefaultLocale().toString())
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Invokes the upload process for a given mime type.
   *
   * @param mimeType The mime type to be uploaded.
   * @return The uploaded bytes.
   */
  fun upload(mimeType: String?): ByteArray {
    TODO()
  }

  /**
   * Closes the uploader component.
   * The uploader should not be detached Immediately from the main application window in order to communicate
   * upload progress.
   * For this we will use a timer to differ schedule the detach event of the uploader component.
   */
  fun close() {
    TODO()
  }

  // TODO: Add uploadFailed event handler

  // TODO: Add uploadFinished event handler

  // TODO: Add uploadStarted event handler

  override fun receiveUpload(filename: String?, mimeType: String?): OutputStream =
          ByteArrayOutputStream().also { output = it }

  /**
   * Returns the current application instance.
   * @return The current application instance.
   */
  internal val application: VApplication
    get() = ApplicationContext.applicationContext.getApplication() as VApplication

  /**
   * Returns the parent mime type of the holded mime type.
   * @return The parent mime type of the holded mime type.
   */
  private fun getParentMIMEType(mimeType: String): String {
    return if (mimeType.indexOf('/') != -1) {
      mimeType.substring(0, mimeType.indexOf('/')).trim { it <= ' ' }
    } else {
      mimeType.trim { it <= ' ' }
    }
  }

  override fun onComponentEvent(event: ProgressUpdateEvent?) {
    TODO("Not yet implemented")
  }
}
