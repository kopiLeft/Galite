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
 * $Id: DColumnChart.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kopi.galite.visual.chart.VDataSeries;

@SuppressWarnings("serial")
public class DColumnChart extends DAbstractChartType {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new column chart from a given data series.
   * @param title The chart title.
   * @param series The data series
   */
  public DColumnChart(String title, VDataSeries[] series) {
    super(title, series);
  }

  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------
  
  @Override
  protected JFreeChart createChart(String title, VDataSeries[] series) {
    CategoryDataset		dataset;
    JFreeChart			chart;
    CategoryPlot		plot;
    
    dataset = createDataset(series);
    chart = ChartFactory.createBarChart(title,
	                                series[0].getDimension().getName(),
	                                null,
	                                dataset,
	                                getPlotOrientation(),
	                                true,
	                                true,
	                                false);
    plot = (CategoryPlot) chart.getPlot();
    plot.setBackgroundPaint(getPlotBackground());
    chart.setBackgroundPaint(getChartBackground());
    plot.setRangeGridlinePaint(getGridLineColor());
    
    return chart;
  }
  
  /**
   * Returns the plot orientation.
   * @return the plot orientation.
   */
  protected PlotOrientation getPlotOrientation() {
    return PlotOrientation.VERTICAL;
  }
}
