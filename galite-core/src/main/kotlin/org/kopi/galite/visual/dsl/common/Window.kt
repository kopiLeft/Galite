/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
import org.kopi.galite.visual.util.base.InconsistencyException
import org.kopi.galite.visual.visual.VWindow
import org.kopi.galite.visual.form.VConstants

/**
 * This class represents the definition of a window
 */
abstract class Window {
  /** The title of this form */
  abstract val title: String

  /** The window locale */
  open val locale: Locale? = null

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
    val number = when (ident) {
      VKConstants.CMD_AUTOFILL -> {
        VForm.CMD_AUTOFILL
      }
      VKConstants.CMD_NEWITEM -> {
        VForm.CMD_NEWITEM
      }
      VKConstants.CMD_EDITITEM -> {
        VForm.CMD_EDITITEM
      }
      VKConstants.CMD_SHORTCUT -> {
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
    voidTriggers.add(command)
    return command
  }

  fun triggerType(trigger : Trigger, triggerTypes: IntArray) : Int {
    var type = -1
    val TRG_TYPES: IntArray = triggerTypes

    for (i in TRG_TYPES.indices) {
      if (trigger.events shr i and 1 > 0) {
        if (type == -1) {
          type = TRG_TYPES[i]

        } else if (TRG_TYPES[i] != type) {
          //throw PositionedError(getTokenReference(), BaseMessages.TRIGGER_DIFFERENT_RETURN, TRG_NAMES.get(i)) FIXME
        }
      }
    }
    return type
  }

  fun addTrigger(triggers: MutableList<Trigger>, triggerTypes: IntArray) : IntArray {
    val triggerArray = IntArray(triggerTypes.size)
    var pos = 0

    triggers.forEach { trigger ->
      val type = triggerType(trigger, triggerTypes)

      when (type) {
        VConstants.TRG_VOID -> pos = voidTriggers.size
        VConstants.TRG_PRTCD -> pos = voidProtectedTriggers.size
        VConstants.TRG_OBJECT -> pos = objectTriggers.size
        VConstants.TRG_BOOLEAN -> pos = booleanTriggers.size
        VConstants.TRG_INT -> pos = integerTriggers.size
        else -> throw InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG $type")
      }

      pos += 1 // we want to start our switches at 1

      when (type) {
        VConstants.TRG_VOID -> voidTriggers.add(trigger)
        VConstants.TRG_PRTCD -> voidProtectedTriggers.add(trigger)
        VConstants.TRG_OBJECT -> objectTriggers.add(trigger)
        VConstants.TRG_BOOLEAN -> booleanTriggers.add(trigger)
        VConstants.TRG_INT -> integerTriggers.add(trigger)
        else -> throw InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG $type")
      }

      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          triggerArray[i] = pos
        }
      }
    }

    return triggerArray
  }

  abstract fun genLocalization(destination: String? = null, locale: Locale? = this.locale)

  /**
   * Returns the qualified source file name where this object is defined.
   */
  protected val sourceFile: String
    get() {
      val basename = this.javaClass.`package`.name.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }

  val voidProtectedTriggers = mutableListOf<Trigger>()
  val voidTriggers = mutableListOf<Any>()
  val objectTriggers = mutableListOf<Trigger>()
  val booleanTriggers = mutableListOf<Trigger>()
  val integerTriggers = mutableListOf<Trigger>()
}
