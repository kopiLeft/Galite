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
 * $Id: KeyNavigator.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.kopi.galite.visual.form.VField;
import org.kopi.galite.visual.util.PrintJob;
import org.kopi.galite.visual.visual.Action;
import org.kopi.galite.visual.visual.PrinterManager;
import org.kopi.galite.visual.visual.VException;
import org.kopi.vkopi.lib.ui.swing.base.Stateful;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;

public class KeyNavigator extends AbstractAction {

    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------

    protected KeyNavigator(int keyCode) {
        super("navigation-key");
        this.keyCode = keyCode;
    }

    public void actionPerformed(final ActionEvent e) {
        final Stateful sharedText;
        final DField fieldView;

        try {
            sharedText = (Stateful) ((JTextComponent) e.getSource()).getDocument();
            // !! find fieldView toDo: find more beautyful way
            fieldView = (DField) Utils.getRoot(((JComponent) e.getSource()), DField.class);
        } catch (ClassCastException cce) {
            return;
        }

        if (sharedText == null) {
            return;
        } else {
            final VField field = (VField) sharedText.getModel();
            if (field == null ||
                    field.getForm() == null ||
                    field.getForm().getActiveBlock() == null ||
                    (field.getForm().getActiveBlock().getActiveField() != field &&
                            keyCode != KEY_NEXT_BLOCK)) {
                return;
            } else {
                processKeyCode(fieldView, field, (JTextComponent) e.getSource());
            }
        }
    }

    protected final void processKeyCode(final DField fieldView, final VField field, final JTextComponent sharedText) {
        Action action;

        switch (keyCode) {
            case KEY_NEXT_FIELD:
                action = new Action("keyKEY_TAB") {
                    public void execute() {
                        field.getBlock().getForm().getActiveBlock().gotoNextField();
                    }
                };
                break;
            case KEY_PREV_FIELD:
                action = new Action("keyKEY_STAB") {
                    public void execute() {
                        field.getBlock().getForm().getActiveBlock().gotoPrevField();
                    }
                };
                break;
            case KEY_NEXT_BLOCK:
                action = new Action("keyKEY_BLOCK") {
                    public void execute() {
                        field.getBlock().getForm().gotoNextBlock();
                    }
                };
                break;
            case KEY_REC_UP:
                action = new Action("keyKEY_REC_UP") {
                    public void execute() {
                        fieldView.getBlockView().getModel().gotoPrevRecord();
                    }
                };
                break;
            case KEY_REC_DOWN:
                action = new Action("keyKEY_REC_DOWN") {
                    public void execute() {
                        fieldView.getBlockView().getModel().gotoNextRecord();
                    }
                };
                break;
            case KEY_REC_FIRST:
                action = new Action("keyKEY_REC_FIRST") {
                    public void execute() {
                        field.getBlock().getForm().getActiveBlock().gotoFirstRecord();
                    }
                };
                break;
            case KEY_REC_LAST:
                action = new Action("keyKEY_REC_LAST") {
                    public void execute() {
                        field.getBlock().getForm().getActiveBlock().gotoLastRecord();
                    }
                };
                break;
            case KEY_EMPTY_FIELD:
                action = new Action("keyKEY_ALTENTER") {
                    public void execute() {
                        field.getBlock().getForm().getActiveBlock().gotoNextEmptyMustfill();
                    }
                };
                break;
            case KEY_DIAMETER:
                action = new Action("keyKEY_DIAMETER") {
                    public void execute() {
                        JTextComponent text = sharedText;

                        text.replaceSelection("\u00D8");
                        text.select(text.getSelectionStart() + 1, text.getSelectionStart() + 1);
                    }
                };
                // execute it in the event-dispatching-thread!
                fieldView.getBlockView().getFormView().performBasicAction(action);
                return;
            case KEY_ESCAPE:
                fieldView.getBlockView().getFormView().closeWindow();
                action = null;
                break;
            case KEY_PRINTFORM:
                action = new Action("keyKEY_ALTENTER") {
                    public void execute() {
                        PrintJob job = null;
                        try {
                            job = fieldView.getBlockView().getFormView().printForm();
                        } catch (VException e) {
                            e.printStackTrace();
                        }

                        PrinterManager.Companion.getPrinterManager().getCurrentPrinter().print(job);

                    }
                };
                break;
            default:
                action = processSpecificKeyCode(fieldView, field);
        }
        if (action != null) {
            fieldView.getBlockView().getFormView().performAsyncAction(action);
        }
    }

    /**
     * Subclasses must override this method to process their specific
     * keys they are added by addSpecificNavigationKeys
     */
    protected Action processSpecificKeyCode(final DField fieldView, final VField field) {
        Action action;

        switch (keyCode) {
            case KEY_PREV_VAL:
                action = new Action("keyKEY_LIST_UP") {
                    public void execute() {
                        fieldView.getRowController().previousEntry();
                    }
                };
                break;
            case KEY_NEXT_VAL:
                action = new Action("keyKEY_LIST_DOWN") {
                    public void execute() {
                        fieldView.getRowController().nextEntry();
                    }
                };
                break;
            default:
                action = null;
        }

        return action;
    }

    // ----------------------------------------------------------------------
    // get a navigator
    // ----------------------------------------------------------------------

    public static KeyNavigator getKeyNavigator(int code) {
        return navigators[code];
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    protected final int keyCode;

    public static final int KEY_NEXT_FIELD = 0;
    public static final int KEY_PREV_FIELD = 1;
    public static final int KEY_REC_UP = 2;
    public static final int KEY_REC_DOWN = 3;
    public static final int KEY_REC_FIRST = 4;
    public static final int KEY_REC_LAST = 5;
    public static final int KEY_EMPTY_FIELD = 6;
    public static final int KEY_NEXT_BLOCK = 7;
    public static final int KEY_PREV_VAL = 8;
    public static final int KEY_NEXT_VAL = 9;
    public static final int KEY_DIAMETER = 10;
    public static final int KEY_ESCAPE = 11;
    public static final int KEY_PRINTFORM = 12;

    public static final KeyNavigator[] navigators;

    static {
        navigators = new KeyNavigator[]{
                new KeyNavigator(KEY_NEXT_FIELD),
                new KeyNavigator(KEY_PREV_FIELD),
                new KeyNavigator(KEY_REC_UP),
                new KeyNavigator(KEY_REC_DOWN),
                new KeyNavigator(KEY_REC_FIRST),
                new KeyNavigator(KEY_REC_LAST),
                new KeyNavigator(KEY_EMPTY_FIELD),
                new KeyNavigator(KEY_NEXT_BLOCK),
                new KeyNavigator(KEY_PREV_VAL),
                new KeyNavigator(KEY_NEXT_VAL),
                new KeyNavigator(KEY_DIAMETER),
                new KeyNavigator(KEY_ESCAPE),
                new KeyNavigator(KEY_PRINTFORM)
        };
    }
}
