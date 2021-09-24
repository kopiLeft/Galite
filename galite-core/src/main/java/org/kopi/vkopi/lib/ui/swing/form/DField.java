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
 * $Id: DField.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.kopi.galite.base.UComponent;
import org.kopi.galite.form.UBlock;
import org.kopi.galite.form.UField;
import org.kopi.galite.form.VBlock;
import org.kopi.galite.form.VConstants;
import org.kopi.galite.form.VField;
import org.kopi.galite.form.VFieldUI;
import org.kopi.galite.form.VForm;
import org.kopi.vkopi.lib.ui.swing.base.FieldStates;
import org.kopi.vkopi.lib.ui.swing.base.JFieldButton;
import org.kopi.vkopi.lib.ui.swing.base.Utils;
import org.kopi.vkopi.lib.ui.swing.visual.SwingThreadHandler;
import org.kopi.galite.visual.Action;
import org.kopi.galite.visual.VColor;

/**
 * DField is a panel composed in a text field and an information panel
 * The text field appear as a JLabel until it is edited
 */
public abstract class DField extends JPanel implements UField {

    // ----------------------------------------------------------------------
    // CONSTRUCTION
    // ----------------------------------------------------------------------

    public DField(VFieldUI model,
                  DLabel label,
                  int align,
                  int options,
                  boolean detail) {
        setLayout(new BorderLayout());
        addMouseListener(new DFieldMouseListener());

        this.inDetail = detail;
        this.model = model;
        this.options = options;
        this.label = label;
        this.align = align;
        isEditable = (options & VConstants.FDO_NOEDIT) == 0;

        if ((!getModel().getBlock().isMulti() || getModel().getBlock().noChart() || isInDetail())) {
            //&& (getModel().getDefaultAccess() >= VConstants.ACS_SKIPPED)) { //!!! hidden fields can be visible in other block modes.
            JPanel optionPane = new JPanel();

            optionPane.setLayout(new BorderLayout());
            if (model.hasAutofill()) {
                info = new JFieldButton(listImg);
                info.addActionListener(new AbstractAction() {
                    /**
                     * Comment for <code>serialVersionUID</code>
                     */
                    private static final long serialVersionUID = 2172836625163999381L;

                    public void actionPerformed(ActionEvent e) {
                        getModel().getForm().performAsyncAction(new Action("autofill") {
                            public void execute() {
                                DField.this.model.transferFocus(DField.this);
                                DField.this.model.autofillButton();
                            }
                        });
                    }
                });
                info.setEnabled(getModel().getDefaultAccess() > VConstants.ACS_SKIPPED);

                optionPane.add(info, BorderLayout.WEST);
            }
            if (model.getDecrementCommand() != null) {
                decr = new JFieldButton(leftImg);
                decr.addActionListener(new AbstractAction() {
                    /**
                     * Comment for <code>serialVersionUID</code>
                     */
                    private static final long serialVersionUID = -6114904737254808842L;

                    public void actionPerformed(ActionEvent e) {
                        DField.this.model.getDecrementCommand().performAction();
                    }
                });
                optionPane.add(decr, BorderLayout.CENTER);
            }
            if (model.getIncrementCommand() != null) {
                incr = new JFieldButton(rightImg);
                incr.addActionListener(new AbstractAction() {
                    /**
                     * Comment for <code>serialVersionUID</code>
                     */
                    private static final long serialVersionUID = -3670989616287237005L;

                    public void actionPerformed(ActionEvent e) {
                        DField.this.model.getIncrementCommand().performAction();
                    }
                });
                optionPane.add(incr, BorderLayout.EAST);
            }
            add(optionPane, BorderLayout.EAST);
        }
    }


    /**
     * Returns the object associed to record r
     *
     * @return the displayed value at this position
     */
    public abstract Object getObject();


    /**
     * Field cell renderer
     */
    public void setPosition(int pos) {
        this.pos = pos;
    }

