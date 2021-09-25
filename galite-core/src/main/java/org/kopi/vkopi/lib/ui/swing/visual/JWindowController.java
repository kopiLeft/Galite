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
 * $Id: JWindowController.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.Window;

import javax.swing.FocusManager;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.kopi.galite.visual.visual.ApplicationContext;
import org.kopi.galite.visual.visual.VException;
import org.kopi.galite.visual.visual.VRuntimeException;
import org.kopi.galite.visual.visual.VWindow;
import org.kopi.galite.visual.visual.WindowBuilder;
import org.kopi.galite.visual.visual.WindowController;

/**
 * {@code JWindowController} is a swing implementation of the {@link WindowController}.
 */
@SuppressWarnings("serial")
public class JWindowController extends WindowController {

  //----------------------------------------------------------------------
  // WINDOWCONTROLLER IMPLEMENTATION
  //----------------------------------------------------------------------

  
  public boolean doModal(VWindow model) {
    final ModalViewRunner     viewStarter = new ModalViewRunner(model);

    synchronized(model) {
      SwingThreadHandler.startAndWait(viewStarter);
      try {
	if (SwingUtilities.isEventDispatchThread()) {
	  // !! prevent that these code is executed
	  // Real event handling is much more sophisticated
	  DWindow       	view;
	  EventQueue    	eventQueue;

	  view = viewStarter.getView();
	  if (view == null) {
	    return false;
	  }
	  eventQueue = view.getToolkit().getSystemEventQueue();

	  while (view.isShowing()) {
	    AWTEvent    event = eventQueue.getNextEvent();
	    Object      source = event.getSource();

	    try {
	      if (event instanceof ActiveEvent) {
		((ActiveEvent)event).dispatch();
	      } else if (source instanceof Component) {
		((Component)source).dispatchEvent(event);
	      } else if (source instanceof MenuComponent) {
		((MenuComponent)source).dispatchEvent(event);
	      } else {
		System.err.println("unable to dispatch event: " + event);
	      }
	    } catch (RuntimeException e) {
	      // is ignored
	      ApplicationContext.Companion.reportTrouble("JWindowController",
		  		               "JWindowController.dispatch (I should not be here!)",
		  		               event.toString(),
		  		               e);
	    }
	  }
	} else {
	  model.wait();
	}
      } catch (InterruptedException e) {
	// wait interrupted
      }
    }
    return (viewStarter.getView() == null) ? false : viewStarter.getView().getReturnCode() == VWindow.CDE_VALIDATE;
  }


  public boolean doModal(org.kopi.galite.visual.common.Window model) {
  	return doModal(model.getModel());
  }

  
  public void doNotModal(final VWindow model) {
    SwingThreadHandler.start(new Runnable() {
      public void run() {
	try {
	  DWindow    		view;
	  WindowBuilder   	builder;

	  builder = getWindowBuilder(model);
	  view = (DWindow)builder.createWindow(model);
	  view.createFrame();
	  view.run();
	} catch (VException e) {
	  // report error to user
	  // this is called in the event-handling-thread
	  // so this exceptions have not to be forwarded
	  reportError(e);
	} catch (VRuntimeException e) {
	  // report error to user
	  // this is called in the event-handling-thread
	  // so this exceptions have not to be forwarded
	  reportError(e);
	}
      }
    });
  }

  public void doNotModal(final org.kopi.galite.visual.common.Window model) {
  	doNotModal(model.getModel());
  }

  //------------------------------------------------------------------------
  // UTILS
  //------------------------------------------------------------------------

  /**
   * Reports an error.
   * @param e The Exception to be reported
   */
  public void reportError(Exception e) {
    if (e.getMessage() != null) {
      DWindow.displayError(null, e.getMessage());
    }
  }

  //------------------------------------------------------------------------
  // MODAL VIEW STARTER
  //------------------------------------------------------------------------

  /*package*/ class ModalViewRunner implements Runnable {

    //----------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------

    /*package*/ ModalViewRunner(final VWindow model) {
      this.model = model;
    }

    public void run() {
      try {
	WindowBuilder   builder;
	Window      	focus = FocusManager.getCurrentManager().getFocusedWindow();

	builder = getWindowBuilder(model);
	view = (DWindow)builder.createWindow(model);

	if (focus instanceof JFrame) {
	  view.createModalDialog((Frame) focus);
	} else {
	  view.createFrame();
	}

	view.run();
      } catch (VException e) {
	throw new VRuntimeException(e.getMessage(), e);
      }
    }

    /**
     * Returns the window view.
     * @return The window view.
     */
    public DWindow getView() {
      return view;
    }

    //----------------------------------------------------
    // DATA MEMBERS
    //----------------------------------------------------

    private DWindow       			view;
    private VWindow       			model;
  }
}
