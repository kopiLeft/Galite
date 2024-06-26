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
package org.kopi.galite.visual.ui.vaadin.form

import java.awt.Color
import java.time.LocalDate

import org.kopi.galite.visual.form.AbstractPredefinedValueHandler
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.ui.vaadin.upload.FileUploader
import org.kopi.galite.visual.VException

/**
 * The `VPredefinedValueHandler` is the VAADIN implementation of
 * the predefined value handler specifications.
 *
 * @param model The row controller.
 * @param form The form model.
 * @param field The field model.
 */
class VPredefinedValueHandler(model: VFieldUI,
                              form: VForm,
                              field: VField)
  : AbstractPredefinedValueHandler(model, form, field) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun selectColor(color: Color?): Color? {
    // no color selection
    return null
  }

  override fun selectDate(date: LocalDate): LocalDate {
    return date
  }

  /**
   * This method will open the file chooser to select an image.
   * @return the selected image from the user file system
   * @throws VException
   * @see org.kopi.galite.visual.form.PredefinedValueHandler.selectImage
   */
  override fun selectImage(): ByteArray? = FileUploader().upload("image/*")
}
