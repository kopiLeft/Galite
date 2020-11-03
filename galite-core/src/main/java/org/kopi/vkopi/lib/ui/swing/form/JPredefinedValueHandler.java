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
 * $Id: JPredefinedValueHandler.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.vkopi.lib.form.AbstractPredefinedValueHandler;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.xkopi.lib.type.Date;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

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
    Color       f = JColorChooser.showDialog((Component)form.getDisplay(),
                                             Message.getMessage("color-chooser"),
                                             color);

    return f;
  }

  /**
   * 
   */
  public Date selectDate(Date date) {
    return DateChooser.getDate((Container)form.getDisplay(), (Component)field.getDisplay(), date);
  }

  /**
   * 
   */
  public byte[] selectImage() throws VException {
    File        f = ImageFileChooser.chooseFile((Component)form.getDisplay());

    if (f == null) {
      return null;
    }

    try {
      FileInputStream	is = new FileInputStream(f);
      byte[]            b = new byte[is.available()];
      is.read(b);

      is.close();
      return b;
    } catch (Exception e) {
      throw new VExecFailedException("bad-file", e);
    }
  }

}
