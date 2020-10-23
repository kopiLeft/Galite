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
package org.kopi.galite.form

import java.awt.Event

import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VlibProperties

/**
 * This class implements a pretty printer
 */
class VDocGenerator(val latexPrinter: LatexPrintWriter) : VHelpGenerator() {

  init {
    super.printer = latexPrinter
  }

  /**
   * prints a compilation unit
   */
  fun helpOnForm(name: String,
                 commands: Array<VCommand>?,
                 blocks: Array<VBlock>?,
                 title: String,
                 help: String,
                 code: String): String {
    latexPrinter.println("\\section{$title}")
    latexPrinter.println("\\label{$code}")
    latexPrinter.println(help)
    latexPrinter.println()
    if (commands != null) {
      sortCommands(commands)
      latexPrinter.println("\\begin{description}")
      commands.forEach { command ->
        //latexPrinter.print("<BR>");
        command.helpOnCommand(this)
      }
      latexPrinter.println("\\end{description}")
    }

    blocks?.forEach { block ->
      block.helpOnBlock(this)
    }

    latexPrinter.close()
    latexPrinter.println("\\pagebreak")
    return ""
  }

  /**
   * printlns a compilation unit
   */
  override fun helpOnBlock(formCode: String,
                  title: String,
                  help: String,
                  commands: Array<VCommand>?,
                  fields: Array<VField?>?,
                  alone: Boolean) {
    latexPrinter.println("\\subsection{$title}")
    latexPrinter.uncheckedPrintln("\\begin{center}\\includegraphics{images/" +
            formCode + "_" + title.replace(' ', '_') + ".ps" +
            "}\\end{center}")
    if (help != null) {
      latexPrinter.println(help)
    }
    latexPrinter.println()
    var countNNull = 0
    for (i in fields!!.indices) {
      if (fields[i]!!.label != null) {
        countNNull++
        break
      }
    }
    if (countNNull == 0 && (commands == null || commands.isEmpty())) {
      return
    }
    latexPrinter.uncheckedPrintln("\\begin{itemize}")
    if (commands != null && commands.isNotEmpty()) {
      var query = 0
      var update = 0
      var insert = 0
      var all = 0
      sortCommands(commands)
      for (i in commands.indices) {
        val command: VCommand = commands[i]
        if (command.isActive(VConstants.MOD_QUERY) &&
                command.isActive(VConstants.MOD_UPDATE) &&
                command.isActive(VConstants.MOD_INSERT)) {
          all += 1
        } else {
          query += if (command.isActive(VConstants.MOD_QUERY)) 1 else 0
          update += if (command.isActive(VConstants.MOD_UPDATE)) 1 else 0
          insert += if (command.isActive(VConstants.MOD_INSERT)) 1 else 0
        }
      }
      if (all > 0) {
        latexPrinter.println("\\item{Funktionen}")
        latexPrinter.uncheckedPrintln("\\begin{description}")
        for (i in commands.indices) {
          val command: VCommand = commands[i]
          if (command.isActive(VConstants.MOD_QUERY) &&
                  command.isActive(VConstants.MOD_UPDATE) &&
                  command.isActive(VConstants.MOD_INSERT)) {
            command.helpOnCommand(this)
          }
        }
        latexPrinter.println("\\end{description}")
      }
      if (query > 0) {
        latexPrinter.println("\\item{Funktionen - Suchmodus}")
        latexPrinter.uncheckedPrintln("\\begin{description}")
        for (i in commands.indices) {
          val command: VCommand = commands[i]
          if (command.isActive(VConstants.MOD_QUERY) &&
                  !(command.isActive(VConstants.MOD_QUERY) &&
                          command.isActive(VConstants.MOD_UPDATE) &&
                          command.isActive(VConstants.MOD_INSERT))) {
            command.helpOnCommand(this)
          }
        }
        latexPrinter.println("\\end{description}")
      }
      if (update > 0) {
        latexPrinter.println("\\item{Funktionen - \u00C4nderungsmodus}")
        latexPrinter.uncheckedPrintln("\\begin{description}")
        for (i in commands.indices) {
          val command: VCommand = commands[i]
          if (command.isActive(VConstants.MOD_UPDATE) &&
                  !(command.isActive(VConstants.MOD_QUERY) &&
                          command.isActive(VConstants.MOD_UPDATE) &&
                          command.isActive(VConstants.MOD_INSERT)))
          {
            command.helpOnCommand(this)
          }
        }
        latexPrinter.println("\\end{description}")
      }
      if (insert > 0) {
        latexPrinter.println("\\item{Funktionen - Einf\u00FCgemodus}")
        latexPrinter.uncheckedPrintln("\\begin{description}")
        for (i in commands.indices) {
          val command: VCommand = commands[i]
          if (command.isActive(VConstants.MOD_INSERT) &&
                  !(command.isActive(VConstants.MOD_QUERY) &&
                          command.isActive(VConstants.MOD_UPDATE) &&
                          command.isActive(VConstants.MOD_INSERT)))
          {
            command.helpOnCommand(this)
          }
        }
        latexPrinter.println("\\end{description}")
      }
    }
    if (fields != null && fields.isNotEmpty()) {
      if (countNNull > 0) {
        latexPrinter.println("\\item{Felder}")
        latexPrinter.println("\\begin{description}")

        fields.forEach { field ->
          field!!.helpOnField(this)
        }

        latexPrinter.println("\\end{description}")
      }
    }
    latexPrinter.uncheckedPrintln("\\end{itemize}")
  }

