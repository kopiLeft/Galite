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
package org.kopi.galite.ui.vaadin.visual

import java.io.File

import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VWindow

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div

/**
 * The `DWindow` is an abstract implementation of an [UWindow] component.
 */
abstract class DWindow protected constructor(private val model: VWindow) : Div(), UWindow {
  val returnCode: Int
    get() = TODO()

  /**
   * The current application instance.
   */
  val application: VApplication
    get() =  ApplicationContext.applicationContext.getApplication() as VApplication

  override fun run() {
    // TODO
  }

  override fun getModel(): VWindow {
    return model
  }

  override fun setTitle(title: String) {
    // TODO
  }

  override fun setInformationText(text: String?) {
    // TODO
  }

  override fun setTotalJobs(totalJobs: Int) {
    // TODO
  }

  override fun setCurrentJob(currentJob: Int) {
    // TODO
  }

  override fun updateWaitDialogMessage(message: String) {
    // TODO
  }

  override fun closeWindow() {
    // TODO
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {
    // TODO
  }

  override fun performBasicAction(action: Action) {
    // TODO
  }

  override fun isEnabled(): Boolean {
    // TODO
    return true
  }

  override fun setEnabled(enabled: Boolean) {
    // TODO
  }

  override fun performAsyncAction(action: Action) {
    // TODO
  }

  override fun modelClosed(type: Int) {
    // TODO
  }

  override fun setWaitDialog(message: String, maxtime: Int) {
    // TODO
  }

  override fun unsetWaitDialog() {
    // TODO
  }

  override fun setWaitInfo(message: String) {
    // TODO
  }

  override fun unsetWaitInfo() {
    // TODO
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {
    // TODO
  }

  override fun unsetProgressDialog() {
    // TODO
  }

  override fun fileProduced(file: File, name: String) {
    // TODO
  }

  open fun reportError(e: VRuntimeException) {
    // TODO
  }

  open fun release() {
    // TODO
  }

  /**
   * Sets the window content.
   * @param content The window content.
   */
  open fun setContent(content: Component) {
    add(content)
  }
}
