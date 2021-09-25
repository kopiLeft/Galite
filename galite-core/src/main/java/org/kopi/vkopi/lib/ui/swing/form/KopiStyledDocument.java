/*
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH
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
 * $Id: KopiStyledDocument.java 35230 2017-09-13 18:27:19Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.IOException;
import java.text.DecimalFormatSymbols;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.kopi.galite.visual.util.base.InconsistencyException;
import org.kopi.galite.visual.form.ModelTransformer;
import org.kopi.galite.visual.form.VField;
import org.kopi.galite.visual.form.VFixnumField;
import org.kopi.galite.visual.visual.ApplicationContext;

public class KopiStyledDocument extends HTMLDocument implements KopiDocument {

    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------

    public KopiStyledDocument(VField model, ModelTransformer transformer) {
        this.model = model;
        this.transformer = transformer;
    }

    // ----------------------------------------------------------------------
    // MODEL / VIEW INTERFACE
    // ----------------------------------------------------------------------

    public void setEditorKit(HTMLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    /**
     * Returns the text currently showed by this document
     */
    public synchronized String getModelText() {
        try {
            String text;

            text = getText(0, getLength());
            return transformer.toModel(text);
        } catch (BadLocationException e) {
            throw new InconsistencyException("BadLocationException in KopiFieldDocument");
        }
    }

    /**
     * Changes the text of this document without checking
     */
    public synchronized void setModelText(String s) {
        try {
            super.remove(0, getLength());
            s = transformer.toGui(s);
            try {
                editorKit.insertHTML(this, getLength(), s, 0, 0, null);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (BadLocationException e) {
            throw new InconsistencyException("BadLocationException in KopiFieldDocument");
        }

    }

    // ----------------------------------------------------------------------
    // DOCUMENT IMPLEMENTATION
    // ----------------------------------------------------------------------

    public void remove(int offs, int len) throws BadLocationException {
        String text;

        text = getText(0, getLength());
        text = text.substring(0, offs) + text.substring(offs + len);
        text = transformer.toModel(text);

        if (!transformer.checkFormat(text)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        if (model.checkText(text)) {
            super.remove(offs, len);
            model.onTextChange(getText(0, getLength()));
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {

        if (str == null) {
            return;
        }
        // special treatment for decimal separator
        if (model instanceof VFixnumField && str.equals(".")) {
            DecimalFormatSymbols symbols;

            symbols = new DecimalFormatSymbols(ApplicationContext.Companion.getDefaultLocale());
            if (symbols.getDecimalSeparator() != '.') {
                str = str.replace('.', symbols.getDecimalSeparator());
            }
        }

        String text = getText(0, getLength());
        text = text.substring(0, offs) + str + text.substring(offs);
        text = transformer.toModel(text);

        if (!transformer.checkFormat(text)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        if (model.checkText(text)) {
            super.insertString(offs, str, a);
            model.onTextChange(getText(0, getLength()));
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // ----------------------------------------------------------------------
    // STATEFUL IMPLEMENTATION
    // ----------------------------------------------------------------------

    public int getState() {
        return state;
    }

    public boolean getAutofill() {
        return autofill;
    }

    public boolean isAlert() {
        return alert;
    }

    public boolean hasCriticalValue() {
        return hasCriticalValue;
    }

    public boolean hasAction() {
        return hasAction;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Object getModel() {
        return model;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setHasCriticalValue(boolean hasCriticalValue) {
        this.hasCriticalValue = hasCriticalValue;
    }

    public void setHasAction(boolean hasAction) {
        this.hasAction = hasAction;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public void setAutofill(boolean autofill) {
        this.autofill = autofill;
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private final VField model;
    private final ModelTransformer transformer;
    private HTMLEditorKit editorKit;

    private int state;
    private boolean alert;
    private boolean autofill = false;
    private boolean hasCriticalValue;
    private boolean hasAction;
    private Color bgColor;
}
