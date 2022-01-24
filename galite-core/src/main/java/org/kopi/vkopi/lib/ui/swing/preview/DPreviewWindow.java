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
 * $Id: DPreviewWindow.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import org.kopi.galite.visual.preview.PreviewListener;
import org.kopi.galite.visual.preview.VPreviewWindow;
import org.kopi.galite.visual.util.base.InconsistencyException;
import org.kopi.galite.visual.visual.Action;
import org.kopi.galite.visual.visual.ApplicationConfiguration;
import org.kopi.galite.visual.visual.DPositionPanelListener;
import org.kopi.galite.visual.visual.UserConfiguration;
import org.kopi.vkopi.lib.ui.swing.visual.DPositionPanel;
import org.kopi.vkopi.lib.ui.swing.visual.DWindow;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;

/**
 * A window with an html pane
 */
public class DPreviewWindow extends DWindow implements DPositionPanelListener, PreviewListener {

  /**
   *
   */
  public DPreviewWindow(VPreviewWindow model) {
    super(model);
    this.model = model;
    registerKeyboardAction(new AbstractAction() {
                             /**
                              * Comment for <code>serialVersionUID</code>
                              */
                             private static final long serialVersionUID = -3945777720296639744L;

                             public void actionPerformed(ActionEvent e) {
                               closeWindow();
                             }},
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    label = new JLabel(); // model.label;
    bodypane = new JScrollPane(label,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    getContentPanel().setLayout(new BorderLayout());
    getContentPanel().add(bodypane, BorderLayout.CENTER);
    label.setIconTextGap(0);
    label.setRequestFocusEnabled(true);
    setStatePanel(blockInfo = new DPositionPanel(this));
    model.addPreviewListener(this);
    label.addKeyListener(new KeyAdapter () {
      public void keyPressed(KeyEvent k) {
        if (k.getKeyCode() == KeyEvent.VK_PAGE_UP
                && DPreviewWindow.this.model.getCurrentPage() > 1)
        {
          gotoPrevPosition();
        }
        if (k.getKeyCode() == KeyEvent.VK_PAGE_DOWN
                && DPreviewWindow.this.model.getCurrentPage() < DPreviewWindow.this.model.getNumberOfPages())
        {
          gotoNextPosition();
        }
        if (k.getKeyCode() == KeyEvent.VK_HOME
                && DPreviewWindow.this.model.getCurrentPage() > 1)
        {
          gotoFirstPosition();
        }
        if (k.getKeyCode() == KeyEvent.VK_END
                && DPreviewWindow.this.model.getCurrentPage() < DPreviewWindow.this.model.getNumberOfPages())
        {
          gotoLastPosition();
        }
      }
    });
    // not used
     /*
    try {
      useRotation = ApplicationConfiguration.getConfiguration().getBooleanFor("print.preview.rotate");
    } catch (PropertyException e) {
    }*/
  }

  /**
   *
   */
  public void init() {
  }

  public void run() {
    Frame       frame;
    Rectangle   bounds;

    setTitle(model.getTitle());
    label.setIcon(createIcon(1));

    frame = getFrame();
    frame.pack(); // layout frame; get preferred size
    // calulate bounds for frame to fit screen
    bounds = Utils.calculateBounds(frame, null, null);
    frame.setBounds(bounds);


    setPagePosition(model.getCurrentPage(), model.getNumberOfPages());

    WindowStateListener listener = new WindowAdapter() {
      public void windowStateChanged(WindowEvent evt) {
        int   oldState = evt.getOldState();
        int   newState = evt.getNewState();

        if ((oldState & Frame.MAXIMIZED_BOTH) == 0
                && (newState & Frame.MAXIMIZED_BOTH) != 0) {
          getFrame().invalidate();
          getFrame().validate();

          UserConfiguration   userConfig = ApplicationConfiguration.Companion.getConfiguration().getUserConfiguration();

          zoomFit(userConfig == null ? PreviewListener.FIT_BOTH : userConfig.getPreviewMode());
        }
      }
    };

    frame.addWindowStateListener(listener);

    frame.setVisible(true);
    label.requestFocusInWindow();

    UserConfiguration   userConfig = ApplicationConfiguration.Companion.getConfiguration().getUserConfiguration();

    if (userConfig != null && userConfig.getPreviewScreen() == UserConfiguration.PRS_FULLSCREEN) {
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    } else {
      if (userConfig != null && userConfig.getPreviewMode() != UserConfiguration.PRM_OPT) {
        zoomFit(userConfig.getPreviewMode());
      }
    }
  }


  /**
   * setPagePosition
   * inform user about nb records fetched and current one
   */
  public void setPagePosition(int current, int count) {
    blockInfo.setPosition(current, count);
  }

  // ----------------------------------------------------------------------
  // INTERFACE PreviewListener
  // ----------------------------------------------------------------------

  private void setIcon(int current) {
    ImageIcon   img = (ImageIcon)label.getIcon();

    if (img != null) {
      img.getImage().flush();
    }

    ImageIcon   imgNew = createIcon(current);

    label.setIcon(imgNew);

    if (img != null) {
      bodypane.invalidate();
      bodypane.validate();

      centerScrollbar(bodypane.getHorizontalScrollBar());
      centerScrollbar(bodypane.getVerticalScrollBar());
    }
  }

  private void centerScrollbar(JScrollBar bar) {
    BoundedRangeModel   rangemodel = bar.getModel();

    rangemodel.setValue(Math.max((rangemodel.getMaximum()-rangemodel.getExtent())/2, 0));
  }

  public  void pageChanged(final int current) {
    setIcon(current);
    setPagePosition(current, model.getNumberOfPages());
  }

  public void zoomChanged() {
    setIcon(model.getCurrentPage());
  }

  public void zoomFit(int type) {
    Dimension         dim = bodypane.getSize(); // size of view
    float             ratio;

    switch (type) {
      case FIT_BOTH:
        // round the ratio with 0.99f, so that there are definitly no
        // scrollbars
        ratio = Math.min((float) dim.height / model.getHeight(),
                (float) dim.width / model.getWidth()) * 0.99f;
        break;
      case FIT_HEIGHT:
        // 1.03 : do not show the white border
        ratio = dim.height * 1.03f / model.getHeight();
        break;
      case FIT_WIDTH:
        // 1.05 : do not show the white border
        ratio = dim.width * 1.05f / model.getWidth();
        break;
      default:
        throw new InconsistencyException("Unkown type of zoom");
    }

    model.zoom(ratio);
  }

  // ----------------------------------------------------------------------
  // INTERFACE DPositionPanelListener
  // ----------------------------------------------------------------------

  /**
   * Requests to go to the next position.
   */
  public void gotoNextPosition() {
    getModel().performAsyncAction(new Action("preview right") {
      public void execute() {
        getModel().executeVoidTrigger(VPreviewWindow.CMD_RIGHT);
      }
    });
  }

  /**
   * Requests to go to the previous position.
   */
  public void gotoPrevPosition() {
    getModel().performAsyncAction(new Action("preview left") {
      public void execute() {
        getModel().executeVoidTrigger(VPreviewWindow.CMD_LEFT);
      }
    });
  }

  /**
   * Requests to go to the last position.
   */
  public void gotoLastPosition() {
    getModel().performAsyncAction(new Action("preview last") {
      public void execute() {
        getModel().executeVoidTrigger(VPreviewWindow.CMD_LAST);
      }
    });
  }

  /**
   * Requests to go to the first position.
   */
  public void gotoFirstPosition() {
    getModel().performAsyncAction(new Action("preview first") {
      public void execute() {
        getModel().executeVoidTrigger(VPreviewWindow.CMD_FIRST);
      }
    });
  }

  /**
   * Requests to go to the specified position.
   */
  public void gotoPosition(final int posno) {
    getModel().performAsyncAction(new Action("preview position") {
      public void execute() {
        ((VPreviewWindow)getModel()).gotoPosition(posno);
      }
    });
  }

  private ImageIcon createIcon(int idx) {
    return new ImageIcon(model.getPreviewFileName(idx));
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static BufferedImage rotate(String file) throws IOException {
    BufferedImage	  image = ImageIO.read(new File(file));
    GraphicsEnvironment   ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    int	                  w = image.getWidth();
    int	                  h = image.getHeight();
    int	                  neww = h;
    int	                  newh = w;
    int	                  transparency = image.getColorModel().getTransparency();
    BufferedImage         result = gc.createCompatibleImage(neww, newh, transparency);
    Graphics2D            g = result.createGraphics();

    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.translate((neww-w)/2, (newh-h)/2);
    g.rotate(Math.PI/2, w/2, h/2);
    g.drawRenderedImage(image, null);
    return result;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VPreviewWindow        model;
  private JLabel                label;
  private JScrollPane           bodypane;
  private DPositionPanel        blockInfo;
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -3057089222899270837L;
}
