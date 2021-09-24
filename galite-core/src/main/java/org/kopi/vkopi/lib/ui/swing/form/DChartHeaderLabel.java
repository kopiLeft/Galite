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
 * $Id: DChartHeaderLabel.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.UIManager;

import org.kopi.galite.form.UChartLabel;
import org.kopi.galite.form.VBlock;

public class DChartHeaderLabel extends DLabel implements UChartLabel {

  //----------------------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------------------

  /*package*/ DChartHeaderLabel(String text, String help, int index, VBlock.OrderModel model) {
    super(text, help);

    fieldIndex = index;
    sortModel = model;

    sortModel.addSortingListener(this);

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
	sortModel.sortColumn(fieldIndex);
      }
    });
  }

  
  public void repaint() {
    super.repaint();
  }

  
  public void orderChanged() {
    repaint();
  }

  
  public void paint(Graphics g) {
    super.paint(g);

    int      w = getSize().width;
    int      order = sortModel.getColumnOrder(fieldIndex);

    switch (order) {
      case VBlock.STE_INC:
      g.setColor(color_active);
      g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{1, 8, 8}, 3);
      g.setColor(color_mark);
      g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{16, 10, 10}, 3);
      break;
      case VBlock.STE_DESC:
      g.setColor(color_mark);
      g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{1, 8, 8}, 3);
      g.setColor(color_active);
      g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{16, 10, 10}, 3);
      break;
      case VBlock.STE_UNORDERED:
    default:
      g.setColor(color_mark);
      g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{1, 8, 8}, 3);
      g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{16, 10, 10}, 3);
    }
  }

  //----------------------------------------------------------------------
  // DATA MEMBERS
  //----------------------------------------------------------------------

  /*package*/ int                 		fieldIndex;
  /*package*/ VBlock.OrderModel   		sortModel;

  private static final Color    		color_mark = UIManager.getColor("KopiField.ul.chart");
  private static final Color    		color_active = UIManager.getColor("KopiField.ul.chart.active");

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long 			serialVersionUID = 8142184714004772529L;
}
