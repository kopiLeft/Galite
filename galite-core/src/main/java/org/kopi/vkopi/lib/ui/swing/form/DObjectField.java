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
 * $Id: DObjectField.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * DImageField is a panel composed in a Image field and a label behind
 */
public abstract class DObjectField extends DField {

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
  public DObjectField(VFieldUI model,
		      DLabel label,
		      int align,
		      int options,
                      boolean detail)
  {
    super(model, label, align, options, detail);
    addNavigationKey();
    setRequestFocusEnabled(true);
    setFocusCycleRoot(true);
  }

  // ----------------------------------------------------------------------
  // KEY EVENTS
  // ----------------------------------------------------------------------

  /**
   *
   */
  /*package*/ void addNavigationKey() {
    addKey(KEY_TAB, KeyEvent.VK_ENTER, 0);
    addKey(KEY_TAB, KeyEvent.VK_TAB, 0);
    addKey(KEY_STAB, KeyEvent.VK_TAB, InputEvent.SHIFT_MASK);
    addKey(KEY_BLOCK, KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK);
    addKey(KEY_REC_UP, KeyEvent.VK_PAGE_UP, 0);
    addKey(KEY_REC_DOWN, KeyEvent.VK_PAGE_DOWN, 0);
    addKey(KEY_REC_FIRST, KeyEvent.VK_HOME, 0);
    addKey(KEY_REC_LAST, KeyEvent.VK_END, 0);
    addKey(KEY_STAB, KeyEvent.VK_LEFT, InputEvent.CTRL_MASK);
    addKey(KEY_TAB, KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK);
    addKey(KEY_REC_UP, KeyEvent.VK_UP, InputEvent.CTRL_MASK);
    addKey(KEY_REC_DOWN, KeyEvent.VK_DOWN, InputEvent.CTRL_MASK);
  }

  /**
   *
   */
  private void addKey(int code, int key, int mod) {
    KeyStroke	keyStroke = KeyStroke.getKeyStroke(key, mod);
    registerKeyboardAction((ActionListener)new Navigator(code),
			   null,
			   keyStroke,
			   JComponent.WHEN_FOCUSED);
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  void enter(boolean refresh) {
    super.enter(refresh);
    requestFocusInWindow();
    //    Done in DField
    //    fireFocusHasChanged();
    setBlink(true);
  }

  void leave() {
    setBlink(false);
    super.leave();
  }

  // ----------------------------------------------------------------------
  // FOCUS
  // ----------------------------------------------------------------------

  /**
   *
   */
  static class Navigator extends AbstractAction {
    	
	Navigator(int keyCode) {
      super("navigation-key");
      this.keyCode = keyCode;
    }

    public void actionPerformed(final ActionEvent e) {
      KopiAction	action = null;
      if (e.getSource() == null) {
	return;
      }
      final VField	field = ((DField)e.getSource()).getModel();
      if (field == null ||
	  field.getForm() == null ||
	  field.getForm().getActiveBlock() == null ||
	  (field.getForm().getActiveBlock().getActiveField() != field &&
	  keyCode != KEY_BLOCK)) {
	return;
      }
      switch (keyCode) {
      case KEY_TAB:
	action = new KopiAction("key: next field") {
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoNextField();
	  }
	};
	break;
      case KEY_STAB:
	action = new KopiAction("key: previous field") {
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoPrevField();
	    }
	};
	break;
      case KEY_BLOCK:
	action = new KopiAction("key: next block") {
	  public void execute() throws VException {
	    field.getForm().gotoNextBlock();
	  }
	};
	break;
      case KEY_REC_UP:
	action = new KopiAction("key: previous record") {
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoPrevRecord();
	  }
	};
	break;
      case KEY_REC_DOWN:
	action = new KopiAction("key: next record") {
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoNextRecord();
	  }
	};
	break;
      case KEY_REC_FIRST:
	action = new KopiAction("key: first record") {
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoFirstRecord();
	  }
	};
	break;
      case KEY_REC_LAST:
	action = new KopiAction("key: last record") {
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoLastRecord();
	  }
	};
	break;
      }

      //      field.getForm().performAction(action, false);
      // REPLACED BY:
      field.getForm().performAsyncAction(action);
    }

    private	int		keyCode;
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4243258834913065429L;

  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int	KEY_TAB			= 0;
  private static final int	KEY_STAB		= 1;
  private static final int	KEY_REC_UP		= 2;
  private static final int	KEY_REC_DOWN		= 3;
  private static final int	KEY_REC_FIRST		= 4;
  private static final int	KEY_REC_LAST		= 5;
  private static final int	KEY_BLOCK		= 6;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -4243258834913065429L;
}
