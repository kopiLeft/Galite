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
 * $Id: DReport.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.kopi.galite.visual.report.ColumnStyle;
import org.kopi.galite.visual.report.Constants;
import org.kopi.galite.visual.report.MReport;
import org.kopi.galite.visual.report.Parameters;
import org.kopi.galite.visual.report.Point;
import org.kopi.galite.visual.report.UReport;
import org.kopi.galite.visual.report.VDecimalColumn;
import org.kopi.galite.visual.report.VIntegerColumn;
import org.kopi.galite.visual.report.VReport;
import org.kopi.galite.visual.report.VReportColumn;
import org.kopi.galite.visual.report.VSeparatorColumn;
import org.kopi.galite.visual.VException;
import org.kopi.galite.visual.VlibProperties;
import org.kopi.vkopi.lib.ui.swing.visual.DWindow;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;

/**
 * This is the display class of a report.
 */
public class DReport extends DWindow implements UReport, TableCellRenderer {

  /**
   * Constructs a new report view
   *
   * @param	report		the report model
   */
  public DReport(VReport report) {
    super(report);

    this.report = report;
    model = report.getModel();
    model.addReportListener(this);
  }

  public void setColumnLabel(int column, String label) {
    table.getColumnModel().getColumn(table.convertColumnIndexToView(column)).setHeaderValue(label);
    table.getTableHeader().resizeAndRepaint();
  }

  public void removeColumn(int position) {
    int index = position;
    position = table.convertColumnIndexToModel(position);
    model.removeColumn(position);
    model.initializeAfterRemovingColumn(index);
    table.removeColumn(tablecolumns[position]);
    TableColumn[] newTableColumns = new TableColumn[model.getAccessibleColumnCount()];
    for (int i = 0; i < position; i++) {
      newTableColumns[i] = tablecolumns[i];
    }
    // skip position
    for (int i = position; i < model.getAccessibleColumnCount(); i++) {
      newTableColumns[i] = tablecolumns[i + 1];
      newTableColumns[i].setModelIndex(newTableColumns[i].getModelIndex() - 1);
    }
    tablecolumns = newTableColumns;
    // set new order.
    int[] pos = new int[model.getAccessibleColumnCount()];

    for (int i = 0; i < model.getAccessibleColumnCount(); i ++) {
      pos[i] = (model.getDisplayOrder(i) > position ) ? model.getDisplayOrder(i) - 1 :  model.getDisplayOrder(i);
    }
    report.columnMoved(pos);
  }

  public void addColumn(int position) {
    String headerLabel;
    TableColumn column;

    table.setAutoCreateColumnsFromModel(false);
    column = new TableColumn(model.getColumnCount());
    headerLabel ="col" + (model.getColumnCount());
    column.setHeaderValue(headerLabel);
    table.getColumnModel().addColumn(column);
    model.addColumn(headerLabel, position);
    // save  columns order
    TableColumn[] newTableColumns = new TableColumn[model.getAccessibleColumnCount()];

    newTableColumns[model.getAccessibleColumnCount() - 1] = table.getColumnModel().getColumn(table.getColumnCount() - 1);
    for (int i = 0; i < model.getAccessibleColumnCount() - 1; i++) {
      newTableColumns[i] = tablecolumns[i];
    }
    tablecolumns = newTableColumns;
    // move last column to position.
    int[] pos = new int[model.getAccessibleColumnCount()];

    for (int i = 0; i < position; i ++) {
      pos[i] = model.getDisplayOrder(i);
    }
    for (int i = position + 1; i < model.getAccessibleColumnCount(); i ++) {
      pos[i] = model.getDisplayOrder(i - 1);
    }
    pos[position] = model.getDisplayOrder(model.getAccessibleColumnCount() - 1);
    report.columnMoved(pos);
  }

  /**
   *
   */
  public void addColumn() {
    addColumn(table.getColumnCount());
  }

  /**
   *
   */
  public UTable getTable() {
    return table;
  }

