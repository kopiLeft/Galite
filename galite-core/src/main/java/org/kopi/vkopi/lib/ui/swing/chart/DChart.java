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
 * $Id: DChart.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.chart;

import org.kopi.galite.visual.chart.ChartTypeFactory;
import org.kopi.galite.visual.chart.UChart;
import org.kopi.galite.visual.chart.UChartType;
import org.kopi.galite.visual.chart.VChart;
import org.kopi.vkopi.lib.ui.swing.visual.DWindow;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;
import org.kopi.galite.visual.visual.VException;
import org.kopi.galite.visual.visual.VWindow;

import java.awt.*;

@SuppressWarnings("serial")
public class DChart extends DWindow implements UChart {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new chart view from its model.
   * @param model The chart model.
   */
  public DChart(VWindow model) {
    super(model);
  }

  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------
  
  /**
   * @Override
   */
  public void refresh() {
    getContentPanel().invalidate();
    getContentPanel().repaint();
    getContentPanel().validate();
    setFocusable(true);
    requestFocusInWindow();
  }

  /**
   * @Override
   */
  public void setType(UChartType type) {
    if (this.type != null && type != null) {
      getContentPanel().remove((Component)this.type);
    }
    if (type != null) {
      this.type = type;
      type.build();
      getContentPanel().add((Component)this.type);
    }
  }

  /**
   * @Override
   */
  public void typeChanged() {
    getContentPanel().repaint();
    setFocusable(true);
    requestFocusInWindow();
    ((VChart)getModel()).setMenu();
  }

  /**
   * @Override
   */
  public void run() throws VException {
    run(true);
  }

  /**
   * start a block and enter in the good field (rec)
   * @exception VException  may be raised by triggers
   */
  public void run(final boolean visible) throws VException {
    ((VChart)getModel()).initChart();
    ((VChart)getModel()).setMenu();

    if(visible) {
      Frame       frame;
      Rectangle   bounds;

      frame = getFrame();
      frame.pack(); // layout frame; get preferred size
      // calulate bounds for frame to fit screen
      bounds = Utils.calculateBounds(frame, null, null);
      bounds.width = Math.max(bounds.width, 900);
      bounds.height = Math.max(bounds.height, 500);
      frame.setBounds(bounds);
      frame.setVisible(true);
      // Focus this panel to dispatch the key-events to the menu.
      // If "table" is focused, it will handle "esc" and "F2"
      // itself and will consume them.
      setFocusable(true);
      requestFocusInWindow();
    }
  }

  /**
   * @Override
   */
  public UChartType getType() {
    return type;
  }

  //---------------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------------
  
  private UChartType					type;
  
  static {
    ChartTypeFactory.chartTypeFactory = new JChartTypeFactory();
  }
}
