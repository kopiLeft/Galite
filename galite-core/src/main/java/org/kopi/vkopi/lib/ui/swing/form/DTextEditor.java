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
 * $Id: DTextEditor.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.galite.form.VFieldUI;

/**
 * DField is a panel composed in a text field and an information panel
 * The text field appear as a JLabel until it is edited
 */
public class DTextEditor extends DTextField {

    // ----------------------------------------------------------------------
    // CONSTRUCTION
    // ----------------------------------------------------------------------


    /**
     * Constructor
     *
     * @param    model        the model for this text field
     * @param    label        The label that describe this field
     * @param    width        the number of column
     * @param    options        The possible options (NO EDIT, NO ECHO)
     */
    public DTextEditor(VFieldUI model,
                       DLabel label,
                       int align,
                       int options,
                       int height,
                       boolean detail) {
        super(model, label, align, options, detail);
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 449581722481726591L;

}
