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

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VAnchorPanel
import org.kopi.galite.ui.vaadin.common.VSpan

import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.dependency.CssImport

/**
 * The label server component.
 */
@CssImport("./styles/galite/label.css")
open class Label : VAnchorPanel(), HasEnabled {

  /**
   * The info text used to display search operator.
   */
  open var infoText: String? = ""
    set(value) {
      info.text = value
      field = value
    }

  /**
   * Sets the label in auto fill mode.
   * @return The label can execute field action trigger?
   */
  var hasAction = false
    set(value) {
      if(value) {
        setHasAction()
      }
      field = value
    }

  /**
   * Is it a sortable label ?
   */
  var sortable = false

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var info: VSpan
  private var label: VSpan
  private var marker: VSpan

  init {
    className = Styles.LABEL
    label = VSpan()
    label.className = "label"
    info = VSpan()
    info.className = "info-text"
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

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /*override fun setVisible(visible: Boolean) { TODO
    if (!visible) {
      element.style.setVisibility(Visibility.HIDDEN)
      if (DOM.getParent(element) != null) {
        DOM.getParent(element).getStyle().setVisibility(Visibility.HIDDEN)
      }
    } else {
      element.style.setVisibility(Visibility.VISIBLE)
      if (DOM.getParent(element) != null) {
        DOM.getParent(element).getStyle().setVisibility(Visibility.VISIBLE)
      }
    }
    // now, we try to set the scroll bar visibility
    // cause the block may be fully invisible.
    handleChartLayoutVisiblility()
  }*/

  override fun setWidth(width: String?) {
    if (width == null || width == "") {
      return  // don't override calculated width
    }
    super.setWidth(width)
  }

  /**
   * Sets the label text.
   * @param text The label text.
   */
  override fun setText(text: String?) {
    label.text = text
  }

  /**
   * Sets this label to indicate that its field is mandatory.
   *
   * @return Is the field of this label is mandatory
   */
  var mandatory: Boolean = false
    set(value) {
      if (value) {
        this.marker.className = "mandatory"
        this.marker.text = "*"
      } else {
        this.marker.removeClassName("mandatory")
        this.marker.text = ""
      }
      field = value
    }

  /**
   * Sets the label in auto fill mode.
   */
  open fun setHasAction() {
    addStyleDependentName("has-action")
    marker.className = "action"
    marker.text = "*"
  }

  /*override fun setEnabled(enabled: Boolean) { TODO
    enabled = enabled
    if (!enabled) {
      addStyleName("v-disabled")
      label.addStyleName("v-disabled")
      info.addStyleName("v-disabled")
    } else {
      removeStyleName("v-disabled")
      label.removeStyleName("v-disabled")
      info.removeStyleName("v-disabled")
    }
  }*/

  /*protected open fun onLoad() { TODO
    super.onLoad()
    Scheduler.get().scheduleFinally(object : ScheduledCommand() {
      fun execute() {
        // The chart layout can be null if the label belongs to a simple block
        chartLayout = WidgetUtils.getParent(this@VLabel, VChartBlockLayout::class.java)
      }
    })
  }*/

  /*open fun clear() { TODO
    super.clear()
    info = null
    label = null
    marker = null
    chartLayout = null
  }*/

  /**
   * Handles the chart layout caption and scroll bar visibililty.
   */
  /*protected open fun handleChartLayoutVisiblility() { TODO
    if (chartLayout != null) {
      //chartLayout.maybeHideOrShowScrollBar();
      chartLayout.handleLayoutVisibility()
    }
  }*/

  fun addStyleDependentName(dependentClassName: String) {
    if(className != null) {
      element.classList.add("${Styles.LABEL}-$dependentClassName")
    }
  }
}
