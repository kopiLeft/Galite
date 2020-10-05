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

package org.kopi.galite.preview

import java.awt.event.KeyEvent
import java.io.File
import java.util.Locale

import javax.swing.event.EventListenerList

import org.kopi.galite.base.Utils
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.PrintJob
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.Constants
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.WindowBuilder
import org.kopi.galite.visual.WindowController

/**
 * A special window that display an html help
 */
class VPreviewWindow : VWindow() {
  companion object {
    private const val DEF_ZOOM_RATIO = 1.30f
    private const val PREVIEW_LOCALIZATION_RESOURCE = "org/kopi/galite/Preview"

    // the following commands *MUST* be in the same order than
    // in 'actors' field set in the contructor of the current class.
    const val CMD_QUIT = 0
    const val CMD_FIRST = 1
    const val CMD_LEFT = 2
    const val CMD_RIGHT = 3
    const val CMD_LAST = 4
    protected const val CMD_ZOOM_FIT = 5
    protected const val CMD_ZOOM_FIT_W = 6
    protected const val CMD_ZOOM_FIT_H = 7
    protected const val CMD_ZOOM_PLUS = 8
    protected const val CMD_ZOOM_MINUS = 9

    init {
      WindowController.windowController.registerWindowBuilder(Constants.MDL_PREVIEW, object : WindowBuilder {
        override fun createWindow(model: VWindow): UWindow {
          return UIFactory.uiFactory.createView(model) as UWindow
        }
      })
    }
  }

  override fun getType(): Int = Constants.MDL_PREVIEW

  /**
   * The user want to show an help
   */
  fun preview(printJob: PrintJob, command: String) {
    val tempFile: File = Utils.getTempFile("PREVIEW", "JPG")
    this.printJob = printJob
    numberOfPages = printJob.numberOfPages
    printFile = printJob.dataFile
    imageFile = tempFile.path
    imageFile = imageFile!!.substring(0, imageFile!!.lastIndexOf('.'))
    height = printJob.getHeight()
    width = printJob.getWidth()
    this.command = command
    createImagesFromPostscript()
    currentPage = 1
    setActorEnabled(CMD_QUIT, true)
    setActorEnabled(CMD_FIRST, currentPage > 1)
    setActorEnabled(CMD_LEFT, currentPage > 1)
    setActorEnabled(CMD_RIGHT, currentPage < numberOfPages)
    setActorEnabled(CMD_LAST, currentPage < numberOfPages)
    setActorEnabled(CMD_ZOOM_FIT, true)
    setActorEnabled(CMD_ZOOM_FIT_H, true)
    setActorEnabled(CMD_ZOOM_FIT_W, true)
    setActorEnabled(CMD_ZOOM_PLUS, true)
    setActorEnabled(CMD_ZOOM_MINUS, true)
    doNotModal()
  }

  private fun createImagesFromPostscript() {
    try {
      val resolution: Int = (72f * height / printJob!!.getHeight()) as Int
      val p = Runtime.getRuntime().exec(command +
              " -q" +
              " -sOutputFile=" + imageFile + "%d.JPG" +
              " -sDEVICE=jpeg" +
              " -r" + resolution + "x" + resolution +
              " -g" + width + "x" + height +
              " -dNOPAUSE" +
              " " + printFile +
              " -c quit ")
      p.waitFor()
    } catch (e: Exception) {
      fatalError(this, "VPreviewWindow.preview(File ...)", e)
    }
  }

  fun zoom(ratio: Float) {
    var ratio = ratio
    if (height.coerceAtMost(width) * ratio < 50) {
      ratio = 50 / height.coerceAtMost(width).toFloat()
    }
    if (height.coerceAtLeast(width) * ratio > 3000) {
      ratio = 3000 / height.coerceAtLeast(width).toFloat()
    }
    width = (width * ratio).toInt()
    height = (height * ratio).toInt()
    createImagesFromPostscript()
    fireZoomChanged()
  }

  /**
   * Performs the appropriate action.
   *
   * @param        actor                the number of the actor.
   */
  override fun executeVoidTrigger(key: Int) {
    when (key) {
      CMD_QUIT -> getDisplay()!!.closeWindow()
      CMD_FIRST -> {
        setWaitInfo(VlibProperties.getString("WAIT"))
        currentPage = 1
        firePageChanged(currentPage)
        unsetWaitInfo()
      }
      CMD_LEFT -> {
        setWaitInfo(VlibProperties.getString("WAIT"))
        currentPage -= 1
        firePageChanged(currentPage)
        unsetWaitInfo()
      }
      CMD_RIGHT -> {
        setWaitInfo(VlibProperties.getString("WAIT"))
        currentPage += 1
        firePageChanged(currentPage)
        unsetWaitInfo()
      }
      CMD_LAST -> {
        setWaitInfo(VlibProperties.getString("WAIT"))
        currentPage = numberOfPages
        firePageChanged(currentPage)
        unsetWaitInfo()
      }
      CMD_ZOOM_PLUS -> {
        setWaitInfo(VlibProperties.getString("WAIT"))
        zoom(DEF_ZOOM_RATIO)
        unsetWaitInfo()
      }
      CMD_ZOOM_MINUS -> {
        setWaitInfo(VlibProperties.getString("WAIT"))
        zoom(1 / DEF_ZOOM_RATIO)
        unsetWaitInfo()
      }
      CMD_ZOOM_FIT -> {
        // ask gui to calculate zoom
        // gui calls method zoom with the good value
        setWaitInfo(VlibProperties.getString("WAIT"))
        fireZoomFit(PreviewListener.FIT_BOTH)
        unsetWaitInfo()
      }
      CMD_ZOOM_FIT_H -> {
        // ask gui to calculate zoom
        // gui calls method zoom with the good value
        setWaitInfo(VlibProperties.getString("WAIT"))
        fireZoomFit(PreviewListener.FIT_HEIGHT)
        unsetWaitInfo()
      }
      CMD_ZOOM_FIT_W -> {
        // ask gui to calculate zoom
        // gui calls method zoom with the good value
        setWaitInfo(VlibProperties.getString("WAIT"))
        fireZoomFit(PreviewListener.FIT_WIDTH)
        unsetWaitInfo()
      }
    }
    setMenu()
  }

