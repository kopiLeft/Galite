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

import org.kopi.galite.base.Utils
import org.kopi.galite.visual.VColor

/**
 * Some vaadin version utilities to obtain images and resources.
 * FIXME: The implementation in other PR
 */
object Utils : Utils() {

  /**
   * Returns the string representation of the given [VColor].
   * @param color The color model.
   * @return The equivalent String color or empty string if the color is `null`.
   */
  fun toString(color: VColor?): String {
    return if (color == null) {
      ""
    } else {
      "rgb(${color.red},${color.green},${color.blue})"
    }
  }

  /**
   * Builds a list of identifiers.
   * @param size The list size.
   * @return The resulting list.
   */
  fun buildIdList(size: Int): List<Int> {
    val list: MutableList<Int> = ArrayList(size)
    for (i in 0 until size) {
      list.add(i)
    }
    return list
  }

  /**
   * Creates a HTML tooltip that wraps a string content.
   *
   * @param content The content (String or html).
   *
   * @return The decorated tooltip
   */
  fun createTooltip(content: String?): String =
          "<div class=\"info\"><i class=\"fa fa-sort-asc\" aria-hidden=\"true\"></i>$content</div>"

  /**
   * Returns the equivalent font awesome icon from the given icon name.
   * @param iconName The model icon name.
   * @return The font awesome icon.
   */
  fun getFontAwesomeIcon(iconName: String?): String? = pngToFontAwesomeMap[iconName]

  /**
   * Returns true if the given two objects are equals.
   * @param o1 The first object.
   * @param o2 The second object.
   * @return true if the given two objects are equals.
   */
  fun equals(o1: Any?, o2: Any?): Boolean {
    return if (o1 == null) {
      o2 == null
    } else if (o2 == null) {
      false
    } else {
      o1 == o2
    }
  }

  /**
   * Returns the complete theme resource URL of the given resource path.
   * @param resourcePath The theme complete resource path.
   * @return The URL of the given theme resource.
   */
  fun getThemeResourceURL(resourcePath: String): String {
    TODO()
  }

  // --------------------------------------------------
  // PRIVATE DATA
  // --------------------------------------------------
  private const val VAADIN_RESOURCE_DIR = "org/kopi/galite/ui/vaadin"
  private const val THEME_DIR = "resource"
  private const val APPLICATION_DIR = "resources"
  private const val RESOURCE_DIR = "org/kopi/galite"
  val UKN_IMAGE: Image = Image("$THEME_DIR/unknown.png")
  private var pngToFontAwesomeMap = mutableMapOf<String, String>()

