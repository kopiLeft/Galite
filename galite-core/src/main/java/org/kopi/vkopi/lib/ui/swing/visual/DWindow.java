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
 * $Id: DWindow.java 35325 2018-08-29 11:27:17Z iayadi $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultFocusManager;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.kopi.galite.visual.ui.swing.visual.DMenuTree;
import org.kopi.galite.visual.util.LineBreaker;
import org.kopi.galite.visual.ApplicationContext;
import org.kopi.galite.visual.MessageCode;
import org.kopi.galite.visual.MessageListener;
import org.kopi.galite.visual.UIFactory;
import org.kopi.galite.visual.UWindow;
import org.kopi.galite.visual.VActor;
import org.kopi.galite.visual.VException;
import org.kopi.galite.visual.VRuntimeException;
import org.kopi.galite.visual.VWindow;
import org.kopi.galite.visual.VlibProperties;
import org.kopi.galite.visual.WaitInfoListener;
import org.kopi.vkopi.lib.ui.swing.base.JButtonPanel;
import org.kopi.vkopi.lib.ui.swing.base.KnownBugs;
import org.kopi.vkopi.lib.ui.swing.base.Utils;

/**
 * This class displays a window with a menu, a tool bar, a content panel
 * and a footbar
 */
public abstract class DWindow extends JPanel implements UWindow {

