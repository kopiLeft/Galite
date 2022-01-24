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
 * $Id: DTable.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.report;

import javax.swing.JTable;

import org.kopi.galite.visual.report.UReport.UTable;

/**
 * {@code DTable} is swing implementation of {@link UTable}
 */
public class DTable extends JTable implements UTable {

  //--------------------------------------------------
  // CONSTRCUTOR
  //--------------------------------------------------

  /**
   * Creates a new {@code DTable} from a table model.
   * @param model The table model.
   */
  public DTable(VTable model) {
    super(model);
  }

  //--------------------------------------------------
  // UTABLE IMPLEMENTATION
  //--------------------------------------------------

  /**
   *
   */
  public int convertColumnIndexToModel(int viewColumnIndex) {
    return super.convertColumnIndexToModel(viewColumnIndex);
  }

  /**
   *
   */
  public int convertColumnIndexToView(int modelColumnIndex) {
    return super.convertColumnIndexToView(modelColumnIndex);
  }

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------

  private static final long	serialVersionUID = 9116679922188281081L;
}
