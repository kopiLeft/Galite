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
 * $Id: DHelpViewer.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

import org.kopi.galite.visual.visual.VHelpViewer;

/**
 * A window with an html pane
 */
/*package*/ class DHelpViewer extends DWindow implements HyperlinkListener {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -5681828259231318466L;

  /**
   *
   */
  public DHelpViewer(VHelpViewer model) {
    super(model);
    registerKeyboardAction(new AbstractAction() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = -4312276387422409187L;

      public void actionPerformed(ActionEvent e) {
	closeWindow();
      }},
                           null,
                           KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    getContentPanel().setLayout(new BorderLayout());
    try {
      html = new JEditorPane(model.getUrl());
    } catch (java.io.IOException e) {
      throw new org.kopi.galite.visual.util.base.InconsistencyException(e);
    }
    html.setEditable(false);
    html.addHyperlinkListener(this);

    JScrollPane         scroller = new JScrollPane(html,
	                                           ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
	                                           ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroller.setPreferredSize(new Dimension(600, 500));
    getContentPanel().add(scroller, BorderLayout.CENTER);
  }

  /**
   *
   */
  public void run() {
    setVisible(true);
    html.requestFocus();
    getModel().setActorEnabled(VHelpViewer.CMD_QUIT, true);
  }

  /**
   *
   */
  public void setURL(final URL url) {
    SwingThreadHandler.startAndWait(new Runnable() {
      public void run() {
	Document    doc = html.getDocument();

	try {
	  html.setPage(url);
	} catch (java.io.IOException ioe) {
	  html.setDocument(doc);
	  getToolkit().beep();
	}
      }
    });
  }

  // ----------------------------------------------------------------------
  // HYPER LINK LISTENERS
  // ----------------------------------------------------------------------

  /**
   * Notification of a change relative to a
   * hyperlink.
   */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      linkActivated(e.getURL());
    }
  }

  /**
   * Follows the reference in an
   * link.  The given url is the requested reference.
   * By default this calls <a href="#setPage">setPage</a>,
   * and if an exception is thrown the original previous
   * document is restored and a beep sounded.  If an
   * attempt was made to follow a link, but it represented
   * a malformed url, this method will be called with a
   * null argument.
   *
   * @param u the URL to follow
   */
  protected void linkActivated(URL u) {
    ((VHelpViewer)getModel()).setUrl(u);
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JEditorPane html;
}
