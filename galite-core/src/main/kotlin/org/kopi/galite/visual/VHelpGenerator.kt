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

import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.PrintWriter

/**
 * This class implements a pretty printer
 */
open class VHelpGenerator {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected open lateinit var printer: PrintWriter

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
  fun helpOnCommands(commands: Array<VCommand>?) {
    helpOnCommands(commands!!.toList())
  }

  /**
   * print commands
   */
  fun helpOnCommands(commands: List<VCommand>) {
    if (commands.isNotEmpty()) {
      printer.println("<TABLE valign=\"top\">")
      for (i in commands.indices) {
        commands[i].helpOnCommand(this)
      }
      printer.println("</TABLE>")
    }
  }

  /**
   * print a command
   */
  open fun helpOnCommand(menu: String?,
                         item: String?,
                         icon: String?,
                         accKey: Int,
                         accMod: Int,
                         help: String?) {
    printer.println("<TR><TD>")
    if (icon != null) {
      addButton("$icon.png")
    } else {
      printer.println("&nbsp;")
    }
    printer.println("</TD><TD>")
    if (accMod != 0) {
      if (accMod == InputEvent.SHIFT_MASK) {
        printer.print("Shift-")
      }
    }
    printer.println(keyToName(accKey))
    printer.println("</TD><TD><STRONG>$menu:$item:</STRONG></TD><TD>")
    if (help != null) {
      printer.println(help)
    } else {
      printer.println("no help")
    }
    printer.println("</TD></TR>")
  }

  /**
   * Add an image
   */
  private fun addImage(name: String, border: Int) {
    printer.print("<img src=\"" + ImageHandler.imageHandler.getURL(name))
    printer.print("\" BORDER =\"$border")
    printer.println("\" alt=\"$name\">")
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
}
