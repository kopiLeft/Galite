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
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VWindow

import com.vaadin.flow.component.html.Div

/**
 * The `DWindow` is an abstract implementation of an [UWindow] component.
 */
abstract class DWindow protected constructor(model: VWindow) : Div(), UWindow {
  override fun run() {
    TODO("Not yet implemented")
  }

  override fun getModel(): VWindow {
    TODO("Not yet implemented")
  }

  override fun setTitle(title: String) {
    TODO("Not yet implemented")
  }

  override fun setInformationText(text: String?) {
    TODO("Not yet implemented")
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

  override fun closeWindow() {
    TODO("Not yet implemented")
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override fun performBasicAction(action: Action) {
    TODO("Not yet implemented")
  }

  override fun isEnabled(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override fun performAsyncAction(action: Action) {
    TODO("Not yet implemented")
  }

  override fun modelClosed(type: Int) {
    TODO("Not yet implemented")
  }

  override fun setWaitDialog(message: String, maxtime: Int) {
    TODO("Not yet implemented")
  }

  override fun unsetWaitDialog() {
    TODO("Not yet implemented")
  }

  override fun setWaitInfo(message: String) {
    TODO("Not yet implemented")
  }

  override fun unsetWaitInfo() {
    TODO("Not yet implemented")
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

  open fun reportError(e: VRuntimeException) {
    TODO()
  }

  open fun release() {
    TODO()
  }
}
