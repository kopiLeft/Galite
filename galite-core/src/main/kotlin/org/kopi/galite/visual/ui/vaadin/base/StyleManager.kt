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
package org.kopi.galite.visual.ui.vaadin.base

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.visual.VColor

import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.UI

/**
 * A centralized way to apply styles on some component.
 */
class StyleManager(val currentUI: UI) {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  /*
   * Key is the style instance and the object is the style name
   */
  private val stylers = mutableMapOf<HasStyle, Styler>()

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  /**
   * Creates (if needed) and apply a CSS style characterized by its align, foreground color
   * and background color.
   *
   * The styles are generated only when needed. if a style does not exists in the styles map,
   * a new style is generated and applied on a specific component. Otherwise, an existing styler
   * will be used.
   *
   * We note that two styles are equals only if they have the same align,
   * foreground color and background color.
   *
   * @param align The alignment of the style.
   * @param foreground The foreground color of the style.
   * @param background The background color of the style.
   */
  fun createAndApplyStyle(component: HasStyle, align: Int?, foreground: VColor?, background: VColor?) {
    val styler = Styler(component, align, foreground, background)
    val componentStyle = stylers[component]

    // apply only when needed.
    if (componentStyle != styler) {
      if (componentStyle != null
        || (align != null && align != VConstants.ALG_LEFT)
        || foreground != null
        || background != null
      ) {
        access(currentUI) {
          stylers[component] = styler
          styler.style()
        }
      }
    }
  }

  // --------------------------------------------------
  // INNER CLASSES
  // --------------------------------------------------
  /**
   * A styler is represented by its align, foreground color and background color.
   */
  inner class Styler(private val component: HasStyle,
                     private val align: Int?,
                     private val foreground: VColor?,
                     private val background: VColor?) {
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    /**
     * Apply this style to current component.
     */
    fun style() {
      if (align != null && align != VConstants.ALG_LEFT) {
        component.style["text-align"] = "$cssAlign !important"
      }
      if (background != null) {
        component.style["background-color"] = Utils.toString(background) + " !important"
      } else {
        component.style["background-color"] = null
      }
      if (foreground != null) {
        component.style["color"] = Utils.toString(foreground) + " !important"
      } else {
        component.style["color"] = null
      }
    }

    /**
     * Return the CSS align of the alignment constants.
     * @return The CSS align of the alignment constants.
     */
    protected val cssAlign: String
      get() = when (align) {
        VConstants.ALG_LEFT -> "left"
        VConstants.ALG_RIGHT -> "right"
        VConstants.ALG_CENTER -> "center"
        else -> "left" // cell are left aligned by default;
      }

    override fun equals(obj: Any?): Boolean {
      return when {
        this === obj -> {
          true
        }
        obj is Styler -> {
          component == obj.component
                  && (align == obj.align)
                  && Utils.equals(foreground, obj.foreground)
                  && Utils.equals(background, obj.background)
        }
        else -> {
          super.equals(obj)
        }
      }
    }

    override fun hashCode(): Int = align ?: 0 + (foreground?.hashCode() ?: 0) + (background?.hashCode() ?: 0)
  }
}
