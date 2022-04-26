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
 * $Id: DListDialog.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoundedRangeModel;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.kopi.galite.visual.form.UField;
import org.kopi.galite.visual.form.UListDialog;
import org.kopi.galite.visual.form.VDictionary;
import org.kopi.galite.visual.form.VForm;
import org.kopi.galite.visual.form.VListDialog;
import org.kopi.galite.util.base.InconsistencyException;
import org.kopi.galite.visual.MessageCode;
import org.kopi.galite.visual.UWindow;
import org.kopi.galite.visual.VException;
import org.kopi.galite.visual.VRuntimeException;
import org.kopi.galite.visual.VlibProperties;
import org.kopi.vkopi.lib.ui.swing.base.ListDialogCellRenderer;
import org.kopi.vkopi.lib.ui.swing.visual.DObject;
import org.kopi.vkopi.lib.ui.swing.visual.DWindow;
import org.kopi.vkopi.lib.ui.swing.visual.SwingThreadHandler;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;

public class DListDialog extends JPanel implements UListDialog {

    // --------------------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------------------

    public DListDialog(VListDialog model) {
        this.model = model;
    }

    // --------------------------------------------------------------------
    // IMPLEMENTATIONS
    // --------------------------------------------------------------------


    public int selectFromDialog(UWindow window, UField field, boolean showSingleEntry) {
        return selectFromDialogIn((Component) field, showSingleEntry);
    }


    public int selectFromDialog(UWindow window, boolean showSingleEntry) {
        try {
            return selectFromJDialog(window != null ? (Component) window : DObject.phantom, showSingleEntry);
        } catch (VException v) {
            throw new VRuntimeException(v);
        }
    }

    /**
     * Displays a dialog box returning position of selected element.
     *
     * @throws VException an exception may be raised by string formatter
     */
    private int selectFromJDialog(Component parent, boolean showSingleEntry) throws VException {
        if (!showSingleEntry && model.getCount() == 1) {
            return model.convert(0);
        }

        if (parent instanceof Frame) {
            dialog = new JDialog((Frame) parent, true);
        } else {
            dialog = new JDialog(Utils.getFrameAncestor(parent), true);
        }

        build();
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);

        addKeyListener(); // (DWindow)null);
        dialog.pack();
        table.requestFocusInWindow();
        dialog.setVisible(true);

