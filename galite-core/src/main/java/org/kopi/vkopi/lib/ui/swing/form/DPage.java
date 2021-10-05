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
 * $Id: DPage.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class DPage extends JPanel {

    public DPage(boolean align) {
        super(true);
        setLayout(new BoxLayout(this, align ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
    }

    public void addBlock(Component block) {
        if (getComponentCount() > 0) {
            add(Box.createRigidArea(new Dimension(10, 10)));
        }
        add(block);
        last = block;
    }

    public void addFollowBlock(Component block) {
        if (last != null) {
            JPanel temp = new JPanel() {
                /**
                 * Comment for <code>serialVersionUID</code>
                 */
                private static final long serialVersionUID = 4802353020694430279L;

                public Dimension getMaximumSize() {
                    return this.getPreferredSize();
                }
            };
            temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
            remove(last);
            temp.add(last);
            temp.add(block);
            add(temp);
        } else {
            add(block);
        }
        last = null;
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private Component last;
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 9005939783618490430L;
}
