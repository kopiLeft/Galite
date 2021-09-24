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
 * $Id: SpellcheckerDialog.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.spellchecker;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.kopi.galite.visual.VlibProperties;

public class SpellcheckerDialog extends JDialog {

  public SpellcheckerDialog(Frame owner, String title,  SpellChecker spellchecker) {
    super( owner );
    this.spellChecker = spellchecker;
    setModal(true);
    setTitle(title);
    buildDisplay();
    initSuggestionElements();
  }


  public String getSelectedWord() {
    return changeTo.getText();
  }

  protected JRootPane createRootPane() {
    JRootPane           rootPane = new JRootPane();
    KeyStroke           stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    ActionListener      actionListener = new CloseDialogActionListener();

    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    return rootPane;
  }

  private void addRow(Box mainBox, JComponent labelComponent, Component component) {
    Box                 hBox = Box.createHorizontalBox();

    mainBox.add( hBox );

    Dimension           labelComponentDim =
            new Dimension( 100, labelComponent.getPreferredSize().height );
    labelComponent.setPreferredSize( labelComponentDim );
    labelComponent.setMinimumSize( labelComponentDim );
    labelComponent.setMaximumSize( labelComponentDim );
    hBox.add( labelComponent );

    hBox.add( Box.createHorizontalGlue() );
    hBox.add( component );
    hBox.add( Box.createHorizontalGlue() );
  }

  private void buildDisplay() {
    JButton cancelButton    = new JButton(new CancelAction());
    JButton changeButton    = new JButton(new ChangeAction());
    JButton changeAllButton = new JButton(new ChangeAllAction());
    JButton ignoreButton    = new JButton(new IgnoreAction());
    JButton ignoreAllButton = new JButton(new IgnoreAllAction());

    changeTo = new JTextField();
    changeTo.setMinimumSize( new Dimension(200, changeTo.getPreferredSize().height));
    changeTo.setMaximumSize( new Dimension(Integer.MAX_VALUE, changeTo.getPreferredSize().height));

    originalWordTextField = new JTextField(); //originalWord);


    originalWordTextField.setMinimumSize(new Dimension(200, originalWordTextField.getPreferredSize().height));
    originalWordTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, originalWordTextField.getPreferredSize().height));

    suggestionsList = new JList(); //suggestions.toArray());
    suggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    suggestionsList.addListSelectionListener( new MyListSelectionListener());
    suggestionsList.setMinimumSize(new Dimension(200, 300 ));
    suggestionsList.setMaximumSize(new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE));
    suggestionsList.setPreferredSize(new Dimension( 200, 300 ));

    JScrollPane         suggestionsPane = new JScrollPane( suggestionsList );
    Box                 mainBox = Box.createVerticalBox();
    Box                 hBox;
    JLabel              jLabel;
    JPanel              buttonPanel = new JPanel();

    getContentPane().add(mainBox);
    jLabel = new JLabel(VlibProperties.getString("aspell-dialog-not-in"));
    addRow(mainBox, jLabel, originalWordTextField);

    jLabel = new JLabel(VlibProperties.getString("aspell-dialog-change-to"));
    addRow(mainBox, jLabel, changeTo );

    jLabel = new JLabel(VlibProperties.getString("aspell-dialog-suggestions"));
    hBox = Box.createHorizontalBox();

    hBox.add(suggestionsPane);
    hBox.add(Box.createHorizontalGlue());

    getRootPane().setDefaultButton(ignoreButton);
    buttonPanel.setPreferredSize( new Dimension(200, 100));
    buttonPanel.add(ignoreButton);
    buttonPanel.add(ignoreAllButton);
    buttonPanel.add(changeButton);
    buttonPanel.add(changeAllButton);
    buttonPanel.add(cancelButton);
    hBox.add(buttonPanel);
    hBox.add(Box.createHorizontalGlue());

    addRow(mainBox, jLabel, hBox);

    pack();
  }

  private void checkNext() {
    if (spellChecker.checkNext()) {
      initSuggestionElements();
    } else {
      dispose();
    }
  }

  private void initSuggestionElements() {
    DefaultComboBoxModel              model;
    Suggestions               	result;

    result = spellChecker.getSuggestions();
    suggestions = result.getSuggestions();
    model= new DefaultComboBoxModel(result.getSuggestions().toArray());
    suggestionsList.setModel(model);
    originalWordTextField.setText(result.getOriginalWord());
    if (suggestions.size() > 0) {
      suggestionsList.setSelectedIndex(0);
      suggestionsList.grabFocus();
    }
  }

  private class CancelAction extends AbstractAction {

    private CancelAction() {
      super(VlibProperties.getString("aspell-dialog-cancel"));
    }

    public void actionPerformed(ActionEvent event) {
      dispose();
    }
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 9054956716054138769L;
  }

  private class ChangeAction extends AbstractAction {

    private ChangeAction() {
      super(VlibProperties.getString("aspell-dialog-change"));
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
      putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_C));
    }

    public void actionPerformed(ActionEvent event) {
      spellChecker.change(getSelectedWord());
      checkNext();
    }
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -9046898360749709345L;
  }

  private class ChangeAllAction extends AbstractAction {

    private ChangeAllAction() {
      super(VlibProperties.getString("aspell-dialog-change-all"));
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
      putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_L));
    }

    public void actionPerformed(ActionEvent event) {
      spellChecker.changeAll(getSelectedWord());
      checkNext();
    }
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 8492059401975099177L;
  }

  private class IgnoreAction extends AbstractAction {

    private IgnoreAction() {
      super(VlibProperties.getString("aspell-dialog-ignore"));
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
      putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_I));
    }

    public void actionPerformed(ActionEvent event) {
      spellChecker.ignore(getSelectedWord());
      checkNext();
    }
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -5969056223868591429L;
  }

  private class IgnoreAllAction extends AbstractAction {

    private IgnoreAllAction() {
      super(VlibProperties.getString("aspell-dialog-ignore-all"));
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
      putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_G));
    }

    public void actionPerformed( ActionEvent event ) {
      spellChecker.ignoreAll(getSelectedWord());
      checkNext();
    }
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3082583855533470939L;
  }

  private class CloseDialogActionListener implements ActionListener {
    public void actionPerformed(ActionEvent actionEvent) {
      dispose();
    }
  }


  private class MyListSelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      int       selectedIndex = suggestionsList.getSelectedIndex();

      if( selectedIndex  >= 0 ) {
        changeTo.setText(suggestions.get(selectedIndex));
      }
    }
  }

  private SpellChecker          spellChecker;
  private List<String>          suggestions;
  private JTextField            changeTo;
  private JTextField            originalWordTextField;
  private JList                 suggestionsList;
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -220369137180783262L;
}
