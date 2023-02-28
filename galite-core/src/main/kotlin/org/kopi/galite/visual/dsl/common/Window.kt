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

import java.awt.Frame
import java.io.File
import java.util.Locale

import org.kopi.galite.visual.Action
import org.kopi.galite.visual.Actor
import org.kopi.galite.visual.Command
import org.kopi.galite.visual.DefaultActor
import org.kopi.galite.visual.Mode
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VWindow

/**
 * This class represents the definition of a window

 * @param title The title of this form.
 * @param locale the window locale
 */
abstract class Window(val title: String, val locale: Locale?) {

  /** Actors added to this window */
  internal val actors = mutableListOf<Actor>()

  /** Commands added to this window */
  internal var commands = mutableListOf<Command>()

  /** Triggers added to this window */
  internal var triggers = mutableListOf<Trigger>()

  /** Menus added to this window */
  val menus = mutableListOf<Menu>()

  internal var isModelInitialized: Boolean = false

  /** Number of internal actors added to this window */
  private var internalActorCount = 0

  /**
   * Adds a new menu to this form. Defining a menu means adding an entry to the menu bar in the top of the form
   *
   * @param label                the menu label in default locale
   * @return                     the menu. It is used later to adding actors to this menu by specifying
   * the menu name in the actor definition.
   */
  fun menu(label: String): Menu {
    val menu = Menu(label, "actor${actors.size}", sourceFile)

    menus.add(menu)
    return menu
  }

  /**
   * Adds a menu to this form. Defining a menu means adding an entry to the menu bar in the top of the form
   *
   * @param menu                the menu
   * @return                     the menu. It is used later to adding actors to this menu by specifying
   * the menu name in the actor definition.
   */
  fun menu(menu: Menu): Menu {
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
    if (!menus.contains(actor.menu)) {
      menus.add(actor.menu)
    }
    actors.add(actor)
    model.addActor(actor)
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
            init: (Actor.() -> Unit)? = null): Actor {
    val actor = Actor(menu, label, help, "actor${internalActorCount}", sourceFile)
    internalActorCount++

    if (init != null) {
      actor.init()
    }
    actor(actor)

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
   * @param command              the predefined command
   */
  fun actor(menu: Menu,
            label: String,
            help: String,
            command: PredefinedCommand,
            init: (DefaultActor.() -> Unit)? = null): DefaultActor {
    val actor = DefaultActor(menu, label, help, command, "actor${internalActorCount}", sourceFile)
    internalActorCount++

    if (init != null) {
      actor.init()
    }
    actor(actor)

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
    val command = Command(item, modes, model, action = action)

    if(!actors.contains(item)) {
      actor(item)
    }
    commands.add(command)
    model.commands.add(command)
    addCommandTrigger()

    return command
  }

  open fun addCommandTrigger() {}

  /**
   * Resets window to initial state
   */
  fun reset() {
    model.reset()
  }

  /**
   * Open an URL in the navigator.
   * @param url The URL to navigate to.
   */
  open fun openURL(url: String) {
    model.openURL(url)
  }


  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  fun doModal(frame: Frame): Boolean = model.doModal(frame)

  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  fun doModal(): Boolean = model.doModal()

  /**
   * doNotModal
   * no modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  fun doNotModal() {
    model.doNotModal()
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * Returns true if asynchronous operations can be performed
   * Override to change behavior
   */
  fun allowAsynchronousOperation(): Boolean = model.allowAsynchronousOperation()

  /**
   * Returns true if it is allowed to quit this model
   * (the form for this model)
   */
  open fun allowQuit(): Boolean = model.allowQuit()

  fun performAsyncAction(action: Action) {
    model.performAsyncAction(action)
  }

  // ----------------------------------------------------------------------
  // INFORMATION HANDLING
  // ----------------------------------------------------------------------
  fun notice(message: String) {
    model.notice(message)
  }

  fun error(message: String?) {
    model.error(message)
  }

  /**
   * Displays a warning message.
   */
  fun warn(message: String) {
    model.warn(message)
  }

  /**
   * Displays an ask dialog box
   */
  fun ask(message: String, yesIsDefault: Boolean = false): Boolean =
    model.ask(message, yesIsDefault)

  /**
   * Sets a the text to be appended to the title.
   */
  fun appendToTitle(text: String) {
    model.appendToTitle(text)
  }

  /**
   * change the title of this form
   */
  open fun setTitle(title: String?) {
    model.setTitle(title)
  }

  /**
   * Enables/disables the actor.
   */
  open fun setActorEnabled(position: Int, enabled: Boolean) {
    model.setActorEnabled(position, enabled)
  }

  /**
   * close model if allowed
   */
  open fun willClose(code: Int) {
    model.willClose(code)
  }

  /**
   * Closes the window
   */
  open fun close(code: Int) {
    model.close(code)
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------
  /**
   * setInformationText
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
  fun setWaitDialog(message: String, maxTime: Int) {
    model.setWaitDialog(message, maxTime)
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
  fun unsetWaitInfo() {
    model.unsetWaitInfo()
  }

  open fun enableCommands() {
    model.enableCommands()
  }

  open fun setCommandsEnabled(enable: Boolean) {
    model.setCommandsEnabled(enable)
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF DBContextHandler
  // ----------------------------------------------------------------------
  /**
   * Returns true if the exception allows a retry of the
   * transaction, false in the other case.
   *
   * @param reason the reason of the transaction failure
   * @return true if a retry is possible
   */
  fun retryableAbort(reason: Exception): Boolean = model.retryableAbort(reason)

  /**
   * Asks the user, if she/he wants to retry the exception
   *
   * @return true, if the transaction should be retried.
   */
  fun retryProtected(): Boolean = model.retryProtected()

  /**
   * Returns the current user name
   */
  open fun getUserName(): String? = model.getUserName()

  /**
   * Returns the user ID
   */
  open fun getUserID(): Int = model.getUserID()

  // ----------------------------------------------------------------------
  // MESSAGES HANDLING
  // ----------------------------------------------------------------------

  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident  the message identifier
   * @param     param1 the first message parameter
   * @param     param2 the second message parameter
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, param1: Any?, param2: Any? = null): String? =
    model.formatMessage(ident, param1, param2)

  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident  the message identifier
   * @param     params the message parameters
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, params: Array<Any?>): String? = model.formatMessage(ident, params)

  /**
   * Try to handle an exception
   */
  fun fatalError(data: Any?, line: String, reason: Throwable) {
    model.fatalError(data, line, reason)
  }

  /** The model generated from this class. */
  abstract val model: VWindow

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