    /**
     * Field cell renderer
     *
     * @return the position in chart (0..nbDisplay)
     */
    public int getPosition() {
        return pos;
    }

    /**
     * Returns the alignment
     */
    public int getAlign() {
        return align;
    }

    // ----------------------------------------------------------------------
    // UI MANAGEMENT
    // ----------------------------------------------------------------------

    /*package*/ void enter(boolean refresh) {
        updateFocus();
    }

    /*package*/ void leave() {
        updateFocus();
    }

    // ----------------------------------------------------------------------
    // DRAWING
    // ----------------------------------------------------------------------

    public void updateText() {
    }

    public void updateFocus() {
        SwingThreadHandler.verifyRunsInEventThread("DField.updateFocus");
        if (modelHasFocus()) {
            final VForm form = getModel().getForm();

            form.setInformationText(getModel().getToolTip());
            form.setFieldSearchOperator(getModel().getSearchOperator());
        }
    }

    public void forceFocus() {
        // not used
    }

    public void updateAccess() {
        SwingThreadHandler.verifyRunsInEventThread("DField.updateAccess");
        access = getAccess();

        setVisible(access != VConstants.ACS_HIDDEN);
        fireMouseHasChanged(); // $$$

        update(info);
        update(incr);
        update(decr);
    }

    /**
     * This method is called after an action of the user, object should
     * be redisplayed accordingly to changes.
     */
    public void update() {
        // overridden in subclasses
    }

    /**
     *
     */
    public UBlock getBlockView() {
        return model.getBlockView();
    }

    protected final boolean modelHasFocus() {
        if (getModel() == null) {
            return false;
        }

        final VBlock block = getModel().getBlock();
        return getModel().hasFocus()
                && block.getActiveRecord() == model.getBlockView().getRecordFromDisplayLine(pos);
    }

    protected final boolean isSkipped() {
        final VBlock block = getModel().getBlock();
        return getAccess() == VConstants.ACS_SKIPPED
                || !block.isRecordAccessible(model.getBlockView().getRecordFromDisplayLine(getPosition()));
    }

    public final int getAccess() {
        return getAccessAt(getPosition());
    }

    public final Color getForeground() {
        return getForegroundAt(getPosition());
    }

    public final Color getBackground() {
        return getBackgroundAt(getPosition());
    }

    protected final int getAccessAt(int at) {
        if (getModel() != null) {
            return getModel().getAccess(model.getBlockView().getRecordFromDisplayLine(at));
        } else {
            return VConstants.ACS_SKIPPED;
        }
    }

    protected final Color getForegroundAt(int at) {
        if (model != null) {
            VColor foreground;

            foreground = getModel().getForeground(model.getBlockView().getRecordFromDisplayLine(at));
            return foreground == null ? null : new Color(foreground.getRGB());
        } else {
            return null;
        }
    }

    protected final Color getBackgroundAt(int at) {
        if (model != null) {
            VColor background;

            background = getModel().getBackground(model.getBlockView().getRecordFromDisplayLine(at));
            return background == null ? null : new Color(background.getRGB());
        } else {
            return null;
        }
    }

    public final VField getModel() {
        return model.getModel();
    }

    public final void fireMouseHasChanged() {
        final int localAccess = getAccess();
        final int oldState = state;

        state = 0;

        if (modelHasFocus()) {
            state |= FieldStates.FOCUSED;
        }
        if (!isEditable) {
            state |= FieldStates.NOEDIT;
        }
        if (getModel().getBorder() == VConstants.BRD_HIDDEN) {
            state |= FieldStates.NOBORDER;
        }

        VBlock block = model.getBlock();

        if (block.isMulti() && !block.noChart() && !isInDetail()) {
            state |= FieldStates.CHART;
            if (block.getActiveRecord() == model.getBlockView().getRecordFromDisplayLine(pos)) {
                state |= FieldStates.ACTIVE;
            }
        } else {
            state |= FieldStates.ACTIVE;
        }

        if (mouseInside) {
            state |= FieldStates.ROLLOVER;
        }

        switch (localAccess) {
            case VConstants.ACS_HIDDEN:
                state |= FieldStates.HIDDEN;
                break;
            case VConstants.ACS_MUSTFILL:
                state |= FieldStates.MUSTFILL;
                break;
            case VConstants.ACS_VISIT:
                state |= FieldStates.VISIT;
                break;
            default:
                state |= FieldStates.SKIPPED;
                break;
        }

        if (oldState != state) {
            setDisplayProperties();
        }
    }

