/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: ImageField.java 34961 2016-11-04 17:20:49Z hacheni $
 */
package org.kopi.galite.ui.vaadin.field

import java.util.*

/**
 * The server side component of the image field.
 */
class ImageField : VObjectField() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the image width.
   * @param width The image width.
   */
  fun setImageWidth(width: Int) {
    state.imageWidth = width
  }

  /**
   * Sets the image height.
   * @param height The image height.
   */
  fun setImageHeight(height: Int) {
    state.imageHeight = height
  }

  protected val state: ImageFieldState
    protected get() = super.getState() as ImageFieldState

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
      l?.onRemove()
    }
  }

  /**
   * Fired when the image is clicked.
   */
  protected fun fireClicked() {
    for (l in listeners) {
      l?.onImageClick()
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val listeners: MutableList<ImageFieldListener>
  private val rpc: ImageFieldServerRpc = object : ImageFieldServerRpc() {
    fun onRemove() {
      fireRemoved()
    }

    fun onClick() {
      fireClicked()
    }
  }
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new `ImageField` component.
   */
  init {
    listeners = LinkedList()
    registerRpc(rpc)
  }
}