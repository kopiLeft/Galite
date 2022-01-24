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
 * $Id: VTable.java 34103 2013-11-07 17:30:39Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.report;

import javax.swing.table.AbstractTableModel;

import org.kopi.galite.visual.report.MReport;

public class VTable extends AbstractTableModel {

  //-----------------------------------------------------------------
  // CONSTRUCTOR
  //-----------------------------------------------------------------

  public VTable(MReport model) {
    this.model = model;
  }

  //-----------------------------------------------------------------
  // ABSTRACTTABLEMODEL IMPLEMENTATION
  //-----------------------------------------------------------------

  /**
   * Returns the number of records managed by the data source object.
   *
   * @return	the number or rows in the model
   */
  public int getRowCount() {
    return model.getRowCount();
  }

  /**
   * Returns the number of columns managed by the data source object.
   *
   * @return	the number or columns to display
   */
  public int getColumnCount() {
    return model.getColumnCount();
  }

  /**
   * Returns an attribute value for a cell.
   *
   * @param	rowIndex		the index of the row whose value is to be looked up
   * @param	columnIndex		the index of the column whose value is to be looked up (column of the model)
   * @return	the value Object at the specified cell
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    return model.getValueAt(rowIndex, columnIndex);
  }

  /**
   * Returns always false since report cells are never editable.
   *
   * @param	row		the index of the row whose value is to be looked up
   * @param	column		the index of the column whose value is to be looked up
   * @return	true if the cell is editable.
   */
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  /**
   * Returns the name of a column.
   * Note, this name does not need to be unique.
   *
   * @param	column		the index of the column
   * @return	the name of the column
   */
  public String getColumnName(int column) {
    return model.getColumnName(column);
  }

  //-----------------------------------------------------------------
  // DATA MEMBERS
  //-----------------------------------------------------------------

  private MReport			model;
  private static final long 		serialVersionUID = -8541215011797681655L;
}
