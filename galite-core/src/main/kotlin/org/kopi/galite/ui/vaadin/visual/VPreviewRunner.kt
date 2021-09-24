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

import org.kopi.galite.preview.VPreviewWindow
import org.kopi.galite.print.PSPrintException
import org.kopi.galite.util.AbstractPrinter
import org.kopi.galite.util.PrintJob
import org.kopi.galite.visual.PreviewRunner
import org.kopi.galite.visual.VException

/**
 * The `VPreviewRunner` is the vaadin implementation of the
 * [PreviewRunner] specification.
 */
class VPreviewRunner : PreviewRunner {
  override fun run(data: PrintJob, command: String) {
    try {
      VPreviewWindow().preview(
              if (data.dataType != PrintJob.DAT_PS) data else AbstractPrinter.convertToGhostscript(data), command)
    } catch (e: VException) {
      throw PSPrintException("PreviewPrinter.PrintTaskImpl::print()", e)
    }
  }
}
