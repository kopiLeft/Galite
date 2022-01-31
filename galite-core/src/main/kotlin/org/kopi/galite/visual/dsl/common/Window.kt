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
import java.util.Locale

import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.visual.VWindow

/**
 * This class represents the definition of a window

 * @param title The title of this form.
 * @param locale the window locale
 */
abstract class Window(val title: String, val locale: Locale?) {

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
  val menus = mutableListOf<Menu>()

  /**
   * Adds a new menu to this form. Defining a menu means adding an entry to the menu bar in the top of the form
   *
   * @param label                the menu label in default locale
   * @return                     the menu. It is used later to adding actors to this menu by specifying
   * the menu name in the actor definition.
   */
  fun menu(label: String): Menu {
    val menu = Menu(label, sourceFile)

    menus.add(menu)
    return menu
  }

  /**
   * Adds an actor to this form.
   *
   * An Actor is an item to be linked to a command.
   *
   * @param actor the actor to add.
   */
  fun actor(actor: Actor): Actor {
    actor.ident = actor.command?.ident ?: "actor${actors.size}"

    if (!menus.contains(actor.menu)) {
      menus.add(actor.menu)
    }
    actors.add(actor)
    return actor
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
  fun actor(menu: Menu,
            label: String,
            help: String,
            command: PredefinedCommand? = null,
            init: (Actor.() -> Unit)? = null): Actor {
    val actor = Actor(menu, label, help, command, source = sourceFile)

    actor.ident = command?.ident ?: "actor${actors.size}"
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
   * @param modes   the modes in which the command should be executed.
   * @param action  the action function.
   */
  fun command(item: Actor, vararg modes: Mode, action: () -> Unit): Command {
    val command = Command(item)

    if (modes.isNotEmpty()) {
      command.setMode(*modes)
    }
    command.action = action

    if(!actors.contains(item)) {
      actors.add(item)
    }
    commands.add(command)
    return command
  }


  /**
   * Resets window to initial state
   */
  fun reset() {
    model.reset()
  }

  abstract fun genLocalization(destination: String? = null, locale: Locale? = this.locale)

  /**
   * Returns the qualified source file name where this object is defined.
   */
  internal val sourceFile: String
    get() {
      val basename = this.javaClass.`package`.name.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }
}
