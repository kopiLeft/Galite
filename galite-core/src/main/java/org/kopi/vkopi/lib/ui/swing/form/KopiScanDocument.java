/*
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH
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
 * $Id: KopiScanDocument.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.kopi.galite.visual.form.ModelTransformer;
import org.kopi.galite.visual.form.VField;

/**
 * !!! NEED COMMENTS
 */
/*package*/ class KopiScanDocument extends KopiFieldDocument {

    public KopiScanDocument(VField model, ModelTransformer transformer) {
        super(model, transformer);

        scanedtext = new StringBuffer();
        progress = 0;
    }

    // ----------------------------------------------------------------------
    // MODEL / VIEW INTERFACE
    // -----------------------------------------------------------------------

    /**
     * Returns the text currently showed by this document
     */
    public String getModelText() {
        return scanedtext.toString();
    }

    /**
     * Changes the text of this document without checking
     */
    public void setModelText(String s) {
        scanedtext = new StringBuffer(s);
        if (s == null || s.length() == 0) {
            super.setModelText(""); // Ready to read
        } else {
            super.setModelText(PROGRESS_BAR.substring(0, 1)); // Reading
        }
        progress = 0;
    }

    // ----------------------------------------------------------------------
    // DOCUMENT IMPLEMENTATION
    // ----------------------------------------------------------------------

    public void remove(int offs, int len) throws BadLocationException {
        // delete is not supported
    }

    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {

        if (str == null) {
            return;
        }

        String text = scanedtext.toString();

        text = text + str;

        if (((VField) getModel()).checkText(text)) {
            scanedtext.append(str);
            ((VField) getModel()).onTextChange();
            progress = (progress + 1) % PROGRESS_BAR.length();
            super.setModelText(PROGRESS_BAR.substring(0, progress + 1));
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private StringBuffer scanedtext;
    private int progress;

    private static String PROGRESS_BAR = ".........................";
}
