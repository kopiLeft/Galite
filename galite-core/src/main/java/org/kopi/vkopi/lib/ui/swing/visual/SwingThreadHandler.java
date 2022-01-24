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
 * $Id: SwingThreadHandler.java 34961 2016-11-04 17:20:49Z hacheni $
 */
package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InvocationEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.kopi.galite.visual.visual.ApplicationContext;
import org.kopi.galite.visual.visual.VException;
import org.kopi.galite.visual.visual.VRuntimeException;

/**
 * Helps to run code in the Event dispatch Thread. Subclass it
 * and define the run Method.
 */
public class SwingThreadHandler {

  //-----------------------------------------------
  // CONSTRCUTOR
  //-----------------------------------------------

  private SwingThreadHandler() {}

  /**
   * starts it in the event dispatch Thread
   */
  private void invoke(Runnable runnable) {
    if (SwingUtilities.isEventDispatchThread()) {
      try {
        runnable.run();
      } catch (Throwable failure) {
        // also a VException or VRuntimeException is not allowed here
        sendDebugMail("invoke in awt-thread ", runnable.toString(), failure);
      }
    } else {
      addDebugableEventToQueue(runnable);
    }
  }

  private void invokeAndWait(Runnable runnable) {
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      try {
        SwingUtilities.invokeAndWait(runnable);
      } catch (InterruptedException ie) {
      } catch (java.lang.reflect.InvocationTargetException ite) {
        Throwable       throwable = ite.getTargetException();

        if (throwable instanceof VRuntimeException) {
          throw (VRuntimeException) throwable;
        } if (throwable instanceof VException) {
          throw new VRuntimeException(throwable.getMessage(), throwable);
        } else if (throwable != null){
          throw new RuntimeException(throwable.getMessage(), throwable);
        } else {
          throw new RuntimeException(ite);
        }
      }
    }
  }

  public static void start(Runnable runnable) {
    runner.invoke(runnable);
  }

  public static void startAndWait(Runnable runnable) {
    runner.invokeAndWait(runnable);
  }

  public static void start(final DWindow model) {
    Runnable    runnable;

    runnable = new Runnable() {
        public void run() {
          try {
            model.run();
          } catch (Exception e) {
            throw new VRuntimeException(e.getMessage(), e);
          }
        }
      };
    runner.invoke(runnable);
  }

  public static void startAndWait(final DWindow model) {
    Runnable    runnable;

    runnable = new Runnable() {
        public void run() {
          try {
            model.run();
          } catch (Exception e) {
            throw new VRuntimeException(e.getMessage(), e);
          }
        }
      };
    runner.invokeAndWait(runnable);
  }

  public static void verifyRunsInEventThread(String message) {
    if (! SwingUtilities.isEventDispatchThread()) {
      System.out.println("Must be called in event disp, Thread. " + message);
      Thread.dumpStack();
      if (!ApplicationContext.Companion.getDefaults().isDebugModeEnabled()) {
        try {
          ApplicationContext.Companion.reportTrouble("SwingThreadHandler " + Thread.currentThread(),
                                           "verifyRunsInEventThread",
                                           "message",
                                           new RuntimeException(message));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void startEnqueued(Runnable runner) {
    if (SwingUtilities.isEventDispatchThread()) {
      try {
        runner.run();
      } catch (Throwable failure) {
        // also a VException or VRuntimeException is not allowed here
        sendDebugMail("invoke in awt-thread ", runner.toString(), failure);
      }
    } else {
      synchronized (queue) {
        queue.add(runner);
      }
    }
  }

  private static class QueueHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      synchronized (queue) {
        Runnable        runner;
        int             size;
        int             i;

        size = queue.size();
        if (size > 0) {
          for (i = 0; i < size; i++) {
            runner = (Runnable) queue.get(i);
            try {
              runner.run();
            }catch(Throwable failure) {
              // AssertionError's, InconsistencyException's, ...
              // VRuntimeException is not allowed too
              sendDebugMail("enqued jobs", runner.toString(), failure);
            }
          }
          queue.clear();
        }
      }
    }
  }

  private static final void sendDebugMail(final String where,
                                          final String data,
                                          final Throwable failure)
  {
    if (!ApplicationContext.Companion.getDefaults().isDebugModeEnabled()) {
      // send the mail NOT in the awt-event-thread
      Runnable  localRunner = new Runnable() {
          public void run() {
            ApplicationContext.Companion.reportTrouble("Event Handling Queue",
                                             where,
                                             data,
                                             failure);
          }
        };
      new Thread(localRunner).start();
    } else {
      failure.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  // Invocation Event Handling
  // ----------------------------------------------------------------------

  private void addDebugableEventToQueue(Runnable runnable) {
    EventQueue  eq = Toolkit.getDefaultToolkit().getSystemEventQueue();

    eq.postEvent(new DebugableEvent(Toolkit.getDefaultToolkit(), runnable));
  }

  /*package*/ class DebugableEvent extends InvocationEvent {

	DebugableEvent(Object source, Runnable runnable) {
      super(source, runnable);
    }

    public void dispatch() {
      try {
        super.dispatch();
      } catch (Throwable failure) {
        // also a VException or VRuntimeException is not allowed here
        sendDebugMail("invoke in awt-thread ", runnable.toString(), failure);
      }
    }
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
     private static final long serialVersionUID = -8836047819796761837L;

  }

  // ----------------------------------------------------------------------
  // fields
  // ----------------------------------------------------------------------

  private static final int                      INTERVAL = 80;
  private static final QueueHandler             queueHandler = new QueueHandler();
  private static final Timer                    swingTimer = new Timer(INTERVAL, queueHandler);
  private static final ArrayList<Runnable>      queue = new ArrayList<Runnable>(2000);
  static {
    swingTimer.setInitialDelay(0);
    swingTimer.setCoalesce(true);
    swingTimer.start();
  }

  private static final SwingThreadHandler       runner = new SwingThreadHandler();
}
