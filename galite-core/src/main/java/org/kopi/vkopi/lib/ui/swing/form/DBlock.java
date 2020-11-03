/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * $Id: DBlock.java 35293 2018-03-07 09:37:08Z iayadi $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.galite.util.base.InconsistencyException;
import org.kopi.galite.base.UComponent;
import org.kopi.galite.form.*;
import org.kopi.vkopi.lib.ui.swing.base.KopiTitledBorder;
import org.kopi.vkopi.lib.ui.swing.visual.SwingThreadHandler;
import org.kopi.galite.visual.VException;
import org.kopi.galite.visual.VExecFailedException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;

public class DBlock extends JPanel implements UBlock {

  /*
   * ----------------------------------------------------------------------
   * CONSTRUCTION
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public DBlock(DForm parent, VBlock model) {
    SwingThreadHandler.verifyRunsInEventThread("DBlock <init>");

    setBorder(model.getBorder(), model.getTitle());
    setBorder(border);
    setFocusCycleRoot(true);
    setFocusable(false);

    model.addBlockListener(this);

    this.maxRowPos = model.getMaxRowPos();
    this.maxColumnPos = model.getMaxColumnPos();
    this.displayedFields = model.getDisplayedFields();
    this.model = model;
    this.formView = parent;

    if (model.isMulti()) {
      sortedRecToDisplay   = new int[model.getBufferSize()];
      displayToSortedRec   = new int[model.getDisplaySize()];
    } else {
      sortedRecToDisplay   = new int[1];
      displayToSortedRec   = new int[1];
    }

    setLayout(layout = createLayoutManager());
    rebuildCachedInfos();
    createFields();
    if (model.isDroppable()) {
      setDropTarget(new DropTarget(null, DnDConstants.ACTION_COPY, new DBlockDropTargetHandler(model)));
    }
  }

  protected void createFields() {
    VField[]  fields = model.getFields();

    columnViews = new VFieldUI[fields.length];
    for (int i = 0; i < fields.length; i++) {
      columnViews[i] = createFieldDisplays(fields[i]);
    }
  }


  protected KopiLayoutManager createLayoutManager() {
    return new KopiSimpleBlockLayout(2 * maxColumnPos, // label + field => 2
                                     maxRowPos,
                                     (model.getAlignment() == null) ?
                                         null :
                                         new ViewBlockAlignment(formView, model.getAlignment()));
  }


  public Insets getInsets() {
    Insets      insets = super.getInsets();

    return new Insets(insets.top+5, insets.left+5, insets.bottom+5, insets.right+5);
  }

  /**
   * Gets the model
   */
  public VBlock getModel() {
    return model;
  }

  /**
   * get Column Pos, returns the pos of a column
   */
  public int getColumnPos(int x) {
    return layout.getColumnPos(x);
  }

  /**
   *
   */
  @SuppressWarnings("deprecation")
  private void setBorder(int style, String title) {
    if (style == VConstants.BRD_NONE) {
      border = KopiTitledBorder.BRD_EMPTY;
    } else {
      switch (style) {
      case VConstants.BRD_LINE:
	border = KopiTitledBorder.BRD_LINE;
	break;

      case VConstants.BRD_RAISED:
	border = KopiTitledBorder.BRD_RAISED;
	break;

      case VConstants.BRD_LOWERED:
	border = KopiTitledBorder.BRD_LOWERED;
	break;

      case VConstants.BRD_ETCHED:
	border = KopiTitledBorder.BRD_ETCHED;
	break;

      default:
	throw new InconsistencyException();
      }

      if (title != null) {
	border = new KopiTitledBorder(border, title);
      }
    }
  }

  private VFieldUI createFieldDisplays(VField model) {
    if (!model.isInternal() /*hidden field */) {
      VFieldUI  ui;

      ui = new DFieldUI(this, model);
      return ui;
    } else {
      return null;
    }
  }

  public void gotoNextRecord() throws VException {
    getModel().gotoNextRecord();
  }

  public void gotoPrevRecord() throws VException {
    getModel().gotoPrevRecord();
  }