        if (escaped) {
            return -1;
        } else if (doNewForm) {
            //      ((DWindow)listenerOwner).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            return doNewForm(model.getForm(), model.getNewForm());
        } else {
            return ((ListDialogTableModel) table.getModel()).getSelectedElement();
        }
    }

    /**
     * Displays a dialog box returning position of selected element.
     *
     * @throws VException an exception may be raised by string formater
     */
    private int selectFromDialogIn(final Component field, final boolean showSingleEntry) {
        DisplayHandler handler = new DisplayHandler(field, showSingleEntry);

        SwingThreadHandler.startAndWait(handler);
        return handler.getReturnValue();
    }

    /**
     * Build
     */
    private void build() {
        setLayout(new BorderLayout());
        table = new JTable(new ListDialogTableModel());

        table.setFocusable(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoscrolls(true);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setRowSelectionInterval(0, 0); // select first row
        table.setIntercellSpacing(new Dimension(2, 0));
        table.setRowHeight(rowHeight);
        table.setShowGrid(false);

        ListDialogCellRenderer renderer = new ListDialogCellRenderer(model.getColumns());

        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                escaped = false;
                e.consume();
                dispose();
            }

            public void mouseReleased(MouseEvent e) {
                escaped = false;
                e.consume();
                dispose();
            }
        });

        table.getColumnModel().addColumnModelListener((ListDialogTableModel) table.getModel());

        // set columns width and table width = sum columns width
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 0;
        int height = 0;

        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            @SuppressWarnings("deprecation")
            int columnWidth = Toolkit.getDefaultToolkit().getFontMetrics(table.getFont()).stringWidth("W") * model.getSizes()[i];
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidth);
            width += columnWidth;
        }

        height = table.getRowHeight() * table.getRowCount();

        table.setPreferredScrollableViewportSize(new Dimension(Math.min(width, (int) (screen.width * 0.8f)),
                Math.min(height, (int) (screen.height * 0.8f))));

        if (table.getModel().getColumnCount() == 1) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setTableHeader(null);
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        scrollpane = new JScrollPane(table);
        scrollpane.getVerticalScrollBar().setFocusable(false);
        scrollpane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
        add(scrollpane);
    }

    /**
     * Add Key Listener
     */
    private void addKeyListener() {
        listener = new KeyAdapter() {
            String current = "";

            public void keyPressed(KeyEvent k) {
                int key = k.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_SPACE:
                        if (model.getNewForm() != null || model.isForceNew()) {
                            doNewForm = true;
                        }
                    case KeyEvent.VK_ENTER:
                        escaped = false;
                    case KeyEvent.VK_ESCAPE:
                        dispose();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        int select = table.getSelectedRow();
                        select = Math.max(0, select - 20);
                        ensureSelectionIsVisible(select);
                        current = "";
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        select = table.getSelectedRow();
                        select = Math.min(table.getRowCount() - 1, select + 20);
                        ensureSelectionIsVisible(select);
                        current = "";
                        break;
                    case KeyEvent.VK_DOWN:
                        select = table.getSelectedRow();
                        if (++select < table.getRowCount()) {
                            ensureSelectionIsVisible(select);
                        }
                        current = "";
                        break;
                    case KeyEvent.VK_UP:
                        select = table.getSelectedRow();
                        if (select-- > 0) {
                            ensureSelectionIsVisible(select);
                        }
                        current = "";
                        break;
                    default:
                        char aKey = k.getKeyChar();
                        int i;

                        if (!Character.isLetterOrDigit(aKey)) {
                            return;
                        }

                        current += ("" + aKey).toLowerCase().charAt(0);

                        for (i = 0; i < table.getModel().getRowCount(); i++) {
                            String text2 = ((ListDialogTableModel) table.getModel()).getDisplayedValueAt(i).toString();
                            int comp = Math.min(text2.length(), current.length());
                            if (current.equalsIgnoreCase(text2.substring(0, comp))) {
                                ensureSelectionIsVisible(i);
                                break;
                            }
                        }
                        if (i == table.getModel().getRowCount()) {
                            Toolkit.getDefaultToolkit().beep();
                            current = "";
                        }
                }
                k.consume();
            }
        };
        table.addKeyListener(listener);
        table.requestFocusInWindow();
    }

    private void ensureSelectionIsVisible(final int select) {
        BoundedRangeModel brm = scrollpane.getVerticalScrollBar().getModel();
        int oldSel = -1;
        int size = table.getRowHeight(); // border
        int min = brm.getValue() / size;
        int max = (brm.getValue() + brm.getExtent()) / size;

        if (select < min) {
            brm.setValue((select + 1) * size - brm.getExtent());
            oldSel = table.getSelectedRow();
        } else if (select > max - 1) {
            brm.setValue(select * size);
            oldSel = table.getSelectedRow();
        }
        table.setRowSelectionInterval(select, select);

        if ((brm.getValue() / size != min)) {
            int min2 = brm.getValue() / size;
            int max2 = (brm.getValue() + brm.getExtent()) / size;
            if (oldSel >= min2 && oldSel <= max2) {
                // oldSel visible => redisplay after that
                final int oldSelFinal = oldSel;
                ((AbstractTableModel) table.getModel()).fireTableRowsUpdated(oldSel, oldSel);
                javax.swing.SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        ((AbstractTableModel) table.getModel()).fireTableRowsUpdated(oldSelFinal, oldSelFinal);
                    }
                });
            }
        }
    }

    /**
     * Do a new form
     */
    private int doNewForm(final VForm form, final VDictionary cstr) throws VException {
        if (form != null && cstr != null) {
            return cstr.add();
        } else {
            return VListDialog.Companion.getNEW_CLICKED();
        }
    }

    /**
     * Dispose the List Dialog
     */
    private void dispose() {
        if (dialog != null) {
            dialog.dispose();
        } else {
            popup.dispose();
        }
    }

    // --------------------------------------------------------------------
    // DISPLAYHANDLER
    // --------------------------------------------------------------------

    private class DisplayHandler implements Runnable {

        // ----------------------------------------------------
        // CONSTRUCTOR
        // ----------------------------------------------------

        public DisplayHandler(final Component field, final boolean showSingleEntry) {
            this.field = field;
            this.showSingleEntry = showSingleEntry;
        }

        public void run() {
            retVal = selectFromDialogAWT(field, showSingleEntry);
        }

        public int getReturnValue() {
            return retVal;
        }

        //-----------------------------------------------------
        // IMPLEMENTATIONS
        //-----------------------------------------------------

        private int selectFromDialogAWT(Component field, boolean showSingleEntry) {
            try {
                Frame focus;

                if (field != null) {
                    focus = Utils.getFrameAncestor(field);
                } else {
                    Window window;

                    window = FocusManager.getCurrentManager().getFocusedWindow();

                    while (!(window instanceof Frame) && window != null) {
                        window = window.getOwner();
                    }

                    if (window instanceof Frame) {
                        focus = (Frame) window;
                    } else {
                        focus = null;
                    }
                }

                if (model.isTooManyRows()) {
                    Object[] options = {VlibProperties.getString("CLOSE")};

                    JOptionPane.showOptionDialog(focus,
                            MessageCode.INSTANCE.getMessage("VIS-00028"),
                            VlibProperties.getString("Notice"),
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            DWindow.ICN_NOTICE,
                            options,
                            options[0]);
                }

                build();

                if (!showSingleEntry && table.getModel().getRowCount() == 1) {
                    return model.convert(0);
                }

                popup = new JDialog(focus, true);
                popup.setUndecorated(true);

                JPanel panel = new JPanel();
                panel.setBorder(new EtchedBorder());

                popup.getContentPane().add(panel);
                panel.setLayout(new BorderLayout());
                panel.add(DListDialog.this, BorderLayout.CENTER);
                if (model.getNewForm() != null || model.isForceNew()) {
                    JButton button = new JButton(VlibProperties.getString("new-record"));

                    panel.add(button, BorderLayout.SOUTH);
                    button.setFocusable(true); // !!! laurent 20020411
                    //      button.setFont(DObject.FNT_DIALOG);
                    // use to allow the escape command when the button is focused
                    button.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                dispose();
                                e.consume();
                            }
                        }
                    });
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            doNewForm = true;
                            escaped = false;
                            dispose();
                        }
                    });
                }

                addKeyListener();
                popup.pack();
                positionPopup(focus, field, panel);

                if (listenerOwner != null) {
                    ((DWindow) listenerOwner).setCursor(Cursor.getDefaultCursor());
                }

                popup.setFocusCycleRoot(true);
                popup.setVisible(true);

                VForm temp = model.getForm();

                model.setForm(null);
                if (escaped) {
                    return -1;
                } else if (doNewForm) {
                    if (listenerOwner != null) {
                        ((DWindow) listenerOwner).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                    return doNewForm(temp, model.getNewForm());
                } else {
                    return ((ListDialogTableModel) table.getModel()).getSelectedElement();
                }
            } catch (VException v) {
                throw new VRuntimeException(v);
            }
        }

        private void positionPopup(Frame frame, Component field, JPanel panel) {
            Rectangle location;
            Point tryHere;

            if (field != null) {
                // try to position under field
                tryHere = field.getLocationOnScreen();
                tryHere.y += field.getSize().height;
            } else {
                tryHere = null;
            }

            location = Utils.calculateBounds(popup, tryHere, frame);

            popup.setLocation(new Point(location.x, location.y));
        }

        // ----------------------------------------------------
        // DATA MEMBERS
        // ----------------------------------------------------

        private int retVal;
        private Component field;
        private boolean showSingleEntry;
    }

    //---------------------------------------------------------------------
    // LISTDIALOGTABLEMODEL
    //---------------------------------------------------------------------

    private class ListDialogTableModel extends AbstractTableModel implements TableColumnModelListener {

        public ListDialogTableModel() {
            if (model.getData().length != 0 && model.getCount() > model.getData()[0].length) {
                throw new InconsistencyException("UNEXPECTED DIFFERENT SIZE IN SELECTi DIALOG");
            }

            if (model.getTitles() == null) {
            } else {
                if (model.getData().length > model.getTitles().length) {
                    throw new InconsistencyException("UNEXPECTED DIFFERENT SIZE IN SELECT DIALOG");
                }
            }
        }

        // sort with colon 0
        public void columnMoved(TableColumnModelEvent e) {
            sort();
        }

        // No adapter class exists for TableColumnModelListener
        public void columnMarginChanged(ChangeEvent e) {
        }

        public void columnAdded(TableColumnModelEvent e) {
        }

        public void columnRemoved(TableColumnModelEvent e) {
        }

        public void columnSelectionChanged(ListSelectionEvent e) {
        }

        public int getSelectedElement() {
            return model.convert(table.getSelectedRow());
        }

        public int getColumnCount() {
            return model.getColumnCount();
        }

        public int getRowCount() {
            return model.getCount() - (model.isSkipFirstLine() ? 1 : 0);
        }

        public Object getValueAt(int row, int col) {
            return model.getData()[col][model.getTranslatedIdents()[row]];
        }

        public Object getValueAt(int row) {
            return model.getData()[table.convertColumnIndexToModel(0)][model.getTranslatedIdents()[row]];
        }

        public Object getDisplayedValueAt(int row) {
            return DListDialog.this.model.getColumns()[table.convertColumnIndexToModel(0)].formatObject(getValueAt(row));
        }

        public String getColumnName(int column) {
            return model.getColumnName(column);
        }

        /**
         * Bubble sort the columns from right to left
         */
        private void sort() {
            int left = 0;
            int sel = -1;

            if (table != null) {
                sel = getSelectedElement();
                left = table.convertColumnIndexToModel(0);
            }

            model.sort(left);

            if (table != null) {
                for (int i = 0; i < model.getCount(); i++) {
                    if (model.getIdents()[model.getTranslatedIdents()[i]] == sel) {
                        table.setRowSelectionInterval(i, i);
                    }
                }

                table.tableChanged(new TableModelEvent(this));
            }
        }

        // ----------------------------------------------------------------------
        // DATA MEMBERS
        // ----------------------------------------------------------------------

        private static final long serialVersionUID = 1956519774210966774L;
    }

    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------

    private final VListDialog model;
    private JTable table;
    private JDialog popup;
    private JDialog dialog;
    private KeyListener listener;
    private Component listenerOwner;
    private JScrollPane scrollpane;
    private boolean escaped = true;
    private boolean doNewForm;
    private static final int rowHeight = UIManager.getInt("ListDialog.row.height");

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 7488615622311380907L;
}
