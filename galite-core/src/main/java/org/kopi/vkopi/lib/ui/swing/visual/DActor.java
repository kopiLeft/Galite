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
 * $Id: DActor.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import org.kopi.vkopi.lib.ui.swing.base.JMenuButton;
import org.kopi.galite.visual.visual.UActor;
import org.kopi.galite.visual.visual.VActor;

@SuppressWarnings("serial")
public class DActor implements UActor {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public DActor(VActor model) {
    this.model = model;
    init();
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  
  public boolean isEnabled() {
    return action.isEnabled();
  }

  
  public void setEnabled(boolean enabled) {
    action.setEnabled(enabled);
  }

  
  public boolean isVisible() {
    return button != null && button.isVisible();
  }

  
  public void setVisible(boolean visible) {
    if (button != null) {
      button.setVisible(visible);
    }
  }

  
  public void setModel(VActor model) {
    this.model = model;
    init();
  }

  
  public VActor getModel() {
    return model;
  }

  public JButton getButton() {
    return button;
  }

  /**
   * Returns the actor action.
   */
  public Action getAction() {
    return action;
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  /**
   * Loads the actor icon
   */
  private final ImageIcon loadImage(String iconName) {
    ImageIcon   image;

    image = Utils.getImage(iconName + ".png");

    if (image == null || image == Utils.UKN_IMAGE) {
      image = Utils.getImage(iconName + ".gif");
    }

    return image;
  }

  private void init() {
    action = new DActorAction(model.getMenuItem(),
                              (model.getIconName() != null) ?
                              loadImage(model.getIconName()) :
                              null);
    if (model.getAcceleratorKey() != KeyEvent.VK_UNDEFINED) {
      action.putValue(Action.ACCELERATOR_KEY,
                      KeyStroke.getKeyStroke(model.getAcceleratorKey(),
                                             model.getAcceleratorModifier()));
    }

    action.putValue(Action.SHORT_DESCRIPTION, model.getHelp());
    action.setEnabled(false);

    if (button == null) {
      button = new JMenuButton(action);
    }
  }

  // --------------------------------------------------------------------
  // DACTOR ACTION
  // --------------------------------------------------------------------

  private class DActorAction extends AbstractAction {

    // -----------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------

    /*package*/ DActorAction(String name, Icon icon) {
      super(name, icon);
    }

    // ------------------------------------------------
    // ABSTRACT ACTION IMPLEMNTATION
    // ------------------------------------------------

    public void actionPerformed(ActionEvent e) {
      model.performAction();
    }

    // -------------------------------------------------
    // DATA MEMBERS
    // -------------------------------------------------

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long	serialVersionUID = -6510825866215273279L;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JButton					button;
  private Action					action;
  private VActor					model;
}
