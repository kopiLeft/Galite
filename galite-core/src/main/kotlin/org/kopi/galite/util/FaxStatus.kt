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

package org.kopi.galite.util

/*
* A class for handling the status of HYLAFAX entries returned by various queues
*/
class FaxStatus {
  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR OUTGOING FAXES
  // ----------------------------------------------------------------------
  private var id: String? = null
  private var tag: String? = null
  private var user: String? = null
  private var dialNo: String? = null
  private var dials: String? = null
  private var state: String? = null
  private var filename: String? = null
  private var incomingtime: String? = null
  private var sender: String? = null
  private var duration: String? = null

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR BOTH IN/OUT FAXES
  // ----------------------------------------------------------------------
  private var pages: String?
  private var text: String?

  // ----------------------------------------------------------------------
  // CONSTRUCTOR FOR OUTGOING FAXES
  // ----------------------------------------------------------------------
  constructor(id: String,
              tag: String,
              user: String,
              dialNo: String,
              state: String,
              pages: String,
              dials: String,
              text: String) {
    this.id = if (id.isEmpty()) null else id
    this.tag = if (tag.isEmpty()) null else tag
    this.user = if (user.isEmpty()) null else user
    this.dialNo = if (dialNo.isEmpty()) null else dialNo
    this.state = if (state.isEmpty()) null else state
    this.pages = if (pages.isEmpty()) null else pages
    this.text = if (text.isEmpty()) null else text
    this.dials = if (dials.isEmpty()) null else dials
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTOR FOR INCOMING FAXES
  // ----------------------------------------------------------------------
  constructor(filename: String,
              incomingtime: String,
              sender: String,
              pages: String,
              duration: String,
              text: String) {
    this.filename = if (filename.isEmpty()) null else filename
    this.incomingtime = if (incomingtime.isEmpty()) null else incomingtime
    this.sender = if (sender.isEmpty()) null else sender
    this.pages = if (pages.isEmpty()) null else pages
    this.duration = if (duration.isEmpty()) null else duration
    this.text = if (text.isEmpty()) null else text
  }

  // ----------------------------------------------------------------------
  // RETURNS THE ID (DATABASE ID) INSIDE THE TAG
  // THE ID IS A NUMBER SO STRIP THEREFORE ANY OTHER LEADING CHARACTERS
  // IF NO ID IS FOUND RETURN -1
  // ----------------------------------------------------------------------
  val tagId: Int
    get() {
      var startpos = 0
      var id = -1
      if (tag == null) {
        return -1
      }
      startpos = tag!!.indexOfFirst { it >= '0' && it <= '9' }

      id = try {
        Integer.valueOf(tag!!.substring(startpos)).toInt()
      } catch (e: Exception) {
        return -1
      }
      return id
    }

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF TAG STARTS WITH TAGSTR
  // ----------------------------------------------------------------------
  fun isTagged(tagstr: String): Boolean {
    return if (tag == null) {
      false
    } else (tag!!.startsWith((tagstr)))
  }

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF HAS BEEN SENT
  // ----------------------------------------------------------------------
  val isSent: Boolean
    get() = if (state!!.compareTo("D") == 0) {
      (text.isNullOrEmpty())
    } else {
      false
    }
}
