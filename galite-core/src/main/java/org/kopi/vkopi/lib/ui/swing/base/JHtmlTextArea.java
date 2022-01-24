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
 * $Id: JHtmlTextArea.java 35230 2017-09-13 18:27:19Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JTextPane;
import javax.swing.JViewport;

/**
 * A JText pane that can hanle rows and column to calculate its preffered size
 */
public class JHtmlTextArea extends JTextPane {

  /**
   * Creates a new HTML text area with a given number of rows and columns.
   * @param rows The number of rows must be positive.
   * @param columns The number of columns must be positive
   */
  public JHtmlTextArea(int rows, int columns) {
    if (rows < 0) {
      throw new IllegalArgumentException("rows: " + rows);
    }
    
    if (columns < 0) {
      throw new IllegalArgumentException("columns: " + columns);
    }
    this.rows = rows;
    this.columns = columns;
    setContentType("text/html");
  }
  
  /**
   * Returns the preferred size of the viewport if this component
   * is embedded in a JScrollPane.  This uses the desired column
   * and row settings if they have been set, otherwise the superclass
   * behavior is used.
   *
   * @return The preferredSize of a JViewport whose view is this Scrollable.
   * @see JViewport#getPreferredSize
   */
  public Dimension getPreferredScrollableViewportSize() {
    Dimension   size = super.getPreferredScrollableViewportSize();
    Insets      insets = getInsets();
    
    size = (size == null) ? new Dimension(400, 400) : size;
    size.width = (columns == 0) ? size.width : columns * getColumnWidth() + insets.left + insets.right;
    size.height = (rows == 0) ? size.height : rows * getRowHeight() + insets.top + insets.bottom;
    
    return size;
  }
  
  /**
   * Returns the preferred size of the TextArea.  This is the
   * maximum of the size needed to display the text and the
   * size requested for the viewport.
   *
   * @return the size
   */
  public Dimension getPreferredSize() {
    Dimension   d = super.getPreferredSize();
    Insets      insets = getInsets();
    
    d = (d == null) ? new Dimension(400, 400) : d;
    if (columns != 0) {
      d.width = Math.max(d.width, columns * getColumnWidth() + insets.left + insets.right);
    }
    if (rows != 0) {
      d.height = Math.max(d.height, rows * getRowHeight() + insets.top + insets.bottom);
    }
    
    return d;
  }

  /**
   * Gets column width.
   * The meaning of what a column is can be considered a fairly weak
   * notion for some fonts.  This method is used to define the width
   * of a column.  By default this is defined to be the width of the
   * character <em>m</em> for the font used.  This method can be
   * redefined to be some alternative amount.
   *
   * @return the column width &gt;= 1
   */
  protected int getColumnWidth() {
    if (columnWidth == 0) {
      FontMetrics metrics = getFontMetrics(getFont());
      columnWidth = metrics.charWidth('m');
    }
    
    return columnWidth;
  }

  /**
   * Defines the meaning of the height of a row.  This defaults to
   * the height of the font.
   *
   * @return the height &gt;= 1
   */
  protected int getRowHeight() {
    if (rowHeight == 0) {
      FontMetrics metrics = getFontMetrics(getFont());
      rowHeight = metrics.getHeight();
    }
    
    return rowHeight;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final int             rows;
  private final int             columns;
  private int                   columnWidth;
  private int                   rowHeight;
  private static final long     serialVersionUID = 8176289608711629627L;
}
