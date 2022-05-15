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

package org.kopi.galite.visual

import java.io.File

/**
 * `FileHandler` handles all supported actions on a [File].
 */
abstract class FileHandler {
  //------------------------------------------------------------
  // ABSTRACT METHODS
  //------------------------------------------------------------
  /**
   * Shows a dialog box to choose a file with a given default name.
   * @param window The caller window of the choice operation.
   * @param defaultName The file default name.
   * @return The chosen file.
   */
  abstract fun chooseFile(window: UWindow, defaultName: String): File?

  /**
   * Shows a dialog box to choose a file with a given default name from a given directory.
   * @param window The caller window of the choice operation.
   * @param dir The directory where the file should be chosen from.
   * @param defaultName The file default name.
   * @return The chosen file.
   */
  abstract fun chooseFile(window: UWindow, dir: File?, defaultName: String): File?

  /**
   * Shows a dialog box to open a file with a given default name.
   * @param window The caller window of the open operation.
   * @param defaultName The file default name.
   * @return The opened file.
   */
  abstract fun openFile(window: UWindow, defaultName: String): File?

  /**
   * Shows a dialog box to open a file with a given default name.
   * @param window The caller window of the open operation.
   * @param filter The file selection filter.
   * @return The opened file.
   */
  abstract fun openFile(window: UWindow, filter: FileFilter): File?

  /**
   * Shows a dialog box to open a file with a given default name from a given directory.
   * @param window The caller window of the open operation.
   * @param dir The directory where the file should be opened from.
   * @param defaultName The file default name.
   * @return The opened file.
   */
  abstract fun openFile(window: UWindow, dir: File?, defaultName: String): File?

  //------------------------------------------------------------
  // FILE FILTER
  //------------------------------------------------------------

  /**
   * A filter for abstract pathnames.
   */
  interface FileFilter {
    /**
     * Tests whether or not the specified abstract pathname should be
     * included in a pathname list.
     *
     * @param  pathname  The abstract pathname to be tested
     * @return  `true` if and only if `pathname`
     * should be included
     */
    fun accept(pathname: File?): Boolean

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     */
    val description: String
  }

  // -----------------------------------------------------------
  // PDF FILE FILTER
  // -----------------------------------------------------------

  class PdfFilter : FileFilter {
    /**
     * Tests if the [pathname] of the file has the extension .PDF
     */
    override fun accept(pathname: File?): Boolean {
      return pathname != null && pathname.name.toUpperCase().endsWith(".PDF")
    }

    /**
     * The description of this filter.
     */
    override val description: String = "All PDF files"
  }

  companion object {
    var fileHandler: FileHandler? = null
      set(handler) {
        assert(handler != null) { "FileHandler cannot be null" }
        field = handler
      }
  }
}
