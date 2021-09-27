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
package org.kopi.galite.visual.ui.vaadin.upload

import java.io.ByteArrayOutputStream
import java.io.OutputStream

import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.accessAndPush
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.startAndWaitAndPush
import org.kopi.galite.visual.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.visual.ApplicationContext

import com.vaadin.flow.component.upload.FailedEvent
import com.vaadin.flow.component.upload.Receiver
import com.vaadin.flow.component.upload.StartedEvent

/**
 * The `FileUploader` handles file upload operations.
 */
class FileUploader : Receiver {

  //--------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------
  /**
   * The file uploader component.
   */
  private lateinit var uploadDialog: UploadDialog

  /**
   * The output stream.
   */
  var output: ByteArrayOutputStream? = null

  private var mimeType: String? = null

  /**
   * The uploaded file name.
   */
  var fileName: String? = null
    private set

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Invokes the upload process for a given mime type.
   *
   * @param mimeType The mime type to be uploaded.
   * @return The uploaded bytes.
   */
  fun upload(mimeType: String?): ByteArray? {
    this.mimeType = mimeType
    startAndWaitAndPush(this as Object) {
      uploadDialog = UploadDialog(this)
      val uploader = uploadDialog.upload
      //uploader.addProgressListener(::updateProgress)
      uploader.addStartedListener(::uploadStarted)
      //uploader.addFinishedListener(::uploadFinished)
      uploader.addFailedListener(::uploadFailed)
      uploadDialog.setLocale(application.defaultLocale.toString())
      //uploader.acceptedFileTypes.add(mimeType)
      uploadDialog.open()
    }

    close()

    return if (output != null) {
      output!!.toByteArray()
    } else {
      null
    }
  }

  /**
   * Closes the uploader dialog containing the uploader.
   */
  fun close() {
    accessAndPush {
      uploadDialog.close()
    }
  }

  fun uploadFailed(event: FailedEvent) {
    if (event.reason != null) {
      event.reason.printStackTrace(System.err)
    }
  }

  fun uploadStarted(event: StartedEvent) {
    val mimeTypes: Array<String>
    var accepted: Boolean
    if (mimeType == null) {
      accepted = true
    } else {
      accepted = false
      mimeTypes = mimeType!!.split(",".toRegex()).toTypedArray()
      for (mimeType in mimeTypes) {
        if (event.getMIMEType().startsWith(getParentMIMEType(mimeType))) {
          accepted = true
          break
        }
      }
    }
    if (!accepted) {
      uploadDialog.upload.interruptUpload()
    }
  }

  override fun receiveUpload(fileName: String?, mimeType: String?): OutputStream? {
    val stream = if(fileName == null) {
      output = null
      null
    } else {
      ByteArrayOutputStream()
    }

    output = stream
    this.fileName = fileName

    return stream
  }

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
}