  /**
   *
   */
  public void columnMoved(final int[] pos) {
    SwingUtilities.invokeLater (new Runnable() {
      public void run() {
        reorder(pos);
        model.columnMoved(pos);
        redisplay();
      }
    });
  }

  /**
   *
   */
  public void contentChanged() {
    if (table != null) {
      ((VTable)table.getModel()).fireTableDataChanged();
    }
  }

  /**
   * run
   */
  @SuppressWarnings("deprecation")
  public void build() {
    // load personal configuration
    parameters = new Parameters(Color.blue);

    // create table view
    table = new DTable(new VTable(model));

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setIntercellSpacing(interCellSpacing);
    table.setRowSelectionAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setCellSelectionEnabled(true);
    table.getTableHeader().setFont(parameters.getFont());
    table.getTableHeader().setUpdateTableInRealTime(true);

    buildCellRenderers();

    // set row height
    FontMetrics         fontmetrics = getFontMetrics(parameters.getFont());
    int                 nbLinesMax = 1;

    for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
      VReportColumn     column = model.getAccessibleColumn(i);

      if (column.getHeight() > nbLinesMax) {
        nbLinesMax = column.getHeight();
      }
    }
    table.setRowHeight(nbLinesMax * fontmetrics.getHeight());

    resetWidth();

