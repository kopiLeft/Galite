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
 * $Id: DTextField.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;

import org.kopi.galite.form.ModelTransformer;
import org.kopi.galite.form.UTextField;
import org.kopi.galite.form.VConstants;
import org.kopi.galite.form.VField;
import org.kopi.galite.form.VFieldUI;
import org.kopi.galite.form.VStringField;
import org.kopi.vkopi.lib.ui.swing.base.JHtmlTextArea;
import org.kopi.vkopi.lib.ui.swing.base.TextSelecter;
import org.kopi.vkopi.lib.ui.swing.spellchecker.SpellChecker;
import org.kopi.vkopi.lib.ui.swing.spellchecker.SpellException;
import org.kopi.galite.ui.swing.visual.DMenuTree;
import org.kopi.vkopi.lib.ui.swing.visual.DWindow;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;
import org.kopi.galite.visual.ApplicationConfiguration;
import org.kopi.galite.visual.ApplicationContext;
import org.kopi.galite.visual.VlibProperties;

/**
 * DTextField is a panel composed in a text field and an information panel
 * The text field appear as a JLabel until it is edited
 */
public class DTextField extends DField implements UTextField, VConstants {

    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------

    public DTextField(VFieldUI model,
                      DLabel label,
                      int align,
                      int options,
                      boolean detail) {
        super(model, label, align, options, detail);

        noEdit = (options & VConstants.FDO_NOEDIT) != 0;
        scanner = (options & VConstants.FDO_NOECHO) != 0 && getModel().getHeight() > 1;

        if (getModel().getHeight() == 1
                || (!scanner && ((getModel().getTypeOptions() & FDO_DYNAMIC_NL) > 0))) {
            transformer = new DefaultTransformer(getModel().getWidth(), getModel().getHeight());
        } else if (!scanner) {
            transformer = new NewlineTransformer(getModel().getWidth(),
                    getModel().getHeight());
        } else {
            transformer = new ScannerTransformer(this);
        }

        if (hasStyledContent()) {
            document = new KopiStyledDocument(getModel(), transformer);
        } else if (!scanner) {
            document = new KopiFieldDocument(getModel(), transformer);
        } else {
            document = new KopiScanDocument(getModel(), transformer);
        }

        undoManager = new UndoManager();

        listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                DTextField.this.getModel().setChanged(true);
            }

            public void removeUpdate(DocumentEvent e) {
                DTextField.this.getModel().setChanged(true);
            }

