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

import org.kopi.galite.chart.CConstants
import org.kopi.galite.chart.VChartType
import org.kopi.galite.form.VConstants
import org.kopi.galite.report.Constants

/**
 * This class represents a trigger, ie an action to be executed on events
 * @param events        the event of the trigger
 * @param action       the action to perform
 */
abstract class Trigger(val events: Long, val action: Action<*>) {

  /**
   * Return trigger array
   */
  abstract fun getTriggers(): IntArray
}

///////////////////////////////////////////////////////////////////////////
// FORM TRIGGERS
///////////////////////////////////////////////////////////////////////////

/**
 * Form Triggers
 *
 * @param event the event of the trigger
 */
open class FormTriggerEvent<T>(val event: Int)

/**
 * executed when initializing the form and before the PREFORM Trigger, also executed at ResetForm command
 */
object INITFORM : FormTriggerEvent<Unit>(VConstants.TRG_INIT)             // void trigger

/**
 * executed before the form is displayed and after the INIT Trigger, not executed at ResetForm command
 */
object PREFORM : FormTriggerEvent<Unit>(VConstants.TRG_PREFORM)       // void trigger

/**
 * executed when closing the form
 */
object POSTFORM : FormTriggerEvent<Unit>(VConstants.TRG_POSTFORM)     // void trigger

/**
 * executed upon ResetForm command
 */
object RESETFORM : FormTriggerEvent<Boolean>(VConstants.TRG_RESET)        // Boolean trigger

/**
 * a special trigger that returns a boolean value of whether the form have been changed or not,
 * you can use it to bypass the system control for changes this way :
 *
 * trigger(CHANGED) {
 *   false
 * }
 */
object CHANGEDFORM : FormTriggerEvent<Boolean>(VConstants.TRG_CHANGED)    // Boolean trigger

/**
 * executed when quitting the form
 * actually not available
 */
object QUITFORM : FormTriggerEvent<Boolean>(VConstants.TRG_QUITFORM)  // Boolean trigger

///////////////////////////////////////////////////////////////////////////
// BLOCK TRIGGERS
///////////////////////////////////////////////////////////////////////////

/**
 * Block Triggers
 *
 * @param event the event of the trigger
 */
open class BlockTriggerEvent<T>(val event: Int)

/**
 * Block protected Triggers
 *
 * @param event the event of the trigger
 */
open class BlockProtectedTriggerEvent(event: Int) : BlockTriggerEvent<Unit>(event)

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
object PREREC : BlockTriggerEvent<Unit>(VConstants.TRG_PREREC)    // void trigger

/**
 * executed upon record exit
 */
object POSTREC : BlockTriggerEvent<Unit>(VConstants.TRG_POSTREC)  // void trigger

/**
 * executed upon block entry
 */
object PREBLK : BlockTriggerEvent<Unit>(VConstants.TRG_PREBLK)    // void trigger

/**
 * executed upon block exit
 */
object POSTBLK : BlockTriggerEvent<Unit>(VConstants.TRG_POSTBLK)  // void trigger

/**
 * executed upon block validation
 */
object VALBLK : BlockTriggerEvent<Unit>(VConstants.TRG_VALBLK)    // void trigger

/**
 * executed upon record validation
 */
object VALREC : BlockTriggerEvent<Unit>(VConstants.TRG_VALREC)    // void trigger

/**
 * is executed when the block is in the InsertMode. This trigger becomes active when
 * the user presses the key F4. It will then enable the system to load standard values
 * which will be proposed to the user if he wishes to enter new data.
 */
object DEFAULT : BlockTriggerEvent<Unit>(VConstants.TRG_DEFAULT)  // void trigger

/**
 * executed upon block initialization
 */
object INIT : BlockTriggerEvent<Unit>(VConstants.TRG_INIT)        // void trigger

/**
 * executed upon Reset command (ResetForm)
 */
object RESET : BlockTriggerEvent<Boolean>(VConstants.TRG_RESET)      // Boolean trigger

/**
 * a special trigger that returns a boolean value of whether the block have been changed or not,
 * you can use it to bypass the system control for changes by returning false in the trigger's method:
 *
 * trigger(CHANGED) {
 *   false
 * }
 *
 */
object CHANGED : BlockTriggerEvent<Boolean>(VConstants.TRG_CHANGED)  // Boolean trigger

/**
 * defines whether a block can or not be accessed, it must always return a boolean value.
 *
 * trigger(ACCESS) {
 *   Block.getMode == MOD_QUERY  // Tests if the block is in query mode,
 *                               //this block is only accessible on query mode
 * }
 *
 */
object ACCESS : BlockTriggerEvent<Unit>(VConstants.TRG_ACCESS)    // Void trigger

///////////////////////////////////////////////////////////////////////////
// REPORT TRIGGERS
///////////////////////////////////////////////////////////////////////////

/**
 * Block Triggers
 *
 * @param event the event of the trigger
 */
open class ReportTriggerEvent<T>(val event: Int)

/**
 * Executed before the report is displayed.
 */
object PREREPORT : ReportTriggerEvent<Unit>(Constants.TRG_PREREPORT)

/**
 * Executed after the report is closed.
 */
object POSTREPORT : ReportTriggerEvent<Unit>(Constants.TRG_POSTREPORT)

///////////////////////////////////////////////////////////////////////////
// CHART TRIGGERS
///////////////////////////////////////////////////////////////////////////

/**
 * Chart Triggers
 *
 * @param event the event of the trigger
 */
open class ChartTriggerEvent<T>(val event: Int)

/**
 * Executed before the chart is displayed.
 */
object PRECHART : ChartTriggerEvent<Unit>(CConstants.TRG_PRECHART)

/**
 * Executed at chart initialization.
 */
object INITCHART : ChartTriggerEvent<Unit>(CConstants.TRG_INIT)

/**
 * Executed after the chart initialization. This trigger should return a fixed type for the chart
 * [org.kopi.galite.chart.VChartType].
 */
object CHARTTYPE : ChartTriggerEvent<VChartType>(CConstants.TRG_CHARTTYPE)

/**
 * Executed after the chart is closed.
 */
object POSTCHART : ChartTriggerEvent<Unit>(CConstants.TRG_POSTCHART)
