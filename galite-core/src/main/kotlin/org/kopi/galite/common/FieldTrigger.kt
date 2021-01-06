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

/**
 * This class represents field triggers, ie an action to be executed on events
 */
class FieldTriggers {

  /**
   * ACCESS is a special trigger that defines how a field can be accessed.
   * This trigger must return one of these values
   * ACS_SKIPPED, ACS_HIDDEN, ACS_VISIT or ACS_MUSTFILL.
   */
  object ACCESS : FieldIntTriggerEvent(VConstants.TRG_FLDACCESS)

  /**
   * Not defined actually
   */
  object FORMAT : FieldVoidTriggerEvent(VConstants.TRG_FORMAT)

  /**
   * Must return a boolean value, if "true" the cursor will move to the next field
   */
  object AUTOLEAVE : FieldBooleanTriggerEvent(VConstants.TRG_AUTOLEAVE)

  /**
   * Defines the default value of the field to be set if the setDefault() method is called
   * (this method is automatically called when the user choose the insert command)
   */
  object DEFAULT : FieldVoidTriggerEvent(VConstants.TRG_DEFAULT)

  /**
   * Executed on field content change
   */
  object POSTCHG : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Executed before dropping file
   */
  object PREDROP : FieldVoidTriggerEvent(VConstants.TRG_PREDROP)

  /**
   * Executed after dropping file
   */
  object POSTDROP : FieldVoidTriggerEvent(VConstants.TRG_POSTDROP)

  /**
   * Executed upon exit of field
   */
  object POSTFLD : FieldVoidTriggerEvent(VConstants.TRG_POSTFLD)

  /**
   * Executed after inserting a row of the database
   */
  object POSTINS : FieldProtectedTriggerEvent(VConstants.TRG_POSTINS)

  /**
   * Executed after updating a row of the database
   */
  object POSTUPD : FieldProtectedTriggerEvent(VConstants.TRG_POSTUPD)

  /**
   * Executed before deleting a row of the database
   */
  object PREDEL : FieldProtectedTriggerEvent(VConstants.TRG_PREDEL)

  /**
   * Executed upon entry of field
   */
  object PREFLD : FieldVoidTriggerEvent(VConstants.TRG_PREFLD)

  /**
   * Executed before inserting a row of the database
   */
  object PREINS : FieldProtectedTriggerEvent(VConstants.TRG_PREINS)

  /**
   * Executed before updating a row of the database
   */
  object PREUPD : FieldProtectedTriggerEvent(VConstants.TRG_PREUPD)

  /**
   * Executed before validating any new entry
   */
  object PREVAL : FieldVoidTriggerEvent(VConstants.TRG_PREVAL)

  /**
   * Executed after field change and validation
   */
  object VALFLD : FieldVoidTriggerEvent(VConstants.TRG_VALFLD)

  /**
   * This is the same trigger as VALFLD
   */
  object VALIDATE : FieldVoidTriggerEvent(VConstants.TRG_VALFLD)

  /**
   * Equates the value of two fields
   */
  object VALUE : FieldObjectTriggerEvent(VConstants.TRG_VALUE)

  /**
   * Make field clickable and execute an action
   */
  object ACTION : FieldVoidTriggerEvent(VConstants.TRG_ACTION)
}

/**
 * Field Triggers
 *
 * @param event the event of the trigger
 */
open class FieldTriggerEvent(val event: Int)

/**
 * Field void Triggers
 *
 * @param event the event of the trigger
 */
open class FieldVoidTriggerEvent(event: Int) : FieldTriggerEvent(event)

/**
 * Field boolean Triggers
 *
 * @param event the event of the trigger
 */
open class FieldBooleanTriggerEvent(event: Int) : FieldTriggerEvent(event)

/**
 * Field protected Triggers
 *
 * @param event the event of the trigger
 */
open class FieldProtectedTriggerEvent(event: Int) : FieldTriggerEvent(event)

/**
 * Field object Triggers
 *
 * @param event the event of the trigger
 */
open class FieldObjectTriggerEvent(event: Int) : FieldTriggerEvent(event)

/**
 * Field init Triggers
 *
 * @param event the event of the trigger
 */
open class FieldIntTriggerEvent(event: Int) : FieldTriggerEvent(event)
