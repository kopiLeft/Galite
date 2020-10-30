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

package org.kopi.galite.ui.base

import org.kopi.galite.base.Image
import org.kopi.galite.visual.VColor
import java.util.ArrayList
import java.util.Hashtable

/**
 * Some vaadin version utilities to obtain images and resources.
 */
object Utils : org.kopi.galite.base.Utils {
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  /**
   * Returns image from theme
   * @param img Must be an image from resource theme path separator is "/"
   * @return An Image or null if not found.
   */
  fun getImage(image: String): Image? {
    var img: Image? = cache[image]
    if (img == null) {
      img = getImageImpl(image)
      cache[image] = img
    }
    return img
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource directory path separator is "/"
   * @return An Image or null if not found.
   */
  private fun getImageImpl(img: String): Image {
    var icon: Image? = getDefaultImage(img)
    if (icon == null) {
      icon = getKopiResourceImage(img)
    }
    if (icon == null) {
      icon = getApplicationImage(img)
    }
    if (icon == null) {
      System.err.println("Utils ==> cant load: $img")
      return UKN_IMAGE
    }
    return icon
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource directory path separator is "/"
   * @return An imageIcon or null if not found
   */
  fun getDefaultImage(img: String): Image? {
    return getImageFromResource(VAADIN_RESOURCE_DIR, img)
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource application directory
   * path separator is "/"
   * @return An Image or null if not found
   */
  fun getApplicationImage(img: String): Image? {
    return getImageFromResource(APPLICATION_DIR, img)
  }

  /**
   * Returns an image from kopi resources.
   * @param img Must be an image from resource application directory
   * path separator is "/"
   * @return An Image or null if not found
   */
  fun getGaliteResourceImage(img: String): Image? {
    return getImageFromResource(RESOURCE_DIR, img)
  }

  /**
   * Return image from resources or null if not found.
   * @param directory The image directory.
   * @param name The image name.
   * @return An Image or null if not found
   */
  fun getImageFromResource(directory: String, name: String): Image? {
    return if (Utils::class.java.classLoader.getResource("$directory/$name") != null) {
      org.kopi.galite.base.Image(ClassResource("/$directory/$name"))
    } else null
  }

  /**
   * Returns the corresponding CSS color of a given [VColor].
   * @param color The color model.
   * @return The CSS color.
   */
  fun getCSSColor(color: VColor?): String {
    return if (color == null) {
      "inherit;"
    } else {
      "rgb(" + color.getRed().toString() + "," + color.getGreen().toString() + "," + color.getBlue().toString() + ") ;"
    }
  }

  /**
   * Returns the string representation of the given [VColor].
   * @param color The color model.
   * @return The equivalent String color or empty string if the color is `null`.
   */
  fun toString(color: VColor?): String {
    return if (color == null) {
      ""
    } else {
      "rgb(" + color.getRed().toString() + "," + color.getGreen().toString() + "," + color.getBlue().toString() + ")"
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
   * @param content The content (String or html).
   * @return The decoredted tooltip
   */
  fun createTooltip(content: String): String {
    return "<div class=\"info\"><i class=\"fa fa-sort-asc\" aria-hidden=\"true\"></i>$content</div>"
  }

  /**
   * Returns the equivalent font awesome icon from the given icon name.
   * @param iconName The model icon name.
   * @return The font awesome icon.
   */
  fun getFontAwesomeIcon(iconName: String): String? {
    return (pngToFontAwesomeMap as HashMap<String, String>)!![iconName]
  }

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
    return "./VAADIN/themes/" + UI.getCurrent().getTheme().toString() + "/" + resourcePath
  }

  // --------------------------------------------------
  // PRIVATE DATA
  // --------------------------------------------------
  private const val VAADIN_RESOURCE_DIR = "org/kopi/vkopi/lib/ui/vaadin/resource"
  private const val THEME_DIR = "resource"
  private const val APPLICATION_DIR = "resources"
  private const val RESOURCE_DIR = "org/kopi/vkopi/lib/resource"
  val UKN_IMAGE: Image = Image(ThemeResource(THEME_DIR + "/" + "unknown.png"))
  private val cache: Hashtable<String, Image?> = Hashtable<String, Image?>()
  private var pngToFontAwesomeMap: MutableMap<String, String>? = null

  init {
    pngToFontAwesomeMap  = HashMap()
    (pngToFontAwesomeMap as HashMap<String, String>)["all"] = "hand-paper-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["block"] = "ban"
    (pngToFontAwesomeMap as HashMap<String, String>)["border"] = "cog"
    (pngToFontAwesomeMap as HashMap<String, String>)["bread_crumb_separator"] = "angle-double-right"
    (pngToFontAwesomeMap as HashMap<String, String>)["break"] = "times-circle"
    (pngToFontAwesomeMap as HashMap<String, String>)["calendar"] = "calendar-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["collapsed"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["collapsed_p"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["copy"] = "files-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["delete"] = "trash-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["desk"] = "desktop"
    (pngToFontAwesomeMap as HashMap<String, String>)["detail"] = "search-plus"
    (pngToFontAwesomeMap as HashMap<String, String>)["detail_view"] = "search"
    (pngToFontAwesomeMap as HashMap<String, String>)["down"] = "angle-double-down"
    (pngToFontAwesomeMap as HashMap<String, String>)["duke"] = "expeditedssl"
    (pngToFontAwesomeMap as HashMap<String, String>)["edit"] = "pencil-square-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["expanded_a"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["expanded"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["expanded_p"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["exportCsv"] = "file-text-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["exportPdf"] = "file-pdf-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["exportXlsx"] = "file-excel-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["foldColumn"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["fold"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["formula"] = "calculator"
    (pngToFontAwesomeMap as HashMap<String, String>)["help"] = "question-circle-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["home"] = "home"
    (pngToFontAwesomeMap as HashMap<String, String>)["insertline"] = "list-ol"
    (pngToFontAwesomeMap as HashMap<String, String>)["insert"] = "pencil-square-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["list"] = "mouse-pointer"
    (pngToFontAwesomeMap as HashMap<String, String>)["loading"] = "spinner"
    (pngToFontAwesomeMap as HashMap<String, String>)["login_img"] = "coffee"
    (pngToFontAwesomeMap as HashMap<String, String>)["mail"] = "envelope-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["menuquery"] = "file-text-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["note"] = "sticky-note-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["nothing"] = "file-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["open"] = "file-text-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["options"] = "cogs"
    (pngToFontAwesomeMap as HashMap<String, String>)["preview"] = "file-text-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["print"] = "print"
    (pngToFontAwesomeMap as HashMap<String, String>)["quit"] = "power-off"
    (pngToFontAwesomeMap as HashMap<String, String>)["save"] = "floppy-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["searchop"] = "search"
    (pngToFontAwesomeMap as HashMap<String, String>)["search"] = "search"
    (pngToFontAwesomeMap as HashMap<String, String>)["serialquery"] = "binoculars"
    (pngToFontAwesomeMap as HashMap<String, String>)["serviceoff"] = "toggle-off"
    (pngToFontAwesomeMap as HashMap<String, String>)["serviceon"] = "toggle-on"
    (pngToFontAwesomeMap as HashMap<String, String>)["store"] = "building-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["suggest"] = "phone"
    (pngToFontAwesomeMap as HashMap<String, String>)["timeStamp"] = "clock-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["tri"] = "sort-alpha-desc"
    (pngToFontAwesomeMap as HashMap<String, String>)["unfoldColumn"] = "folder-open-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["unfold"] = "folder-open-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["up"] = "angle-double-up"
    (pngToFontAwesomeMap as HashMap<String, String>)["add"] = "floppy-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["align_center"] = "align-center"
    (pngToFontAwesomeMap as HashMap<String, String>)["align_justify"] = "align-justify"
    (pngToFontAwesomeMap as HashMap<String, String>)["align_left"] = "align-left"
    (pngToFontAwesomeMap as HashMap<String, String>)["align_right"] = "align-right"
    (pngToFontAwesomeMap as HashMap<String, String>)["apply"] = "cogs"
    (pngToFontAwesomeMap as HashMap<String, String>)["area_chart"] = "area-chart"
    (pngToFontAwesomeMap as HashMap<String, String>)["arrowfirst"] = "step-backward"
    (pngToFontAwesomeMap as HashMap<String, String>)["arrowlast"] = "step-forward"
    (pngToFontAwesomeMap as HashMap<String, String>)["arrowleft"] = "backward"
    (pngToFontAwesomeMap as HashMap<String, String>)["arrowright"] = "forward"
    (pngToFontAwesomeMap as HashMap<String, String>)["article"] = "file-text-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["ask"] = "question-circle"
    (pngToFontAwesomeMap as HashMap<String, String>)["bar_chart"] = "bar-chart"
    (pngToFontAwesomeMap as HashMap<String, String>)["bkup3"] = "exclamation-triangle"
    (pngToFontAwesomeMap as HashMap<String, String>)["bkup"] = "exclamation-triangle"
    (pngToFontAwesomeMap as HashMap<String, String>)["block2"] = "undo"
    (pngToFontAwesomeMap as HashMap<String, String>)["board"] = "cogs"
    (pngToFontAwesomeMap as HashMap<String, String>)["bold"] = "bold"
    (pngToFontAwesomeMap as HashMap<String, String>)["bomb"] = "bomb"
    (pngToFontAwesomeMap as HashMap<String, String>)["bookmark"] = "bookmark"
    (pngToFontAwesomeMap as HashMap<String, String>)["boxarrow"] = "dropbox"
    (pngToFontAwesomeMap as HashMap<String, String>)["bw"] = "step-backward"
    (pngToFontAwesomeMap as HashMap<String, String>)["calculate"] = "calculator"
    (pngToFontAwesomeMap as HashMap<String, String>)["cfolder"] = "folder"
    (pngToFontAwesomeMap as HashMap<String, String>)["chart_view"] = "bar-chart"
    (pngToFontAwesomeMap as HashMap<String, String>)["checkbox"] = "square-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["clip"] = "paperclip"
    (pngToFontAwesomeMap as HashMap<String, String>)["collapsedb"] = "long-arrow-right"
    (pngToFontAwesomeMap as HashMap<String, String>)["collapsed_f"] = "folder"
    (pngToFontAwesomeMap as HashMap<String, String>)["collapsed_t"] = "folder-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["column_chart"] = "bar-chart"
    (pngToFontAwesomeMap as HashMap<String, String>)["combo"] = "lightbulb-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["config"] = "wrench"
    (pngToFontAwesomeMap as HashMap<String, String>)["convert"] = "exchange"
    (pngToFontAwesomeMap as HashMap<String, String>)["cut"] = "scissors"
    (pngToFontAwesomeMap as HashMap<String, String>)["deleteline"] = "list-ol"
    (pngToFontAwesomeMap as HashMap<String, String>)["done"] = "check-square-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["error"] = "minus-circle"
    (pngToFontAwesomeMap as HashMap<String, String>)["expandedb"] = "long-arrow-down"
    (pngToFontAwesomeMap as HashMap<String, String>)["expanded_f"] = "folder-open"
    (pngToFontAwesomeMap as HashMap<String, String>)["expanded_s"] = "folder-open"
    (pngToFontAwesomeMap as HashMap<String, String>)["expanded_t"] = "folder-open"
    (pngToFontAwesomeMap as HashMap<String, String>)["export"] = "cog"
    (pngToFontAwesomeMap as HashMap<String, String>)["fax"] = "fax"
    (pngToFontAwesomeMap as HashMap<String, String>)["fw"] = "step-forward"
    (pngToFontAwesomeMap as HashMap<String, String>)["gifIcon"] = "file-image-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["green"] = "map-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["guide"] = "map-signs"
    (pngToFontAwesomeMap as HashMap<String, String>)["ident"] = "long-arrow-right"
    (pngToFontAwesomeMap as HashMap<String, String>)["index"] = "book"
    (pngToFontAwesomeMap as HashMap<String, String>)["interrupt"] = "stop-circle-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["italic"] = "italic"
    (pngToFontAwesomeMap as HashMap<String, String>)["jpgIcon"] = "picture-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["launch"] = "long-arrow-right"
    (pngToFontAwesomeMap as HashMap<String, String>)["line_chart"] = "line-chart"
    (pngToFontAwesomeMap as HashMap<String, String>)["lock"] = "lock"
    (pngToFontAwesomeMap as HashMap<String, String>)["login"] = "user-circle"
    (pngToFontAwesomeMap as HashMap<String, String>)["moneycheck"] = "money"
    (pngToFontAwesomeMap as HashMap<String, String>)["money"] = "money"
    (pngToFontAwesomeMap as HashMap<String, String>)["notice"] = "lightbulb-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["ofolder"] = "folder-open"
    (pngToFontAwesomeMap as HashMap<String, String>)["pageFirst"] = "step-backward"
    (pngToFontAwesomeMap as HashMap<String, String>)["pageLast"] = "step-forward"
    (pngToFontAwesomeMap as HashMap<String, String>)["pageLeft"] = "backward"
    (pngToFontAwesomeMap as HashMap<String, String>)["pageRight"] = "forward"
    (pngToFontAwesomeMap as HashMap<String, String>)["password"] = "lock"
    (pngToFontAwesomeMap as HashMap<String, String>)["paste"] = "clipboard"
    (pngToFontAwesomeMap as HashMap<String, String>)["phone"] = "phone-square"
    (pngToFontAwesomeMap as HashMap<String, String>)["pie_chart"] = "pie-chart"
    (pngToFontAwesomeMap as HashMap<String, String>)["printoptions"] = "wrench"
    (pngToFontAwesomeMap as HashMap<String, String>)["project"] = "cubes"
    (pngToFontAwesomeMap as HashMap<String, String>)["red"] = "map-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["redo"] = "repeat"
    (pngToFontAwesomeMap as HashMap<String, String>)["refresh"] = "refresh"
    (pngToFontAwesomeMap as HashMap<String, String>)["reload"] = "refresh"
    (pngToFontAwesomeMap as HashMap<String, String>)["report"] = "table"
    (pngToFontAwesomeMap as HashMap<String, String>)["sec"] = "unlock"
    (pngToFontAwesomeMap as HashMap<String, String>)["selected"] = "long-arrow-right"
    (pngToFontAwesomeMap as HashMap<String, String>)["send"] = "paper-plane"
    (pngToFontAwesomeMap as HashMap<String, String>)["sort"] = "sort-numeric-asc"
    (pngToFontAwesomeMap as HashMap<String, String>)["split"] = "chain-broken"
    (pngToFontAwesomeMap as HashMap<String, String>)["standard"] = "pencil"
    (pngToFontAwesomeMap as HashMap<String, String>)["stick"] = "thumb-tack"
    (pngToFontAwesomeMap as HashMap<String, String>)["stop"] = "times-circle-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["todo"] = "magic"
    (pngToFontAwesomeMap as HashMap<String, String>)["top"] = "book"
    (pngToFontAwesomeMap as HashMap<String, String>)["underline"] = "underline"
    (pngToFontAwesomeMap as HashMap<String, String>)["undo"] = "undo"
    (pngToFontAwesomeMap as HashMap<String, String>)["unident"] = "arrow-circle-left"
    (pngToFontAwesomeMap as HashMap<String, String>)["unstick"] = "thumb-tack"
    (pngToFontAwesomeMap as HashMap<String, String>)["update"] = "pencil-square-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["users"] = "users"
    (pngToFontAwesomeMap as HashMap<String, String>)["utils"] = "cogs"
    (pngToFontAwesomeMap as HashMap<String, String>)["validate"] = "check"
    (pngToFontAwesomeMap as HashMap<String, String>)["wait"] = "clock-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["warning"] = "exclamation-triangle"
    (pngToFontAwesomeMap as HashMap<String, String>)["window"] = "angle-right"
    (pngToFontAwesomeMap as HashMap<String, String>)["yellow"] = "map-o"
    (pngToFontAwesomeMap as HashMap<String, String>)["zoomheight"] = "search"
    (pngToFontAwesomeMap as HashMap<String, String>)["zoomminus"] = "search-minus"
    (pngToFontAwesomeMap as HashMap<String, String>)["zoomoptimal"] = "search"
    (pngToFontAwesomeMap as HashMap<String, String>)["zoomplus"] = "search-plus"
    (pngToFontAwesomeMap as HashMap<String, String>)["zoomwidth"] = "search"
  }
}