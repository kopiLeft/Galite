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
package org.kopi.galite.visual.dsl.common

import java.io.File

/**
 * An element that can be inserted into a window. it can be form element, report element or chart element.
 */
abstract class WindowElement(ident: String? = null, open val source: String? = null) {

  open var ident: String = ident ?: javaClass.name.removePrefix("${javaClass.`package`.name}.")
          .substringAfterLast('$')

  /**
   * Returns the qualified source file name where this element is defined.
   */
  internal val sourceFile: String
    get() {
      if(source != null) {
        return source!!
      }

      val basename = this.javaClass.`package`.name.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }
}