  init {
    pngToFontAwesomeMap["all"] = "hand-paper-o"
    pngToFontAwesomeMap["block"] = "ban"
    pngToFontAwesomeMap["border"] = "cog"
    pngToFontAwesomeMap["bread_crumb_separator"] = "angle-double-right"
    pngToFontAwesomeMap["break"] = "times-circle"
    pngToFontAwesomeMap["calendar"] = "calendar-o"
    pngToFontAwesomeMap["collapsed"] = "folder-o"
    pngToFontAwesomeMap["collapsed_p"] = "folder-o"
    pngToFontAwesomeMap["copy"] = "files-o"
    pngToFontAwesomeMap["delete"] = "trash-o"
    pngToFontAwesomeMap["desk"] = "desktop"
    pngToFontAwesomeMap["detail"] = "search-plus"
    pngToFontAwesomeMap["detail_view"] = "search"
    pngToFontAwesomeMap["down"] = "angle-double-down"
    pngToFontAwesomeMap["duke"] = "expeditedssl"
    pngToFontAwesomeMap["edit"] = "pencil-square-o"
    pngToFontAwesomeMap["expanded_a"] = "folder-o"
    pngToFontAwesomeMap["expanded"] = "folder-o"
    pngToFontAwesomeMap["expanded_p"] = "folder-o"
    pngToFontAwesomeMap["exportCsv"] = "file-text-o"
    pngToFontAwesomeMap["exportPdf"] = "file-pdf-o"
    pngToFontAwesomeMap["exportXlsx"] = "file-excel-o"
    pngToFontAwesomeMap["foldColumn"] = "folder-o"
    pngToFontAwesomeMap["fold"] = "folder-o"
    pngToFontAwesomeMap["formula"] = "calculator"
    pngToFontAwesomeMap["help"] = "question-circle-o"
    pngToFontAwesomeMap["home"] = "home"
    pngToFontAwesomeMap["insertline"] = "list-ol"
    pngToFontAwesomeMap["insert"] = "pencil-square-o"
    pngToFontAwesomeMap["list"] = "mouse-pointer"
    pngToFontAwesomeMap["loading"] = "spinner"
    pngToFontAwesomeMap["login_img"] = "coffee"
    pngToFontAwesomeMap["mail"] = "envelope-o"
    pngToFontAwesomeMap["menuquery"] = "file-text-o"
    pngToFontAwesomeMap["note"] = "sticky-note-o"
    pngToFontAwesomeMap["nothing"] = "file-o"
    pngToFontAwesomeMap["open"] = "file-text-o"
    pngToFontAwesomeMap["options"] = "cogs"
    pngToFontAwesomeMap["preview"] = "file-text-o"
    pngToFontAwesomeMap["print"] = "print"
    pngToFontAwesomeMap["quit"] = "power-off"
    pngToFontAwesomeMap["save"] = "floppy-o"
    pngToFontAwesomeMap["searchop"] = "search"
    pngToFontAwesomeMap["search"] = "search"
    pngToFontAwesomeMap["serialquery"] = "binoculars"
    pngToFontAwesomeMap["serviceoff"] = "toggle-off"
    pngToFontAwesomeMap["serviceon"] = "toggle-on"
    pngToFontAwesomeMap["store"] = "building-o"
    pngToFontAwesomeMap["suggest"] = "phone"
    pngToFontAwesomeMap["timeStamp"] = "clock-o"
    pngToFontAwesomeMap["tri"] = "sort-alpha-desc"
    pngToFontAwesomeMap["unfoldColumn"] = "folder-open-o"
    pngToFontAwesomeMap["unfold"] = "folder-open-o"
    pngToFontAwesomeMap["up"] = "angle-double-up"
    pngToFontAwesomeMap["add"] = "floppy-o"
    pngToFontAwesomeMap["align_center"] = "align-center"
    pngToFontAwesomeMap["align_justify"] = "align-justify"
    pngToFontAwesomeMap["align_left"] = "align-left"
    pngToFontAwesomeMap["align_right"] = "align-right"
    pngToFontAwesomeMap["apply"] = "cogs"
    pngToFontAwesomeMap["area_chart"] = "area-chart"
    pngToFontAwesomeMap["arrowfirst"] = "step-backward"
    pngToFontAwesomeMap["arrowlast"] = "step-forward"
    pngToFontAwesomeMap["arrowleft"] = "backward"
    pngToFontAwesomeMap["arrowright"] = "forward"
    pngToFontAwesomeMap["article"] = "file-text-o"
    pngToFontAwesomeMap["ask"] = "question-circle"
    pngToFontAwesomeMap["bar_chart"] = "bar-chart"
    pngToFontAwesomeMap["bkup3"] = "exclamation-triangle"
    pngToFontAwesomeMap["bkup"] = "exclamation-triangle"
    pngToFontAwesomeMap["block2"] = "undo"
    pngToFontAwesomeMap["board"] = "cogs"
    pngToFontAwesomeMap["bold"] = "bold"
    pngToFontAwesomeMap["bomb"] = "bomb"
    pngToFontAwesomeMap["bookmark"] = "bookmark"
    pngToFontAwesomeMap["boxarrow"] = "dropbox"
    pngToFontAwesomeMap["bw"] = "step-backward"
    pngToFontAwesomeMap["calculate"] = "calculator"
    pngToFontAwesomeMap["cfolder"] = "folder"
    pngToFontAwesomeMap["chart_view"] = "bar-chart"
    pngToFontAwesomeMap["checkbox"] = "square-o"
    pngToFontAwesomeMap["clip"] = "paperclip"
    pngToFontAwesomeMap["collapsedb"] = "long-arrow-right"
    pngToFontAwesomeMap["collapsed_f"] = "folder"
    pngToFontAwesomeMap["collapsed_t"] = "folder-o"
    pngToFontAwesomeMap["column_chart"] = "bar-chart"
    pngToFontAwesomeMap["combo"] = "lightbulb-o"
    pngToFontAwesomeMap["config"] = "wrench"
    pngToFontAwesomeMap["convert"] = "exchange"
    pngToFontAwesomeMap["cut"] = "scissors"
    pngToFontAwesomeMap["deleteline"] = "list-ol"
    pngToFontAwesomeMap["done"] = "check-square-o"
    pngToFontAwesomeMap["error"] = "minus-circle"
    pngToFontAwesomeMap["expandedb"] = "long-arrow-down"
    pngToFontAwesomeMap["expanded_f"] = "folder-open"
    pngToFontAwesomeMap["expanded_s"] = "folder-open"
    pngToFontAwesomeMap["expanded_t"] = "folder-open"
    pngToFontAwesomeMap["export"] = "cog"
    pngToFontAwesomeMap["fax"] = "fax"
    pngToFontAwesomeMap["fw"] = "step-forward"
    pngToFontAwesomeMap["gifIcon"] = "file-image-o"
    pngToFontAwesomeMap["green"] = "map-o"
    pngToFontAwesomeMap["guide"] = "map-signs"
    pngToFontAwesomeMap["ident"] = "long-arrow-right"
    pngToFontAwesomeMap["index"] = "book"
    pngToFontAwesomeMap["interrupt"] = "stop-circle-o"
    pngToFontAwesomeMap["italic"] = "italic"
    pngToFontAwesomeMap["jpgIcon"] = "picture-o"
    pngToFontAwesomeMap["launch"] = "long-arrow-right"
    pngToFontAwesomeMap["line_chart"] = "line-chart"
    pngToFontAwesomeMap["lock"] = "lock"
    pngToFontAwesomeMap["login"] = "user-circle"
    pngToFontAwesomeMap["moneycheck"] = "money"
    pngToFontAwesomeMap["money"] = "money"
    pngToFontAwesomeMap["notice"] = "lightbulb-o"
    pngToFontAwesomeMap["ofolder"] = "folder-open"
    pngToFontAwesomeMap["pageFirst"] = "step-backward"
    pngToFontAwesomeMap["pageLast"] = "step-forward"
    pngToFontAwesomeMap["pageLeft"] = "backward"
    pngToFontAwesomeMap["pageRight"] = "forward"
    pngToFontAwesomeMap["password"] = "lock"
    pngToFontAwesomeMap["paste"] = "clipboard"
    pngToFontAwesomeMap["phone"] = "phone-square"
    pngToFontAwesomeMap["pie_chart"] = "pie-chart"
    pngToFontAwesomeMap["printoptions"] = "wrench"
    pngToFontAwesomeMap["project"] = "cubes"
    pngToFontAwesomeMap["red"] = "map-o"
    pngToFontAwesomeMap["redo"] = "repeat"
    pngToFontAwesomeMap["refresh"] = "refresh"
    pngToFontAwesomeMap["reload"] = "refresh"
    pngToFontAwesomeMap["report"] = "table"
    pngToFontAwesomeMap["sec"] = "unlock"
    pngToFontAwesomeMap["selected"] = "long-arrow-right"
    pngToFontAwesomeMap["send"] = "paper-plane"
    pngToFontAwesomeMap["sort"] = "sort-numeric-asc"
    pngToFontAwesomeMap["split"] = "chain-broken"
    pngToFontAwesomeMap["standard"] = "pencil"
    pngToFontAwesomeMap["stick"] = "thumb-tack"
    pngToFontAwesomeMap["stop"] = "times-circle-o"
    pngToFontAwesomeMap["todo"] = "magic"
    pngToFontAwesomeMap["top"] = "book"
    pngToFontAwesomeMap["underline"] = "underline"
    pngToFontAwesomeMap["undo"] = "undo"
    pngToFontAwesomeMap["unident"] = "arrow-circle-left"
    pngToFontAwesomeMap["unstick"] = "thumb-tack"
    pngToFontAwesomeMap["update"] = "pencil-square-o"
    pngToFontAwesomeMap["users"] = "users"
    pngToFontAwesomeMap["utils"] = "cogs"
    pngToFontAwesomeMap["validate"] = "check"
    pngToFontAwesomeMap["wait"] = "clock-o"
    pngToFontAwesomeMap["warning"] = "exclamation-triangle"
    pngToFontAwesomeMap["window"] = "angle-right"
    pngToFontAwesomeMap["yellow"] = "map-o"
    pngToFontAwesomeMap["zoomheight"] = "search"
    pngToFontAwesomeMap["zoomminus"] = "search-minus"
    pngToFontAwesomeMap["zoomoptimal"] = "search"
    pngToFontAwesomeMap["zoomplus"] = "search-plus"
    pngToFontAwesomeMap["zoomwidth"] = "search"
  }
}