  /**
   * printlns a compilation unit
   */
  fun helpOnField(blockTitle: String?,
                  pos: Int,
                  label: String,
                  anchor: String?,
                  help: String?) {
    latexPrinter.printItem(label)
    latexPrinter.println("\\index{$label}")
    if (help != null && help.isNotEmpty()) {
      latexPrinter.println(help + "\\\\")
    } else {
      latexPrinter.println("...\\\\")
      latexPrinter.println("")
    }
  }

  fun helpOnType(modeName: String,
                 modeDesc: String,
                 typeName: String,
                 typeDesc: String,
                 names: Array<String>?) {
    latexPrinter.uncheckedPrint(" \\makebox[0.7in][l]{{\\bf ")
    latexPrinter.print(VlibProperties.getString("Mode"))
    latexPrinter.uncheckedPrintln(":}}")
    latexPrinter.uncheckedPrintln(" \\makebox[0.7in][l]{{\\it $modeName}}")
    latexPrinter.uncheckedPrintln(" \\parbox[t]{4in}{")
    latexPrinter.print(modeDesc)
    latexPrinter.uncheckedPrintln("}\n")
    latexPrinter.uncheckedPrint(" \\makebox[0.7in][l]{{\\bf ")
    latexPrinter.print(VlibProperties.getString("Type"))
    latexPrinter.uncheckedPrintln(":}}")
    latexPrinter.uncheckedPrintln(" \\makebox[0.7in][l]{{\\it $typeName}}")
    latexPrinter.uncheckedPrintln(" \\parbox[t]{4in}{")
    latexPrinter.print(typeDesc)
    latexPrinter.uncheckedPrintln("\n")
    if (names != null) {
      latexPrinter.println("\\begin{enumerate}")

      names.forEach { name ->
        latexPrinter.print("\\item{$name}")
      }

      latexPrinter.println("\\end{enumerate}")
    }
    latexPrinter.uncheckedPrintln("}")
  }

  /**
   * printlns a compilation unit
   */
  fun helpOnFieldCommand(commands: Array<VCommand>?) {
    if (commands != null && commands.isNotEmpty()) {
      sortCommands(commands)
      latexPrinter.println()
      latexPrinter.println("\\begin{description}")
      commands.forEach {  command ->
        command.helpOnCommand(this)
      }
      latexPrinter.println("\\end{description}")
    }
    latexPrinter.println()
  }

  /**
   * printlns a compilation unit
   */
  override fun helpOnCommand(menu: String, item: String, icon: String?, accKey: Int, accMod: Int, help: String?) {
    latexPrinter.print("\\item{")
    if (accMod == Event.SHIFT_MASK) {
      latexPrinter.print("\\Taste{Shift} ")
    }
    if (accKey != 0) {
      latexPrinter.print("\\Taste{" + keyToName(accKey) + "} ")
    }
    latexPrinter.print("$menu $\\Rightarrow$ $item: ")
    latexPrinter.print("}")
    latexPrinter.println("\\index{$menu:$item}")
    if (help != null) {
      latexPrinter.println(help)
    } else {
      latexPrinter.println("no help")
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES
  // ----------------------------------------------------------------------
  private fun sortCommands(cmds: Array<VCommand>) {
    var i = cmds.size
    while (--i >= 0) {
      for (j in 0 until i) {
        var swap = false
        if (cmds[j].getKey() > cmds[j + 1].getKey()) {
          swap = true
        } else if (cmds[j].getKey() == 0) {
          swap = true
        }
        if (swap) {
          val tmp: VCommand = cmds[j]
          cmds[j] = cmds[j + 1]
          cmds[j + 1] = tmp
        }
      }
    }
  }
}
