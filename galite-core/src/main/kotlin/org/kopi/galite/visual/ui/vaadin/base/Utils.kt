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

import java.util.Hashtable
import java.util.concurrent.CompletableFuture

import org.kopi.galite.visual.base.Utils
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.visual.VColor

import com.flowingcode.vaadin.addons.ironicons.AvIcons
import com.flowingcode.vaadin.addons.ironicons.DeviceIcons
import com.flowingcode.vaadin.addons.ironicons.EditorIcons
import com.flowingcode.vaadin.addons.ironicons.FileIcons
import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.flowingcode.vaadin.addons.ironicons.MapsIcons
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.dom.Element

/**
 * Some vaadin version utilities to obtain images and resources.
 */
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
    if(Utils::class.java.classLoader.getResource("META-INF/resources/$directory/$name") != null) { // FIXME
      return Image("$directory/$name")
    }

    return null
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
    //return "<div class=\"info\"><i class=\"fa fa-sort-asc\" aria-hidden=\"true\"></i>${content.orEmpty()}</div>" TODO
    return content.orEmpty()
  }

  /**
   * Returns the equivalent icon from the given icon name.
   *
   * @param iconName The model icon name.
   * @return The equivalent vaadin or iron icon.
   */
  fun getVaadinIcon(iconName: String?): Any? {
    return pngToIconMap[iconName]
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

  fun getOffsetLeft(element: Element?, ui: UI): Double {
    val future = CompletableFuture<Double>()

    ui.access {
      ui.page
        .executeJs("return $0.offsetLeft", element)
        .then(Double::class.java) { value: Double ->
          future.complete(value)
        }
      ui.push()
    }

    return future.get()
  }

  fun getOffsetWidth(element: Element?, ui: UI): Double {
    val future = CompletableFuture<Double>()

    ui.access {
      ui.page
        .executeJs("return $0.offsetWidth", element)
        .then(Double::class.java) { value: Double ->
          future.complete(value)
        }
      ui.push()
    }

    return future.get()
  }

  fun getWidth(element: Element?, ui: UI): String? {
    val future = CompletableFuture<String>()

    ui.access {
      ui.page
        .executeJs("return $0.width", element)
        .then(String::class.java) { value ->
          future.complete(value)
        }
      ui.push()
    }

    return future.get()
  }

  /**
   * Gets the current position of the cursor.
   *
   * @param field the field to return its cursor position
   * @return the cursor's position
   */
  fun getCursorPos(field: Component): Int {
    val future = CompletableFuture<Int>()

    BackgroundThreadHandler.accessAndPush {
      UI.getCurrent().page
        .executeJs("return $0.shadowRoot.querySelector('[part=\"value\"]').selectionStart;", field.element)
        .then(Int::class.java) { value: Int ->
          future.complete(value)
        }
    }

    return future.get()
  }

  fun Component.findMainWindow(): MainWindow? {
    if(this is MainWindow) return this

    var mainWindow: MainWindow? = null
    var parent: Component? = parent.orElse(null)

    while (parent != null && mainWindow == null) {
      if(parent is MainWindow) {
        mainWindow = parent
      }
      parent = parent.parent.orElse(null)
    }

    return mainWindow
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
  private const val VAADIN_RESOURCE_DIR = "ui/vaadin"
  private const val THEME_DIR = "resource"
  private const val APPLICATION_DIR = "resources"
  private const val RESOURCE_DIR = "org/kopi/galite/visual"
  val UKN_IMAGE = Image("$THEME_DIR/unknown.png")
  private val cache = Hashtable<String, Image>()
  private var pngToIconMap = mutableMapOf<String, Any>()

  init {
    pngToIconMap["all"] = VaadinIcon.HAND
    pngToIconMap["block"] = VaadinIcon.BAN
    pngToIconMap["border"] = VaadinIcon.COG
    pngToIconMap["bread_crumb_separator"] = VaadinIcon.ANGLE_DOUBLE_RIGHT
    pngToIconMap["break"] = VaadinIcon.CLOSE_CIRCLE
    pngToIconMap["calendar"] = VaadinIcon.CALENDAR_O
    pngToIconMap["collapsed"] = VaadinIcon.FOLDER_O
    pngToIconMap["collapsed_p"] = VaadinIcon.FOLDER_O
    pngToIconMap["copy"] = VaadinIcon.COPY
    pngToIconMap["delete"] = VaadinIcon.TRASH
    pngToIconMap["desk"] = VaadinIcon.DESKTOP
    pngToIconMap["detail"] = VaadinIcon.SEARCH_PLUS
    pngToIconMap["detail_view"] = VaadinIcon.SEARCH
    pngToIconMap["down"] = VaadinIcon.ANGLE_DOUBLE_DOWN
    pngToIconMap["duke"] = VaadinIcon.LOCK
    pngToIconMap["edit"] = VaadinIcon.EDIT
    pngToIconMap["expanded_a"] = VaadinIcon.FOLDER_OPEN_O
    pngToIconMap["expanded"] = VaadinIcon.FOLDER_OPEN_O
    pngToIconMap["expanded_p"] = VaadinIcon.FOLDER_OPEN_O
    pngToIconMap["exportCsv"] = VaadinIcon.FILE_TEXT_O
    pngToIconMap["exportPdf"] = FileIcons.PDF
    pngToIconMap["exportXlsx"] = FileIcons.EXCEL
    pngToIconMap["foldColumn"] = VaadinIcon.FOLDER_O
    pngToIconMap["fold"] = VaadinIcon.FOLDER_O
    pngToIconMap["formula"] = VaadinIcon.CALC
    pngToIconMap["help"] = VaadinIcon.QUESTION_CIRCLE_O
    pngToIconMap["home"] = VaadinIcon.HOME
    pngToIconMap["insertline"] = VaadinIcon.LIST_OL
    pngToIconMap["insert"] = VaadinIcon.INSERT
    pngToIconMap["list"] = VaadinIcon.LIST
    pngToIconMap["loading"] = VaadinIcon.SPINNER
    pngToIconMap["login_img"] = VaadinIcon.COFFEE
    pngToIconMap["mail"] = VaadinIcon.ENVELOPE_O
    pngToIconMap["menuquery"] = VaadinIcon.FILE_TEXT_O
    pngToIconMap["note"] = VaadinIcon.NOTEBOOK
    pngToIconMap["nothing"] = VaadinIcon.FILE_O
    pngToIconMap["open"] = VaadinIcon.FILE_TEXT_O
    pngToIconMap["options"] = VaadinIcon.COG
    pngToIconMap["preview"] = VaadinIcon.FILE_TEXT_O
    pngToIconMap["print"] = VaadinIcon.PRINT
    pngToIconMap["quit"] = VaadinIcon.POWER_OFF
    pngToIconMap["save"] = IronIcons.SAVE
    pngToIconMap["searchop"] = VaadinIcon.SEARCH
    pngToIconMap["search"] = VaadinIcon.FILE_SEARCH
    pngToIconMap["serialquery"] = VaadinIcon.RECORDS
    pngToIconMap["serviceoff"] = DeviceIcons.SIGNAL_CELLULAR_OFF
    pngToIconMap["serviceon"] = DeviceIcons.SIGNAL_CELLULAR_4_BAR
    pngToIconMap["store"] = VaadinIcon.BUILDING_O
    pngToIconMap["suggest"] = VaadinIcon.PHONE
    pngToIconMap["timeStamp"] = VaadinIcon.CLOCK
    pngToIconMap["tri"] = VaadinIcon.SORT
    pngToIconMap["unfoldColumn"] = VaadinIcon.FOLDER_OPEN_O
    pngToIconMap["unfold"] = VaadinIcon.FOLDER_OPEN_O
    pngToIconMap["up"] = VaadinIcon.ANGLE_DOUBLE_UP
    pngToIconMap["add"] = VaadinIcon.FILE_ADD
    pngToIconMap["align_center"] = VaadinIcon.ALIGN_CENTER
    pngToIconMap["align_justify"] = VaadinIcon.ALIGN_JUSTIFY
    pngToIconMap["align_left"] = VaadinIcon.ALIGN_LEFT
    pngToIconMap["align_right"] = VaadinIcon.ALIGN_RIGHT
    pngToIconMap["apply"] = VaadinIcon.COGS
    pngToIconMap["area_chart"] = VaadinIcon.SPLINE_AREA_CHART
    pngToIconMap["arrowfirst"] = VaadinIcon.STEP_BACKWARD
    pngToIconMap["arrowlast"] = VaadinIcon.STEP_FORWARD
    pngToIconMap["arrowleft"] = VaadinIcon.BACKWARDS
    pngToIconMap["arrowright"] = VaadinIcon.FORWARD
    pngToIconMap["article"] = VaadinIcon.FILE_TEXT_O
    pngToIconMap["ask"] = VaadinIcon.QUESTION_CIRCLE_O
    pngToIconMap["bar_chart"] = VaadinIcon.BAR_CHART
    pngToIconMap["bkup3"] = VaadinIcon.WARNING
    pngToIconMap["bkup"] = VaadinIcon.WARNING
    pngToIconMap["block2"] = IronIcons.UNDO
    pngToIconMap["board"] = VaadinIcon.COGS
    pngToIconMap["bold"] = VaadinIcon.BOLD
    pngToIconMap["bomb"] = VaadinIcon.BOMB
    pngToIconMap["bookmark"] = VaadinIcon.BOOKMARK
    pngToIconMap["boxarrow"] = VaadinIcon.TOOLBOX
    pngToIconMap["bw"] = VaadinIcon.STEP_FORWARD
    pngToIconMap["calculate"] = VaadinIcon.CALC
    pngToIconMap["cfolder"] = VaadinIcon.FOLDER
    pngToIconMap["chart_view"] = VaadinIcon.CHART
    pngToIconMap["checkbox"] = VaadinIcon.THIN_SQUARE
    pngToIconMap["clip"] = VaadinIcon.PAPERCLIP
    pngToIconMap["collapsedb"] = VaadinIcon.ARROWS_LONG_RIGHT
    pngToIconMap["collapsed_f"] = VaadinIcon.FOLDER
    pngToIconMap["collapsed_t"] = VaadinIcon.FOLDER_O
    pngToIconMap["column_chart"] = VaadinIcon.BAR_CHART
    pngToIconMap["combo"] = VaadinIcon.LIGHTBULB
    pngToIconMap["config"] = VaadinIcon.WRENCH
    pngToIconMap["convert"] = VaadinIcon.EXCHANGE
    pngToIconMap["cut"] = VaadinIcon.SCISSORS
    pngToIconMap["deleteline"] = VaadinIcon.LIST_OL
    pngToIconMap["done"] = VaadinIcon.CHECK_SQUARE_O
    pngToIconMap["error"] = VaadinIcon.MINUS_CIRCLE
    pngToIconMap["expandedb"] = VaadinIcon.ARROW_LONG_DOWN
    pngToIconMap["expanded_f"] = VaadinIcon.FOLDER_OPEN
    pngToIconMap["expanded_s"] = VaadinIcon.FOLDER_OPEN
    pngToIconMap["expanded_t"] = VaadinIcon.FOLDER_OPEN
    pngToIconMap["export"] = VaadinIcon.COG
    pngToIconMap["fax"] = VaadinIcon.PHONE
    pngToIconMap["fw"] = VaadinIcon.STEP_FORWARD
    pngToIconMap["gifIcon"] = VaadinIcon.FILE_PICTURE
    pngToIconMap["green"] = MapsIcons.MAP
    pngToIconMap["guide"] = IronIcons.HELP
    pngToIconMap["ident"] = VaadinIcon.ARROWS_LONG_RIGHT
    pngToIconMap["index"] = VaadinIcon.BOOK
    pngToIconMap["interrupt"] = VaadinIcon.STOP
    pngToIconMap["italic"] = VaadinIcon.ITALIC
    pngToIconMap["jpgIcon"] = VaadinIcon.PICTURE
    pngToIconMap["launch"] = VaadinIcon.ARROW_RIGHT
    pngToIconMap["line_chart"] = VaadinIcon.LINE_CHART
    pngToIconMap["lock"] = VaadinIcon.LOCK
    pngToIconMap["login"] = IronIcons.ACCOUNT_CIRCLE
    pngToIconMap["moneycheck"] = VaadinIcon.MONEY
    pngToIconMap["money"] = VaadinIcon.MONEY
    pngToIconMap["notice"] = IronIcons.LIGHTBULB_OUTLINE
    pngToIconMap["ofolder"] = IronIcons.FOLDER_OPEN
    pngToIconMap["pageFirst"] = AvIcons.SKIP_PREVIOUS
    pngToIconMap["pageLast"] = AvIcons.SKIP_NEXT
    pngToIconMap["pageLeft"] = AvIcons.FAST_REWIND
    pngToIconMap["pageRight"] = AvIcons.FAST_FORWARD
    pngToIconMap["password"] = VaadinIcon.PASSWORD
    pngToIconMap["paste"] = VaadinIcon.PASTE
    pngToIconMap["phone"] = VaadinIcon.PHONE
    pngToIconMap["pie_chart"] = VaadinIcon.PIE_CHART
    pngToIconMap["printoptions"] = VaadinIcon.WRENCH
    pngToIconMap["project"] = VaadinIcon.CUBES
    pngToIconMap["red"] = MapsIcons.MAP
    pngToIconMap["redo"] = IronIcons.REDO
    pngToIconMap["refresh"] = VaadinIcon.REFRESH
    pngToIconMap["reload"] = VaadinIcon.REFRESH
    pngToIconMap["report"] = VaadinIcon.TABLE
    pngToIconMap["sec"] = VaadinIcon.UNLOCK
    pngToIconMap["selected"] = VaadinIcon.ARROWS_LONG_RIGHT
    pngToIconMap["send"] = VaadinIcon.OUTBOX
    pngToIconMap["sort"] = VaadinIcon.SORT
    pngToIconMap["split"] = VaadinIcon.SPLIT
    pngToIconMap["standard"] = VaadinIcon.PENCIL
    pngToIconMap["stick"] = VaadinIcon.PIN
    pngToIconMap["stop"] = VaadinIcon.STOP
    pngToIconMap["todo"] = VaadinIcon.TASKS
    pngToIconMap["top"] = VaadinIcon.BOOK
    pngToIconMap["underline"] = VaadinIcon.UNDERLINE
    pngToIconMap["undo"] = IronIcons.UNDO
    pngToIconMap["unident"] = VaadinIcon.ARROW_CIRCLE_LEFT
    pngToIconMap["unstick"] = VaadinIcon.PIN
    pngToIconMap["update"] = EditorIcons.MODE_EDIT
    pngToIconMap["users"] = VaadinIcon.USERS
    pngToIconMap["utils"] = VaadinIcon.TOOLS
    pngToIconMap["validate"] = VaadinIcon.CHECK
    pngToIconMap["wait"] = DeviceIcons.ACCESS_TIME
    pngToIconMap["warning"] = IronIcons.WARNING
    pngToIconMap["window"] = IronIcons.CHEVRON_RIGHT
    pngToIconMap["yellow"] = MapsIcons.MAP
    pngToIconMap["zoomheight"] = VaadinIcon.VAADIN_H
    pngToIconMap["zoomminus"] = VaadinIcon.SEARCH_MINUS
    pngToIconMap["zoomoptimal"] = VaadinIcon.VAADIN_H
    pngToIconMap["zoomplus"] = VaadinIcon.SEARCH_PLUS
    pngToIconMap["zoomwidth"] = VaadinIcon.VAADIN_H
  }
}