    public UComponent getAutofillButton() {
        return info;
    }

    public VFieldUI getRowController() {
        return model;
    }

    protected abstract void setDisplayProperties();

    // ----------------------------------------------------------------------
    // SNAPSHOT PRINTING
    // ----------------------------------------------------------------------

    /**
     * prepare a snapshot
     *
     * @param fieldPos position of this field within block visible fields
     */
    public void prepareSnapshot(int fieldPos, boolean activ) {
        label.prepareSnapshot(activ);
        if (activ) {
            state |= FieldStates.FOCUSED;
            setDisplayProperties();
        } else {
            state |= FieldStates.SKIPPED;
            setDisplayProperties();
        }

        if (info != null) {
            info.setVisible(false);
        }
        if (incr != null) {
            incr.setVisible(false);
        }
        if (decr != null) {
            decr.setVisible(false);
        }
    }

    // ----------------------------------------------------------------------
    // IMPLEMENTATION
    // ----------------------------------------------------------------------

    private void update(final JButton button) {
        if (button != null) {
            boolean was = button.isEnabled();
            boolean will = access >= VConstants.ACS_VISIT;
            if (was != will) {
                button.setEnabled(will);
            }
        }
    }

    public void setInDetail(boolean detail) {
        inDetail = detail;
        label.setInDetailMode(detail);
    }

    public boolean isInDetail() {
        return inDetail;
    }

    // ----------------------------------------------------------------------
    // INNER CLASSES
    // ----------------------------------------------------------------------

    protected final class DFieldMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            performAction();
        }

        public void mouseEntered(MouseEvent e) {
            mouseInside = true;
            fireMouseHasChanged();
        }

        public void mouseExited(MouseEvent e) {
            mouseInside = false;
            fireMouseHasChanged();
        }

        private void performAction() {
            if (!model.hasAction() && !modelHasFocus()) {
                // an empty row in a chart has not calculated
                // the access for each field (ACCESS Trigger)
                if (model.getBlock().isMulti()) {
                    int recno = model.getBlockView().getRecordFromDisplayLine(DField.this.getPosition());

                    if (!model.getBlock().isRecordFilled(recno)) {
                        model.getBlock().updateAccess(recno);
                    }
                }

                if (!model.getBlock().isMulti()
                        || model.getBlock().isDetailMode() == isInDetail()
                        || model.getBlock().noChart()) {
                    Action action = new Action("mouse1") {
                        public void execute() {
                            model.transferFocus(DField.this); // use here a mouse transferfocus
                        }
                    };
                    // execute it as model transforming thread
                    // it is not allowed to execute it not with
                    // the method performAsync/BasicAction.
                    model.performAsyncAction(action);
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    protected VFieldUI model;
    public DLabel label;
    protected JFieldButton info;
    protected JFieldButton incr;
    protected JFieldButton decr;

    protected int state;        // Display state
    protected int pos;
    protected int options;
    protected int align;
    protected int access;        // current access of field
    protected boolean isEditable;    // is this field editable
    protected boolean mouseInside;    // private events

    private boolean inDetail;

    private static final ImageIcon listImg = Utils.getImage("list.gif");
    private static final ImageIcon rightImg = Utils.getImage("arrowright.gif");
    private static final ImageIcon leftImg = Utils.getImage("arrowleft.gif");
}
