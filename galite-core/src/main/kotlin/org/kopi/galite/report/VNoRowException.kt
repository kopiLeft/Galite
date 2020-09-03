/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

package org.kopi.galite.report

import org.kopi.galite.visual.VRuntimeException

/**
 * This class represents exceptions occuring during execution process.
 */
class VNoRowException : VRuntimeException {
  /**
   * Constructs an exception with a message.
   *
   * @param        message                the associated message
   */
  constructor(message: String?) : super(message) {

  }

  /**
   * Constructs an exception with no message.
   */
  constructor() : this(null) {

  }

  /**
   * Comment for `serialVersionUID`
   */
  private val serialVersionUID = -5435963954463379807L

}
