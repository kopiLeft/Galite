/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.testing

import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.window.Window
import org.kopi.galite.visual.ApplicationContext

import com.github.mvysny.kaributesting.v10.MockVaadin
import com.github.mvysny.kaributesting.v10._blur
import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._clickItemWithCaption
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.contextmenu.HasMenuItems

fun HasMenuItems._clickItemWithCaptionAndWait(caption: String, duration: Long = 500) {
  _clickItemWithCaption(caption)
  Thread.sleep(duration)
}

fun ClickNotifier<*>._clickAndWait(duration: Long = 500) {
  _click()
  waitAndRunUIQueue(duration)
}

fun waitAndRunUIQueue(duration: Long) {
  MockVaadin.runUIQueue()
  Thread.sleep(duration)
  MockVaadin.runUIQueue()
}

inline fun <reified T: Component> findInMainWindow(): List<T> {
  val mainWindow = _get<MainWindow>()

  return mainWindow._find()
}

fun <T> T.blurOnLastField() where T: Focusable<*>, T: Component {
  val mainWindow = _get<MainWindow>()
  val lastFocusedField = (mainWindow.currentWindow as? Window)?.lasFocusedField

  if (lastFocusedField != this) {
    org.kopi.galite.testing.blurOnLastField()
  }
}

fun blurOnLastField() {
  val mainWindow = _get<MainWindow>()
  val lastFocusedField = (mainWindow.currentWindow as? Window)?.lasFocusedField

  if(lastFocusedField is Component) {
    lastFocusedField._blur()
  }
}

val defaultLocale get() = ApplicationContext.applicationContext.getApplication().defaultLocale.toString()
