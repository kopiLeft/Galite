/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: IPPHttp.java 34961 2016-11-04 17:20:49Z hacheni $
 */
package org.kopi.galite.util.ipp


class IPPHttp {
  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  constructor(printerName: String?, request: IPP) {
    ippHeader = IPPHttpHeader(printerName, request.size)
    iPP = request
  }

  constructor(`is`: IPPInputStream?) {
    ippHeader = IPPHttpHeader(`is`!!)
    iPP = IPP(`is`)
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  fun write(os: IPPOutputStream?) {
    ippHeader.write(os!!)
    iPP.write(os)
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var ippHeader: IPPHttpHeader
  var iPP: IPP
    private set
}