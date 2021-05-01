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

import com.flowingcode.vaadin.addons.ironicons.AvIcons
import com.flowingcode.vaadin.addons.ironicons.DeviceIcons
import com.flowingcode.vaadin.addons.ironicons.EditorIcons
import com.flowingcode.vaadin.addons.ironicons.FileIcons
import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.flowingcode.vaadin.addons.ironicons.MapsIcons
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.icon.VaadinIcon

import java.util.Hashtable

import org.kopi.galite.base.Utils
import org.kopi.galite.visual.VColor

/**
 * Some vaadin version utilities to obtain images and resources.
 */
@CssImport("./styles/galite/Report.css")
object Utils : Utils() {

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  /**
   * Returns image from theme
   * @param img Must be an image from resource theme path separator is "/"
   * @return An Image or null if not found.
   */
  fun getImage(image: String): Image {
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
  fun getKopiResourceImage(img: String): Image? {
    return getImageFromResource(RESOURCE_DIR, img)
  }

  /**
   * Return image from resources or null if not found.
   * @param directory The image directory.
   * @param name The image name.
   * @return An Image or null if not found
   */
  fun getImageFromResource(directory: String, name: String): Image? {
    TODO()
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
      "rgb(" + color.red.toString() + "," + color.green.toString() + "," + color.blue.toString() + ") ;"
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
      "rgb(" + color.red.toString() + "," + color.green.toString() + "," + color.blue.toString() + ")"
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
  fun createTooltip(content: String?): String {
    return "<div class=\"info\"><i class=\"fa fa-sort-asc\" aria-hidden=\"true\"></i>$content</div>"
  }

  /**
   * Returns the equivalent icon from the given icon name.
   * @param iconName The model icon name.
   * @return The equivalent icon.
   */
  fun getFontAwesomeIcon(iconName: String?): Any? {
    return pngToFontAwesomeMap[iconName]
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
    TODO()
  }

  // --------------------------------------------------
  // PRIVATE DATA
  // --------------------------------------------------
  private const val VAADIN_RESOURCE_DIR = "org/kopi/galite/ui/vaadin"
  private const val THEME_DIR = "resource"
  private const val APPLICATION_DIR = "resources"
  private const val RESOURCE_DIR = "org/kopi/galite"
  val UKN_IMAGE = Image("$THEME_DIR/unknown.png")
  private val cache = Hashtable<String, Image>()
  private var pngToFontAwesomeMap = mutableMapOf<String, Any>()

  init {
    pngToFontAwesomeMap["all"] = VaadinIcon.HAND
    pngToFontAwesomeMap["block"] = VaadinIcon.BAN
    pngToFontAwesomeMap["border"] = VaadinIcon.COG
    pngToFontAwesomeMap["bread_crumb_separator"] = VaadinIcon.ANGLE_DOUBLE_RIGHT
    pngToFontAwesomeMap["break"] = VaadinIcon.CLOSE_CIRCLE
    pngToFontAwesomeMap["calendar"] = VaadinIcon.CALENDAR_O
    pngToFontAwesomeMap["collapsed"] = VaadinIcon.FOLDER_O
    pngToFontAwesomeMap["collapsed_p"] = VaadinIcon.FOLDER_O
    pngToFontAwesomeMap["copy"] = VaadinIcon.COPY
    pngToFontAwesomeMap["delete"] = VaadinIcon.TRASH
    pngToFontAwesomeMap["desk"] = VaadinIcon.DESKTOP
    pngToFontAwesomeMap["detail"] = VaadinIcon.SEARCH_PLUS
    pngToFontAwesomeMap["detail_view"] = VaadinIcon.SEARCH
    pngToFontAwesomeMap["down"] = VaadinIcon.ANGLE_DOUBLE_DOWN
    pngToFontAwesomeMap["duke"] = VaadinIcon.LOCK
    pngToFontAwesomeMap["edit"] = VaadinIcon.EDIT
    pngToFontAwesomeMap["expanded_a"] = VaadinIcon.FOLDER_OPEN_O
    pngToFontAwesomeMap["expanded"] = VaadinIcon.FOLDER_OPEN_O
    pngToFontAwesomeMap["expanded_p"] = VaadinIcon.FOLDER_OPEN_O
    pngToFontAwesomeMap["exportCsv"] = VaadinIcon.FILE_TEXT_O
    pngToFontAwesomeMap["exportPdf"] = FileIcons.PDF
    pngToFontAwesomeMap["exportXlsx"] = FileIcons.EXCEL
    pngToFontAwesomeMap["foldColumn"] = VaadinIcon.FOLDER_O
    pngToFontAwesomeMap["fold"] = VaadinIcon.FOLDER_O
    pngToFontAwesomeMap["formula"] = VaadinIcon.CALC
    pngToFontAwesomeMap["help"] = VaadinIcon.QUESTION_CIRCLE_O
    pngToFontAwesomeMap["home"] = VaadinIcon.HOME
    pngToFontAwesomeMap["insertline"] = VaadinIcon.LIST_OL
    pngToFontAwesomeMap["insert"] = VaadinIcon.INSERT
    pngToFontAwesomeMap["list"] = VaadinIcon.LIST
    pngToFontAwesomeMap["loading"] = VaadinIcon.SPINNER
    pngToFontAwesomeMap["login_img"] = VaadinIcon.COFFEE
    pngToFontAwesomeMap["mail"] = VaadinIcon.ENVELOPE_O
    pngToFontAwesomeMap["menuquery"] = VaadinIcon.FILE_TEXT_O
    pngToFontAwesomeMap["note"] = VaadinIcon.NOTEBOOK
    pngToFontAwesomeMap["nothing"] = VaadinIcon.FILE_O
    pngToFontAwesomeMap["open"] = VaadinIcon.FILE_TEXT_O
    pngToFontAwesomeMap["options"] = VaadinIcon.COG
    pngToFontAwesomeMap["preview"] = VaadinIcon.FILE_TEXT_O
    pngToFontAwesomeMap["print"] = VaadinIcon.PRINT
    pngToFontAwesomeMap["quit"] = VaadinIcon.POWER_OFF
    pngToFontAwesomeMap["save"] = IronIcons.SAVE
    pngToFontAwesomeMap["searchop"] = VaadinIcon.SEARCH
    pngToFontAwesomeMap["search"] = VaadinIcon.FILE_SEARCH
    pngToFontAwesomeMap["serialquery"] = VaadinIcon.RECORDS
    pngToFontAwesomeMap["serviceoff"] = DeviceIcons.SIGNAL_CELLULAR_OFF
    pngToFontAwesomeMap["serviceon"] = DeviceIcons.SIGNAL_CELLULAR_4_BAR
    pngToFontAwesomeMap["store"] = VaadinIcon.BUILDING_O
    pngToFontAwesomeMap["suggest"] = VaadinIcon.PHONE
    pngToFontAwesomeMap["timeStamp"] = VaadinIcon.CLOCK
    pngToFontAwesomeMap["tri"] = VaadinIcon.SORT
    pngToFontAwesomeMap["unfoldColumn"] = VaadinIcon.FOLDER_OPEN_O
    pngToFontAwesomeMap["unfold"] = VaadinIcon.FOLDER_OPEN_O
    pngToFontAwesomeMap["up"] = VaadinIcon.ANGLE_DOUBLE_UP
    pngToFontAwesomeMap["add"] = VaadinIcon.FILE_ADD
    pngToFontAwesomeMap["align_center"] = VaadinIcon.ALIGN_CENTER
    pngToFontAwesomeMap["align_justify"] = VaadinIcon.ALIGN_JUSTIFY
    pngToFontAwesomeMap["align_left"] = VaadinIcon.ALIGN_LEFT
    pngToFontAwesomeMap["align_right"] = VaadinIcon.ALIGN_RIGHT
    pngToFontAwesomeMap["apply"] = VaadinIcon.COGS
    pngToFontAwesomeMap["area_chart"] = VaadinIcon.SPLINE_AREA_CHART
    pngToFontAwesomeMap["arrowfirst"] = VaadinIcon.STEP_BACKWARD
    pngToFontAwesomeMap["arrowlast"] = VaadinIcon.STEP_FORWARD
    pngToFontAwesomeMap["arrowleft"] = VaadinIcon.BACKWARDS
    pngToFontAwesomeMap["arrowright"] = VaadinIcon.FORWARD
    pngToFontAwesomeMap["article"] = VaadinIcon.FILE_TEXT_O
    pngToFontAwesomeMap["ask"] = VaadinIcon.QUESTION_CIRCLE_O
    pngToFontAwesomeMap["bar_chart"] = VaadinIcon.BAR_CHART
    pngToFontAwesomeMap["bkup3"] = VaadinIcon.WARNING
    pngToFontAwesomeMap["bkup"] = VaadinIcon.WARNING
    pngToFontAwesomeMap["block2"] = IronIcons.UNDO
    pngToFontAwesomeMap["board"] = VaadinIcon.COGS
    pngToFontAwesomeMap["bold"] = VaadinIcon.BOLD
    pngToFontAwesomeMap["bomb"] = VaadinIcon.BOMB
    pngToFontAwesomeMap["bookmark"] = VaadinIcon.BOOKMARK
    pngToFontAwesomeMap["boxarrow"] = VaadinIcon.TOOLBOX
    pngToFontAwesomeMap["bw"] = VaadinIcon.STEP_FORWARD
    pngToFontAwesomeMap["calculate"] = VaadinIcon.CALC
    pngToFontAwesomeMap["cfolder"] = VaadinIcon.FOLDER
    pngToFontAwesomeMap["chart_view"] = VaadinIcon.CHART
    pngToFontAwesomeMap["checkbox"] = VaadinIcon.THIN_SQUARE
    pngToFontAwesomeMap["clip"] = VaadinIcon.PAPERCLIP
    pngToFontAwesomeMap["collapsedb"] = VaadinIcon.ARROWS_LONG_RIGHT
    pngToFontAwesomeMap["collapsed_f"] = VaadinIcon.FOLDER
    pngToFontAwesomeMap["collapsed_t"] = VaadinIcon.FOLDER_O
    pngToFontAwesomeMap["column_chart"] = VaadinIcon.BAR_CHART
    pngToFontAwesomeMap["combo"] = VaadinIcon.LIGHTBULB
    pngToFontAwesomeMap["config"] = VaadinIcon.WRENCH
    pngToFontAwesomeMap["convert"] = VaadinIcon.EXCHANGE
    pngToFontAwesomeMap["cut"] = VaadinIcon.SCISSORS
    pngToFontAwesomeMap["deleteline"] = VaadinIcon.LIST_OL
    pngToFontAwesomeMap["done"] = VaadinIcon.CHECK_SQUARE_O
    pngToFontAwesomeMap["error"] = VaadinIcon.MINUS_CIRCLE
    pngToFontAwesomeMap["expandedb"] = VaadinIcon.ARROW_LONG_DOWN
    pngToFontAwesomeMap["expanded_f"] = VaadinIcon.FOLDER_OPEN
    pngToFontAwesomeMap["expanded_s"] = VaadinIcon.FOLDER_OPEN
    pngToFontAwesomeMap["expanded_t"] = VaadinIcon.FOLDER_OPEN
    pngToFontAwesomeMap["export"] = VaadinIcon.COG
    pngToFontAwesomeMap["fax"] = VaadinIcon.PHONE
    pngToFontAwesomeMap["fw"] = VaadinIcon.STEP_FORWARD
    pngToFontAwesomeMap["gifIcon"] = VaadinIcon.FILE_PICTURE
    pngToFontAwesomeMap["green"] = MapsIcons.MAP
    pngToFontAwesomeMap["guide"] = IronIcons.HELP
    pngToFontAwesomeMap["ident"] = VaadinIcon.ARROWS_LONG_RIGHT
    pngToFontAwesomeMap["index"] = VaadinIcon.BOOK
    pngToFontAwesomeMap["interrupt"] = VaadinIcon.STOP
    pngToFontAwesomeMap["italic"] = VaadinIcon.ITALIC
    pngToFontAwesomeMap["jpgIcon"] = VaadinIcon.PICTURE
    pngToFontAwesomeMap["launch"] = VaadinIcon.ARROW_RIGHT
    pngToFontAwesomeMap["line_chart"] = VaadinIcon.LINE_CHART
    pngToFontAwesomeMap["lock"] = VaadinIcon.LOCK
    pngToFontAwesomeMap["login"] = IronIcons.ACCOUNT_CIRCLE
    pngToFontAwesomeMap["moneycheck"] = VaadinIcon.MONEY
    pngToFontAwesomeMap["money"] = VaadinIcon.MONEY
    pngToFontAwesomeMap["notice"] = IronIcons.LIGHTBULB_OUTLINE
    pngToFontAwesomeMap["ofolder"] = IronIcons.FOLDER_OPEN
    pngToFontAwesomeMap["pageFirst"] = AvIcons.SKIP_PREVIOUS
    pngToFontAwesomeMap["pageLast"] = AvIcons.SKIP_NEXT
    pngToFontAwesomeMap["pageLeft"] = AvIcons.FAST_REWIND
    pngToFontAwesomeMap["pageRight"] = AvIcons.FAST_FORWARD
    pngToFontAwesomeMap["password"] = VaadinIcon.PASSWORD
    pngToFontAwesomeMap["paste"] = VaadinIcon.PASTE
    pngToFontAwesomeMap["phone"] = VaadinIcon.PHONE
    pngToFontAwesomeMap["pie_chart"] = VaadinIcon.PIE_CHART
    pngToFontAwesomeMap["printoptions"] = VaadinIcon.WRENCH
    pngToFontAwesomeMap["project"] = VaadinIcon.CUBES
    pngToFontAwesomeMap["red"] = MapsIcons.MAP
    pngToFontAwesomeMap["redo"] = IronIcons.REDO
    pngToFontAwesomeMap["refresh"] = VaadinIcon.REFRESH
    pngToFontAwesomeMap["reload"] = VaadinIcon.REFRESH
    pngToFontAwesomeMap["report"] = VaadinIcon.GRID
    pngToFontAwesomeMap["sec"] = VaadinIcon.UNLOCK
    pngToFontAwesomeMap["selected"] = VaadinIcon.ARROWS_LONG_RIGHT
    pngToFontAwesomeMap["send"] = VaadinIcon.OUTBOX
    pngToFontAwesomeMap["sort"] = VaadinIcon.SORT
    pngToFontAwesomeMap["split"] = VaadinIcon.SPLIT
    pngToFontAwesomeMap["standard"] = VaadinIcon.PENCIL
    pngToFontAwesomeMap["stick"] = VaadinIcon.PIN
    pngToFontAwesomeMap["stop"] = VaadinIcon.STOP
    pngToFontAwesomeMap["todo"] = VaadinIcon.TASKS
    pngToFontAwesomeMap["top"] = VaadinIcon.BOOK
    pngToFontAwesomeMap["underline"] = VaadinIcon.UNDERLINE
    pngToFontAwesomeMap["undo"] = IronIcons.UNDO
    pngToFontAwesomeMap["unident"] = VaadinIcon.ARROW_CIRCLE_LEFT
    pngToFontAwesomeMap["unstick"] = VaadinIcon.PIN
    pngToFontAwesomeMap["update"] = EditorIcons.MODE_EDIT
    pngToFontAwesomeMap["users"] = VaadinIcon.USERS
    pngToFontAwesomeMap["utils"] = VaadinIcon.TOOLS
    pngToFontAwesomeMap["validate"] = VaadinIcon.CHECK
    pngToFontAwesomeMap["wait"] = DeviceIcons.ACCESS_TIME
    pngToFontAwesomeMap["warning"] = IronIcons.WARNING
    pngToFontAwesomeMap["window"] = IronIcons.CHEVRON_RIGHT
    pngToFontAwesomeMap["yellow"] = MapsIcons.MAP
    pngToFontAwesomeMap["zoomheight"] = VaadinIcon.VAADIN_H
    pngToFontAwesomeMap["zoomminus"] = VaadinIcon.SEARCH_MINUS
    pngToFontAwesomeMap["zoomoptimal"] = VaadinIcon.VAADIN_H
    pngToFontAwesomeMap["zoomplus"] = VaadinIcon.SEARCH_PLUS
    pngToFontAwesomeMap["zoomwidth"] = VaadinIcon.VAADIN_H
  }
}
