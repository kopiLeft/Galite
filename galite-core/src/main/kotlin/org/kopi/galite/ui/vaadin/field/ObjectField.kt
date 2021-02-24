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

import com.vaadin.flow.component.Component
import java.util.*

/**
 * The Object field server side component.
 */
abstract class ObjectField : Component() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Registers an object field listener.
   * @param l The object field listener.
   */
  fun addObjectFieldListener(l: ObjectFieldListener) {
    listeners.add(l)
  }

  /**
   * Removes an object field listener.
   * @param l The object field listener.
   */
  fun removeObjectFieldListener(l: ObjectFieldListener) {
    listeners.remove(l)
  }

  /**
   * Fires a goto previous record event on this text field.
   */
  protected fun fireGotoPrevRecord() {
    for (l in listeners) {
      l.gotoPrevRecord()
    }
  }

  /**
   * Fires a goto previous field event on this text field.
   */
  protected fun fireGotoPrevField() {
    for (l in listeners) {
      l.gotoPrevField()
    }
  }

  /**
   * Fires a goto next record event on this text field.
   */
  protected fun fireGotoNextRecord() {
    for (l in listeners) {
      l.gotoNextRecord()
    }
  }

  /**
   * Fires a goto next field event on this text field.
   */
  protected fun fireGotoNextField() {
    for (l in listeners) {
      l.gotoNextField()
    }
  }

  /**
   * Fires a goto next block event on this text field.
   */
  protected fun fireGotoNextBlock() {
    for (l in listeners) {
      l.gotoNextBlock()
    }
  }

  /**
   * Fires a goto last record event on this text field.
   */
  protected fun fireGotoLastRecord() {
    for (l in listeners) {
      l.gotoLastRecord()
    }
  }

  /**
   * Fires a goto first record event on this text field.
   */
  protected fun fireGotoFirstRecord() {
    for (l in listeners) {
      l.gotoFirstRecord()
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val listeners: MutableList<ObjectFieldListener>
  private val rpc: ObjectFieldServerRpc = object : ObjectFieldServerRpc() {
    fun gotoPrevRecord() {
      fireGotoPrevRecord()
    }

    fun gotoPrevField() {
      fireGotoPrevField()
    }

    fun gotoNextRecord() {
      fireGotoNextBlock()
    }

    fun gotoNextField() {
      fireGotoNextField()
    }

    fun gotoNextBlock() {
      fireGotoNextBlock()
    }

    fun gotoLastRecord() {
      fireGotoLastRecord()
    }

    fun gotoFirstRecord() {
      fireGotoFirstRecord()
    }
  }
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new `ObjectField` instance.
   */
  init {
    setImmediate(true)
    listeners = LinkedList()
    registerRpc(rpc)
  }
}