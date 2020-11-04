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
 * $Id: DPositionPanel.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import org.kopi.galite.visual.DPositionPanelListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The position panel is the used to go trough the rows when fetching
 * several rows. Its location should be in right/bottom between the
 * info text and the working bar.
 */
public class DPositionPanel extends JPanel {


    /**
     * Creates a new position panel.
     *
     * @param listener the window that gets the requests
     */
    public DPositionPanel(DPositionPanelListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout());
        setFocusable(false);

        record = new JPanel();
        record.setLayout(new BorderLayout());

        recordLeft = new JPanel();
        recordLeft.setLayout(new BorderLayout());

        recordRight = new JPanel();
        recordRight.setLayout(new BorderLayout());

        // 'goto first' button
        first = new JButton(org.kopi.vkopi.lib.ui.swing.base.Utils.getImage("arrowfirst.gif"));
        first.setFocusable(false);
        first.setBorder(new EtchedBorder());
        first.setMargin(EMPTY_INSETS);
        first.setOpaque(false);
        first.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DPositionPanel.this.listener.gotoFirstPosition();
            }
        });
        recordLeft.add(first, BorderLayout.WEST);

        // 'goto previous' button
        left = new JButton(org.kopi.vkopi.lib.ui.swing.base.Utils.getImage("arrowleft.gif"));
        left.setFocusable(false);
        left.setBorder(new EtchedBorder());
        left.setMargin(EMPTY_INSETS);
        left.setOpaque(false);
        left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DPositionPanel.this.listener.gotoPrevPosition();
            }
        });
        recordLeft.add(left, BorderLayout.EAST);

        record.add(recordLeft, BorderLayout.WEST);

        // 'position/total' label
        info = new JButton();
        info.setFont(new Font(null, Font.PLAIN, 8));
        info.setText(null);
        info.setFocusable(false);
        info.setBorder(new EtchedBorder());
        info.setMargin(EMPTY_INSETS);
        info.setOpaque(false);
        info.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int userInput = DWindow.askPostition(DPositionPanel.this, current, total);
                if (userInput != current) {
                    DPositionPanel.this.listener.gotoPosition(userInput);
                }
            }
        });
        record.add(info, BorderLayout.CENTER);

        // 'goto next' button
        right = new JButton(org.kopi.vkopi.lib.ui.swing.base.Utils.getImage("arrowright.gif"));
        right.setFocusable(false);
        right.setBorder(new EtchedBorder());
        right.setMargin(EMPTY_INSETS);
        right.setOpaque(false);
        right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DPositionPanel.this.listener.gotoNextPosition();
            }
        });
        recordRight.add(right, BorderLayout.WEST);

        // 'goto next' last
        last = new JButton(org.kopi.vkopi.lib.ui.swing.base.Utils.getImage("arrowlast.gif"));
        last.setFocusable(false);
        last.setBorder(new EtchedBorder());
        last.setMargin(EMPTY_INSETS);
        last.setOpaque(false);
        last.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DPositionPanel.this.listener.gotoLastPosition();
            }
        });
        recordRight.add(last, BorderLayout.EAST);

        record.add(recordRight, BorderLayout.EAST);


        recordVisible = false;
    }

    /**
     * setBlockRecords
     * inform user about nb records fetched and current one
     */
    public void setPosition(int current, int total) {
        this.current = current;
        this.total = total;
        if (current == -1 || total == 0) {
            if (recordVisible) {
                remove(record);
                recordVisible = false;
            }
        } else {
            if (!recordVisible) {
                add(record, BorderLayout.CENTER);
                recordVisible = true;
            }

            info.setText(" " + current + " / " + total + " ");
            left.setEnabled(current > 1);
            first.setEnabled(current > 1);
            right.setEnabled(current < total);
            last.setEnabled(current < total);
        }

        doLayout();
    }


    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    private final DPositionPanelListener listener;
    private final JPanel record;
    private final JPanel recordLeft;
    private final JPanel recordRight;
    private final JButton info;
    private final JButton left;
    private final JButton right;
    private final JButton first;
    private final JButton last;

    private boolean recordVisible;
    private int current;
    private int total;

}
