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

package org.kopi.galite.ui.visual

import com.vaadin.flow.router.HasDynamicTitle

import java.io.File

import org.kopi.galite.ui.addons.Window;
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VWindow

/**
 * The `DWindow` is an abstract implementation of an [UWindow] component.
 * The vaadin implementation is based on lightweight components regarding to client side
 * and reduce the window load time.
 *
 * @param model The window model.
 */
abstract class DWindow(private var model: VWindow) : Window(), UWindow, HasDynamicTitle {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the window model.
   * @param model The window model.
   */
  fun setModel(model: VWindow) {
    TODO()
  }

  /**
   * Displays an error message.
   * @param message The error message to be displayed.
   */
  fun displayError(message: String?) {
    TODO()
  }

  /**
   * Reports a visual error from a runtime exception.
   * @param e The runtime exception.
   */
  open fun reportError(e: VRuntimeException?) {
    TODO()
  }

  /**
   * Allow building of a customized edit menu.
   */
  fun createEditMenu() {}

  /**
   * Adds a command in the menu bar.
   * @param actorDefs The [VActor] definitions.
   */
  private fun addActorsToGUI(actorDefs: Array<VActor>?) {
    if (actorDefs != null) {
      for (i in actorDefs.indices) {
        var actor: DActor?
        actor = DActor(actorDefs[i])
        addActor(actor)
      }
    }
  }


  override fun performBasicAction(action: Action) {
    TODO()
  }

  /**
   * Use [.closeWindow] or [.close] instead.
   */
  @Deprecated("", ReplaceWith("closeWindow()"))
  fun close() {
    closeWindow()
  }

  /**
   * Sets the In action state
   * @see ActionRunner.setInAction
   */
  fun setInAction() {
    TODO()
  }

  /**
   * Displays a text in the lower right corner of the window.
   * @param text The statistics text.
   */
  fun setStatisticsText(text: String?) {
    //
  }

  //--------------------------------------------------------------
  // UWINDOW IMPLEMENTATION
  //--------------------------------------------------------------


  override fun performAsyncAction(action: Action) {
    TODO()
  }

  override fun modelClosed(type: Int) {
    TODO()
  }

  override fun setWaitDialog(message: String, maxtime: Int) {
    TODO("Not yet implemented")
  }

  override fun unsetWaitDialog() {
    TODO("Not yet implemented")
  }


  override fun getModel(): VWindow {
    return model
  }

  override fun setTitle(title: String) {
  }

  override fun setInformationText(text: String) {
    // footPanel.setInformationText(text);
  }

  override fun setTotalJobs(totalJobs: Int) {
    TODO("Not yet implemented")
  }

  override fun setCurrentJob(currentJob: Int) {
    TODO("Not yet implemented")
  }

  override fun updateWaitDialogMessage(message: String) {
    TODO("Not yet implemented")
  }


  override fun setWindowFocusEnabled(enabled: Boolean) {
    // do nothing
  }

  override fun setWaitInfo(message: String) {
    TODO()
  }

  override fun unsetWaitInfo() {
    TODO()
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {
    TODO("Not yet implemented")
  }

  override fun unsetProgressDialog() {
    TODO("Not yet implemented")
  }

  override fun fileProduced(file: File, name: String) {
    TODO("Not yet implemented")
  }

  /**
   * Called to close the view (from the user), it does not
   * definitly close the view(it may ask the user before)
   */
  override fun closeWindow() {
    if (!getModel().allowQuit()) {
      return
    }
    getModel().willClose(VWindow.CDE_QUIT)
  }

  /**
   * Displays the application information.
   * @param message The application information.
   */
  fun showApplicationInformation(message: String?) {
    TODO()
  }

  /**
   * Returns the current application instance.
   * @return the current application instance.
   */
  protected open fun getApplication(): VApplication {
    TODO()
  }


  /**
   * Returns the In Action state.
   * @return `true` if an action is being performed.
   */
  var inAction = false
    private set
  private var currentAction: Action? = null
  protected var runtimeDebugInfo: Throwable? = null

  /**
   * Returns the exist code of this window.
   * @return The exist code of this window.
   */
  var returnCode = 0
    private set

  /**
   * Returns `true` if the used has been asked for a request.
   * @return `true` if the used has been asked for a request.
   */
  var isUserAsked = false
    private set

  override fun getPageTitle(): String {
    return model.getTitle()
  }

  init {
    createEditMenu()
    addActorsToGUI(model.getActors().toTypedArray())
    // TODO
  }
}
