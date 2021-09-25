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
 * $Id: DAbstractChartType.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.chart;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;

import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfTemplate;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.ResourceBundleWrapper;

import org.kopi.galite.visual.chart.UChartType;
import org.kopi.galite.visual.chart.VDataSeries;
import org.kopi.galite.visual.chart.VDimensionData;
import org.kopi.galite.visual.chart.VPrintOptions;
import org.kopi.galite.visual.chart.VMeasureData;
import org.kopi.galite.visual.util.PPaperType;
import org.kopi.galite.visual.visual.ApplicationContext;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("serial")
public abstract class DAbstractChartType extends ChartPanel implements UChartType {
  
  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new abstract chart type instance.
   * @param title The chart title.
   * @param series The data series.
   */
  protected DAbstractChartType(String title, VDataSeries[] series) {
    super(null, true, true, true, true, true);
    this.title = title;
    this.series = series;
    localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.LocalizationBundle", ApplicationContext.Companion.getDefaultLocale());
  }
  
  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------
  
  /**
   * @Override
   */
  public void build() {
    JFreeChart		chart;
    
    chart = createChart(title, series);
    if (chart != null) {
      setChart(chart);
      setPreferredSize(new Dimension(800, 500));
    }
  }

  /**
   * @Override
   */
  public void refresh() {
    invalidate();
    repaint();
    validate();
  }

  /**
   * @Override
   */
  public void exportToPDF(OutputStream destination, VPrintOptions options)
    throws IOException
  {
    if (getChart() == null) {
      return;
    } else {
      PPaperType	paper = PPaperType.Companion.getPaperTypeFromCode(options.getPaperType());
      int		width;
      int 		height;

      if (options.getPaperLayout().equals("Landscape")) {
        width = paper.getHeight();
        height = paper.getWidth();
      } else {
        width = paper.getWidth();
        height = paper.getHeight();
      }
      // write the pdf
      writeAsPDF(destination,
	         width,
	         height,
	         options.getLeftMargin(),
	         options.getRightMargin(),
	         options.getTopMargin(),
	         options.getBottomMargin(),
	         new DefaultFontMapper());
    }
  }

  /**
   * @Override
   */
  public void exportToPNG(OutputStream destination, int width, int height)
    throws IOException
  {
    if (getChart() != null) {
      ChartUtilities.writeChartAsPNG(destination, getChart(), width , height);
    }
  }

  /**
   * @Override
   */
  public void exportToJPEG(OutputStream destination, int width, int height) throws IOException {
    if (getChart() != null) {
      ChartUtilities.writeChartAsJPEG(destination, getChart(), width , height);
    }
  }
  
  /**
   * Creates the category data set for the chart.
   * @param series The data series.
   * @return The category data set for the chart.
   */
  protected CategoryDataset createDataset(VDataSeries[] series) {
    DefaultCategoryDataset		dataset;
    
    dataset = new DefaultCategoryDataset();
    for (VDataSeries serie : series) {
      VDimensionData dimension;
      VMeasureData[]		measures;
      
      dimension = serie.getDimension();
      measures = serie.getMeasures();
      for (VMeasureData measure : measures) {
	dataset.addValue(measure.getValue(), measure.getName(), dimension.getValue());
      }
    }
    
    return dataset;
  }
  
  /**
   * Writes the chart as PDF document.
   * @param destination The destination stream.
   * @param width The page width.
   * @param height The page height.
   * @param marginLeft The margin left.
   * @param marginRight The margin right.
   * @param marginTop The margin top.
   * @param marginBottom The margin buttom.
   * @param mapper The font mapper.
   * @throws IOException I/O errors.
   */
  protected void writeAsPDF(OutputStream destination,
                            int width,
                            int height,
                            int marginLeft,
                            int marginRight,
                            int marginTop,
                            int marginBottom,
                            FontMapper mapper)
    throws IOException
  {
    Document			document;
    Rectangle			pageSize;

    pageSize = new Rectangle(width, height);
    document = new Document(pageSize, marginLeft, marginRight, marginTop, marginBottom);
    try {
      PdfWriter writer;
      PdfContentByte cb;
      PdfTemplate 		tp;
      Graphics2D 		g2;
      Rectangle2D 		r2D;

      writer = PdfWriter.getInstance(document, destination);
      document.addAuthor("kopi");
      document.addSubject(title);
      document.open();
      cb = writer.getDirectContent();
      tp = cb.createTemplate(width, height);
      g2 = tp.createGraphics(width, height, mapper);
      r2D = new Rectangle2D.Double(0, 0, width, height);
      getChart().draw(g2, r2D);
      g2.dispose();
      cb.addTemplate(tp, 0, 0);
    } catch (DocumentException de) {
      throw new IOException(de.getMessage());
    }
    document.close();
  }
  
  /**
   * Returns the plot background color.
   * @return The plot background color.
   */
  protected Color getPlotBackground() {
    return new Color(239, 235, 222);
  }
  
  /**
   * Returns the chart background color.
   * @return The chart background color.
   */
  protected Color getChartBackground() {
    return new Color(248, 247, 241);
  }
  
  /**
   * Returns the grid line color.
   * @return The grid line color.
   */
  protected Color getGridLineColor() {
    return new Color(107, 130, 239);
  }

  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------
  
  /**
   * Creates the chart object from the given chart title and chart data.
   * @param title The chart title.
   * @param series The chart series.
   * @return The chart object.
   */
  protected abstract JFreeChart createChart(String title, VDataSeries[] series);
  
  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------
  
  private final String 					title;
  private final VDataSeries[] 				series;
}
