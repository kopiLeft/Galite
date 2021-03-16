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
package org.kopi.galite.ui.vaadin.field

import org.kopi.galite.ui.vaadin.common.VImage

/**
 * The server side component of the image field.
 * TODO: it have to be implemented later, ObjectField<Any?> ?
 */
class ImageField : ObjectField<Any?>() {

  /**
   * The image width.
   */
  var imageWidth = 0

  /**
   * The image height.
   */
  var imageHeight = 0

  private var image: VImage? = VImage()

  private val listeners = mutableListOf<ImageFieldListener>()

  init {
    image!!.setBorder(0)
    image!!.element.setProperty("borderStyle", "none")
    className = "k-imagefield"
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Registers a new image field listener.
   * @param l The image field listener.
   */
  fun addImageFieldListener(l: ImageFieldListener) {
    listeners.add(l)
  }

  /**
   * Removes an image field listener.
   * @param l The image field listener.
   */
  fun removeImageFieldListener(l: ImageFieldListener) {
    listeners.remove(l)
  }

  /**
   * Fired when the image is removed.
   */
  protected fun fireRemoved() {
    for (l in listeners) {
      l.onRemove()
    }
  }

  /**
   * Fired when the image is clicked.
   */
  protected fun fireClicked() {
    for (l in listeners) {
      l.onImageClick()
    }
  }

  override val isNull: Boolean
    get() = image == null || image!!.isEmpty

  override fun setValue(o: Any?) {
    image!!.src = o as String?
  }


  override fun setPresentationValue(newPresentationValue: Any?) {
    value = newPresentationValue
  }

  override fun setColor(foreground: String?, background: String?) {
    // no color for image field
  }

  override fun getValue(): Any? = image!!.src

  override fun generateModelValue(): Any? = value

  override fun checkValue(rec: Int) {
    // nothing to perform
  }

  override fun setParentVisibility(visible: Boolean) {}

  override fun clear() {
    super.clear()
    image = null
  }
}
