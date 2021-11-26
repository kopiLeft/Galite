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

import java.awt.Frame
import java.io.File
import java.util.Locale

import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.VWindow
import org.kopi.galite.visual.visual.WindowController

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
    return command
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  open fun doModal(f: Frame?): Boolean = WindowController.windowController.doModal(this)

  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  open fun doModal(f: VWindow?): Boolean = WindowController.windowController.doModal(this)

  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  open fun doModal(): Boolean = WindowController.windowController.doModal(this)

  /**
   * doNotModal
   * no modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  fun doNotModal() {
    WindowController.windowController.doNotModal(this)
  }

  // ----------------------------------------------------------------------
  // INFORMATION HANDLING
  // ----------------------------------------------------------------------

  open fun notice(message: String) = model.notice(message)

  fun error(message: String?) = model.error(message)

  /**
   * Displays a warning message.
   */
  fun warn(message: String) = model.warn(message)

  /**
   * Displays an ask dialog box
   */
  fun ask(message: String): Boolean = model.ask(message)

  /**
   * Displays an ask dialog box
   */
  fun ask(message: String, yesIsDefault: Boolean): Boolean = model.ask(message, yesIsDefault)

  /**
   * Sets a the text to be appended to the title.
   */
  fun appendToTitle(text: String) {
    model.appendToTitle(text)
  }

  val formTitle: String get() = model.getTitle()

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------
  /**
   * Set information text
   */
  fun setInformationText(text: String?) {
    model.setInformationText(text)
  }

  /**
   * setProgressDialog
   */
  fun setProgressDialog(message: String, currentJob: Int) {
    model.setProgressDialog(message, currentJob)
  }

  fun setTotalJobs(totalJobs: Int) {
    model.setTotalJobs(totalJobs)
  }

  fun setCurrentJob(currentJob: Int) {
    model.setCurrentJob(currentJob)
  }

  fun unsetProgressDialog() {
    model.unsetProgressDialog()
  }

  fun updateWaitDialogMessage(message: String) {
    model.updateWaitDialogMessage(message)
  }

  /**
   * setWaitInfo
   */
  fun setWaitDialog(message: String, maxtime: Int) {
    model.setWaitDialog(message, maxtime)
  }

  /**
   * change mode to free state
   */
  fun unsetWaitDialog() {
    model.unsetWaitDialog()
  }

  /**
   * setWaitInfo
   */
  fun setWaitInfo(message: String?) {
    model.setWaitInfo(message)
  }

  /**
   * change mode to free state
   */
  open fun unsetWaitInfo() {
    model.unsetWaitInfo()
  }

  // ----------------------------------------------------------------------
  // MESSAGES HANDLING
  // ----------------------------------------------------------------------
  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident the message identifier
   * @return    the requested message
   */
  protected fun formatMessage(ident: String): String? = model.formatMessage(ident, null)

  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident the message identifier
   * @param     param message parameter
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, param: Any?): String? = model.formatMessage(ident, param)

  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident  the message identifier
   * @param     param1 the first message parameter
   * @param     param1 the second message parameter
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, param1: Any, param2: Any): String? = model.formatMessage(ident, param1, param2)

  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident  the message identifier
   * @param     params the message parameters
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, params: Array<Any?>?): String? = model.formatMessage(ident, params)

  abstract fun genLocalization(destination: String? = null, locale: Locale? = this.locale)

  /**
   * Returns the qualified source file name where this object is defined.
   */
  protected val sourceFile: String
    get() {
      val basename = this.javaClass.`package`.name.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }
}