            public void changedUpdate(DocumentEvent e) {
                DTextField.this.getModel().setChanged(true);
            }
        };

        JComponent comp;

        comp = createFieldGUI(getModel().getWidth(),
                getModel().getHeight(),
                (getModel().getHeight() == 1) ? 1 :
                        ((VStringField) getModel()).getVisibleHeight(),
                (options & VConstants.FDO_NOECHO) != 0,
                scanner,
                new DFieldMouseListener(),
                align);
        if (model.hasAutofill() && getModel().getDefaultAccess() >= VConstants.ACS_SKIPPED) {
            document.setAutofill(true);
        }
        // fields that has defined an action will be always disabled.
        if (model.hasAction()) {
            mouseListener = new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    DTextField.this.model.executeAction();
                }

                public void mouseEntered(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            };
            field.setEditable(false);
        }
        add(comp, BorderLayout.CENTER);
    }

    public JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();

        if (model.hasAutofill() && getModel().getDefaultAccess() > VConstants.ACS_SKIPPED) {
            popup.add(new ListAction());
        }
        if ((options & VConstants.FDO_NOEDIT) == 0) {
            Action redo = (Action) ((DWindow) getModel().getForm().getDisplay()).getRedoAction();
            Action undo = (Action) ((DWindow) getModel().getForm().getDisplay()).getUndoAction();

            if (redo != null) {
                popup.add(redo);
            }
            if (undo != null) {
                popup.add(undo);
            }
        }


        ApplicationConfiguration appDefaults;
        String dictionaryServer;

        appDefaults = ApplicationConfiguration.Companion.getConfiguration();

        dictionaryServer = appDefaults.getDictionaryServer();

        if (dictionaryServer != null) {
            ApplicationConfiguration.Language[] languages;

            languages = appDefaults.getDictionaryLanguages();

            if (languages.length > 0) {
                JMenu menu = new JMenu(VlibProperties.getString("aspell-menu-title"));

                for (int i = 0; i < languages.length; i++) {
                    final String spellCommand;
                    javax.swing.Action spellChecker;

                    spellCommand = dictionaryServer + " " + languages[i].getOptions();
                    spellChecker = new AbstractAction(VlibProperties.getString(languages[i].getLanguage())) {
                        /**
                         * Comment for <code>serialVersionUID</code>
                         */
                        private static final long serialVersionUID = 5656229829162013955L;

                        public void actionPerformed(ActionEvent e) {
                            if (field.isEditable()) {
                                SpellChecker spellChecker = new SpellChecker(spellCommand,
                                        Utils.getFrameAncestor(field),
                                        document);

                                try {
                                    spellChecker.check();
                                } catch (SpellException se) {
                                    se.printStackTrace();
                                }
                            }
                        }
                    };
                    menu.add(spellChecker);
                }
                popup.add(menu);
            }
        }
        // Bookmarks (Shortcuts)
        if (ApplicationContext.Companion.getMenu() != null) {
            javax.swing.Action[] bookmarks = ((DMenuTree) ApplicationContext.Companion.getMenu().getDisplay()).getBookmarkActions();

            if (bookmarks.length > 0) {
                popup.addSeparator();

                JMenu menu = new JMenu(VlibProperties.getString("toolbar-title"));

                for (int i = 0; i < bookmarks.length; i++) {
                    menu.add(bookmarks[i]);
                }
                popup.add(menu);
            }
        }
        return popup;
    }

    // ----------------------------------------------------------------------
    // IMPLEMENTATION OF ABSTRACTS METHODS
    // ----------------------------------------------------------------------

    /**
     * Returns the object associed to record r
     *
     * @return the displayed value at this position
     */
    public Object getObject() {
        return getText();
    }

    // ----------------------------------------------------------------------
    // UTILITIES
    // ----------------------------------------------------------------------

    /**
     * Returns the string associed to record r
     *
     * @return the displayed value at this position
     */
    public String getText() {
        return document.getModelText();
    }

    // ----------------------------------------------------------------------
    // UI MANAGEMENT
    // ----------------------------------------------------------------------

    public void setDisplayProperties() {
        document.setState(state);
        field.invalidate();
        field.validate();
        field.repaint();
    }

    // ----------------------------------------------------------------------
    // DRAWING
    // ----------------------------------------------------------------------

    public void updateAccess() {
        if (model.hasAction() && mouseListener != null) {
            if (getAccess() >= ACS_VISIT) {
                field.addMouseListener(mouseListener);
                document.setHasAction(true);
            } else {
                field.removeMouseListener(mouseListener);
                document.setHasAction(false);
            }
        }
        label.update(getModel(), getPosition());
        document.setState(state);
        super.updateAccess();
    }

    public synchronized void updateText() {
        document.setState(state);

        String newModelTxt = getModel().getText(getRowController().getBlockView().getRecordFromDisplayLine(getPosition()));
        String currentModelTxt = document.getModelText();

        if ((newModelTxt == null && currentModelTxt != null) || !newModelTxt.equals(currentModelTxt)) {
            // the ui of the field is updated by the model not by the
            // user, so don't inform the model (that the value is changed, ...)
            document.removeDocumentListener(listener);
            document.removeUndoableEditListener(undoManager);
            document.removeUndoableEditListener(((DWindow) getModel().getForm().getDisplay()).getUndoableEditListener());

            document.setModelText(newModelTxt);

            if (inside) {
                document.addDocumentListener(listener);
                document.addUndoableEditListener(undoManager);
                if (field.isEditable()) {
                    document.addUndoableEditListener(((DWindow) getModel().getForm().getDisplay()).getUndoableEditListener());
                }
            }
        }

        super.updateText();
        // LACKNER 2005.11.18
        // If this field has the focus, and the value is set
        // with this method, then the value should be selected.
        // RT #25754
        // this is required because, the focus-gained event
        // is handled before the updateText-event although
        // the focus-gained event is in the queue after the
        // updateText-event.
        if (modelHasFocus() && !selectionAfterUpdateDisabled) {
            TextSelecter.TEXT_SELECTOR.selectText(field);
            selectionAfterUpdateDisabled = false;
        }
    }

    public synchronized void updateFocus() {
        document.setState(state);
        label.update(getModel(), getPosition());
        fireMouseHasChanged();
        if (!modelHasFocus()) {
            if (inside) {
                inside = false;
                leaveMe();
            }
        } else {
            if (!inside) {
                inside = true;
                enterMe();
            }
        }
        super.updateFocus();
    }

    private void enterMe() {
        VField model = getModel();

        if (scanner) {
            document.setModelText("");
        }

        field.setEditable(!noEdit);
        field.setFocusable(true);

        ((DWindow) model.getForm().getDisplay()).setUndoManager(undoManager);
        if (!noEdit) {
            document.addUndoableEditListener(((DWindow) model.getForm().getDisplay()).getUndoableEditListener());
        }

        document.addDocumentListener(listener);

        // request the focus, must be done later, otherwise it
        // does not work
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                field.requestFocusInWindow();
            }
        });
    }

    private void leaveMe() {
        VField model = getModel();

        if (!noEdit) {
            document.removeUndoableEditListener(((DWindow) model.getForm().getDisplay()).getUndoableEditListener());
        }

        document.removeDocumentListener(listener);

        field.setEditable(false);
        field.setFocusable(false);
        reInstallSelectionFocusListener();
        // update GUI: for
        // scanner nescessary
        if (scanner) {
            // trick: it is now displayed on a different way
            document.setModelText(document.getModelText());
        }
    }

    public void setHasCriticalValue(boolean b) {
        if (b) {
            document.setHasCriticalValue(true);
            repaint();
        } else {
            document.setHasCriticalValue(false);
            setDisplayProperties();
            repaint();
        }
    }

    public void updateColor() {
        if (getForeground() != null) {
            field.setForeground(getForeground());
        } else {
            // restore default foreground
            field.setForeground(UIManager.getLookAndFeelDefaults().getColor("TextField.foreground"));
        }
        if (document != null) {
            document.setBgColor(getBackground());
        }
        field.repaint();
    }

    /**
     * set blink state
     */
    public void setBlink(boolean b) {
        if (b) {
            document.setAlert(true);
            repaint();
        } else {
            document.setAlert(false);
            setDisplayProperties();
            repaint();
        }
    }

    /*package*/ class ListAction extends AbstractAction {

        /*package*/ ListAction() {
            super(VlibProperties.getString("item-index"));
        }

        public void actionPerformed(ActionEvent e) {
            getModel().getForm().performAsyncAction(new org.kopi.galite.visual.Action() {
                public void execute() {
                    model.transferFocus(DTextField.this);
                    model.autofillButton();
                }
            });
        }

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 410472821803045205L;
    }

    // ----------------------------------------------------------------------
    // Create field ui
    // ----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private JComponent createFieldGUI(int col,
                                      int rows,
                                      int visibleRows,
                                      boolean noEcho,
                                      boolean scanner,
                                      MouseListener mouseListener,
                                      int align) {
        JTextComponent textfield;

        if (noEcho && rows == 1) {
            textfield = new JPasswordField(col) {
                /**
                 * Comment for <code>serialVersionUID</code>
                 */
                private static final long serialVersionUID = -6305606934554696596L;

                public Dimension getPreferredSize() {
                    // lackner 29.09.2004
                    // the default calkulation is one pixel too small
                    Dimension dim = super.getPreferredSize();

                    dim.width += 1;
                    return dim;
                }
            };
        } else {
            if (rows > 1) {
                if (scanner) {
                    // scanner fields have a witdh of 40
                    col = 40;
                }

                if (hasStyledContent()) {
                    textfield = new JHtmlTextArea(visibleRows, col);
                } else {
                    textfield = new JTextArea(visibleRows, col);
                    ((JTextArea) textfield).setLineWrap(true);
                    ((JTextArea) textfield).setWrapStyleWord(true);
                }
            } else {
                textfield = new JTextField(col) {
                    /**
                     * Comment for <code>serialVersionUID</code>
                     */
                    private static final long serialVersionUID = -2960748127356900841L;

                    public Dimension getPreferredSize() {
                        // lackner 29.09.2004
                        // the default calkulation is one pixel too small
                        // lackner 05.11.2004
                        // jdk 1.4.1 does not use the insets (fixed in 1.4.2)
                        Dimension size = super.getPreferredSize();

                        if (getColumns() != 0) {
                            Insets insets = getInsets();

                            size.width = 1
                                    + getColumns() * getColumnWidth() + insets.left + insets.right;
                        }
                        return size;
                    }
                };
                ((JTextField) textfield).setHorizontalAlignment(align);
            }
        }

        textfield.setFocusable(false);
        textfield.setEditable(false);

        // Tab is used for the default traversal keys. But these do not
        // work with the concept of kopi so we remove that default behavior
        textfield.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        textfield.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        textfield.setDocument(document);

        if (hasStyledContent()) {
            ((KopiStyledDocument) document).setEditorKit((HTMLEditorKit) ((JHtmlTextArea) textfield).getEditorKit());
        }
        textfield.addMouseListener(new RightMenu());
        textfield.addMouseListener(mouseListener);
        if (!noEdit) {
            textfield.addFocusListener(TextSelecter.TEXT_SELECTOR);
        }


        // scroller
        JScrollPane scroller;

        if (rows > visibleRows && !scanner) {
            // scanner fields have never more than one line, so never a scroller is needed
            scroller = new JScrollPane();
            scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scroller.setViewportBorder(null);
            scroller.getViewport().setView(textfield);
            scroller.setSize(getPreferredSize());
            scroller.setBorder(null);
            scroller.setFocusable(false);
            if (mouseListener != null) {
                scroller.addMouseListener(mouseListener);
            }
            scroller.setViewportBorder(textfield.getBorder());
            textfield.setBorder(null);
        } else {
            scroller = null;
        }

        textfield.addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(FocusEvent e) {
                invalidate();
                validate();
                repaint();
            }

            public void focusLost(FocusEvent e) {
                // don't forget to repaint the kopi textfield
                invalidate();
                validate();
                repaint();
            }
        });
        field = textfield;
        ((DForm) getModel().getForm().getDisplay()).getEnvironment().addDefaultTextKey(textfield, rows > 1);
        if (scroller != null) {
            return scroller;
        } else {
            return textfield;
        }
    }

    /**
     * Returns true of this text field should have the ability to handle formatted content.
     */
    protected boolean hasStyledContent() {
        if (getModel() instanceof VStringField) {
            return ((VStringField) getModel()).isStyled();
        } else {
            return false;
        }
    }

    /**
     * remove selection focus Listener
     * if the listener specified by the argument was not previously added to this component
     * or If listener l is null,
     * no exception is thrown and no action is performed.
     */
    public void removeSelectionFocusListener() {
        if (!noEdit) {
            field.removeFocusListener(TextSelecter.TEXT_SELECTOR);
        }
    }

    public void addSelectionFocusListener() {
        if (!noEdit) {
            field.addFocusListener(TextSelecter.TEXT_SELECTOR);
        }
    }

    public void reInstallSelectionFocusListener() {
        removeSelectionFocusListener();
        addSelectionFocusListener();
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    protected JTextComponent field;        // the text component
    protected boolean inside;
    protected boolean noEdit;
    protected boolean scanner;
    protected KopiDocument document;
    protected ModelTransformer transformer;
    protected DocumentListener listener;
    private UndoManager undoManager;
    private MouseListener mouseListener;

    // ----------------------------------------------------------------------
    // INITIALIZERS
    // ----------------------------------------------------------------------

    {
        inside = false;
    }

    /*package*/ static class DefaultTransformer implements ModelTransformer {
        public DefaultTransformer(int col, int row) {
            this.col = col;
            this.row = row;
        }

        public String toGui(String modelTxt) {
            return modelTxt;
        }

        public String toModel(String guiTxt) {
            return guiTxt;
        }

        public boolean checkFormat(String source) {
            return (row == 1) ? true : (convertToSingleLine(source, col, row).length() <= row * col);
        }

        int col;
        int row;
    }

    /*package*/ static class ScannerTransformer implements ModelTransformer {
        public ScannerTransformer(DTextField field) {
            this.field = field;
        }

        public String toGui(String modelTxt) {
            if (modelTxt == null || "".equals(modelTxt)) {
                return VlibProperties.getString("scan-ready");
            } else if (field.field.isEditable()) {
                return VlibProperties.getString("scan-read") + " " + modelTxt;
            } else {
                return VlibProperties.getString("scan-finished");
            }
        }

        public String toModel(String guiTxt) {
            return guiTxt;
        }

        public boolean checkFormat(String software) {
            return true;
        }

        private DTextField field;
    }

    /*package*/ static class NewlineTransformer implements ModelTransformer {
        public NewlineTransformer(int col, int row) {
            this.col = col;
            this.row = row;
        }

        public String toModel(String source) {
            return convertFixedTextToSingleLine(source, col, row);
        }

        public String toGui(String source) {
            StringBuffer target = new StringBuffer();
            int length = source.length();
            int usedRows = 1;

            for (int start = 0; start < length; start += col) {
                String line = source.substring(start, Math.min(start + col, length));
                int last = -1;

                for (int i = line.length() - 1; last == -1 && i >= 0; --i) {
                    if (!Character.isWhitespace(line.charAt(i))) {
                        last = i;
                    }
                }

                if (last != -1) {
                    target.append(line.substring(0, last + 1));
                }
                if (usedRows < row) {
                    if (start + col < length) {
                        target.append('\n');
                    }
                    usedRows++;
                }
            }
            return target.toString();
        }

        public boolean checkFormat(String source) {
            return (source.length() <= row * col);
        }

        int col;
        int row;
    }

    private class RightMenu extends MouseAdapter implements PopupMenuListener {
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if (menu != null) {
                    // do nothing
                } else {
                    if (listener != null) {
                        menu = createPopupMenu();
                        menu.addPopupMenuListener(this);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        }

        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // necessary for garbage collection
            menu.removeAll();
            menu.removePopupMenuListener(this);
            menu = null;
        }

        public void popupMenuCanceled(PopupMenuEvent e) {
        }

        JPopupMenu menu;
    }

    public static String convertToSingleLine(String source, int col, int row) {
        StringBuffer target = new StringBuffer();
        int length = source.length();
        int start = 0;

        while (start < length) {
            int index = source.indexOf('\n', start);

            if (index - start < col && index != -1) {
                target.append(source.substring(start, index));
                for (int j = index - start; j < col; j++) {
                    target.append(' ');
                }
                start = index + 1;
                if (start == length) {
                    // last line ends with a "new line" -> add an empty line
                    for (int j = 0; j < col; j++) {
                        target.append(' ');
                    }
                }
            } else {
                if (start + col >= length) {
                    target.append(source.substring(start, length));
                    for (int j = length; j < start + col; j++) {
                        target.append(' ');
                    }
                    start = length;
                } else {
                    // find white space to break line
                    int i;

                    for (i = start + col - 1; i > start; i--) {
                        if (Character.isWhitespace(source.charAt(i))) {
                            break;
                        }
                    }
                    if (i == start) {
                        index = start + col;
                    } else {
                        index = i + 1;
                    }

                    target.append(source.substring(start, index));
                    for (int j = (index - start) % col; j != 0 && j < col; j++) {
                        target.append(' ');
                    }
                    start = index;
                }
            }
        }

        return target.toString();
    }

    public static String convertFixedTextToSingleLine(String source, int col, int row) {
        StringBuffer target = new StringBuffer();
        int length = source.length();
        int start = 0;

        while (start < length) {
            int index = source.indexOf('\n', start);

            if (index - start < col && index != -1) {
                target.append(source.substring(start, index));
                for (int j = index - start; j < col; j++) {
                    target.append(' ');
                }
                start = index + 1;
                if (start == length) {
                    // last line ends with a "new line" -> add an empty line
                    for (int j = 0; j < col; j++) {
                        target.append(' ');
                    }
                }
            } else {
                if (start + col >= length) {
                    target.append(source.substring(start, length));
                    for (int j = length; j < start + col; j++) {
                        target.append(' ');
                    }
                    start = length;
                } else {
                    // find white space to break line
                    int i;

                    for (i = start + col; i > start; i--) {
                        if (Character.isWhitespace(source.charAt(i))) {
                            break;
                        }
                    }
                    if (i == start) {
                        index = start + col;
                    } else {
                        index = i;
                    }

                    target.append(source.substring(start, index));
                    for (int j = (index - start); j < col; j++) {
                        target.append(' ');
                    }
                    start = index + 1;
                }
            }
        }
        return target.toString();
    }

    public void setSelectionAfterUpdateDisabled(boolean disable) {
        selectionAfterUpdateDisabled = disable;
    }

    private boolean selectionAfterUpdateDisabled = false;
}