  /**
   * Constructor
   */
  protected DWindow(VWindow model) {
    this.model = model;
    setName(model.getTitle());
    model.setDisplay(this);

    for (int i=0; i<10; i++) {
      registerKeyboardAction(BOOKMARKS[i],
                             null,
                             KeyStroke.getKeyStroke(KeyEvent.VK_0+i, InputEvent.CTRL_DOWN_MASK),
                             JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    installKeyMapping();

    this.firstTime = true;
    this.undoableListener = new DUndoableEditListener();
    this.undo = null; //new UndoManager();

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setFocusable(false);
    setFocusCycleRoot(true);
    menuBar = new DMenuBar();
    createEditMenu();

    buttonPanel = new JButtonPanel();
    add(buttonPanel);
    contentPanel = new JPanel();
    add(contentPanel);
    footPanel = new DFootPanel(this);
    add(footPanel);
    addActorsToGUI(getModel().getActors());

    model.addVActionListener(this);
    model.addModelCloseListener(this);

    waitInfoHandler = new WaitInfoHandler();
    model.addWaitInfoListener(waitInfoHandler);
    model.addWaitDialogListener(this);
    model.addProgressDialogListener(this);

    messageHandler = new MessageHandler();
    model.addMessageListener(messageHandler);
  }

  private void installKeyMapping() {
    // 'CNTL n' -> 'down'
    registerKeyboardAction(new AbstractAction() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 5542024298880296191L;

      public void actionPerformed(ActionEvent e) {
	((Component) e.getSource()).dispatchEvent(new KeyEvent((Component) e.getSource(),
                                                               KeyEvent.KEY_PRESSED,
                                                               e.getWhen(),
                                                               0,
                                                               KeyEvent.VK_DOWN,
                                                               KeyEvent.CHAR_UNDEFINED));
      }},
      null,
      KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
    // 'CNTL p' -> 'up'
    registerKeyboardAction(new AbstractAction() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 7162561739019316550L;

      public void actionPerformed(ActionEvent e) {
	((Component) e.getSource()).dispatchEvent(new KeyEvent((Component) e.getSource(),
                                                               KeyEvent.KEY_PRESSED,
                                                               e.getWhen(),
                                                               0,
                                                               KeyEvent.VK_UP,
                                                               KeyEvent.CHAR_UNDEFINED));
      }},
      null,
      KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
    // 'CNTL s' -> 'left'
    registerKeyboardAction(new AbstractAction() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 156538846733910281L;

      public void actionPerformed(ActionEvent e) {
	((Component) e.getSource()).dispatchEvent(new KeyEvent((Component) e.getSource(),
                                                               KeyEvent.KEY_PRESSED,
                                                               e.getWhen(),
                                                               0,
                                                               KeyEvent.VK_LEFT,
                                                               KeyEvent.CHAR_UNDEFINED));
      }},
      null,
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
    // 'CNTL e' -> 'right'
    registerKeyboardAction(new AbstractAction() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 6571246407660269667L;

      public void actionPerformed(ActionEvent e) {
	((Component) e.getSource()).dispatchEvent(new KeyEvent((Component) e.getSource(),
                                                               KeyEvent.KEY_PRESSED,
                                                               e.getWhen(),
                                                               0,
                                                               KeyEvent.VK_RIGHT,
                                                               KeyEvent.CHAR_UNDEFINED));
      }},
      null,
      KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Get Model
   */
  public VWindow getModel() {
    return model;
  }

  /**
   * Set model
   */
  public void setModel(VWindow model) {
    this.model = model;
    addActorsToGUI(getModel().getActors());
    setWindowFocusEnabled(true);
  }

  /**
   * Adds the specified component to the end of the content panel.
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   *
   */
  public synchronized void setWindowFocusEnabled(boolean enabled) {
    if (enabled) {
      setCursor(Cursor.getDefaultCursor());
    } else {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
  }

  /**
   * Show/Hide this window
   */
  public void setVisible(boolean b) {
    setVisibleImpl(b);
  }

  /**
   * set the title
   */
  public void setTitle(String title) {
    if (self != null) {
      self.setTitle(title);
    }
  }

  /**
   * Close the view and the model, definitely
   * @see #closeWindow()
   */
  protected void close(int code) {
    VWindow     model = this.model; //destroyed in release()

    try {
      if (!SwingUtilities.isEventDispatchThread()) {
        System.err.println("ERROR: Call of DWindow.close(int x) ouside of Event-Dispatching Thread");
        Thread.dumpStack();
      }
      if (model != null) {
        release();
      }
      dispose();
      if (model != null) {
        // !! lackner 30.07.2003 why must the model be destroy
        model.destroyModel();
      }
    } finally {
      if (model != null) {
        synchronized (model) {
          // set the return code
          returnCode = code;
          // Inform all threads who wait for this panel
          model.notifyAll();
        }
      }
    }
  }

  public int getReturnCode() {
    return returnCode;
  }

  /**
   * @deprecated Use closeWindow() or close(int code) instead.
   */
  @Deprecated
  public void close() {
    closeWindow();
  }

  /**
   * Called to close the view (from the user), it does not
   * definitly close the view(it may ask the user before)
   * Allowed to call outside the event disp. thread
   */
  public void closeWindow() {
    if (! getModel().allowQuit()) {
      return;
    }

    getModel().willClose(VWindow.CDE_QUIT);
  }

  public void modelClosed(final int type) {
    SwingThreadHandler.start(new Runnable() {
        public void run() {
          close(type);
        }
      });
  }

  /**
   * Builds the display structure
   * @deprecated  do not use anymore
   */
  @Deprecated
  public void build() {
  }

  /**
   *
   */
  public JFrame getFrame() {
    return self;
  }

  // ----------------------------------------------------------------------
  // MESSAGES
  // ----------------------------------------------------------------------

  /**
   * Displays a notice.
   */
  public void displayNotice(String message) {
    SwingThreadHandler.verifyRunsInEventThread("DWindow displayNotice");
    verifyNotInTransaction("DWindow.displayNotice(" + message + ")");
    displayNotice(frame, message);
  }

  public static void displayNotice(Component frame, String message) {
    Object[]    options = { VlibProperties.getString("CLOSE")};

    JOptionPane.showOptionDialog(frame,
				 message,
				 VlibProperties.getString("Notice"),
				 JOptionPane.DEFAULT_OPTION,
				 JOptionPane.INFORMATION_MESSAGE,
				 ICN_NOTICE,
				 options,
				 options[0]);
  }

  /**
   * Displays an error message.
   */
  public void displayError(String message) {
    SwingThreadHandler.verifyRunsInEventThread("DWindow displayError");
    verifyNotInTransaction("DWindow.displayError(" + message + ")");

    //!!!FIXME
    // wrap long text messages:
    // some SQL exceptions are too long and make the error window larger than
    // the screen width, this is a work around until a 'detail' option is added to
    // error windows.
    if (message.length() > 100) {
      message = LineBreaker.Companion.addBreakForWidth(message, 100);
    }

    displayError(frame, message);
  }

  public static void displayError(Component parent, String message) {
    Object[]    options = { VlibProperties.getString("CLOSE")};

    JOptionPane.showOptionDialog(parent,
				 message,
				 VlibProperties.getString("Error"),
				 JOptionPane.DEFAULT_OPTION,
				 JOptionPane.ERROR_MESSAGE,
				 ICN_ERROR,
				 options,
				 options[0]);
  }

  /**
   * Asks a position number
   */
  public static int askPostition(Component parent, int current, int total) {
    Object[]    options = { VlibProperties.getString("OK"), VlibProperties.getString("NO")};
    int         userInput;
    String      s;
    JOptionPane pane;
    JDialog     dialog;
    Object      obj;

    pane = new JOptionPane(VlibProperties.getString("position-number") + " :",
                           JOptionPane.QUESTION_MESSAGE,
                           JOptionPane.YES_NO_OPTION,
                           null,
                           options,
                           options[0]);

    pane.setWantsInput(true);
    pane.setComponentOrientation(parent.getComponentOrientation());
    dialog = pane.createDialog(parent, current +  " " + VlibProperties.getString("from") + " " + total);

    dialog.setVisible(true);
    dialog.dispose();

    obj = pane.getInputValue();
    if(obj == JOptionPane.UNINITIALIZED_VALUE) {
      s = null;
    } else {
      s = (String)obj;
    }

    try {
      userInput = Integer.parseInt(s);
    } catch (NumberFormatException  nfe) {
      return current;
    }

    return (userInput < 1)? 1 : (userInput > total)? total : userInput ;
  }


  /**
   * Displays a warning message.
   */
  public void displayWarning(String message) {
    SwingThreadHandler.verifyRunsInEventThread("DWindow displayWarning");
    verifyNotInTransaction("DWindow.displayWarning(" + message + ")");

    Object[]    options = { VlibProperties.getString("CLOSE")};

    JOptionPane.showOptionDialog(frame,
				 message,
				 VlibProperties.getString("Warning"),
				 JOptionPane.DEFAULT_OPTION,
				 JOptionPane.WARNING_MESSAGE,
				 ICN_WARNING,
				 options,
				 options[0]);
  }

  /**
   * Displays an ask dialog box
   */
  public boolean askUser(String message, boolean yesIsDefault) {
    SwingThreadHandler.verifyRunsInEventThread("DWindow askUser");
    verifyNotInTransaction("DWindow.askUser(" + message + ")");

    return askUserImpl(message, yesIsDefault);
  }

  /**
   * Displays an ask dialog box
   */
  public boolean askUserImpl(String message, boolean yesIsDefault) {
    SwingThreadHandler.verifyRunsInEventThread("DWindow askUser");

    Object[]    options = { VlibProperties.getString("OK"), VlibProperties.getString("NO")};

    return 0 == showOptionDialog(self,
				 message,
				 VlibProperties.getString("Question"),
				 JOptionPane.YES_NO_OPTION,
				 JOptionPane.QUESTION_MESSAGE,
				 ICN_ASK,
				 options,
				 options[yesIsDefault ? 0 : 1]);
  }

  // ---------------------------------------------------------------------
  // UNDO / REDO / CUT / COPY / PASTE
  // ---------------------------------------------------------------------

  /**
   *
   */
  public UndoableEditListener getUndoableEditListener() {
    return undoableListener;
  }

  /**
   *
   */
  public UndoManager getUndoManager() {
    return undo;
  }

  /**
   *
   */
  public void setUndoManager(UndoManager undo) {
    this.undo = undo;
    if (undoAction != null) {
      undoAction.update();
      redoAction.update();
    }
  }

  /**
   * Allow building of a customized edit menu.
   */
  protected void createEditMenu() {
  }

  // The following two methods allow us to find an
  // action provided by the editor kit by its name.
  private static Hashtable<Object, Action> createActionTable(JTextComponent textComponent) {
    Hashtable<Object, Action> actions = new Hashtable<Object, Action>();
    Action[] actionsArray = textComponent.getActions();
    for (int i = 0; i < actionsArray.length; i++) {
      Action a = actionsArray[i];
      actions.put(a.getValue(Action.NAME), a);
    }

    return actions;
  }

  protected Action getActionByName(String name) {
    return (Action)(actions.get(name));
  }

  public class UndoAction extends AbstractAction {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4341094988092005822L;

	public UndoAction() {
      super("Undo");
      this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      try {
	undo.undo();
      } catch (CannotUndoException ex) {
	System.out.println(MessageCode.INSTANCE.getMessage("VIS-00029") + ex);
      }
      update();
      redoAction.update();
    }

    protected void update() {
      if (undo != null && undo.canUndo()) {
	this.setEnabled(true);
	putValue(Action.NAME, VlibProperties.getString("item-undo"));
      } else {
	this.setEnabled(false);
	putValue(Action.NAME, VlibProperties.getString("item-undo"));
      }
    }
  }

  public class RedoAction extends AbstractAction {

	public RedoAction() {
      super("Redo");
      this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      try {
	undo.redo();
      } catch (CannotRedoException ex) {
	System.out.println(MessageCode.INSTANCE.getMessage("VIS-00030") + ex);
	ex.printStackTrace();
      }
      update();
      undoAction.update();
    }

    protected void update() {
      if (undo != null && undo.canRedo()) {
	this.setEnabled(true);
	putValue(Action.NAME, VlibProperties.getString("item-redo"));
	//undo.getRedoPresentationName());
      } else {
	this.setEnabled(false);
	putValue(Action.NAME, VlibProperties.getString("item-redo"));
      }
    }

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -8042055084991466893L;

  }

  // This one listens for edits that can be undone.
  protected class DUndoableEditListener implements UndoableEditListener {
    public void undoableEditHappened(UndoableEditEvent e) {
      //Remember the edit and update the menus
      if (undoAction != null && redoAction != null) {
        // lackner 06.10.2004
        // undoAction and redoAction is null after the window
        // is released (closed). If some events come late (because
        // they are enqueued) they would cause an error here
        undoAction.update();
        redoAction.update();
      }
    }
  }

  public Action getRedoAction() {
    return redoAction;
  }
  public Action getUndoAction() {
    return undoAction;
  }

  /**
   * @deprecated   Use method #performAsyncAction(KopiAction action) without
   *               boolean parameter because this parameter  was ignored.
   */
  @Deprecated
  public void performAction(final org.kopi.galite.visual.Action action, boolean block) {
    performAsyncAction(action);
  }
  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   */
  public void performAsyncAction(final org.kopi.galite.visual.Action action) {
    performActionImpl(action, true);
  }

  /**
   * Performs the appropriate action synchronously.
   *
   * @param	action		the action to perform.
   */
  public void performBasicAction(final org.kopi.galite.visual.Action action) {
    performActionImpl(action, false);
  }

  /**
   * Performs the appropriate action asynchrnously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   */
  private void performActionImpl(final org.kopi.galite.visual.Action action, boolean asynch) {
    SwingThreadHandler.verifyRunsInEventThread("DForm:performActionImpl");

    if (inAction == true) {
      // inform user that comand will not work
      // (and for debugging too)
      Toolkit.getDefaultToolkit().beep();
      System.out.println("Action " + action + " not executed. Another command \"" + currentAction + "\" is processed.");

      return;
    }

    // -- DEBUGGING BEGIN
    if (KnownBugs.paintIconReload != null) {
      String    text = KnownBugs.paintIconReload + "\n" + "failed:" + KnownBugs.paintIconFailure;

      KnownBugs.paintIconReload = null;
      KnownBugs.paintIconFailure = false;
      ApplicationContext.Companion.reportTrouble("DWindow",
                                       "DWindow.performActionImpl(" + action + ", "+asynch+")",
                                       text,
                                       new RuntimeException("Painting Error - Load retried (Never Thrown)"));

    } else if (KnownBugs.paintIconFailure) {
      KnownBugs.paintIconFailure = false;
      ApplicationContext.Companion.reportTrouble("DWindow",
                                       "DWindow.performActionImpl(" + action + ", "+asynch+")",
                                       "no more info",
                                       new RuntimeException("Painting Error (Never Thrown)"));
    }
    // -- DEBUGGING END

    inAction = true;
    // everything must be done after setting inAction true
    currentEventQueue = focusManager.enqueueKeyEvents();

    getModel().setCommandsEnabled(false);
    currentAction = action;
    // DebugInfo#
    runtimeDebugInfo = new RuntimeException(currentAction.toString());

    if (!asynch || !getModel().allowAsynchronousOperation()) {
      // synchronus call
      actionRunner.run();
    } else {
      Thread     currentThread;

      // asyn. work of task
      // must set inAction to false in event-disp.thread
      // after it is fully completed
      currentThread = new Thread(actionRunner);
      currentThread.start();
    }
  }

  @Override
  public void openURL(String url) throws Exception {
    throw new Exception("Unsupported operation");
  }

  public void reportError(VRuntimeException e) {
    if (e.getMessage() != null) {
      messageHandler.error(e.getMessage());
    }
  }

  /**
   * There is only one instance of ActionRunner.
   * It calls user actions.
   */
  class ActionRunner implements Runnable {
    public void run() {
      try {
        if (currentAction == null) {
          return;
        }

        currentAction.run();
        if (getModel() != null) {
          // actions which close the window also
          // set the referenced model to null
          getModel().executedAction(currentAction);
        }
      } catch (VRuntimeException v) {
        v.printStackTrace();
        reportError(v);
        //        getModel().error(v.getMessage());
      } catch (Throwable exc) {
        exc.printStackTrace();
        getModel().fatalError(DWindow.this.getModel(), "VWindow.performActionImpl(final KopiAction action)", exc);
      } finally {
        // sets inAction in event-disp.-thread
        setInAction();
      }
    }

    private void setInAction() {
      try {
        Runnable actionSetter = new Runnable() {
            public void run() {
              final LinkedList<AWTEvent>        eventList = currentEventQueue;

              currentEventQueue = null;
              currentAction = null;
              inAction = false;

              setWindowFocusEnabled(true);

              if (getModel() != null) {
                // commands like "Beenden" destroy the model
                // so it must be tested, that there is still a model
                getModel().setCommandsEnabled(true);
              }
              // when events dequeued, this command must be finished
              // because the keys in the queue may start the next command
              // (otherwise queueing is useless
              focusManager.dequeueKeyEvents(eventList);
            }
          };

        if (SwingUtilities.isEventDispatchThread()) {
          actionSetter.run();
        } else {
          SwingUtilities.invokeAndWait(actionSetter);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  public boolean getInAction() {
    SwingThreadHandler.verifyRunsInEventThread("getInAction");
    return inAction;
  }

  // ---------------------------------------------------------------------
  // INFORMATIONS
  // ---------------------------------------------------------------------

  /**
   * Displays a text in the lower left corner of the window.
   */
  public final void setInformationText(final String text) {
    SwingThreadHandler.startAndWait(new Runnable() {
      public void run() {
	if (footPanel != null) {
	  // if a footpanel exists
	  footPanel.setInformationText(text);
	}
      }
    });
  }

  /**
   * Displays a text in the lower right corner of the window.
   */
  public final void setStatisticsText(final String text) {
    SwingThreadHandler.startAndWait(new Runnable() {
      public void run() {
	if (footPanel != null) {
	  // if a footpanel exists
	  footPanel.setStatisticsText(text);
	}
      }
    });
  }

  public final void setStatePanel(JPanel panel) {
    footPanel.setStatePanel(panel);
  }

  public final void setProgressDialog(String message, int totalJobs) {
    if (progressWindow == null) {
      progressWindow = new ProgressWindow(getFrame());
    }
    progressWindow.setProgressDialog(message, totalJobs);
  }

  public final void unsetProgressDialog() {
    if (progressWindow != null) {
      progressWindow.unsetProgressDialog();
      progressWindow = null;
    }
  }

  public final void setTotalJobs(int totalJobs) {
    if (progressWindow != null) {
      progressWindow.setTotalJobs(totalJobs);
    }
  }
  public final void setCurrentJob(int currentJob) {
    if (progressWindow != null) {
      progressWindow.setCurrentJob(currentJob);
    }
  }

  public final void updateWaitDialogMessage(String message) {
    if (waitWindow != null) {
      waitWindow.updateMessage(message);
    }
  }

  /**
   * setWaitInfo
   */
  public final void setWaitDialog(String message, int maxTime) {
    if (waitWindow == null) {
      waitWindow = new WaitWindow(getFrame());
    }
    waitWindow.setWaitDialog(message, maxTime);
  }

  /**
   * change mode to free state
   */
  public final void unsetWaitDialog() {
    if (waitWindow != null) {
      waitWindow.unsetWaitDialog();
      waitWindow = null;
    }
  }

  /**
   * setWaitInfo
   */
  public final void setWaitInfo(String message) {
    waitInfoHandler.setWaitInfo(message);
  }

  /**
   * change mode to free state
   */
  public final void unsetWaitInfo() {
    waitInfoHandler.unsetWaitInfo();
  }

  public void fileProduced(File file, String name) {
    // not used in swing version.
  }

  /**
   * Default does nothing
   */
//   protected void interruptCurrentAction() {
//   }

  // ---------------------------------------------------------------------
  // DIPLAY BUILDING
  // ---------------------------------------------------------------------

  /**
   *
   */
  // CODE REMOVED
//   public void destroy() {
//     setVisible(false);
//   }

  /**
   * Release: we definitively don't want anymore this window (JFrame)
   */
  public synchronized void release() {
    SwingThreadHandler.verifyRunsInEventThread("DWindow release: DISPOSE MUST BE CALLED in the event dispatching Thread.");
    if (parentFrame != null && parentFrame instanceof JFrame) {
      parentFrame.removeWindowListener(windowhandler);
      parentFrame.removeWindowFocusListener(windowhandler);
      parentFrame.setFocusableWindowState(true);
      ((JFrame) parentFrame).getGlassPane().setVisible(false);
      parentFrame.toFront();
      windowhandler = null;
      setVisible(false);
    }


    model.removeVActionListener(this);
    model.removeWaitInfoListener(waitInfoHandler);
    model.removeMessageListener(messageHandler);

    getActionMap().clear() ;
    getInputMap().clear() ;
    frame = null;
    parentFrame = null;
    undoableListener = null;
    undo = null;
    undoAction = null;
    redoAction = null;
    footPanel = null;
    menuBar = null;
    buttonPanel = null;
    contentPanel = null;
    model = null;
    self.setJMenuBar(null);
    org.kopi.galite.visual.base.Utils.Companion.freeMemory();
    self = null;
  }

  @Override
  public void dispose() {
    Frame       frame = getFrame();

    disposeAfterLostFocus(frame);
  }

  /**
   * !!WORK AROUND 2003.0905 for Focus System of JDK 1.4.2 (also 1.4.1,1.4.0)
   *
   * Focus lost problem: If the focus is request by an other
   * component, and this window is closed before the event processed
   * the focus gained event, then the focus handling is disturbed (nothing is focused)
   */
  private void disposeAfterLostFocus(final Window window) {
    SwingUtilities.invokeLater(new Runnable() {
      // in the event-queue there may be some event
      // which has do be done before this window can close.
      // Therefore enqueue it at the end.

      public void run() {
        if (window != null) {
          window.setVisible(false);
          window.dispose();
        }
      }
    });
  }


  /**
   *
   */
  protected DMenuBar getDMenuBar() {
    return menuBar;
  }

  /**
   * starts the window
   * @exception	VException	an exception may be raised by triggers
   */
  public abstract void run() throws VException;

  /**
   * add a command in the menu bar
   */
  private void addActorsToGUI(List<VActor> actorDefs) {
    if (actorDefs != null) {
      for (int i = 0; i < actorDefs.size(); i++) {
	DActor		actorView;

	actorView = (DActor)UIFactory.uiFactory.createView(actorDefs.get(i));
        addButton(buttonPanel, actorView);
        menuBar.addItem(actorView);
      }
    }

    buttonPanel.add(Box.createGlue());
  }

  private void addButton(JPanel panel, DActor actorView) {
    if (actorView.getModel().getIconName() != null) {
      panel.add(actorView.getButton());
    }
  }

  public JFrame createFrame() {
    dialog = false;
    if (firstTime) {
      final JFrame f = new JFrame();

      f.setTitle(getModel().getTitle());

      if (DObject.windowIcon != null || getModel().getSmallIcon() != null) {
 	f.setIconImage(getModel().getSmallIcon() == null ? DObject.windowIcon : ((ImageIcon)getModel().getSmallIcon()).getImage());
      }

      f.getContentPane().setLayout(new BorderLayout());
      f.getContentPane().add(this, BorderLayout.CENTER);

      f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      f.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  closeWindow();
	}
      });

      f.setLocation(50, 50);

      menuBar.finalizeMenu(); // !! TO BE REMOVED (SEE DMenuBar)
      f.setJMenuBar(menuBar);
      frame = self = f;
    }
    return self;
  }

  /**
   * create a modal dialog containing it
   */
  JFrame createModalDialog(Frame frame) {
    final JFrame f = createFrame();

    parentFrame = frame;
    dialog = true;
    if (parentFrame instanceof JFrame) {
      // create opaque glass pane
      final JPanel glass = new org.kopi.vkopi.lib.ui.swing.base.JDisablePanel();
      glass.setOpaque(false);

      // Attach mouse listeners
      MouseInputAdapter adapter =
        new MouseInputAdapter(){
          public void mouseClicked(MouseEvent e) {
            f.toFront();
            f.requestFocusInWindow();
          }
        };
      glass.addMouseListener(adapter);
      glass.addMouseMotionListener(adapter);

      // Change glass pane to our panel
      ((JFrame) parentFrame).setGlassPane(glass);

      // Show glass pane, then modal dialog
      windowhandler= new WindowHandler(f, true);

      parentFrame.addWindowListener(windowhandler);
      parentFrame.addWindowFocusListener(windowhandler);
      // lackner 08.04.2003 Work around because WindowFocusListener
      // do not work
      parentFrame.setFocusableWindowState(false);
      // end work around
      f.addWindowListener(new WindowHandler(parentFrame, false));
      SwingThreadHandler.verifyRunsInEventThread("DWindow createModalDialog: set glasspane visible.");
      glass.setVisible(true);
    }
    return f;
  }



  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------

  private void setVisibleImpl(boolean b) {
    if (b) {
      if (firstTime) {
	self.pack();
	firstTime = false;
      }
      if (dialog && parentFrame != null) {
	Dimension	screen = Toolkit.getDefaultToolkit().getScreenSize();
	Point		parentPos = new Point(0, 0);
	SwingUtilities.convertPointToScreen(parentPos, parentFrame);

	int		posx = parentPos.x + parentFrame.getSize().width / 2 - self.getSize().width / 2;
	int		posy = parentPos.y + parentFrame.getSize().height / 2 - self.getSize().height / 2;

	if (posx < 0) {
	  posx = 0;
	}
	if (posx + self.getSize().width > screen.width) {
	  posx = screen.width - self.getSize().width;
	}

	if (posy < 0) {
	  posy = 0;
	}
	if (posy + self.getSize().height > screen.height) {
	  posy = screen.height - self.getSize().height;
	}
	posx = Math.max(posx, 0);
	posy = Math.max(posy, 0);

	self.setLocation(posx, posy);
      }
    }
    self.setVisible(b);
    super.setVisible(b);
  }

  /**
   * Reports if a message is shown while in a transaction.
   */
  protected void verifyNotInTransaction(String message) {
    if (VWindow.Companion.inTransaction() && debugMessageInTransaction()) {
      try {
	ApplicationContext.Companion.reportTrouble("DWindow",
                                         message + " IN TRANSACTION",
                                         this.toString(),
                                         new RuntimeException("displayNotice in Transaction"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns true if it should be checked whether a message is shown
   * while in a transaction.
   */
  private boolean debugMessageInTransaction() {
    boolean     debugMessageInTransaction;

    try {
      debugMessageInTransaction = ApplicationContext.Companion.getDefaults().debugMessageInTransaction();
    } catch (Exception e) {
      debugMessageInTransaction = false;
    }
    return debugMessageInTransaction;
  }

  // ---------------------------------------------------------------------
  // ACTION LISTENER PANEL CLASS
  // ---------------------------------------------------------------------

  /**
   * Show a dialog with a key assignated to each button (the first letter)
   */
  @SuppressWarnings("unused")
  public static int showOptionDialog(JFrame frame,
				     Object message,
				     String title,
				     int optionType,
				     int messageType,
				     Icon icon,
				     Object[] options,
				     Object initialValue)
  {
    JOptionPane             pane;

    SwingThreadHandler.verifyRunsInEventThread("DWindow: showOptionDialog");

    pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);
    pane.setInitialValue(initialValue);

    final JDialog         dialog = pane.createDialog(frame, title);

    pane.selectInitialValue();

    closeOptionPaneWith = -1;
    pane.registerKeyboardAction(new AbstractAction() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 8837346594485054607L;

      public void actionPerformed(ActionEvent e) {
	closeOptionPaneWith = 0;
	dialog.dispose();
      }},
      null,
      KeyStroke.getKeyStroke(((String)options[0]).charAt(0), 0),
      JComponent.WHEN_IN_FOCUSED_WINDOW);

    pane.registerKeyboardAction(new AbstractAction() {
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = -2466568795023067881L;

      public void actionPerformed(ActionEvent e) {
	closeOptionPaneWith = 1;
	dialog.dispose();
      }},
      null,
      KeyStroke.getKeyStroke(((String)options[1]).charAt(0), 0),
      JComponent.WHEN_IN_FOCUSED_WINDOW);

    dialog.setVisible(true);

    Object        selectedValue = pane.getValue();

    if (closeOptionPaneWith >= 0) {
      return closeOptionPaneWith;
    }

    if (selectedValue == null) {
      return JOptionPane.CLOSED_OPTION;
    }
    if (options == null) {
      if (selectedValue instanceof Integer) {
	return ((Integer)selectedValue).intValue();
      }
      return JOptionPane.CLOSED_OPTION;
    }
    for (int counter = 0, maxCounter = options.length;
    counter < maxCounter; counter++) {
      if (options[counter].equals(selectedValue)) {
	return counter;
      }
    }
    return JOptionPane.CLOSED_OPTION;
  }

  /*package*/ class MessageHandler implements MessageListener {
    /**
     * Displays a notice.
     */
    public void notice(final String message) {

      Runnable          runner = new Runnable() {

	public void run () {
	  DWindow.this.displayNotice(message);
	}
      };

      // ensure that it is executed in event dispatch Thread
      SwingThreadHandler.startAndWait(runner);
    }

    /**
     * Displays an error message.
     */
    public void error(final String message) {
      Runnable          runner = new Runnable() {

	public void run () {
	  DWindow.this.displayError(message);
	}
      };

      // ensure that it is executed in event dispatch Thread
      SwingThreadHandler.startAndWait(runner);
    }

    /**
     * Displays a warning message.
     */
    public void warn(final String message) {
      Runnable          runner = new Runnable() {

	public void run () {
	  DWindow.this.displayError(message);
	}
      };

      // ensure that it is executed in event dispatch Thread
      SwingThreadHandler.startAndWait(runner);
    }

    /**
     * Displays an ask dialog box
     */
    public int ask(final String message, final boolean yesIsDefault) {
      Runnable          runner = new Runnable() {
	public void run () {
	  boolean     retVal;

	  retVal = DWindow.this.askUser(message, yesIsDefault);
	  if (retVal) {
	    value = MessageListener.AWR_YES;
	  } else  {
	    value = MessageListener.AWR_NO;
	  }
	}
      };

      // ensure that it is executed in event dispatch Thread
      SwingThreadHandler.startAndWait(runner);
      return value;
    }

    int         value; // only for use in ask(...)
  }

  /*package*/ class WaitInfoHandler implements WaitInfoListener {
    public WaitInfoHandler() {
    }

    public void setWaitInfo(final String message) {
      SwingThreadHandler.startAndWait(new Runnable() {

	public void run() {
	  if (footPanel != null) {
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    footPanel.setWaitInfo(message);
	  }
	}
      });
    }

    public void unsetWaitInfo() {
      SwingThreadHandler.startAndWait(new Runnable() {

	public void run() {
	  if (footPanel != null) {
	    setCursor(Cursor.getDefaultCursor());
	    footPanel.unsetWaitInfo();
	  }
	}
      });
    }
  }

  /*package*/ static class WindowHandler extends WindowAdapter implements WindowFocusListener {
    WindowHandler(Frame client, boolean active) {
      this.client = client;
      this.active = active;
    }

    // the methods of WindowFocusListener are not called.
    public void windowGainedFocus(FocusEvent e) {
      if (active) {
        client.toFront();
        client.requestFocusInWindow();
      }
    }
    public void windowLostFocus(FocusEvent e) {
    }

    public void windowActivated(WindowEvent e) {
      if (active) {
        client.toFront();
        client.requestFocusInWindow();
      }
    }
    public void windowIconified(WindowEvent e) {
      client.setState(Frame.ICONIFIED);
    }
    public void windowDeiconified(WindowEvent e) {
      client.setState(Frame.NORMAL);
      // window is activated too!
    }

    private final Frame         client;
    private final boolean       active;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  // for debugging
  public Exception           			runtimeDebugInfo;

  // set/access inAction ONLY in the event-disp.-Thread
  private boolean				inAction;
  // set/access these fields ONLY in the event-disp.-Thread
  private org.kopi.galite.visual.Action currentAction;
  private LinkedList<AWTEvent>            	currentEventQueue;

  private int                   		returnCode = -1;

  private final ActionRunner    		actionRunner = new ActionRunner();

  private static int				closeOptionPaneWith;

  private VWindow				model;

  private ProgressWindow			progressWindow;
  private WaitWindow                    	waitWindow;
  private DMenuBar				menuBar;
  private DFootPanel				footPanel;
  private JPanel				buttonPanel;
  private JPanel				contentPanel;

  private boolean				firstTime;
  private boolean				dialog;

  private Frame					frame;
  private Frame                         	parentFrame;
  private JFrame				self;

  private WindowHandler                 	windowhandler;
  private WaitInfoHandler               	waitInfoHandler;
  private MessageHandler                	messageHandler;

  private DUndoableEditListener 		undoableListener;
  private UndoManager				undo;
  protected UndoAction				undoAction;
  protected RedoAction				redoAction;

  private static Hashtable<Object, Action>	actions = createActionTable(new JTextField());

  public static final ImageIcon		ICN_WAIT = Utils.getImage("wait.gif");
  public static final ImageIcon		ICN_ERROR = Utils.getImage("error.gif");
  public static final ImageIcon		ICN_WARNING = Utils.getImage("warning.gif");
  public static final ImageIcon		ICN_ASK = Utils.getImage("ask.gif");
  public static final ImageIcon		ICN_NOTICE = Utils.getImage("notice.gif");

  public static final KopiFocusManager	focusManager;

  private static final long 			serialVersionUID = -354641750611433552L;

  // ---------------------------------------------------------------------
  // Bookmarks
  // ---------------------------------------------------------------------

  static class Bookmarks extends AbstractAction {

    public Bookmarks(int i) {
      item = i;
    }

    public void actionPerformed(ActionEvent e) {
      // Bookmarks (Shortcuts)
      if (ApplicationContext.Companion.getMenu() != null) {
        Action[]    bookmarks = ((DMenuTree)ApplicationContext.Companion.getMenu().getDisplay()).getBookmarkActions();

        if ( item < bookmarks.length) {
          bookmarks[item].actionPerformed(e);
        }
      }
    }

    int         			item;

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long 	serialVersionUID = -836013840309056928L;
  }

  /*package*/ static Bookmarks[]      BOOKMARKS;

  static {
    BOOKMARKS = new Bookmarks[10];

    for (int i=0; i < 10; i++) {
      BOOKMARKS[i] = new Bookmarks(i);
    }
  }

  /*package*/ static class KopiFocusManager extends DefaultFocusManager {
    /**
     * Dispatch all enqueued events to the current focus owner
     */
    protected void dequeueKeyEvents(final LinkedList<AWTEvent> enqueuedEvents) {
      // process all enqueued events
      // but only if there is still the same queue
      if (enqueuedEvents == enqueuedKeyEvents) {
        if (enqueuedKeyEvents != null) {
          enqueuedKeyEvents = null;
          dispatchEnqueued = false;

          if (enqueuedEvents != null) {
            for (Iterator<AWTEvent> iter = enqueuedEvents.iterator(); iter.hasNext(); ) {
              super.dispatchEvent(iter.next());
            }
          }
        }
      }
    }

    /**
     * Key-events are enqueued after the call of this method till
     * dequeueKeyEvents is called or the focus changes to another
     * window (dialog, listdialog, optionpanel, other form, ...).
     */
    protected LinkedList<AWTEvent> enqueueKeyEvents() {
      // enqueue all key events
      if (enqueuedKeyEvents != null) {
        return enqueuedKeyEvents = new LinkedList<AWTEvent>(enqueuedKeyEvents);
      } else {
        return enqueuedKeyEvents = new LinkedList<AWTEvent>();
      }
    }

    public boolean dispatchEvent(AWTEvent e) {
      if (enqueuedKeyEvents != null) {
        // if some kind of events are enqued:
        switch (e.getID()) {
        case KeyEvent.KEY_TYPED:
        case KeyEvent.KEY_PRESSED:
        case KeyEvent.KEY_RELEASED:
          enqueuedKeyEvents.addLast(e);
        return true;
        case FocusEvent.FOCUS_GAINED:
          try {
            super.dispatchEvent(e);
          } finally {
            if (dispatchEnqueued) {
              // if an element has gained the focus, which is in an other window
              // than the one which enqued the keys, STOP ENQUEUEING
              if (enqueuedKeyEvents != null) {
                final LinkedList<AWTEvent>  enqueuedEvents = enqueuedKeyEvents;

                enqueuedKeyEvents = null;
                dispatchEnqueued = false;

                for (Iterator<AWTEvent> iter = enqueuedEvents.iterator(); iter.hasNext(); ) {
                  super.dispatchEvent(iter.next());
                }
              }
            }
          }
          return true;
        case WindowEvent.WINDOW_GAINED_FOCUS:
          // dispatch all events
          // after a component gets the focus
          dispatchEnqueued = true;
          return super.dispatchEvent(e);
        }
      }

      // in all other cases:
      return super.dispatchEvent(e);
    }

    //-----------------------------------------------------------------
    // DATA MEMBERS
    //-----------------------------------------------------------------

    private LinkedList<AWTEvent>          	enqueuedKeyEvents;
    private boolean             		dispatchEnqueued = false;
  }

  static {
     focusManager = new KopiFocusManager();
     FocusManager.setCurrentManager(focusManager);
  }
}