    // save initial columns order
    tablecolumns = new TableColumn[model.getAccessibleColumnCount()];
    for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
      tablecolumns[i] = table.getColumnModel().getColumn(i);
    }

    // add event handlers
    addTableListeners();

    JScrollPane         scrollpane = new JScrollPane(table);

    scrollpane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
    scrollpane.getViewport().setPreferredSize(table.getPreferredSize());
    getContentPanel().setLayout(new BorderLayout());
    getContentPanel().add(scrollpane);

    super.build();
  }


  public void resetWidth() {
    //  columns width and table width = sum columns width
    for (int i = 0; i < model.getColumnCount(); i++) {
      tableWidth += resetColumnSize(i);
    }
    tableWidth += table.getIntercellSpacing().width * (table.getColumnCount() + 1);
  }

  public void redisplay() {
    buildCellRenderers();
    //resetWidth();
    table.repaint();
  }

  /**
   * Return the new columns order
   */
  public int[] getDisplayOrder() {
    int[] displayOrder = new int[model.getColumnCount()];
    for (int i = 0; i < model.getColumnCount(); i++) {
      displayOrder[i] = table.convertColumnIndexToModel(i);
    }

    return displayOrder;
  }

  /**
   * Sets the title of the report
   */
  public void setTitle(String title) {
    super.setTitle(title);
  }

  /**
   * Reoder
   */
  public void reorder(int[] newOrder) {
    for (int i = 0; i < tablecolumns.length; i++) {
      table.removeColumn(tablecolumns[i]);
    }
    for (int i = 0; i < tablecolumns.length; i++) {
      table.addColumn(tablecolumns[newOrder[i]]);
    }
  }

  /**
   * Returns the number of columns displayed in the table
   *
   * @return	the number or columns displayed
   */
  public int getColumnCount() {
    return table.getColumnCount();
  }

  /**
   * start a block and enter in the good field (rec)
   * @exception VException  may be raised by triggers
   */
  public void run(final boolean visible) throws VException {
    report.initReport();
    report.setMenu();

    if(visible) {
      Frame frame;
      Rectangle bounds;

      frame = getFrame();
      frame.pack(); // layout frame; get preferred size
      // calulate bounds for frame to fit screen
      bounds = Utils.calculateBounds(frame, null, null);
      // set a minimum height for the window; there maybe only
      // one or two lines at the beginning but after opening a column
      // there are 50 lines
      bounds.height = Math.max(bounds.height, 500); // 500 is aprox. 15 lines
      frame.setBounds(bounds);
      frame.setVisible(true);
      setInfoTable();
      // Focus this panel to dispatch the key-events to the menu.
      // If "table" is focused, it will handle "esc" and "F2"
      // itself and will consume them.
      setFocusable(true);
      requestFocusInWindow();
    }
  }

  /**
   * start a block and enter in the good field (rec)
   * @exception VException  may be raised by triggers
   */
  public void run() throws VException {
    run(true);
  }

  // --------------------------------------------------------------------
  // SELECTED COMPONENTS
  // --------------------------------------------------------------------

  /**
   * Returns the selected column
   * The index of the column is relative to the model
   */
  public int getSelectedColumn() {
    int		sel = table.getSelectedColumn();
    return sel == -1 ? -1 : table.convertColumnIndexToModel(sel);
  }

  /**
   * Returns the coordinate of the selected cell
   * The index of the column is relative to the model
   */
  public Point getSelectedCell() {
    int		row = table.getSelectedRow();
    int		col = table.getSelectedColumn();

    return new Point(col, row);
  }

  public static int getState(String text) {
    if (text == null || text.length() == 0) {
      return Constants.STA_EMPTY;
    } else if (text.startsWith("-")) {
      return Constants.STA_NEGATIVE;
    } else if (text.startsWith("0")) {
      return Constants.STA_NULL;
    }
    return Constants.STA_STANDARD;
  }

  /**
   * Returns the right cell component
   */
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    int		col   = table.convertColumnIndexToModel(column);
    int		level = model.getRow(row).getLevel();
    boolean	folded = model.getAccessibleColumn(col).isFolded() &&
            !(model.getAccessibleColumn(col) instanceof VSeparatorColumn);
    String	text = model.getAccessibleColumn(col).format(value);
    CellRenderer cell = null;

    // Compute table cell
    if (folded) {
      cell = foldedCell;
    } else {
      if (cellRenderers[col].length == 1) {
        // normal way
        cell = cellRenderers[col][0];
      } else {
        // specific way
        int	state = getState(text);
        // search
        for (int i = 0; i < cellRenderers[col].length; i++) {
          if (cellRenderers[col][i].getState() == state) {
            cell = cellRenderers[col][i];
          } else if (cell == null && cellRenderers[col][i].getState() == 0) {
            // standard
            cell = cellRenderers[col][i];
          }
        }
        if (cell == null) {
          cell = cellRenderers[col][0];
        }
      }
    }

    cell.set(text, isSelected, parameters.getBackground(level));
    setInfoTable();

    return cell;
  }

  private void buildCellRenderers() {
    TableColumn         tableColumn;
    HeaderRenderer      headerRenderer;

    cellRenderers = new CellRenderer[model.getAccessibleColumnCount()][];

    for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
      headerRenderer = new HeaderRenderer();
      tableColumn = table.getColumnModel().getColumn(i);
      tableColumn.setCellRenderer(this);
      tableColumn.setHeaderRenderer(headerRenderer);
      VReportColumn column = model.getAccessibleColumn(i);
      if (column instanceof VSeparatorColumn) {
        cellRenderers[i] = new CellRenderer[] {new CellRenderer(Constants.STA_SEPARATOR)};
      } else {
        ColumnStyle[]	styles = (ColumnStyle[]) column.getStyles();

        cellRenderers[i] = new CellRenderer[styles.length];
        for (int j = 0; j < styles.length; j++) {
          ColumnStyle style = styles[j];

          cellRenderers[i][j] = new CellRenderer(style.getState(),
                  column.getAlign(),
                  style.getBackground(),
                  style.getForeground(),
                  style.getFont());
        }
      }
    }
  }

  /**
   * Add listener at the table
   */
  private void addTableListeners() {
    final MReport currentModel = model;

    table.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int	row = table.rowAtPoint(e.getPoint());
        int	mod = e.getModifiers();
        int	column	= table.columnAtPoint(e.getPoint());

        if ((mod & InputEvent.BUTTON2_MASK) == 0 && (mod & InputEvent.BUTTON3_MASK) == 0) {
          if (e.getClickCount() == 2) {
            if (currentModel.isRowLine(row)) {
              try {
                report.editLine();
              } catch (VException ef) {
                ef.printStackTrace();
              }
            } else {
              if (row >= 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int index = table.convertColumnIndexToModel(column);

                if (currentModel.isRowFold(row, index)) {
                  currentModel.unfoldingRow(row, index);
                } else {
                  currentModel.foldingRow(row, index);
                }
                setCursor(Cursor.getDefaultCursor());
              }
            }
          }
        } else if ((mod & InputEvent.BUTTON1_MASK) == 0 && (mod & InputEvent.BUTTON3_MASK) == 0) {
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          int index = table.convertColumnIndexToModel(column);

          if (currentModel.isColumnFold(index)) {
            currentModel.unfoldingColumn(index);
          } else {
            currentModel.foldingColumn(index);
          }
          setCursor(Cursor.getDefaultCursor());
        } else if ((mod & InputEvent.BUTTON1_MASK) == 0 && (mod & InputEvent.BUTTON2_MASK) == 0) {
          if (row >= 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int index = table.convertColumnIndexToModel(column);
            if (currentModel.isRowFold(row, index)) {
              currentModel.unfoldingRow(row, index);
            } else {
              currentModel.foldingRow(row, index);
            }
            setCursor(Cursor.getDefaultCursor());
          }
        }
      }

      public void mousePressed(MouseEvent e) {
        int	mod	= e.getModifiers();
        int	row	= table.rowAtPoint(e.getPoint());
        int	column	= table.columnAtPoint(e.getPoint());

        if ((mod & InputEvent.BUTTON2_MASK) == 0 && (mod & InputEvent.BUTTON3_MASK) == 0) {
          // button 1 pressed
          if ((mod & InputEvent.CTRL_MASK) != 0 && (mod & InputEvent.SHIFT_MASK) != 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int index = table.convertColumnIndexToModel(column);

            currentModel.sortColumn(index);
            setCursor(Cursor.getDefaultCursor());
          } else if ((mod & InputEvent.CTRL_MASK) != 0) {
            // CTRL key pressed
            if (row >= 0) {
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              int index = table.convertColumnIndexToModel(column);
              if (currentModel.isRowFold(row, index)) {
                currentModel.unfoldingRow(row, index);
              } else {
                currentModel.foldingRow(row, index);
              }
              setCursor(Cursor.getDefaultCursor());
            }
          } else if ((mod & InputEvent.SHIFT_MASK) != 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int index = table.convertColumnIndexToModel(column);

            if (currentModel.isColumnFold(index)) {
              currentModel.unfoldingColumn(index);
            } else {
              currentModel.foldingColumn(index);
            }
            setCursor(Cursor.getDefaultCursor());
          }
        }
      }
    });

    table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        int	mod	= e.getModifiers();
        final int	column	= table.columnAtPoint(e.getPoint());

        columnMove = false;
        fromIndex  = column;
        if ((mod & InputEvent.BUTTON2_MASK) == 0 && (mod & InputEvent.BUTTON3_MASK) == 0) {
          if ((mod & InputEvent.CTRL_MASK) != 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int index = table.convertColumnIndexToModel(column);
            if (currentModel.isColumnFold(index)) {
              currentModel.unfoldingColumn(index);
            } else {
              currentModel.foldingColumn(index);
            }
            setCursor(Cursor.getDefaultCursor());
          } else if ((mod & InputEvent.SHIFT_MASK) != 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            currentModel.sortColumn(table.convertColumnIndexToModel(column));
            setCursor(Cursor.getDefaultCursor());
          } else if (e.getClickCount() == 2) {
            model.switchColumnFolding(table.convertColumnIndexToModel(column));
            resetWidth();
          } else {
            table.setColumnSelectionInterval(column, column);
            if (getSelectedCell().getY() == -1) {
              table.setRowSelectionInterval(0, 0);
            }
          }
        }
        if ((mod & InputEvent.BUTTON1_MASK) == 0 && (mod & InputEvent.BUTTON2_MASK) == 0) {
          JPopupMenu labelPopupMenu = new JPopupMenu();
          JMenuItem item;

          item = new JMenuItem(VlibProperties.getString("set_column_info"));
          item.addActionListener(new  java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              try {
                report.setColumnInfo();
              } catch(VException ve) {
                // exception thrown by trigger.
              }
              table.setRowSelectionAllowed(false);
              table.setColumnSelectionAllowed(false);
              table.setCellSelectionEnabled(true);
              setCursor(Cursor.getDefaultCursor());
            }
          });
          labelPopupMenu.add(item);
          item = new JMenuItem(VlibProperties.getString("sort_ASC"));
          item.addActionListener(new  java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              currentModel.sortColumn(table.convertColumnIndexToModel(column), 1);
              table.setRowSelectionAllowed(false);
              table.setColumnSelectionAllowed(false);
              table.setCellSelectionEnabled(true);
              setCursor(Cursor.getDefaultCursor());
            }
          });
          labelPopupMenu.add(item);
          item = new JMenuItem(VlibProperties.getString("sort_DSC"));
          item.addActionListener(new  java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              currentModel.sortColumn(table.convertColumnIndexToModel(column), -1);
              table.setRowSelectionAllowed(false);
              table.setColumnSelectionAllowed(false);
              table.setCellSelectionEnabled(true);
              setCursor(Cursor.getDefaultCursor());
            }
          });
          labelPopupMenu.add(item);
          item = new JMenuItem(VlibProperties.getString("add_column"));
          item.addActionListener(new  java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              addColumn(column + 1);
              table.setRowSelectionAllowed(false);
              table.setColumnSelectionAllowed(false);
              table.setCellSelectionEnabled(true);
              setCursor(Cursor.getDefaultCursor());
            }
          });
          labelPopupMenu.add(item);
          if (model.getAccessibleColumn(table.convertColumnIndexToModel(column)).isAddedAtRuntime()) {
            item = new JMenuItem(VlibProperties.getString("remove_column"));
            item.addActionListener(new  java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                removeColumn(column);
                table.setRowSelectionAllowed(false);
                table.setColumnSelectionAllowed(false);
                table.setCellSelectionEnabled(true);
                setCursor(Cursor.getDefaultCursor());
              }
            });
            labelPopupMenu.add(item);
          }
          if (model.getAccessibleColumn(table.convertColumnIndexToModel(column)).isAddedAtRuntime()) {
            item = new JMenuItem(VlibProperties.getString("set_column_data"));
            item.addActionListener(new  java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                  report.setColumnData();
                } catch(VException ve) {
                  // exception thrown by the trigger.
                }
                table.setRowSelectionAllowed(false);
                table.setColumnSelectionAllowed(false);
                table.setCellSelectionEnabled(true);
                setCursor(Cursor.getDefaultCursor());
              }
            });
            labelPopupMenu.add(item);
          }
          labelPopupMenu.addPopupMenuListener( new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {
              table.setRowSelectionAllowed(false);
              table.setColumnSelectionAllowed(false);
              table.setCellSelectionEnabled(true);
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
              table.setColumnSelectionAllowed(true);
              table.setRowSelectionAllowed(false);
              table.setColumnSelectionInterval(column, column);
            }
          });
          labelPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }

      public void mouseReleased(MouseEvent e) {
        boolean columnOrderChanged	= false;

        if (columnMove) {
          int[] newColumnOrder = new int[model.getColumnCount()];

          // test if columns really moved (not 1->2->1)
          if (fromIndex != toIndex) {
            int index = 0;
            int hiddenColumnsCount = 0;
            columnOrderChanged = true;

            for (int i = 0; i < newColumnOrder.length; i++) {
              if (!model.getAccessibleColumn(i).isVisible()) {
                hiddenColumnsCount += 1;
                newColumnOrder[i] = model.getDisplayOrder(index);
                index += 1;
              } else if (i == (toIndex + hiddenColumnsCount)) {
                newColumnOrder[i] = table.convertColumnIndexToModel(toIndex);
              } else {
                if (index == (fromIndex + hiddenColumnsCount)) {
                  index += 1;
                }
                newColumnOrder[i] = model.getDisplayOrder(index);
                index += 1;
              }
            }
          }

          // give the new column order tab
          if (columnOrderChanged) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            model.columnMoved(newColumnOrder);
            setCursor(Cursor.getDefaultCursor());
          }
        }
      }
    });

    table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
      public void columnMoved(TableColumnModelEvent e) {
        columnMove = true;
        toIndex = e.getToIndex();
      }
      public void columnMarginChanged(ChangeEvent e) {}
      public void columnAdded(TableColumnModelEvent e) {}
      public void columnRemoved(TableColumnModelEvent e) {}
      public void columnSelectionChanged(ListSelectionEvent e) {}
    });

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        report.setMenu();
      }
    });
  }

  private int resetColumnSize(int pos) {
    VReportColumn	column = model.getAccessibleColumn(table.convertColumnIndexToModel(pos));
    int			width;
    String              help;

    if (column.isFolded() && !(column instanceof VSeparatorColumn)) {
      width = 1;
      help = column.getLabel();
    } else if (column instanceof VDecimalColumn || column instanceof VIntegerColumn) {
      width = Math.max(column.getLabel().length(), column.getWidth());
      // Integer and Fixed column can contain , data generated by operations like sum, multiplication
      // --> compute column width occording to data.
      width = Math.max(width, model.computeColumnWidth(table.convertColumnIndexToModel(pos)));
      help = column.getHelp();
    } else {
      width = Math.max(column.getLabel().length(), column.getWidth());
      help = column.getHelp();
    }

    if (width != 0) {
      width = width * getFontMetrics(parameters.getFont()).charWidth('W') + 4;
    }
    table.getColumnModel().getColumn(pos).setPreferredWidth(width);

    ((JComponent)table.getColumnModel().getColumn(pos).getHeaderRenderer()).setToolTipText(help);

    return width;
  }

  /**
   * Display table informations in the footer of the table
   */
  private void setInfoTable() {
    setStatisticsText("<html><font color=#736AFF>"
                      + table.getRowCount()
                      + "/"
                      + model.getBaseRowCount()
                      + "/"
                      + model.getVisibleRowCount()
                      + "</font>");
  }

  /**
   * Work around to get tooltips on tables columns
   * the DefaultTableCellRenderer class overwrites border setting
   */
  class HeaderRenderer extends DefaultTableCellRenderer {

    public HeaderRenderer() {
      setHorizontalAlignment(SwingConstants.CENTER);
      setOpaque(true);
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    }

    public void updateUI() {
      super.updateUI();
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
      JTableHeader header;

      if (table == null) {
        header = null;
      } else {
        header = table.getTableHeader();
      }

      if (header != null) {
        setEnabled(header.isEnabled());
        setComponentOrientation(header.getComponentOrientation());
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
      } else {
        setEnabled(true);
        setComponentOrientation(ComponentOrientation.UNKNOWN);
        setForeground(UIManager.getColor("TableHeader.foreground"));
        setBackground(UIManager.getColor("TableHeader.background"));
        setFont(UIManager.getFont("TableHeader.font"));
      }

      setValue(value);
      setInfoTable();
      return this;
    }
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 565901305687203629L;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static CellRenderer foldedCell = new CellRenderer(Constants.STA_FOLDED);
  public static final Dimension interCellSpacing = new Dimension(1, 1);

  // Here are the different components linked to the DReport :
  private final MReport	model;			// report model
  private final VReport	report;			// direct access to the model (VWindow)
  private DTable		table;			// table view

  // These variable contains personalisation parameters for the Report
  private Parameters		parameters;		// report personalisation

  // This variable contain information to render each columns
  private CellRenderer[][]	cellRenderers;		// table cell renderers

  // Here, we store each column of the DReport
  private TableColumn[]		tablecolumns;		// table of tablecolumns

  // Cache and hack variables
  private boolean		columnMove;		// true if a column moved
  private int			fromIndex;		// from index column
  private int			toIndex;		// to index column
  private int			tableWidth;		// Table width

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long	serialVersionUID = -6833408762223390823L;
}
