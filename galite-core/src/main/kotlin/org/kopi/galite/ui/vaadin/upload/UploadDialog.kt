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

import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.releaseLock
import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.common.VSpan

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.upload.Receiver
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.html.NativeButton
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * A dialog showing an upload component.
 */
class UploadDialog(val receiver: Receiver) : EnhancedDialog(), HasStyle {

  private val title = VSpan()
  private val ok = VInputButton()
  private val cancel = VInputButton()
  private val buttons = HorizontalLayout(cancel, ok)
  val upload: Upload = Upload(receiver)

  init {
    className = "k-upload"
    buttons.isSpacing = true
    buttons.justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    isCloseOnOutsideClick = false
    buttons.className = "k-upload-buttons"
    ok.style["cursor"] = "pointer"
    cancel.style["cursor"] = "pointer"
    setHeader(title)
    setFooter(buttons)
    add(upload)
    addHandlers()
  }
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the upload widget image.
   * @param connection The application connection.
   */
  protected fun setImage() {
    //image.setName("upload")
    //image.addStyleName("k-upload-image")
  }

  /**
   * Localizes the upload widget.
   * @param locale The locale to be used.
   */
  fun setLocale(locale: String?) {
    val uploadButton = NativeButton(LocalizedProperties.getString(locale, "BROWSE"))

    upload.uploadButton = uploadButton
    ok.caption = LocalizedProperties.getString(locale, "UPLOAD")
    cancel.caption = LocalizedProperties.getString(locale, "CANCEL")
    title.text = LocalizedProperties.getString(locale, "UPTITLE")
  }

  /**
   * Sets the mime type to be selected.
   * @param mimeType The mime type.
   */
  fun setMimeType(mimeType: String?) {
    upload.setAcceptedFileTypes(mimeType)
  }

  /**
   * Returns the selected file.
   * @return The selected file.
   */
  /**
   * Sets the selected file.
   * @param file The selected file.
   */
  /*var selectedFile: String?
    get() = upload.fu.getFilename()
    set(file) {
      textInput.setValue(file)
    }*/


  /**
   * Adds necessary handlers for this upload component.
   */
  private fun addHandlers() {
    ok.addClickListener {
      releaseLockOnUpload()
    }
    cancel.addClickListener {
      receiver.receiveUpload(null, null)
      releaseLockOnUpload()
    }
  }

  private fun releaseLockOnUpload() {
    releaseLock(receiver as Object)
  }
}
