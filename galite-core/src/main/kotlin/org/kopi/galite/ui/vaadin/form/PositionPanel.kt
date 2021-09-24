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
package org.kopi.galite.ui.vaadin.form

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import org.kopi.galite.ui.vaadin.event.PositionPanelListener
import org.kopi.galite.ui.vaadin.label.Label

/**
 * A position panel widget used to fetch a form records.
 */
class PositionPanel : HorizontalLayout() {


  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val popup = Dialog()
  private var listeners: MutableList<PositionPanelListener>?
  private val first: Button
  private val last: Button
  private val left: Button
  private val right: Button
  private val info: TextField
  private val totalInfo: Label
  private val slash: Label
  private var current = 0
  private var total = 0
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new `VPositionPanel` widget.
   */
  init {
    // setStyleName(Styles.POSITION_PANEL) TODO
    isSpacing = true // TODO
    listeners = ArrayList<PositionPanelListener>()
    first = Button()
    last = Button()
    left = Button()
    right = Button()
    info = TextField()
    totalInfo = Label()
    slash = Label()
    totalInfo.className = "records-totalInfo"
    info.className = "records-info"
    slash.className = "slash"
    add(first)
    add(left)
    add(info)
    add(slash)
    add(totalInfo)
    add(right)
    add(last)
    /*setCellHorizontalAlignment(first, HasHorizontalAlignment.ALIGN_CENTER) TODO
    setCellVerticalAlignment(first, HasVerticalAlignment.ALIGN_MIDDLE)
    setCellHorizontalAlignment(last, HasHorizontalAlignment.ALIGN_CENTER)
    setCellVerticalAlignment(last, HasVerticalAlignment.ALIGN_MIDDLE)
    setCellHorizontalAlignment(left, HasHorizontalAlignment.ALIGN_CENTER)
    setCellVerticalAlignment(left, HasVerticalAlignment.ALIGN_MIDDLE)
    setCellHorizontalAlignment(right, HasHorizontalAlignment.ALIGN_CENTER)
    setCellVerticalAlignment(right, HasVerticalAlignment.ALIGN_MIDDLE)
    setCellHorizontalAlignment(info, HasHorizontalAlignment.ALIGN_CENTER)
    setCellVerticalAlignment(info, HasVerticalAlignment.ALIGN_MIDDLE)
    setCellHorizontalAlignment(totalInfo, HasHorizontalAlignment.ALIGN_CENTER)
    setCellVerticalAlignment(totalInfo, HasVerticalAlignment.ALIGN_MIDDLE)
    first.addClickHandler(this)
    last.addClickHandler(this)
    left.addClickHandler(this)
    right.addClickHandler(this)
    info.addKeyPressHandler(object : KeyPressHandler() {
      fun onKeyPress(event: KeyPressEvent) {
        if (Character.isLetter(event.getCharCode()) || event.getNativeEvent().getKeyCode() === KeyCodes.KEY_SPACE) {
          (event.getSource() as TextField).cancelKey()
        } else if (event.getNativeEvent().getKeyCode() === KeyCodes.KEY_ENTER && info.value != null) {
          val infoVlue: Int = Integer.valueOf(info.value)
          if (infoVlue in 1..total) {
            setCurrent(infoVlue)
            fireGotoPosition(infoVlue)
            setButtonsStyleName()
          } else {
            setCurrent(current)
          }
        }
      }
    })*/
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  /**
   * Sets the current position of the position panel.
   * @param current The current record.
   * @param total The total records.
   */
  fun setPosition(current: Int, total: Int) {
    this.current = current
    this.total = total
    setCurrent(current)
    totalInfo.text = "" + total
    slash.text = "/"
    setButtonIcon(left, VaadinIcon.ANGLE_LEFT)
    setButtonIcon(first, VaadinIcon.ANGLE_DOUBLE_LEFT)
    setButtonIcon(right, VaadinIcon.ANGLE_RIGHT)
    setButtonIcon(last, VaadinIcon.ANGLE_DOUBLE_RIGHT)
    left.isEnabled = current > 1
    first.isEnabled = current > 1
    right.isEnabled = current < total
    last.isEnabled = current < total
  }

  fun setStyleName(button: Button) {
    if (!button.isEnabled) {
      button.classNames.remove("v-enabled-button")
      button.classNames.add("v-disabled-button")
    } else {
      button.classNames.remove("v-disabled-button")
      button.classNames.add("v-enabled-button")
    }
  }

  fun setButtonsStyleName() {
    setStyleName(first)
    setStyleName(last)
    setStyleName(left)
    setStyleName(right)
  }

  /**
   * Sets the current position of the position panel.
   * @param current The current record.
   */
  fun setCurrent(current: Int) {
    this.current = current
    info.setValue(current.toString())
  }

  /**
   * Creates the buttons icons.
   */
  protected fun setButtonIcon(button: Button, icon: VaadinIcon) {
    button.icon = Icon(icon)
    button.className = "button"
    setButtonsStyleName()
  }

  /**
   * Registers a new position panel listener.
   * @param l The listener object.
   */
  fun addPositionPanelListener(l: PositionPanelListener) {
    listeners!!.add(l)
  }

  /**
   * Removes a position panel listener.
   * @param l The listener object.
   */
  fun removePositionPanelListener(l: PositionPanelListener) {
    listeners!!.remove(l)
  }

  /**
   * Requests to go to the next position.
   */
  protected fun fireGotoNextPosition() {
    for (l in listeners!!) {
      if (l != null) {
        l.gotoNextPosition()
      }
    }
  }

  /**
   * Requests to go to the previous position.
   */
  protected fun fireGotoPrevPosition() {
    for (l in listeners!!) {
      if (l != null) {
        l.gotoPrevPosition()
      }
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected fun fireGotoLastPosition() {
    for (l in listeners!!) {
      if (l != null) {
        l.gotoLastPosition()
      }
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected fun fireGotoFirstPosition() {
    for (l in listeners!!) {
      if (l != null) {
        l.gotoFirstPosition()
      }
    }
  }

  /**
   * Requests to go to the specified position.
   * @param posno The position number.
   */
  protected fun fireGotoPosition(posno: Int) {
    for (l in listeners!!) {
      if (l != null) {
        l.gotoPosition(posno)
      }
    }
  }

  fun onClick(event: ClickEvent<HorizontalLayout>) {
    when {
      event.source === first -> {
        // go to the first record
        fireGotoFirstPosition()
        setButtonsStyleName()
      }
      event.source == last -> {
        fireGotoLastPosition()
        setButtonsStyleName()
      }
      event.source == left -> {
        fireGotoPrevPosition()
        setButtonsStyleName()
      }
      event.source == right -> {
        fireGotoNextPosition()
        setButtonsStyleName()
      }
    }
  }

  fun show() {
    setButtonsStyleName()
    this.isVisible = true
    popup.add(this)

    popup.open()
  }

  fun hide() {
    popup.close()
  }
}
