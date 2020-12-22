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

class FieldTriggers {
///////////////////////////////////////////////////////////////////////////
// FIELD TRIGGERS
///////////////////////////////////////////////////////////////////////////
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
   * Executed on field content change
   */
  object POSTCHG : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Executed upon entry of field
   */
  object PREFLD : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * is executed upon exit of field
   */
  object POSTFLD   : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * is executed before validating any new entry
   */
  object PREVAL    : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * is executed after field change and validation
   */
  object VALFLD    : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   *  this is the same trigger as VALFLD
   */
  object VALIDATE  : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Defines the default value of the field to be set if the setDefault() method is called
   * (this method is automatically called when the user choose the insert command)
   */
  object DEFAULT   : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Not defined actually
   */
  object FORMAT    : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * ACCESS is a special trigger that defines how a field can be accessed.
   * This trigger must return one of these values
   * ACS_SKIPPED, ACS_HIDDEN, ACS_VISIT or ACS_MUSTFILL.
   */
  object ACCESS    : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Equates the value of two fields
   */
  object VALUE     : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Must return a boolean value, if "true" the cursor will move to the next field
   */
  object AUTOLEAVE : FieldBooleanTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Executed before inserting a row of the database
   */
  object PREINS    : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Executed before updating a row of the database
   */
  object PREUPD    : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Executed before deleting a row of the database
   */
  object PREDEL    : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Executed after inserting a row of the database
   */
  object POSTINS   : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

  /**
   * Executed after updating a row of the database
   */
  object POSTUPD   : FieldVoidTriggerEvent(VConstants.TRG_POSTCHG)

}
