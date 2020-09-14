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

package org.kopi.galite.l10n

import org.jdom2.Document
import org.jdom2.Element
import org.kopi.galite.util.base.InconsistencyException

/**
 * Implements a report localizer.
 */
class ReportLocalizer(manager: LocalizationManager, document: Document) : Localizer(manager) {
    // ----------------------------------------------------------------------
    // ACCESSORS
    // ----------------------------------------------------------------------
    /**
     * Returns the value of the title attribute.
     */
    val title: String
        get() = root.getAttributeValue("title")

    /**
     * Returns the value of the help attribute.
     */
    val help: String
        get() = root.getAttributeValue("help")

    /**
     * Constructs a field localizer for the given field.
     *
     * @param             ident           the identifier of the field
     */
    fun getFieldLocalizer(ident: String): FieldLocalizer {
        return FieldLocalizer(manager,
                              Utils.lookupChild(root, "field", "ident", ident))
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    private val root: Element = document.rootElement

    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    /**
     * Constructor
     *
     * @param             manager         the manager to use for localization
     * @param             document        the document containing the report localization
     */
    init {
        if (root.name != "report") {
            throw InconsistencyException("bad root element $root")
        }
    }
}
