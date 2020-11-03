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
 * $Id: DActorField.java 35283 2018-01-05 09:00:51Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.galite.form.UActorField;
import org.kopi.galite.form.VConstants;
import org.kopi.galite.form.VFieldUI;
import org.kopi.vkopi.lib.ui.swing.base.JActorFieldButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UI Implementation of actor field in swing environment.
 */
public class DActorField extends DField implements UActorField {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public DActorField(VFieldUI model,
                     DLabel label,
                     int align,
                     int options,
                     boolean detail)
  {
    super(model, label, align, options, detail);
    button = createButton();
    if (button != null) {
      add(button, BorderLayout.CENTER);
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  @Override
  public void updateAccess() {
    super.updateAccess();
    if (button != null) {
      button.getAction().setEnabled(access >= VConstants.ACS_VISIT);
    }
  }
  
  @Override
  public void updateText() {}
  
  @Override
  public void updateFocus() {
    // NO FOCUS FOR ACTOR FIELDS
  }
  
  @Override
  public void forceFocus() {
    // NO FOCUS FOR ACTOR FIELDS
  }
  
  public void setBlink(boolean blink) {}

  public void updateColor() {
    // NOT SUPPORTED
  }

  public Object getObject() {
    return null;
  }

  protected void setDisplayProperties() {
    // NOT SUPPORTED
  }

  public String getText() {
    return null;
  }

  public void setHasCriticalValue(boolean b) {}

  public void addSelectionFocusListener() {}

  public void removeSelectionFocusListener() {}

  public void setSelectionAfterUpdateDisabled(boolean disable) {}

  /**
   * Creates the field button
   * @return The created button
   */
  protected JActorFieldButton createButton() {
    DActorFieldAction           action;
    
    action = new DActorFieldAction(getModel().getLabel());
    action.putValue(Action.SHORT_DESCRIPTION, getModel().getToolTip());
    action.setEnabled(getModel().getDefaultAccess() >= VConstants.ACS_VISIT);
    
    return new JActorFieldButton(action);
  }

  // --------------------------------------------------------------------
  // FILD ACTION
  // --------------------------------------------------------------------

  private class DActorFieldAction extends AbstractAction {

    // -----------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------

    public DActorFieldAction(String name) {
      super(name);
    }

    // ------------------------------------------------
    // MPLEMNTATION
    // ------------------------------------------------

    public void actionPerformed(ActionEvent e) {
      model.executeAction();
    }

    // -------------------------------------------------
    // DATA MEMBERS
    // -------------------------------------------------

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2304974107105391550L;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final JActorFieldButton               button;
  /**
   * Serial version UID
   */
  private static final long                     serialVersionUID = 3697344873853787723L;
}
