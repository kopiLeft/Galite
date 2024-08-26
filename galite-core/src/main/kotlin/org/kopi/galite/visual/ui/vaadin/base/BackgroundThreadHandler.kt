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
package org.kopi.galite.visual.ui.vaadin.base

import java.util.concurrent.ExecutionException

import com.vaadin.flow.component.UI

/**
 * Collects some utilities for background threads in a vaadin application.
 *
 *
 * Note that all performed background tasks are followed by an client UI update
 * using the push mechanism incorporated with vaadin.
 *
 */
object BackgroundThreadHandler {

  /**
   * Exclusive access to the UI from a background thread to perform some updates.
   * @param command the command which accesses the UI.
   */
  fun access(currentUI: UI? = null, command: () -> Unit) {
    if (UI.getCurrent() != null) {
      command()

      return
    }

    val ui = currentUI ?: locateUI()

    if (ui == null) {
      command()
    } else {
      ui.access(command)
    }
  }

  /**
   * Exclusive access to the UI from a background thread to perform some updates.
   * @param command the command which accesses the UI.
   */
  fun accessAndPush(currentUI: UI? = null, command: () -> Unit) {
    if (UI.getCurrent() != null) {
      command()

      return
    }

    val ui = currentUI ?: locateUI()

    if (ui == null) {
      command()
    } else {
      ui.access {
        try {
          command()
        } finally {
          ui.push()
        }
      }
    }
  }

  /**
   * Exclusive access to the UI from a background thread to perform some updates.
   *
   * This will awaits until computation completes.
   *
   * This method is used when you are creating a Vaadin component from a background thread. This will wait until
   * initialization is finished to avoid NPE later.
   *
   *
   * @param command the command which accesses the UI.
   */
  fun accessAndAwait(currentUI: UI? = null, command: () -> Unit) {
    if (UI.getCurrent() != null) {
      command()

      return
    }

    val ui = currentUI ?: locateUI()

    if (ui == null) {
      command()
    } else {
      try {
        ui.access(command).get()
      } catch (executionException: ExecutionException) {
        executionException.cause?.let {
          throw it
        }
      }
    }
  }

  /**
   * Starts a task asynchronously and blocks the current thread. The lock will be released
   * if a notify signal is send to the blocking object.
   *
   * @param lock      The lock object.
   * @param command   The command which accesses the UI.
   */
  fun startAndWait(lock: Object, currentUI: UI? = null, command: () -> Unit) {
    access(currentUI = currentUI, command = command)

    synchronized(lock) {
      try {
        lock.wait()
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }
    }
  }

  /**
   * Starts a task asynchronously and blocks the current thread. The lock will be released
   * if a notify signal is send to the blocking object.
   *
   * @param lock      The lock object.
   * @param command   The command which accesses the UI.
   */
  fun startAndWaitAndPush(lock: Object, currentUI: UI? = null, command: () -> Unit) {
    accessAndPush(currentUI = currentUI, command = command)

    synchronized(lock) {
      try {
        lock.wait()
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }
    }
  }

  /**
   * Releases the lock based on an object.
   * @param lock The lock object.
   */
  fun releaseLock(lock: Object) {
    synchronized(lock) {
      lock.notifyAll()
    }
  }

  fun setUI(ui: UI?) {
    uiThreadLocal.set(ui)
  }

  fun updateUI(ui: UI?) {
    ui?.accessSynchronously {
      ui.push()
    }
  }

  fun locateUI(): UI? = UI.getCurrent() ?: uiThreadLocal.get()

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val uiThreadLocal = ThreadLocal<UI?>()
}
