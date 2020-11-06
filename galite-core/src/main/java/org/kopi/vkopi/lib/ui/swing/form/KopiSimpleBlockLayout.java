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
 * $Id: KopiSimpleBlockLayout.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.UIManager;

import org.kopi.galite.util.base.InconsistencyException;
import org.kopi.galite.form.Alignment;
import org.kopi.galite.form.LayoutManager;
import org.kopi.galite.form.MultiFieldAlignment;
import org.kopi.galite.form.ViewBlockAlignment;

/**
 *
 */
public class KopiSimpleBlockLayout implements LayoutManager {
    Component[][] components;
    Alignment[][] aligns;
    Vector<Component> follows;
    Vector<Alignment> followsAligns;
    int[] minStart;
    int[] sizes;
    int height;
    int width;
    boolean computed = false;
    ViewBlockAlignment align;

    int hgap = 7;
    int vgap = 1;
    int columnHeight = 20;

    /**
     * @param    col    number of columns
     * @param    line    number of lines
     */
    public KopiSimpleBlockLayout(int col, int line, ViewBlockAlignment align) {
        components = new Component[col][line];
        aligns = new Alignment[col][line];
        sizes = new int[col];
        minStart = new int[components.length + 2];
        follows = new Vector<Component>();
        followsAligns = new Vector<Alignment>();
        this.align = align;
    }

    /**
     * Returns the horizontal gap between components.
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * Sets the horizontal gap between components.
     *
     * @param hgap the horizontal gap between components
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    /**
     * Returns the vertical gap between components.
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * Sets the vertical gap between components.
     *
     * @param vgap the vertical gap between components
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
    }

    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     *
     * @param comp        the component to be added.
     * @param constraints an object that specifies how and where
     */
    public void addLayoutComponent(Component comp, Object constraints) {
        synchronized (comp.getTreeLock()) {
            if (constraints instanceof Alignment) {
                Alignment align = (Alignment) constraints;
                if (align.getWidth() < 0) {
                    follows.addElement(comp);
                    followsAligns.addElement(align);
                } else {
                    aligns[align.getX()][align.getY()] = align;
                    components[align.getX()][align.getY()] = comp;
                }
            } else {
                throw new IllegalArgumentException("cannot add to layout: constraint must be a Alignment");
            }
        }
    }

    /**
     * @deprecated replaced by <code>addLayoutComponent(Component, Object)</code>.
     */
    public void addLayoutComponent(String name, Component comp) {
        throw new IllegalArgumentException("dont use this deprecated method please");
    }

    /**
     * Removes the specified component from this border layout. This
     * method is called when a container calls its <code>remove</code> or
     * <code>removeAll</code> methods. Most applications do not call this
     * method directly.
     *
     * @param comp the component to be removed.
     * @see Container#remove(Component)
     * @see Container#removeAll()
     * @since JDK1.0
     */
    public void removeLayoutComponent(Component comp) {
        throw new InconsistencyException("removeLayoutComponent not yet supported");
    }

    /**
     * Determines the minimum size of the <code>target</code> container
     * using this layout manager.
     * <p>
     * This method is called when a container calls its
     * <code>getMinimumSize</code> method. Most applications do not call
     * this method directly.
     *
     * @param target the container in which to do the layout.
     * @return the minimum dimensions needed to lay out the subcomponents
     * of the specified container.Math.max(org.kopi.vkopi.lib.visual.DObject.FNT_FIXED_HEIGHT,
     * org.kopi.vkopi.lib.visual.DObject.FNT_DIALOG_HEIGHT)
     * @see Container
     * @see java.awt.BorderLayout#preferredLayoutSize
     * @see Container#getMinimumSize()
     * @since JDK1.0
     */
    public Dimension minimumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    /**
     *
     */
    private void precalculateSize() {
        // Compute all size
        height = 0;
        width = 0;
        columnHeight = textHeight + 9;

        for (int x = 0; x < components.length + 1; x++) {
            minStart[x] = 0;
        }

        if (components.length == 0) {
            return;
        }

        for (int x = 0; x < components.length; x++) {
            int width_c = 0;
            int height_c = 0;

            for (int y = 0; y < components[0].length; y++) {
                if (components[x][y] != null && !(aligns[x][y] instanceof MultiFieldAlignment)) {
                    // use dimension with all follows
                    Dimension dim = getDimension(x, y);

                    minStart[x + aligns[x][y].getWidth()] = Math.max(minStart[x] + dim.width + hgap,
                            minStart[x + aligns[x][y].getWidth()]);
                    if (aligns[x][y].getWidth() == 1) {
                        width_c = Math.max(width_c, dim.width + hgap);
                    }
                    height_c = Math.max(height_c, y * (columnHeight + vgap) + dim.height);
                } else if (components[x][y] != null) {
                    // !!! lackner 2003.04.24 Multifields:
                    // size of multifields are not correctly calculated
                }
            }

            sizes[x] = width_c;
            width = Math.max(width + width_c, minStart[x + 1]);
            height = Math.max(height, height_c);
        }
        if (align != null) {
            // block alignment
            for (int x = 0; x < components.length; x++) {
                if ((x % 2 == 1) && align.isChart() && align.isAligned(x / 2 + 1)) {
                    minStart[x + 1] = align.getMinStart(x / 2 + 1);
                } else if (!align.isChart()) {
                    // alignment if block is not a chart
                    if (x % 2 == 1) {
                        // fields
                        minStart[x] = align.getMinStart(x / 2 + 1);
                    } else {
                        // labels
                        minStart[x] = align.getLabelMinStart(x / 2 + 1);
                    }
                }
            }
        }
        if (width > hgap) {
            // remove hgap at the and
            width -= hgap;
        }
        if (height > vgap) {
            // remove vgap at the end
            height -= vgap;
        }
        computed = true;
    }

