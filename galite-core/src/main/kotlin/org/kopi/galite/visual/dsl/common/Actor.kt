/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.dsl.common

import java.awt.event.InputEvent
import java.awt.event.KeyEvent

import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.VActor

/**
 * This class represents an actor, ie a menu element with a name and may be an icon, a shortcut
 * and a help
 *
 * An Actor is an item to be linked to a command, if its [icon] is specified, it will appear
 * in the icon_toolbar located under the menu bar, otherwise, it will only be accessible from the menu bar
 *
 * @param menu                the containing menu
 * @param label               the label
 * @param help                the help
 * @param source              path localization file
 */
open class Actor(val menu: Menu,
                 val label: String,
                 help: String,
                 ident: String? = null,
                 source: String? = null)
  : VActor(menu.label, menu.sourceFile, ident, source, help = help, userActor = true) {

  init {
    menuItem = label
  }

  // The shortcut key
  var key: Key? = null
    set(key) {
      checkKey(key)
      field = key
    }

  // The actor icon
  var icon: Icon? = null
    set(value) {
      iconName = value?.iconName
      field = value
    }

  fun checkKey(key: Key?) {
    if (key == null) {
      acceleratorModifier = 0
      acceleratorKey = KeyEvent.VK_UNDEFINED
    } else {
      acceleratorKey = key.value
      acceleratorModifier = if (key.toString().contains("SHIFT_")) InputEvent.SHIFT_MASK else 0
    }
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  fun genLocalization(writer: LocalizationWriter) {
    writer.genActorDefinition(ident, label, help)
  }
}

enum class Icon(val iconName: String) {
  ALL("all"),
  BLOCK("block"),
  BORDER("border"),
  BREAD_CRUMB_SEPARATOR("bread_crumb_separator"),
  BREAK("break"),
  CALENDAR("calendar"),
  COLLAPSED("collapsed"),
  COLLAPSED_P("collapsed_p"),
  COPY("copy"),
  DELETE("delete"),
  DESK("desk"),
  DETAIL("detail"),
  DETAIL_VIEW("detail_view"),
  DOWN("down"),
  DUKE("duke"),
  EDIT("edit"),
  EXPANDED_A("expanded_a"),
  EXPANDED("expanded"),
  EXPANDED_P("expanded_p"),
  EXPORT_CSV("exportCsv"),
  EXPORT_PDF("exportPdf"),
  EXPORT_XLSX("exportXlsx"),
  FOLD_COLUMN("foldColumn"),
  FOLD("fold"),
  FORMULA("formula"),
  HELP("help"),
  HOME("home"),
  INSERT_LINE("insertline"),
  INSERT("insert"),
  LIST("list"),
  LOADING("loading"),
  LOGIN_IMG("login_img"),
  MAIL("mail"),
  MENU_QUERY("menuquery"),
  NOTE("note"),
  NOTHING("nothing"),
  OPEN("open"),
  OPTIONS("options"),
  PREVIEW("preview"),
  PRINT("print"),
  QUIT("quit"),
  SAVE("save"),
  SEARCH_OP("searchop"),
  SEARCH("search"),
  SERIAL_QUERY("serialquery"),
  SERVICE_OFF("serviceoff"),
  SERVICE_ON("serviceon"),
  STORE("store"),
  SUGGEST("suggest"),
  TIMESTAMP("timeStamp"),
  TRI("tri"),
  UNFOLD_COLUMN("unfoldColumn"),
  UNFOLD("unfold"),
  UP("up"),
  ADD("add"),
  ALIGN_CENTER("align_center"),
  ALIGN_JUSTIFY("align_justify"),
  ALIGN_LEFT("align_left"),
  ALIGN_RIGHT("align_right"),
  APPLY("apply"),
  AREA_CHART("area_chart"),
  ARROW_FIRST("arrowfirst"),
  ARROW_LAST("arrowlast"),
  ARROW_LEFT("arrowleft"),
  ARROW_RIGHT("arrowright"),
  ARTICLE("article"),
  ASK("ask"),
  BAR_CHART("bar_chart"),
  BKUP3("bkup3"),
  BKUP("bkup"),
  BLOCK2("block2"),
  BOARD("board"),
  BOLD("bold"),
  BOMB("bomb"),
  BOOKMARK("bookmark"),
  BOX_ARROW("boxarrow"),
  BW("bw"),
  CALCULATE("calculate"),
  CFOLDER("cfolder"),
  CHART_VIEW("chart_view"),
  CHECKBOX("checkbox"),
  CLIP("clip"),
  COLLAPSE_DB("collapsedb"),
  COLLAPSED_F("collapsed_f"),
  COLLAPSED_T("collapsed_t"),
  COLUMN_CHART("column_chart"),
  COMBO("combo"),
  CONFIG("config"),
  CONVERT("convert"),
  CUT("cut"),
  DELETE_LINE("deleteline"),
  DONE("done"),
  ERROR("error"),
  EXPANDED_B("expandedb"),
  EXPANDED_F("expanded_f"),
  EXPANDED_S("expanded_s"),
  EXPANDED_T("expanded_t"),
  EXPORT("export"),
  FAX("fax"),
  FW("fw"),
  GIF_ICON("gifIcon"),
  GREEN("green"),
  GUIDE("guide"),
  IDENT("ident"),
  INDEX("index"),
  INTERRUPT("interrupt"),
  ITALIC("italic"),
  JPG_ICON("jpgIcon"),
  LAUNCH("launch"),
  LINE_CHART("line_chart"),
  LOCK("lock"),
  LOGIN("login"),
  MONEY_CHECK("moneycheck"),
  MONEY("money"),
  NOTICE("notice"),
  OFOLDER("ofolder"),
  PAGE_FIRST("pageFirst"),
  PAGE_LAST("pageLast"),
  PAGE_LEFT("pageLeft"),
  PAGE_RIGHT("pageRight"),
  PASSWORD("password"),
  PASTE("paste"),
  PHONE("phone"),
  PIE_CHART("pie_chart"),
  PRINT_OPTIONS("printoptions"),
  PROJECT("project"),
  RED("red"),
  REDO("redo"),
  REFRESH("refresh"),
  RELOAD("reload"),
  REPORT("report"),
  SEC("sec"),
  SELECTED("selected"),
  SEND("send"),
  SORT("sort"),
  SPLIT("split"),
  STANDARD("standard"),
  STICK("stick"),
  STOP("stop"),
  TODO("todo"),
  TOP("top"),
  UNDERLINE("underline"),
  UNDO("undo"),
  UNIDENT("unident"),
  UNSTICK("unstick"),
  UPDATE("update"),
  USERS("users"),
  UTILS("utils"),
  VALIDATE("validate"),
  WAIT("wait"),
  WARNING("warning"),
  WINDOW("window"),
  YELLOW("yellow"),
  ZOOM_HEIGHT("zoomheight"),
  ZOOM_MINUS("zoomminus"),
  ZOOM_OPTIMAL("zoomoptimal"),
  ZOOM_PLUS("zoomplus"),
  ZOOM_WIDTH("zoomwidth")
}
