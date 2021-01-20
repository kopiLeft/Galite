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
package org.kopi.galite.ui.vaadin.label

import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.shared.Registration
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VAnchorPanel
import org.kopi.galite.ui.vaadin.block.BlockLayout
import org.kopi.galite.ui.vaadin.common.VSpan

/**
 * The field label component.
 */
open class Label : VAnchorPanel(), HasEnabled {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override fun setVisible(visible: Boolean) {
    if (!visible) {
      element.style.set("visibility", "VISIBILITY_HIDDEN")
      if (element.parent != null) {
        element.parent.style.set("visibility", "VISIBILITY_HIDDEN")
      }
    } else {
      element.style.set("visibility", "VISIBILITY_VISIBLE")
      if (element.parent != null) {
        element.parent.style.set("visibility", "VISIBILITY_VISIBLE")
      }
    }
    // now, we try to set the scroll bar visibility
    // cause the block may be fully invisible.
    handleChartLayoutVisibility()
  }

  override fun setWidth(width: String?) {
    if (width == null || width == "") {
      return  // don't override calculated width
    }
    super.setWidth(width)
  }

  /**
   * Adds a click handler to this
   * @param handler The click handler.
   * @return The handler registration.
   */
  fun addClickHandler(handler: (Any) -> Unit): Registration {
    return label!!.addAttachListener(handler)
  }

  /**
   * Sets the label text.
   * @param text The label text.
   */
  override fun setText(text: String?) {
    label!!.text = text
  }

  /**
   * Sets this label to indicate that its field is mandatory.
   * @param mandatory Is the field of this label is mandatory
   */
  fun setMandatory(mandatory: Boolean) {
    if (mandatory) {
      marker!!.classNames.add("mandatory")
      marker!!.text = "*"
    } else {
      marker!!.classNames.remove("mandatory")
      marker!!.text = ""
    }
  }

  /**
   * Sets the info text.
   * @param text The info text.
   */
  open fun setInfoText(text: String?) {
    this.info!!.text = text
  }

  /**
   * Sets the label in auto fill mode.
   */
  fun setHasAction() {
    element.style.set("styleSuffix", "has-action")
    marker!!.style.set("name", "action")
    marker!!.text = "*"
  }

  /**
   * Sets the inner label width.
   * @param width The label width in pixels.
   */
  fun setWidth(width: Int) {
    setWidth("$width px")
  }

  open fun clear() {
    super.removeAll()
    info = null
    label = null
    marker = null
    chartLayout = null
  }

  /**
   * Handles the chart layout caption and scroll bar visibililty.
   */
  protected fun handleChartLayoutVisibility() {
    if (chartLayout != null) {
      chartLayout!!.handleLayoutVisibility()
    }
  }

  override fun isEnabled(): Boolean {
    return isEnabled
  }

  override fun setEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (!enabled) {
      element.style.set("name", "v-disabled")
      label!!.classNames.add("v-disabled")
      info!!.classNames.add("v-disabled")
    } else {
      element.style.remove("v-disabled")
      label!!.classNames.remove("v-disabled")
      info!!.classNames.remove("v-disabled")
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var info: VSpan?
  private var label: VSpan?
  private var marker: VSpan?
  /**
   * Needed to update scroll bar and caption visibility
   * if all fields are invisible.
   */
  private var chartLayout: BlockLayout? = null

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new label widget.
   */
  init {
    label = VSpan()
    label!!.element.setAttribute("name", "label")
    info = VSpan()
    info!!.element.setAttribute("name", "info-text")
    marker = VSpan()
    className = Styles.LABEL
    add(label)
    add(marker)
    add(info)
    // Hide focus outline in Mozilla/Webkit/Opera
    element.style["outline"] = "0px"
    // Hide focus outline in IE 6/7
    element.setAttribute("hideFocus", "true")
  }
}