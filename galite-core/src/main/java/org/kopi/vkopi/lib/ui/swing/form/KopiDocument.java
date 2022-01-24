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
 * $Id: KopiDocument.java 35230 2017-09-13 18:27:19Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.Color;

import javax.swing.text.Document;

import org.kopi.vkopi.lib.ui.swing.base.Stateful;

/**
 * A text container for text that serves
 * as the model for swing text components.
 * The goal for this interface is to scale from very simple needs (a plain text textfield)
 * to complex needs (an HTML or XML document, for example).
 */
public interface KopiDocument extends Document, Stateful {

    public String getModelText();

    public void setModelText(String text);

    public void setState(int state);

    public void setHasCriticalValue(boolean hasCriticalValue);

    public void setHasAction(boolean hasAction);

    public void setBgColor(Color bgColor);

    public void setAlert(boolean alert);

    public void setAutofill(boolean autofill);
}
