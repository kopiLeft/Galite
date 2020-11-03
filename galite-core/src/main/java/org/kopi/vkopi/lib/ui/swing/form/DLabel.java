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
 * $Id: DLabel.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.galite.form.ULabel;
import org.kopi.galite.form.VConstants;
import org.kopi.galite.form.VField;
import org.kopi.vkopi.lib.ui.swing.base.JFieldLabel;
import org.kopi.vkopi.lib.ui.swing.base.MultiLineToolTip;

import javax.swing.*;
import java.awt.*;

/**
 * !!! NEED COMMENTS
 */
public class DLabel extends JFieldLabel implements ULabel {

/**
   * Constructor
   */
  public DLabel(String text, String help) {
    init(text, help);
  }

  /**
   *
   */
  public void init(String text, String help) {
    setText(text == null ? "" : text);
    setToolTipText(help);
  }

  public JToolTip createToolTip() {
    MultiLineToolTip tip = new MultiLineToolTip();
    tip.setComponent(this);

    return tip;
  }

  /**
   * Model access has changed, change accordingly.
   */
  public void update(VField model, int row) {
    int		oldState = getState();
    int         newState = 0;

    if (model.hasFocus()) {
      newState |= FOCUSED;
    }
    if ((model.getOptions() & VConstants.FDO_NOEDIT) != 0) {
      newState |= NOEDIT;
    }
    if (model.getBlock().isMulti() && !model.getBlock().noChart()) {
      newState |= CHART;
    }

    switch (model.getAccess(row)) {
    case VConstants.ACS_HIDDEN:
      newState |= HIDDEN;
      break;
    case VConstants.ACS_MUSTFILL:
      newState |= MUSTFILL;
      break;
    case VConstants.ACS_VISIT:
      newState |= VISIT;
      break;
    default:
      newState |= SKIPPED;
      break;
    }
    setState(newState);

    if (getState() == oldState) {
      return;
    }

    if ((getState() & FLD_MASK) == HIDDEN) {
     if (isVisible()) {
	setVisible(false);
      }
    } else {
      if (!isVisible()) {
	setVisible(true);
      }

      repaint();
    }

  }

  /**
   * prepare a snapshot
   *
   * @param	fieldPos	position of this field within block visible fields
   */
  public void prepareSnapshot(boolean activ) {
    if (activ) {
      setForeground(Color.black);
      //      setFont(DObject.FNT_DIALOG);
      setFont(UIManager.getFont("snapshot.font.dialog"));
      setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
    } else {
      setForeground(Color.darkGray);
      setFont(UIManager.getFont("snapshot.font.dialog"));
    }
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -8806871660434906097L;
}
