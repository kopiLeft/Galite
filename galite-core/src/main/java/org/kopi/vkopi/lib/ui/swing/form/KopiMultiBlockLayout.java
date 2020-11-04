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
 * $Id: KopiMultiBlockLayout.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.form.KopiAlignment;
import org.kopi.vkopi.lib.form.KopiLayoutManager;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class KopiMultiBlockLayout implements KopiLayoutManager {
  Component[][]		components;
  KopiAlignment[][]	aligns;
  int[]			sizes;
  int[]			realPos;
  int			height;
  int			width;
  boolean		computed = false;

  int			hgap = 0;
  int			vgap = 0;
  int			columnHeight;

  JScrollBar		scrollBar;

  /**
   * @param	col	number of columns
   * @param	line	number of lines
   */
  public KopiMultiBlockLayout(int col, int line) {
    components = new Component[col][line];
    aligns = new KopiAlignment[col][line];
    sizes = new int[col];
    realPos = new int[col];
  }

  /**
   * Returns the horizontal gap between components.
   */
  public int getHgap() {
    return hgap;
  }

  /**
   * Sets the horizontal gap between components.
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
   * @param vgap the vertical gap between components
   */
  public void setVgap(int vgap) {
    this.vgap = vgap;
  }

  /**
   * Adds the specified component to the layout, using the specified
   * constraint object.    public void layoutContainer(Container target) {

   * @param   comp         the component to be added.
   * @param   constraints  an object that specifies how and where
   */
  public void addLayoutComponent(Component comp, Object constraints) {
    synchronized (comp.getTreeLock()) {
      if (constraints instanceof KopiAlignment) {
	KopiAlignment	align = (KopiAlignment)constraints;

	aligns[align.x][align.y] = align;
	components[align.x][align.y] = comp;
      } else if (comp instanceof JScrollBar) {
	scrollBar = (JScrollBar)comp;
      } else {
	throw new IllegalArgumentException("cannot add to layout: constraint must be a KopiAlignment");
      }
    }
  }

  /**
   * @deprecated  replaced by <code>addLayoutComponent(Component, Object)</code>.
   */
  public void addLayoutComponent(String name, Component comp) {
      throw new IllegalArgumentException("dont use this deprecated method please");
  }

    /**
     * Removes the specified component from this border layout. This
     * method is called when a container calls its <code>remove</code> or
     * <code>removeAll</code> methods. Most applications do not call this
     * method directly.
     * @param   comp   the component to be removed.
     * @see     Container#remove(Component)
     * @see     Container#removeAll()
     * @since   JDK1.0
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
     * @param   target   the container in which to do the layout.
     * @return  the minimum dimensions needed to lay out the subcomponents
     *          of the specified container.
     * @see     Container
     * @see     java.awt.BorderLayout#preferredLayoutSize
     * @see     Container#getMinimumSize()
     * @since   JDK1.0
     */
    public Dimension minimumLayoutSize(Container target) {
      return preferredLayoutSize(target);
    }

  /**
   * get Column Pos, returns the pos of a column
   */
  public int getColumnPos(int x) {
    if (x < realPos.length - 1) {
      return realPos[x + 1];
    } else if (x == realPos.length - 1) {
      return realPos[x] + sizes[x] + hgap;
    }
    return 0;
  }

    /**
     *
     */
    private void precalculateSize() {
      // Compute all size
      height	= 0;
      width	= hgap;

      // compute column height
      columnHeight = textHeight;

      // labels
      for (int x = 0; x < components.length; x++) {
        // breite von label soll mit der von spalten
        //	sizes[x] = components[x][0].getPreferredSize().width;
      }

      // fields
      for (int x = 0; x < components.length; x++) {
	sizes[x] = Math.max(sizes[x], components[x][1].getPreferredSize().width);
	width += sizes[x] + hgap;
      }

      height = components[0].length * (vgap + columnHeight) +  vgap;

      if (scrollBar != null) {
	width += 2 * hgap + scrollBar.getPreferredSize().width;
      }

      computed = true;
    }

    /**
     * Determines the preferred size of the <code>target</code>
     * container using this layout manager, based on the components
     * in the container.
     * <p>
     * Most applications do not call this method directly. This method
     * is called when a container calls its <code>getPreferredSize</code>
     * method.
     * @param   target   the container in which to do the layout.
     * @return  the preferred dimensions to lay out the subcomponents
     *          of the specified container.
     * @see     Container
     * @see     java.awt.BorderLayout#minimumLayoutSize
     * @see     Container#getPreferredSize()
     * @since   JDK1.0
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
     * @param   target   the container in which to do the layout.
     * @see     Container
     * @see     Container#doLayout()
     * @since   JDK1.0
     */
    public void layoutContainer(Container target) {
      synchronized (target.getTreeLock()) {
	Insets insets = target.getInsets();
	int top = insets.top;
	int left = insets.left;
	int right = target.getSize().width - insets.right;

	if (!computed) {
	  precalculateSize();
	}

	int	extra_size_H = (right - left - width);

	if (extra_size_H < 0) {
	  extra_size_H = 0;
	}

	left += hgap + extra_size_H / 2;

	for (int x = 0; x < components.length; x++) {	// for every component
	  realPos[x] = left - 2 * hgap;

	    Dimension d1 = components[x][1].getPreferredSize();
	    components[x][0].setBounds(aligns[x][1].alignRight ? left + sizes[x] - d1.width :
				       left,
				       0 * (columnHeight + vgap) + vgap + insets.top,
				       d1.width,
				       columnHeight);
	  for (int y = 1; y < components[0].length; y++) {
	    Dimension d = components[x][y].getPreferredSize();
	    components[x][y].setBounds(aligns[x][y].alignRight ? left + sizes[x] - d.width :
				       left,
				       y * (columnHeight + vgap) + vgap + (y == 0 ? 2 : 0) + insets.top,
				       d.width,
				       d.height);
	  }
	  left += sizes[x] + hgap;
	}

	if (scrollBar != null) {
	  scrollBar.setBounds(left,
			      columnHeight + vgap + top,
			      scrollBar.getPreferredSize().width,
			      height - columnHeight - vgap);
	}
      }
    }

    /**
     * Returns a string representation of the state of this border layout.
     * @return    a string representation of this border layout.
     * @since     JDK1.0
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }


  private static final int textHeight;

  static {
    Font                font = UIManager.getFont("KopiLayout.font");
    @SuppressWarnings("deprecation")
    FontMetrics         fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

    textHeight = fm.getHeight()+ UIManager.getInt("FieldText.y.space");
  }
}