    private Dimension getDimension(int x, int y) {
        Dimension dim = components[x][y].getPreferredSize();
        int width = dim.width;
        int height = dim.height;

        for (int i = 0; i < follows.size(); i++) {
            Alignment align = followsAligns.elementAt(i);

            if (align.getX() != x || align.getY() != y) {
                continue;
            }

            Component comp = follows.elementAt(i);
            Dimension dim2 = comp.getPreferredSize();

            width += dim2.width + hgap;
            height = Math.max(height, dim2.height);
        }

        if (width != dim.width || height != dim.height) {
            return new Dimension(width, height);
        } else {
            return dim;
        }
    }

    /**
     * get Column Pos, returns the pos of a column
     */
    public int getColumnPos(int x) {
        if (x < minStart.length) {
            return minStart[x];
        }
        return 0;
    }

    /**
     * Determines the preferred size of the <code>target</code>
     * container using this layout manager, based on the components
     * in the container.
     * <p>
     * Most applications do not call this method directly. This method
     * is called when a container calls its <code>getPreferredSize</code>
     * method.
     *
     * @param target the container in which to do the layout.
     * @return the preferred dimensions to lay out the subcomponents
     * of the specified container.
     * @see Container
     * @see java.awt.BorderLayout#minimumLayoutSize
     * @see Container#getPreferredSize()
     * @since JDK1.0
     */
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            if (!computed) {
                precalculateSize();
            }

            Dimension dim = new Dimension(width, height);
            Insets insets = target.getInsets();

            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;
            return dim;
        }
    }

    /**
     * Returns the maximum dimensions for this layout given the components
     * in the specified target container.
     *
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     * @see #preferredLayoutSize
     */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
        computed = false;
    }

    /**
     * Lays out the container argument using this border layout.
     * <p>
     * This method actually reshapes the components in the specified
     * container in order to satisfy the constraints of this
     * <code>BorderLayout</code> object. The <code>North</code>
     * and <code>South</code>components, if any, are placed at
     * the top and bottom of the container, respectively. The
     * <code>West</code> and <code>East</code> components are
     * then placed on the left and right, respectively. Finally,
     * the <code>Center</code> object is placed in any remaining
     * space in the middle.
     * <p>
     * Most applications do not call this method directly. This method
     * is called when a container calls its <code>doLayout</code> method.
     *
     * @param target the container in which to do the layout.
     * @see Container
     * @see Container#doLayout()
     * @since JDK1.0
     */
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int top = insets.top;
            int left = insets.left;

            if (components.length == 0) {
                return;
            }

            if (!computed) {
                precalculateSize();
            }

            for (int x = 0; x < components.length; x++) {    // for every component
                for (int y = 0; y < components[0].length; y++) {
                    if (components[x][y] != null) {
                        Dimension d = components[x][y].getPreferredSize();
                        int cleft;
                        int sup = 0;

                        if (aligns[x][y] instanceof MultiFieldAlignment) {
                            if (aligns[x][y].getAlignRight()) {
                                // label
                                cleft = ((x - 1) / 2) * (Math.max(d.width, components[x][y + 1].getPreferredSize().width) + hgap) + insets.left;
                                sup = 5;
                            } else {
                                // fields
                                cleft = ((x - 1) / 2) * (Math.max(d.width, components[x][y - 1].getPreferredSize().width) + hgap) + insets.left;
                            }
                        } else if (!aligns[x][y].getAlignRight() && align != null && align.isAligned(x / 2 + 1)) {
                            if (components[x][y] instanceof DLabel) {
                                cleft = insets.left;
                            } else {
                                if (align.isChart()) {
                                    cleft = minStart[x + 1] - d.width;
                                } else {
                                    cleft = minStart[x];
                                }
                            }
                        } else if (align != null && align.isAligned(x / 2 + 1)) {
                            cleft = insets.left;
                        } else if (aligns[x][y].getAlignRight()) {
                            cleft = minStart[x + 1] - d.width;
                        } else {
                            cleft = left;
                        }
                        components[x][y].setBounds(cleft,
                                y * (vgap + columnHeight) + top + sup,
                                (aligns[x][y].getAlignRight() || !aligns[x][y].getUseAll()) ?
                                        d.width
                                        : minStart[x + 1] - cleft,
                                d.height);
                    }
                }
                left = minStart[x + 1];
            }

            for (int i = 0; i < follows.size(); i++) {
                Alignment align = (Alignment) followsAligns.elementAt(i);
                Component comp = (Component) follows.elementAt(i);
                Dimension d = comp.getPreferredSize();

                comp.setBounds(components[align.getX()][align.getY()].getLocation().x
                                + hgap
                                + components[align.getX()][align.getY()].getPreferredSize().width,
                        align.getY() * (vgap + columnHeight) + top,
                        d.width,
                        d.height);
            }
        }
    }

    /**
     * Returns a string representation of the state of this border layout.
     *
     * @return a string representation of this border layout.
     * @since JDK1.0
     */
    public String toString() {
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }

    private static final int textHeight;

    static {
        Font font = UIManager.getFont("KopiLayout.font");
        @SuppressWarnings("deprecation")
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

        textHeight = fm.getHeight();
    }
}
