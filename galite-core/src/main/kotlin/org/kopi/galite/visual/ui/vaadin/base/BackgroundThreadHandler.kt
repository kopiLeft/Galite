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
 * Utility object for managing background threads in a Vaadin application.
 *
 * Each background task is followed by a client UI update using Vaadin's push mechanism.
 */
object BackgroundThreadHandler {

  private val uiThreadLocal = ThreadLocal<UI?>()

  /**
   * Provides exclusive access to the UI from a background thread.
   * @param command The command to execute, which can access the UI.
   */
  fun access(currentUI: UI? = null, command: () -> Unit) {
    val ui = currentUI ?: getUI()
    if (ui == null) {
      command()
    } else {
      ui.access(command)
    }
  }

  /**
   * Provides exclusive access to the UI and pushes an update.
   * @param command The command to execute, which can access the UI.
   */
  fun accessAndPush(currentUI: UI? = null, command: () -> Unit) {
    val ui = currentUI ?: getUI()
    ui?.session?.lock()
    try {
      ui?.access {
        command()
        ui.push()
      } ?: command()
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      ui?.session?.unlock()
    }
  }

  /**
   * Provides exclusive access to the UI, waits for the command to complete, and catches execution exceptions.
   * @param command The command to execute, which can access the UI.
   */
  fun accessAndAwait(currentUI: UI? = null, command: () -> Unit) {
    val ui = currentUI ?: getUI()
    if (ui == null) {
      command()
    } else {
      runCatching {
        ui.access(command).get()
      }.onFailure {
        (it as? ExecutionException)?.cause?.let { cause ->
          cause.printStackTrace()
          throw cause
        }
      }
    }
  }

  /**
   * Starts a task asynchronously and blocks until notified.
   * @param lock The lock object for synchronization.
   * @param command The command to execute, which can access the UI.
   */
  fun startAndWait(lock: Object, currentUI: UI? = null, command: () -> Unit) {
    access(currentUI, command)
    synchronized(lock) {
      try {
        lock.wait()
      } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        e.printStackTrace()
      }
    }
  }

  /**
   * Starts a task asynchronously with UI access and push, blocking until notified.
   * @param lock The lock object for synchronization.
   * @param command The command to execute, which can access the UI.
   */
  fun startAndWaitAndPush(lock: Object, currentUI: UI? = null, command: () -> Unit) {
    accessAndPush(currentUI, command)
    synchronized(lock) {
      try {
        lock.wait()
      } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        e.printStackTrace()
      }
    }
  }

  /**
   * Notifies all threads waiting on the provided lock.
   * @param lock The lock object to release.
   */
  fun releaseLock(lock: Object) {
    synchronized(lock) {
      lock.notifyAll()
    }
  }

  /**
   * Sets the UI in a thread-local variable for later retrieval.
   * Useful for scenarios where `UI.getCurrent()` is null.
   * @param ui The UI instance to set.
   */
  fun setUI(ui: UI?) {
    uiThreadLocal.set(ui)
  }

  /**
   * Forces an immediate push of the current UI.
   * @param ui The UI instance to push.
   */
  fun updateUI(ui: UI?) {
    ui?.accessSynchronously { ui.push() }
  }

  /**
   * Attempts to retrieve the current UI from `UI.getCurrent()` or the thread-local storage.
   */
  fun getUI(): UI? = UI.getCurrent() ?: uiThreadLocal.get()
}