  /**
   *
   */
  private void rebuildCachedInfos() {
    int		cnt = 0;
    int		i = 0;

    // sortedRecToDisplay
    for (; i < sortedToprec; i++) {
      sortedRecToDisplay[i] = -1;
    }

    for (; cnt < model.getDisplaySize() && i < model.getBufferSize(); i++) {
      // sortedRecToDisplay: view pos not real record number
      sortedRecToDisplay[i] = model.isSortedRecordDeleted(i) ? -1 : cnt++;
    }

    for (; i < model.getBufferSize(); i++) {
      sortedRecToDisplay[i] = -1;
    }

    // displayToSortedRec
    cnt = sortedToprec;
    for (i = 0; i < model.getDisplaySize(); i++) {
      while (cnt < model.getBufferSize() && model.isSortedRecordDeleted(cnt)) {
 	cnt++;
      }
      // the last one can be deleted too
      if (cnt < model.getBufferSize()) {
        displayToSortedRec[i] = cnt++;
      }
    }
  }

  
  public void add(UComponent comp, KopiAlignment constraints) {
    super.add((Component)comp, constraints);
  }

  /**
   * Refreshes the block on screen.
   *
   * Arranges displayed lines to make sure that the current record is visible.
   * Redisplays only if forced or if the current record is off-screen.
   * If there is no current record, the first valid record is used
   */
  protected void refresh(boolean force) {
    SwingThreadHandler.verifyRunsInEventThread("DBlock refresh");

    boolean		redisplay = false;
    int			recno; // row in view

    if (!model.isMulti()) {
      return;
    }

    if (model.getActiveRecord() != -1) {
      recno = model.getSortedPosition(model.getActiveRecord());
    } else {
      // !!! laurent 20020419 Why is it commented ?
/*      recno = -1;

      for (int i = 0; i < getBufferSize() && recno == -1; i++) {
	if (!isRecordDeleted(i)) {
	  recno = i;
	}
      }

      assert recno != -1;
*/

      rebuildCachedInfos();
      // !!redisplay!! lackner 07.08.2003
      // only necessary if valid-recordChanged
      // I mean the redisplay in DChartBlock V 1.4 (other are empty)

      for (int i = 0; i < columnViews.length; i++) {
        if (columnViews[i] != null) {
          columnViews[i].scrollTo(sortedToprec);
        }
      }

      return;
    }

    if (recno < sortedToprec) {
      // record to be displayed is above screen => redisplay
      sortedToprec = recno;

      // scroll some more, if there are some (non deleted) records
      int     i = recno -1;
      int     scrollMore =  model.getDisplaySize() / 4;

      while (scrollMore > 0 && i > 0) {
        // is there a non deleted record to see?
        if (! model.isSortedRecordDeleted(i)) {
          sortedToprec -= 1;
          scrollMore--;
        }
        i--;
      }

      redisplay = true;
    } else {
      int	displine = 0;

      for (int i = sortedToprec; i < recno; i += 1) {
	if (!model.isSortedRecordDeleted(i)) {
	  displine += 1;
	}
      }

      if (displine < model.getDisplaySize()) {
	// record should be visible => redisplay iff requested
	redisplay = force;// do nothing
      } else {
	// scroll upwards until record is visible => redisplay
	do {
	  if (!model.isSortedRecordDeleted(sortedToprec)) {
	    displine -= 1;
	  }
	  sortedToprec += 1;
	} while (displine >= model.getDisplaySize());

        // scroll some more, if there are some (non deleted) records
        int     i = recno +1;
        int     scrollMore =  model.getDisplaySize() / 4;

        while (scrollMore > 0 && i < model.getBufferSize()) {
            // is there a non deleted record to see?
          if (! model.isSortedRecordDeleted(i)) {
            sortedToprec += 1;
            scrollMore--;
          }
          i++;
        }

	redisplay = true;
      }
    }

    rebuildCachedInfos();
    // !!redisplay!! lackner 07.08.2003
    // only necessary if valid-recordChanged
    // I mean the redisplay in DChartBlock V 1.4 (other are empty)

    if (redisplay) {
      for (int i = 0; i < columnViews.length; i++) {
        if (columnViews[i] != null) {
          columnViews[i].scrollTo(sortedToprec);
        }
      }
    }
  }

  /**
   * sort the records to order it by the value of the
   * given column.
   *
   * @param     column column to order or if -1 back to original order
   */
  public void orderChanged() {
    SwingThreadHandler.startAndWait(synchronRefresh);
  }

  /**
   * Returns the display line of the current record (-1 if it is off-screen).
   */
  public int getDisplayLine() {
    return getDisplayLine(model.getActiveRecord());
  }

