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
package org.kopi.galite.common

import java.io.File
import java.util.Locale

import org.kopi.galite.form.VForm
import org.kopi.galite.visual.VWindow

/**
 * This class represents the definition of a window
 */
abstract class Window {
  /** The title of this form */
  abstract val title: String

  /** The window locale */
  open val locale: Locale? = null

  /** The help text */
  var help: String? = null

  /** The window options */
  internal var options: Int? = null

  /** Actors added to this window */
  internal val actors = mutableListOf<Actor>()

  /** Commands added to this window */
  internal var commands = mutableListOf<Command>()

  /** Triggers added to this window */
  internal var triggers = mutableListOf<Trigger>()

  /** The model generated from this class. */
  abstract val model: VWindow

  /** Menus added to this window */
  internal val menus = mutableListOf<Menu>()

  /**
   * Adds a new menu to this form. Defining a menu means adding an entry to the menu bar in the top of the form
   *
   * @param label                the menu label in default locale
   * @return                     the menu. It is used later to adding actors to this menu by specifying
   * the menu name in the actor definition.
   */
  fun menu(label: String): Menu {
    val menu = Menu(label)
    menus.add(menu)
    return menu
  }

  /**
   * Adds a new actor to this form.
   *
   * An Actor is an item to be linked to a command.
   *
   * @param menu                 the containing menu
   * @param label                the label
   * @param help                 the help
   */
  fun actor(ident: String, menu: Menu, label: String, help: String, init: (Actor.() -> Unit)? = null): Actor {
    val number = when {
      ident == VKConstants.CMD_AUTOFILL -> {
        VForm.CMD_AUTOFILL
      }
      ident == VKConstants.CMD_NEWITEM -> {
        VForm.CMD_NEWITEM
      }
      ident == VKConstants.CMD_EDITITEM -> {
        VForm.CMD_EDITITEM
      }
      ident == VKConstants.CMD_SHORTCUT -> {
        VForm.CMD_EDITITEM_S
      }
      else -> {
        0
      }
    }
    val actor = Actor(ident, menu, label, help, number)
    if (init != null) {
      actor.init()
    }
    actors.add(actor)
    return actor
  }

  /**
   * Adds a new command to this window.
   *
   * @param item    the actor linked to the command.
   * @param init    initialization method.
   */
  fun command(item: Actor, init: Command.() -> Unit): Command {
    val command = Command(item)
    command.init()
    commands.add(command)
    return command
  }

  /**
   * Returns the qualified source file name where this object is defined.
   */
  protected val sourceFile: String
    get() {
      val basename = this.javaClass.packageName.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }
}
