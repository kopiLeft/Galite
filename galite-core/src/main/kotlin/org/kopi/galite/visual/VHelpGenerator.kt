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

package org.kopi.galite.visual

import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.PrintWriter


/**
 * This class implements a Kopi pretty printer
 */
open class VHelpGenerator {
  /**
   * Key to name
   */
  fun keyToName(key: Int): String {
    return when (key) {
      KeyEvent.VK_F1 -> "F1"
      KeyEvent.VK_F2 -> "F2"
      KeyEvent.VK_F3 -> "F3"
      KeyEvent.VK_F4 -> "F4"
      KeyEvent.VK_F5 -> "F5"
      KeyEvent.VK_F6 -> "F6"
      KeyEvent.VK_F7 -> "F7"
      KeyEvent.VK_F8 -> "F8"
      KeyEvent.VK_F9 -> "F9"
      KeyEvent.VK_F10 -> "F10"
      KeyEvent.VK_F11 -> "F11"
      KeyEvent.VK_F12 -> "F12"
      KeyEvent.VK_ESCAPE -> "Esc"
      else -> "?"
    }
  }

  /**
   * print commands
   */
  fun helpOnCommands(commands: Array<VCommand>) {
    if (commands.size > 0) {
      p!!.println("<TABLE valign=\"top\">")
      for (i in commands.indices) {
        commands[i].helpOnCommand(this)
      }
      p!!.println("</TABLE>")
    }
  }

  /**
   * print a command
   */
  fun helpOnCommand(menu: String,
                    item: String,
                    icon: String?,
                    accKey: Int,
                    accMod: Int,
                    help: String?) {
    //p.println("<DT>");
    p!!.println("<TR><TD>")
    if (icon != null) {
      addButton("$icon.png")
    } else {
      p!!.println("&nbsp;")
    }
    p!!.println("</TD><TD>")
    if (accMod != 0) {
      if (accMod == InputEvent.SHIFT_MASK) {
        p!!.print("Shift-")
      }
    }
    p!!.println(keyToName(accKey))
    p!!.println("</TD><TD><STRONG>$menu:$item:</STRONG></TD><TD>")
    //p.println("<DD>");
    if (help != null) {
      p!!.println(help)
    } else {
      p!!.println("no help")
    }
    p!!.println("</TD></TR>")
  }

  /**
   * Add an image
   */
  private fun addImage(name: String, border: Int) {
    p!!.print("<img src=\"" + ImageHandler.imageHandler!!.getURL(name))
    p!!.print("\" BORDER =\"$border")
    p!!.println("\" alt=\"$name\">")
  }

  /**
   * Add an image
   */
  fun addImage(name: String) {
    addImage(name, 0)
  }

  /**
   * Add a button
   */
  fun addButton(name: String) {
    addImage(name, 1)
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected open lateinit var p: PrintWriter
}
