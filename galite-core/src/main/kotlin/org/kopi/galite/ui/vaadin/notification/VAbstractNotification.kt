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
package org.kopi.galite.ui.vaadin.notification

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.confirmdialog.ConfirmDialog.CancelEvent
import com.vaadin.flow.component.confirmdialog.ConfirmDialog.ConfirmEvent
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VInputButton

/**
 * An abstract implementation of notification components such as
 * warnings, errors, confirms and information.
 */
abstract class VAbstractNotification(): Dialog() {

  /**
   * Localization string.
   */
  var locale: String? = null

  /**
   * Shows the notification popup.
   */
  fun show() {
    super.open()
  }

  /**
   * Closes the notification panel.
   */
  override fun close() {
    this.hide()
  }

  /**
   * Hides the notification dialog.
   */
  protected fun hide() {
  super.close()
  }

  /**
   * Registers a new notification listener.
   * @param l The listener to be added.
   */
  fun addNotificationListener(l: ComponentEventListener<ClickEvent<VInputButton>>) {
    listeners!!.add(l)
  }

  fun addConfirmationListener(button: VInputButton, listener: ComponentEventListener<ClickEvent<VInputButton>>?) {
    button.addClickListener(listener)
  }

  /**
   * Fires a close event.
   * @param event The close event.
   */
  protected fun fireOnClose(event: CancelEvent) {}

  /**
   * Fires a confirm event.
   * @param event The confirm event.
   */
  protected fun fireOnConfirm(event: ConfirmEvent) {}


  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------

  /**
   * Sets the notification title.
   * @param title The notification title.
   */
  fun setNotificationTitle(title: String?) {
    this.title!!.text = title
  }

  /**
   * Sets the notification message.
   * @param text The notification message.
   */
  fun setNotificationMessage(text: String?) {
    this.message!!.text = text
  }

  /**
   * Shows an optional glass pane.
   * @return `true` if a glass pane should be shown
   */
  protected open fun showGlassPane(): Boolean {
    return false
  }

  /**
   * Should we go back to the last focused field when the notification is closed ?
   * @return `true` if we should go back to the last focused field when the notification is closed.
   */
  protected open fun goBackToLastFocusedWindow(): Boolean {
    return true
  }

  fun clear() {
    super.remove()
    listeners = null
    title = null
    message = null
    buttons = null
    dialog = null
  }

  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------

  /**
   * Sets the notification buttons.
   * @param locale The notification locale.
   */
  abstract fun setButtons(locale: String?)

  /**
   * Returns the icon name to be used with this notification.
   * @return The icon name to be used with this notification.
   */
  protected abstract val iconName: String?

  //-------------------------------------------------
  // INNER CLASSES
  //-------------------------------------------------

  /**
   * A simple component that wraps a h3 HTML tag.
   */
  private class VH3 : H3() {

    /**
     * Sets the inner text for this element.
     * @param text The inner text.
     */
    override fun setText(text: String?) {
          element.text=text
    }
  }

  /**
   * A simple component that wraps a p html tag.
   */
  private class VParagraph : Paragraph() {

    /**
     * Sets the inner HTML for this component element.
     * @param html The component inner HTML.
     */
    fun setHtml(html: String?) {
      element.setProperty("innerHTML", html)
    }
  }

  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  private var listeners : MutableList<ComponentEventListener<ClickEvent<VInputButton>>>? = null
  private var icon: Icon? = null
  private var title: Label? = null
  private var message: Label? = null
  open var yesIsDefault = false
  protected var buttons: MutableList<VInputButton>? = null
  var dialog: ConfirmDialog? = null

  //-------------------------------------------------
  // CONSTRUCTORS
  //-------------------------------------------------

  init {
    dialog!!.className = Styles.NOTIFICATION
    element.setAttribute("hideFocus", "true")
    element.style["outline"] = "0px"
    title = Label()
    val header = HorizontalLayout()
    header.add(title)
    buttons!!.forEach {
      it.className = Styles.NOTIFICATION_BUTTONS
    }
    icon = Icon()
    message = Label()
    message!!.className= Styles.NOTIFICATION_MESSAGE
    val content = VerticalLayout()
    content.add(icon)
    content.add(message)
    val footer = HorizontalLayout()
    buttons!!.forEach {
      footer.add(it)
    }
    dialog!!.add(header)
    dialog!!.add(content)
    dialog!!.add(footer)
    add(dialog)
  }

  /**
   * Creates a new notification component with a popup containing
   * a title, a message, an icon and buttons location.
   */
  constructor(title: String, message: String, listener: ComponentEventListener<ClickEvent<VInputButton>>? = null) : this() {
    setNotificationTitle(title)
    setNotificationMessage(message)
    addNotificationListener(listener!!)
  }
}
