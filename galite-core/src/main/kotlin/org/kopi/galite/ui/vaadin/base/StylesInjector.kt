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
package org.kopi.galite.ui.vaadin.base

import org.kopi.galite.form.VConstants
import org.kopi.galite.visual.VColor

/**
 * A centralized way to inject styles in the browser page.
 * TODO : use this class to handle report styles.
 */
class StylesInjector {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  /*
   * Key is the style instance and the object is the style name
   */
  private val styles: MutableMap<Style, String> = mutableMapOf()
  private var injectedStyleId = 0

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  /**
   * Creates (if needed) and inject a CSS style characterized by its align, foreground color
   * and background color.
   *
   * The styles are generated only when needed. if a style does not exists in the styles map,
   * a new style is generated and injected to the current browser page. Otherwise, an existing style
   * will be used and its name will be returned.
   *
   * We note that two styles are equals only if they have the same align,
   * foreground color and background color.
   *
   * @param align The alignment of the style.
   * @param foreground The foreground color of the style.
   * @param background The background color of the style.
   * @return
   */
  fun createAndInjectStyle(align: Int, foreground: VColor?, background: VColor?): String? {
    val style = Style(++injectedStyleId, align, foreground, background)
    if (!styles.containsKey(style)) {
      styles[style] = style.name
      style.inject()
    }
    return styles[style]
  }
  // --------------------------------------------------
  // INNER CLASSES
  // --------------------------------------------------
  /**
   * A style is represented by its align, foreground color and background color.
   */
  internal inner class Style(private val id: Int,
          private val align: Int,
          foreground: VColor?,
          background: VColor?) {
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    /**
     * Apply this style to current browser page.
     */
    fun inject() {
      // apply only when needed.
      if (align != VConstants.ALG_LEFT || foreground != null || background != null) {
        //BackgroundThreadHandler.access(Runnable { TODO: access
        //Page.getCurrent().getStyles().add(toCSS()) TODO
        //}
      }
    }

    /**
     * returns the style name
     * @return
     */
    val name: String
      get() = "injected-style-$id"

    /**
     * Returns the equivalent CSS style of this style object.
     * @return The equivalent CSS style of this style object.
     */
    fun toCSS(): String = buildString {

      append(".$name{")
      if (align != VConstants.ALG_LEFT) {
        append("text-align: $cssAlign !important;")
      }
      if (background != null) {
        append("background-color: " + Utils.toString(background).toString() + " !important;")
      }
      if (foreground != null) {
        append("color: " + Utils.toString(foreground).toString() + " !important;")
      }
      append("}")
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
      return if (obj is Style) {
        ((align == obj.align
                ) && Utils.equals(foreground, obj.foreground)
                && Utils.equals(background, obj.background))
      } else {
        super.equals(obj)
      }
    }

    override fun hashCode(): Int {
      return (align + (if (foreground == null) 0 else foreground.hashCode())
              + if (background == null) 0 else background.hashCode())
    }

    private val foreground = foreground
    private val background = background
  }
}
