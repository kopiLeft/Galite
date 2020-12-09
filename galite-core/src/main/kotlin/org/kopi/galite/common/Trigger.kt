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
package org.kopi.galite.common

import org.kopi.galite.form.VConstants
import org.kopi.galite.report.Constants

/**
 * This class represents a trigger, ie an action to be executed on events
 */
abstract class Trigger(val events: Long, val action: Action<*>) {
  private var type = 0

  init {
    initialize()
  }

  /**
   * Return trigger array
   */
  abstract fun getTriggers(): IntArray

  /**
   * Initializes the trigger type
   */
  fun initialize() {
    type = -1
    val trgTypes = getTriggers()
    trgTypes.forEachIndexed { index, trgType ->
      if (events shr index and 1 > 0) {
        if (type == -1) {
          type = trgType
        } else if (trgType != type) {
          // throw PositionedError(getTokenReference(), BaseMessages.TRIGGER_DIFFERENT_RETURN, TRG_NAMES.get(i)) TODO
        }
      }
    }
  }
}

///////////////////////////////////////////////////////////////////////////
// BLOCK TRIGGERS
///////////////////////////////////////////////////////////////////////////

/**
 * Block Triggers
 *
 * @param event the event of the trigger
 */
open class BlockTriggerEvent(val event: Int)

/**
 * Block protected Triggers
 *
 * @param event the event of the trigger
 */
open class BlockProtectedTriggerEvent(event: Int) : BlockTriggerEvent(event)

/**
 * Block void Triggers
 *
 * @param event the event of the trigger
 */
open class BlockVoidTriggerEvent(event: Int) : BlockTriggerEvent(event)

/**
 * Block boolean Triggers
 *
 * @param event the event of the trigger
 */
open class BlockBooleanTriggerEvent(event: Int) : BlockTriggerEvent(event)

/**
 * Block Int Triggers
 *
 * @param event the event of the trigger
 */
open class BlockIntTriggerEvent(event: Int) : BlockTriggerEvent(event)

/**
 * Block object Triggers
 *
 * @param event the event of the trigger
 */
open class BlockObjectTriggerEvent(event: Int) : BlockTriggerEvent(event)

/**
 * executed before querying the database
 */
object PREQRY : BlockProtectedTriggerEvent(VConstants.TRG_PREQRY)    // protected trigger

/**
 * executed after querying the database
 */
object POSTQRY : BlockProtectedTriggerEvent(VConstants.TRG_POSTQRY)  // protected trigger

/**
 * executed before a row is deleted
 */
object PREDEL : BlockProtectedTriggerEvent(VConstants.TRG_PREDEL)    // protected trigger

/**
 * executed after a row is deleted
 */
object POSTDEL : BlockProtectedTriggerEvent(VConstants.TRG_POSTDEL)  // protected trigger

/**
 * executed before a row is inserted
 */
object PREINS : BlockProtectedTriggerEvent(VConstants.TRG_PREINS)    // protected trigger

/**
 * executed after a row is inserted
 */
object POSTINS : BlockProtectedTriggerEvent(VConstants.TRG_POSTINS)  // protected trigger

/**
 * executed before a row is updated
 */
object PREUPD : BlockProtectedTriggerEvent(VConstants.TRG_PREUPD)    // protected trigger

/**
 * executed after a row is updated
 */
object POSTUPD : BlockProtectedTriggerEvent(VConstants.TRG_POSTUPD)  // protected trigger

/**
 * executed before saving a row
 */
object PRESAVE : BlockProtectedTriggerEvent(VConstants.TRG_PRESAVE)  // protected trigger

/**
 * executed upon record entry
 */
object PREREC : BlockVoidTriggerEvent(VConstants.TRG_PREREC)    // void trigger

/**
 * executed upon record exit
 */
object POSTREC : BlockVoidTriggerEvent(VConstants.TRG_POSTREC)  // void trigger

/**
 * executed upon block entry
 */
object PREBLK : BlockVoidTriggerEvent(VConstants.TRG_PREBLK)    // void trigger

/**
 * executed upon block exit
 */
object POSTBLK : BlockVoidTriggerEvent(VConstants.TRG_POSTBLK)  // void trigger

/**
 * executed upon block validation
 */
object VALBLK : BlockVoidTriggerEvent(VConstants.TRG_VALBLK)    // void trigger

/**
 * executed upon record validation
 */
object VALREC : BlockVoidTriggerEvent(VConstants.TRG_VALREC)    // void trigger

/**
 * is executed when the block is in the InsertMode. This trigger becomes active when
 * the user presses the key F4. It will then enable the system to load standard values
 * which will be proposed to the user if he wishes to enter new data.
 */
object DEFAULT : BlockVoidTriggerEvent(VConstants.TRG_DEFAULT)  // void trigger

/**
 * executed upon block initialization
 */
object INIT : BlockVoidTriggerEvent(VConstants.TRG_INIT)        // void trigger

/**
 * executed upon Reset command (ResetForm)
 */
object RESET : BlockBooleanTriggerEvent(VConstants.TRG_RESET)      // Boolean trigger

/**
 * a special trigger that returns a boolean value of whether the block have been changed or not,
 * you can use it to bypass the system control for changes by returning false in the trigger's method:
 *
 * triggers(BlockTrigger.CHANGED) {
 *   false
 * }
 *
 */
object CHANGED : BlockBooleanTriggerEvent(VConstants.TRG_CHANGED)  // Boolean trigger

/**
 * defines whether a block can or not be accessed, it must always return a boolean value.
 *
 * triggers(BlockTrigger.ACCESS) {
 *   Block.getMode == MOD_QUERY  // Tests if the block is in query mode,
 *                               //this block is only accessible on query mode
 * }
 *
 */
object ACCESS : BlockVoidTriggerEvent(VConstants.TRG_ACCESS)    // Void trigger


///////////////////////////////////////////////////////////////////////////
// REPORT TRIGGERS
///////////////////////////////////////////////////////////////////////////

/**
 * Block Triggers
 *
 * @param event the event of the trigger
 */
open class ReportTriggerEvent(val event: Int)

/**
 * Executed before the report is displayed.
 */
object PREREPORT : ReportTriggerEvent(Constants.TRG_PREREPORT)

/**
 * Executed after the report is closed.
 */
object POSTREPORT : ReportTriggerEvent(Constants.TRG_POSTREPORT)
