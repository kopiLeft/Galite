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

package org.kopi.galite.visual

/**
 * Represents an VDefaultActor model.
 *
 * @param code                the code of this default command
 * @param menuIdent           the qualified name of menu source file which this actor belongs to
 * @param menuSource          the menu source qualified name
 * @param actorIdent          the qualified name of actor's source file
 * @param actorSource         the actor source qualified name
 * @param acceleratorKey      the accelerator key description
 * @param acceleratorModifier The modifier accelerator key
 */
class VDefaultActor(val code: Int,
                    menuIdent: String,
                    menuSource: String,
                    actorIdent: String,
                    actorSource: String,
                    iconName: String?,
                    acceleratorKey: Int,
                    acceleratorModifier: Int,
                    userActor: Boolean = false
) : VActor(menuIdent, menuSource, actorIdent, actorSource, iconName, acceleratorKey, acceleratorModifier, userActor)
