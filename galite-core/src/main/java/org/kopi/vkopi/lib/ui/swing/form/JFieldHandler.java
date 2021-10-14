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
 * $Id: JFieldHandler.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.galite.visual.form.AbstractFieldHandler;
import org.kopi.galite.visual.form.VConstants;
import org.kopi.galite.visual.form.VFieldUI;
import org.kopi.vkopi.lib.ui.swing.visual.SwingThreadHandler;

/**
 * {@code JFieldHandler} is a swing implementation of the {@link AbstractFieldHandler}.
 */
@SuppressWarnings("serial")
public class JFieldHandler extends AbstractFieldHandler {

    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------

    public JFieldHandler(VFieldUI rowController) {
        super(rowController);
    }

    // ----------------------------------------------------------------------
    // ABSTRACTFIELDHANDLER IMPLEMENTATION
    // ----------------------------------------------------------------------

    @Override
    public boolean predefinedFill() {
        boolean filled;

        filled = getModel().fillField(new JPredefinedValueHandler(getRowController(), getModel().getForm(), getModel()));
        if (filled) {
            getRowController().getBlock().gotoNextField();
        }

        return filled;
    }

    @Override
    public void enter() {
        // this is the correct thread to calculate the display of the
        // field NOT later in the event thread
        final DField enterMe = (DField) getCurrentDisplay();

        if (enterMe != null) {
            SwingThreadHandler.start(new Runnable() {
                public void run() {
                    getRowController().resetCommands();
                    enterMe.enter(true);
                }
            });
        }
    }

    @Override
    public void leave() {
        // this is the correct thread to calculate the display of the
        // field NOT later in the event thread
        final DField leaveMe = (DField) getCurrentDisplay();

        if (leaveMe != null) {
            SwingThreadHandler.start(new Runnable() {
                public void run() {
                    getRowController().resetCommands();
                    leaveMe.leave();
                }
            });
        }
    }

    @Override
    public void labelChanged() {
        SwingThreadHandler.startEnqueued(new Runnable() {
            public void run() {
                getRowController().resetLabel();
            }
        });
    }

    @Override
    public void searchOperatorChanged() {
        int operator = getModel().getSearchOperator();
        final String info = operator == VConstants.SOP_EQ ? null : VConstants.Companion.getOPERATOR_NAMES()[operator];

        SwingThreadHandler.startEnqueued(new Runnable() {
            public void run() {
                if (getRowController().getLabel() != null) {
                    ((DLabel) getRowController().getLabel()).setInfoText(info);
                }
                if (getRowController().getDetailLabel() != null) {
                    ((DLabel) getRowController().getDetailLabel()).setInfoText(info);
                }
            }
        });
    }

    @Override
    public void valueChanged(int r) {
        final int dispRow = getRowController().getBlockView().getDisplayLine(r);

        if (dispRow != -1) {
            SwingThreadHandler.startEnqueued(new Runnable() {
                public void run() {
                    if (getRowController().getDisplays() != null) {
                        getRowController().getDisplays()[dispRow].updateText();
                    }
                    if (getRowController().getDetailDisplay() != null) {
                        getRowController().getDetailDisplay().updateText();
                    }
                }
            });
        }
    }

    @Override
    public void accessChanged(final int row) {
        if (getRowController().getBlockView().getDisplayLine(row) != -1) {
            SwingThreadHandler.startEnqueued(new Runnable() {
                public void run() {
                    getRowController().fireAccessHasChanged(row);
                }
            });
        }
    }

    @Override
    public void colorChanged(final int r) {
        SwingThreadHandler.startEnqueued(new Runnable() {
            public void run() {
                getRowController().fireColorHasChanged(r);
            }
        });
    }
}
