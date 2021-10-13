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
 * $Id: DImageField.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.kopi.galite.visual.form.VFieldUI;
import org.kopi.galite.visual.form.VImageField;

/**
 * DImageField is a panel composed in a Image field and a label behind
 */
public class DImageField extends DObjectField {

  // ----------------------------------------------------------------------
  // CONSTRUCTION
  // ----------------------------------------------------------------------


/**
   * Constructor
   *
   * @param	model		the model for this text field
   * @param	label		The label that describe this field
   * @param	height		the number of line
   * @param	width		the number of column
   * @param	options		The possible options (NO EDIT, NO ECHO)
   */
  public DImageField(VFieldUI model,
		     DLabel label,
		     int align,
		     int options,
		     int width,
		     int height,
                     boolean detail)
  {
    super(model, label, align, options, detail);
    this.width = width;
    this.height = height;

    icon = new JLabel();

    empty = new JPanel() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
        private static final long serialVersionUID = 4603093785481453119L;

	public Dimension getPreferredSize() {
          return new Dimension(DImageField.this.width, DImageField.this.height);
        }
      };

    //    empty.setBackground(DObject.CLR_FLD_BACK);

    add(empty, BorderLayout.CENTER);

    registerKeyboardAction(new AbstractAction() {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = -555322973317509685L;

	public void actionPerformed(ActionEvent e) {
          setObject(null);
        }},
      null,
      KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
      JComponent.WHEN_FOCUSED);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF ABSTRACTS METHODS
  // ----------------------------------------------------------------------

  /**
   * Returns the object associed to record r
   *
   * @return	the displayed value at this position
   */
  public Object getObject() {
    return image;
  }

  /**
   * Sets the object associed to record r
   *
   * @param	s		the object to set in
   */
  public void setObject(Object s) {
    if (image == null) {
      remove(empty);
    } else {
      remove(icon);
    }

    image = (byte[])s;

    if (image != null) {
      icon.setIcon(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(width, height, Image.SCALE_FAST)));
    }

    if (image == null) {
      add(empty, BorderLayout.CENTER);
    } else {
      add(icon, BorderLayout.CENTER);
    }

    setBlink(false);
    setBlink(true);
    repaint();
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  public void setDisplayProperties() {
    repaint();
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------

  /**
   * This method is called after an action of the user, object should
   * be redisplayed accordingly to changes.
   */

  public void updateAccess() {
    label.update(getModel(), getPosition());
  }

  public void updateText() {
    setObject(((VImageField)getModel()).getImage(model.getBlockView().getRecordFromDisplayLine(getPosition())));
    super.updateText();
  }

  public void updateFocus() {
    label.update(getModel(), getPosition());
    fireMouseHasChanged();
    super.updateFocus();
  }
  
  public void updateColor() {}

  /**
   * set blink state
   */
  public void setBlink(boolean start) {}

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	JLabel			icon;		// the image component
  private	JPanel			empty;		// the no image component
  private	int			width;
  private	int			height;
  private	byte[]			image;
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -4798732518710333986L;
}