  public int getDisplayLine(int recno) {
    if (recno < 0) {
      return -1;
    }
    int         pos = model.getSortedPosition(recno);

    if (pos < 0) {
      return -1;
    }

    return sortedRecToDisplay[pos];
  }

  public int getRecordFromDisplayLine(int line) {
    return model.getDataPosition(displayToSortedRec[line]);
  }

  /**
   * scroll action
   * @exception	VException	an exception may be raised record.leave()
   */
  public void setScrollPos(int val) throws VException {
    // Can not be called in event dispatch thread
    // Scrollbar timer is not stop if you click on one of the two buttons

    assert val < model.getBufferSize(); //getRecordSize

    if (sortedToprec != val) {
      int               recno = 0;  //temp sortedToprec

      while (val > 0) {
	if (!model.isSortedRecordDeleted(recno)) {
	  val--;
	}
	recno++;
      }

      if (model.getActiveField() != null) {
	int             lastVisibleRec = recno;
	int             nbDisplay = model.getDisplaySize() - 1;
        int             activeRecord = model.getActiveRecord();
        boolean         inside = false; // is active record still in the shown rows

	while (nbDisplay > 0) {
	  if (!model.isSortedRecordDeleted(lastVisibleRec)) {
	    nbDisplay--;
	  }
          if (activeRecord == model.getDataPosition(lastVisibleRec)) {
            // active record is still in the shown rows
            inside = true;
          }
          lastVisibleRec += 1;
	}

	sortedToprec = recno;
	if (inside) {
          if (model.getActiveField() != null) {
            model.getActiveField().updateText();
          }
	  blockChanged();
	} else {
	  int		nextRec;

	  if (model.getSortedPosition(model.getActiveRecord()) < recno) {
	    nextRec = model.getDataPosition(recno);
	  } else {
	    nextRec = model.getDataPosition(lastVisibleRec);
	  }

	  if (model.noMove() || !model.isRecordAccessible(nextRec)) {
	    throw new VExecFailedException();
	  }
          model.changeActiveRecord(nextRec);
	}
      } else {
	if (model.noMove() || model.isRecordDeleted(model.getDataPosition(recno))) {
            // || !model.isRecordAccessible(model.getDataPosition(recno))) {
	  throw new VExecFailedException();
	}
	sortedToprec = recno;
 	blockChanged();
	if (model != model.getForm().getActiveBlock()) {  // NICHT AKTUELLER BLOCK -> ZURUECK AUF -1
	  model.setActiveRecord(-1);
	}
     }
    }
  }

  public void clear() {
    sortedToprec = 0;
    refresh(true);
  }

  
  public UForm getFormView() {
    return formView;
  }

  // ----------------------------------------------------------------------
  // LISTENER BlockListener
  // ----------------------------------------------------------------------

  // don't create always a new runnable class
  private class SynchronRefresh implements Runnable {
    public void run() {
      refresh(true);
    }
  }

  private final SynchronRefresh synchronRefresh = new SynchronRefresh();


  public void blockClosed() {
  }

  public void blockCleared() {
    SwingThreadHandler.start(new Runnable() {
        public void run() {
          clear();
        }
      });
  }

  public void blockAccessChanged(VBlock block, boolean newAccess) {
  }
  public void blockViewModeEntered(VBlock block, VField field) {}
  public void blockViewModeLeaved(VBlock block, VField field) {}

  public void blockChanged() {
    SwingThreadHandler.startAndWait(synchronRefresh);
  }

  public void validRecordNumberChanged() {
  }
  
  public void recordInfoChanged(int rec, int info) {  
  }

  public UBlock getCurrentDisplay() {
    return this;
  }

  public void filterHidden() {}

  public void filterShown() {}

  public boolean inDetailMode() {
    return false;
  }

  //---------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------

  private final DForm				formView;
  protected final VBlock			model;
  private VFieldUI[]           			columnViews;
  protected KopiLayoutManager  		        layout;

  protected final int				maxRowPos;
  protected final int				maxColumnPos;
  protected final int				displayedFields;
  protected       Border			border;
  // cached infos
  private  int					sortedToprec;		// first record displayed
  private  int[]				sortedRecToDisplay;
  private  int[]				displayToSortedRec;

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long 	serialVersionUID = -8665529498396399382L;
}