  /**
   * Goto the specified page.
   *
   * @param     posno           the page position number.
   */
  fun gotoPosition(posno: Int) {
    setWaitInfo(VlibProperties.getString("WAIT"))
    currentPage = posno
    firePageChanged(currentPage)
    unsetWaitInfo()
    setMenu()
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  fun localize(locale: Locale) {
    var manager: LocalizationManager?
    manager = LocalizationManager(locale, ApplicationContext.getDefaultLocale())

    // localizes the actors in VWindow
    super.localizeActors(manager)
    manager = null
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  fun addPreviewListener(bl: PreviewListener) {
    previewListener.add(PreviewListener::class.java, bl)
  }

  fun removePreviewListener(bl: PreviewListener) {
    previewListener.remove(PreviewListener::class.java, bl)
  }

  protected fun firePageChanged(current: Int) {
    val listeners = previewListener.listenerList
    var i = listeners.size - 2
    while (i >= 0) {
      if (listeners[i] === PreviewListener::class.java) {
        (listeners[i + 1] as PreviewListener).pageChanged(current)
      }
      i -= 2
    }
  }

  protected fun fireZoomChanged() {
    val listeners = previewListener.listenerList
    var i = listeners.size - 2
    while (i >= 0) {
      if (listeners[i] === PreviewListener::class.java) {
        (listeners[i + 1] as PreviewListener).zoomChanged()
      }
      i -= 2
    }
  }

  protected fun fireZoomFit(type: Int) {
    val listeners = previewListener.listenerList
    var i = listeners.size - 2
    while (i >= 0) {
      if (listeners[i] === PreviewListener::class.java) {
        (listeners[i + 1] as PreviewListener).zoomFit(type)
      }
      i -= 2
    }
  }

  private fun setMenu() {
    setActorEnabled(CMD_FIRST, currentPage > 1)
    setActorEnabled(CMD_LEFT, currentPage > 1)
    setActorEnabled(CMD_RIGHT, currentPage < numberOfPages)
    setActorEnabled(CMD_LAST, currentPage < numberOfPages)
  }

  fun getPreviewFileName(current: Int): String {
    return "$imageFile$current.JPG"
  }

  override fun getTitle(): String {
    return windowTitle
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
  var currentPage = 0
    private set
  var numberOfPages = 0
    private set
  private var command: String? = null
  var printJob: PrintJob? = null
    private set
  private var printFile: File? = null
  private var imageFile: String? = null
  var height = 0
    private set
  var width = 0
    private set
  private val previewListener: EventListenerList

  init {
    setTitle(VlibProperties.getString("Preview"))
    setActors(arrayOf<VActor>(
            VActor("File",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "Close",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "quit",
                    KeyEvent.VK_ESCAPE,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PageFirst",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "pageFirst",
                    KeyEvent.VK_HOME,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PageLeft",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "pageLeft",
                    KeyEvent.VK_PAGE_UP,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PageRight",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "pageRight",
                    KeyEvent.VK_PAGE_DOWN,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PageLast",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "pageLast",
                    KeyEvent.VK_END,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PreviewFit",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "zoomoptimal",
                    KeyEvent.VK_F5,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PreviewFitWidth",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "zoomwidth",
                    KeyEvent.VK_F8,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PreviewFitHeight",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "zoomheight",
                    KeyEvent.VK_F9,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PreviewPlus",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "zoomplus",
                    KeyEvent.VK_F6,
                    0),
            VActor("Action",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "PreviewMinus",
                    PREVIEW_LOCALIZATION_RESOURCE,
                    "zoomminus",
                    KeyEvent.VK_F7,
                    0)
    ))

    // localize the preview using the default locale
    localize(Locale.getDefault())
    getActor(CMD_QUIT).number = CMD_QUIT
    getActor(CMD_FIRST).number = CMD_FIRST
    getActor(CMD_LEFT).number = CMD_LEFT
    getActor(CMD_RIGHT).number = CMD_RIGHT
    getActor(CMD_LAST).number = CMD_LAST
    getActor(CMD_ZOOM_FIT).number = CMD_ZOOM_FIT
    getActor(CMD_ZOOM_FIT_W).number = CMD_ZOOM_FIT_W
    getActor(CMD_ZOOM_FIT_H).number = CMD_ZOOM_FIT_H
    getActor(CMD_ZOOM_PLUS).number = CMD_ZOOM_PLUS
    getActor(CMD_ZOOM_MINUS).number = CMD_ZOOM_MINUS
    previewListener = EventListenerList()
  }
}
