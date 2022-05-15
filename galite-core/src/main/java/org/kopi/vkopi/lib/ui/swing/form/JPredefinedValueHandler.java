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
 * $Id: JPredefinedValueHandler.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;

import javax.swing.JColorChooser;

import org.kopi.galite.visual.form.AbstractPredefinedValueHandler;
import org.kopi.galite.visual.form.VField;
import org.kopi.galite.visual.form.VFieldUI;
import org.kopi.galite.visual.form.VForm;
import org.kopi.galite.visual.Message;
import org.kopi.galite.visual.VExecFailedException;

@SuppressWarnings("serial")
public class JPredefinedValueHandler extends AbstractPredefinedValueHandler {

    //----------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------

    public JPredefinedValueHandler(VFieldUI model, VForm form, VField field) {
        super(model, form, field);
    }

    /**
     *
     */
    public Color selectColor(Color color) {
        Color f = JColorChooser.showDialog((Component) getForm().getDisplay(),
                Message.INSTANCE.getMessage("color-chooser"),
                color);

        return f;
    }

    /**
     *
     */
    public LocalDate selectDate(LocalDate date) {
        return DateChooser.getDate((Container) getForm().getDisplay(), (Component) getField().getDisplay(), date);
    }

    /**
     *
     */
    public byte[] selectImage() throws VExecFailedException {
        File f = ImageFileChooser.chooseFile((Component) getForm().getDisplay());

        if (f == null) {
            return null;
        }

        try {
            FileInputStream is = new FileInputStream(f);
            byte[] b = new byte[is.available()];
            is.read(b);

            is.close();
            return b;
        } catch (Exception e) {
            throw new VExecFailedException("bad-file", e);
        }
    }
}
