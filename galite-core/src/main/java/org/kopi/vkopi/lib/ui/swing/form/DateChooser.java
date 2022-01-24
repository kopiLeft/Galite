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
 * $Id: DateChooser.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.kopi.galite.visual.type.Date;
import org.kopi.galite.visual.type.Month;
import org.kopi.galite.visual.type.Week;
import org.kopi.galite.visual.visual.VlibProperties;
import org.kopi.vkopi.lib.ui.swing.base.Utils;

/**
 * This class represents a date chooser. The chooser allows an arbitrary date
 * to be selected by presenting a calendar with day, month and year selectors.
 *
 * ORIGINAL CODE: Kiwi (PING Software Group)
 */
public class DateChooser extends JPanel implements ActionListener {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 8823525349311235417L;

  /**
   * Construct a new <code>DateChooser</code>. The date for the chooser will
   * be initialized to the current date.
   */
  public DateChooser(Date date) {
    selectedDate = date == null ? Date.Companion.now() : date;

    setLayout(new BorderLayout(5, 5));
    setOpaque(false);
    setFocusCycleRoot(true);

    Insets	emptyInsets = new Insets(0, 0, 0, 0);

    JPanel top = new JPanel();
    top.setLayout(new BorderLayout(0, 0));

    JPanel p1 = new JPanel();
    p1.setLayout(new FlowLayout(FlowLayout.LEFT));
    top.add(p1, BorderLayout.WEST);

    prevMonthButton = new JButton(Utils.getImage("arrowleft.gif"));
    prevMonthButton.setRequestFocusEnabled(false);
    prevMonthButton.setMargin(emptyInsets);
    prevMonthButton.setFocusPainted(false);
    prevMonthButton.setOpaque(false);
    prevMonthButton.addActionListener(this);
    p1.add(prevMonthButton);

    nextMonthButton = new JButton(Utils.getImage("arrowright.gif"));
    nextMonthButton.setRequestFocusEnabled(false);
    nextMonthButton.setMargin(emptyInsets);
    nextMonthButton.setFocusPainted(false);
    nextMonthButton.setOpaque(false);
    nextMonthButton.addActionListener(this);
    p1.add(nextMonthButton);

    monthLabel = new JLabel();
    monthLabel.setForeground(Color.black);
    monthLabel.setOpaque(false);
    p1.add(monthLabel);

    JPanel p2 = new JPanel();
    p2.setLayout(new FlowLayout(FlowLayout.LEFT));
    top.add(p2, BorderLayout.EAST);

    yearLabel = new JLabel();
    yearLabel.setForeground(Color.black);
    yearLabel.setOpaque(false);
    p2.add(yearLabel);

    prevYearButton = new JButton(Utils.getImage("arrowleft.gif"));
    prevYearButton.setRequestFocusEnabled(false);
    prevYearButton.setMargin(emptyInsets);
    prevYearButton.setFocusPainted(false);
    prevYearButton.setOpaque(false);
    prevYearButton.addActionListener(this);
    p2.add(prevYearButton);

    nextYearButton = new JButton(Utils.getImage("arrowright.gif"));
    nextYearButton.setRequestFocusEnabled(false);
    nextYearButton.setMargin(emptyInsets);
    nextYearButton.setFocusPainted(false);
    nextYearButton.setOpaque(false);
    nextYearButton.addActionListener(this);
    p2.add(nextYearButton);

    add(top, BorderLayout.NORTH);


    cal = new CalendarPane(this);
    cal.setOpaque(false);
    add(cal, BorderLayout.CENTER);

    // add the button today
    JPanel end = new JPanel();
    end.setLayout(new BorderLayout(1, 2));

    JPanel button = new JPanel();
    button.setLayout(new FlowLayout(FlowLayout.CENTER));
    end.add(button, BorderLayout.NORTH);

    todayButton = new JButton(VlibProperties.getString("today"));
    todayButton.setRequestFocusEnabled(false);
    todayButton.setMargin(emptyInsets);
    todayButton.setFocusPainted(false);
    todayButton.setOpaque(false);
    todayButton.addActionListener(this);
    button.add(todayButton, BorderLayout.NORTH);

    add(end, BorderLayout.SOUTH);

    updateCalendar();
    refresh();

    Font f = getFont();
    setFont(new Font(f.getName(), Font.BOLD, f.getSize()));


    rootPaneFocusListener = new FocusAdapter() {
      public void focusGained(FocusEvent ev) {
        Component opposite = ev.getOppositeComponent();
        if (opposite != null) {
          lastFocused = opposite;
        }
        ev.getComponent().removeFocusListener(this);
      }
    };
  }

  /**
   * Handles events. This method is public as an implementation side-effect.
   */
  public void actionPerformed(ActionEvent evt) {
    Object	o = evt.getSource();

    if (o == todayButton) {
      selectedDate = Date.Companion.now();
      okay = true;
      dispose();
    } else {
      if (o == prevMonthButton) {
	incrementMonth(-1);
      } else if (o == nextMonthButton) {
	incrementMonth(1);
      } else if (o == prevYearButton) {
	incrementYear(-1);
      } else if (o == nextYearButton) {
	incrementYear(1);
      }

      updateCalendar();
      refresh();
    }
  }

  /**
   * @return the first day of the selected month
   */
  public int getFirstDay() {
    return firstDay;
  }

  /**
   * @return the current selected date (can be null)
   */
  public Date getSelectedDate() {
    return selectedDate;
  }

  /**
   *
   */
  public void setSelectedDate(Date selectedDate) {
    this.selectedDate = selectedDate;
  }

  /**
   * set the okay variable
   */
  /*package*/ void setOkay(boolean okay) {
    this.okay = okay;
  }

  private void incrementDay(int count) {
    selectedDate = selectedDate.add(count);
  }

  private void incrementMonth(int count) {
    Month	m = new Month(selectedDate).add(count);

    if (selectedDate.getDay() < m.getLastDay().getDay()) {
      selectedDate = new Date(m.getYear(), m.getMonth(), selectedDate.getDay());
    } else {
      selectedDate = m.getLastDay();
    }
  }

  private void incrementYear(int count) {
    incrementMonth(12 * count);
  }

  /**
   * Returns the number of days in the specified month
   */
  /*package*/ static int getDaysInMonth(Date d) {
    return new Month(d).getLastDay().getDay();
  }

  private void updateCalendar() {
    firstDay = new Date(selectedDate.getYear(), selectedDate.getMonth(), 1).getWeekday();

    yearLabel.setText(String.valueOf(selectedDate.getYear()));
    monthLabel.setText(MONTH_NAMES[selectedDate.getMonth() - 1]);
  }

  /*package*/ void refresh() {
    cal.repaint();
  }

  public static Date getDate(final Container container, final Component field, final Date date) {
    final DateChooser	chooser = new DateChooser(date);
    return chooser.doModal(container, field, date);
  }

  private Date doModal(final Container parent, final Component field, final Date date) {
    popup = new JPopupMenu() {
      /**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 3509735766988472178L;

	public Dimension getPreferredSize() {
	Dimension dim = super.getPreferredSize();
	return new Dimension(dim.width, dim.height);
      }
    };
    //    popup.setLightWeightPopupEnabled(false);
    popup.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	synchronized(DateChooser.this) {
	  DateChooser.this.notify();
	}
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
	synchronized(DateChooser.this) {
	  DateChooser.this.notify();
	}
      }
    });

    popup.insert(this, 0);
    addKeyListener(parent);
    popup.pack();

    Dimension	screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    Dimension popupSize = popup.getPreferredSize();
    Point  fieldPositionOnScreen = field.getLocationOnScreen();
    Point  offset = new Point(0, 0);

    if (fieldPositionOnScreen.x < 20) {
      offset.x = 20 - fieldPositionOnScreen.x;
    } else if (fieldPositionOnScreen.x + popupSize.width > screen.width - 20) {
      offset.x = screen.width - 20 - popupSize.width - fieldPositionOnScreen.x;
    }

    if (fieldPositionOnScreen.y < 20) {
      offset.y = 20 - fieldPositionOnScreen.y ;
    } else if (fieldPositionOnScreen.y + popupSize.height > screen.height - 20) {
      offset.y = screen.height - 20 - popupSize.height - fieldPositionOnScreen.y;
    } else {
      offset.y = field.getSize().height;
    }

    show(field, offset.x, offset.y);

    synchronized (this) {
      try {
        // !!! CORRECT with SwingThreadHandler
	this.wait();
      } catch (InterruptedException e) {}
    }

    return okay ? selectedDate : date;
  }

  private void show(final Component comp, final int x, final int y) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
	popup.show(comp, x, y);
      }
    });
  }

  /**
   * Add Key Listener
   */
  private void addKeyListener(Component parent) {
    listener = new KeyAdapter() {
      public void keyPressed(KeyEvent k) {
	int		key = k.getKeyCode();

	if (key == KeyEvent.VK_ESCAPE) {
	  okay = false;
	  dispose();
	} else if (key == KeyEvent.VK_SPACE
                   || (key == KeyEvent.VK_ENTER && !k.isShiftDown())) {
	  okay = true;
	  dispose();
	} else {
	  if (key == KeyEvent.VK_LEFT
              || (key == KeyEvent.VK_TAB && k.isShiftDown())) {
	    incrementDay(-1);
	  } else if (key == KeyEvent.VK_RIGHT
                     || key == KeyEvent.VK_TAB) {
	    incrementDay(1);
	  } else if (key == KeyEvent.VK_UP) {
	    incrementDay(-7);
	  } else if (key == KeyEvent.VK_DOWN) {
	    incrementDay(7);
	  } else if (key == KeyEvent.VK_PAGE_UP) {
	    incrementMonth(-1);
	  } else if (key == KeyEvent.VK_PAGE_DOWN) {
	    incrementMonth(1);
	  }
	  updateCalendar();
	  refresh();
	}
      }
    };

    // remember current focus owner
    lastFocused = KeyboardFocusManager.
      getCurrentKeyboardFocusManager().getFocusOwner();
    // request focus on root pane and install keybindings
    // used for menu navigation
    invokerRootPane = SwingUtilities.getRootPane(parent);
    if (invokerRootPane != null) {
      invokerRootPane.addFocusListener(rootPaneFocusListener);
      invokerRootPane.requestFocus(true);
      invokerRootPane.addKeyListener(listener);
      focusTraversalKeysEnabled = invokerRootPane.
        getFocusTraversalKeysEnabled();
      invokerRootPane.setFocusTraversalKeysEnabled(false);
    }
  }

  private void removeKeyListener() {
    if (lastFocused != null) {
      lastFocused.requestFocus();
    }
    if (invokerRootPane != null) {
      invokerRootPane.removeKeyListener(listener);
      invokerRootPane.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
    }
  }

  /**
   *
   */
  /*package*/ void dispose() {
    popup.setVisible(false);
    removeKeyListener();
    synchronized(DateChooser.this) {
      DateChooser.this.notify();
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static final String[]		MONTH_NAMES = {
    VlibProperties.getString("Jan"),
    VlibProperties.getString("Feb"),
    VlibProperties.getString("Mar"),
    VlibProperties.getString("Apr"),
    VlibProperties.getString("May"),
    VlibProperties.getString("Jun"),
    VlibProperties.getString("Jul"),
    VlibProperties.getString("Aug"),
    VlibProperties.getString("Sep"),
    VlibProperties.getString("Oct"),
    VlibProperties.getString("Nov"),
    VlibProperties.getString("Dec")
  };

  private Date				selectedDate;

  private boolean			okay;
  private JPopupMenu			popup;
  private FocusListener                 rootPaneFocusListener;
  private Component                     lastFocused = null;
  private boolean                       focusTraversalKeysEnabled;
  private KeyListener			listener;
  private JLabel			yearLabel;
  private JLabel			monthLabel;
  private JButton			prevYearButton;
  private JButton			nextYearButton;
  private JButton			prevMonthButton;
  private JButton			nextMonthButton;
  private JButton			todayButton;
  private CalendarPane			cal;
  private int				firstDay;

  JRootPane                             invokerRootPane;
}

// inner class to draw the calendar itself

/*package*/ class CalendarPane extends JComponent {

  /**
   * Construct a new CalendarPane object.
   */
  /*package*/ CalendarPane(final DateChooser dateChooser) {
    this.dateChooser = dateChooser;

    highlightColor = new Color(180, 180, 240);
    addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent evt) {
          final Date selectedDate = CalendarPane.this.dateChooser.getSelectedDate();

          Insets ins = getInsets();
          int x0 = ((getSize().width - getPreferredSize().width) / 2);
          // not for get the shift for the KW column (on the left of the calendar)
          int x = evt.getX() - ins.left - x0 - SHIFT_FOR_KW_COLUMN;
          int y = evt.getY() - ins.top - 20;
          int maxw = (CELL_SIZE + 2) * 7;
          int maxh = (CELL_SIZE + 2) * 6;

          // check if totally out of range.
          if ((x < 0) || (x > maxw) || (y < 0) || (y > maxh)) {
            return;
          }

          y /= (CELL_SIZE + 2);
          x /= (CELL_SIZE + 2);

          int	day = (7 * y) + x - (dp - 1);

          if (day >= 1 && day <= DateChooser.getDaysInMonth(selectedDate)) {
            CalendarPane.this.dateChooser.setSelectedDate(new Date(selectedDate.getYear(), selectedDate.getMonth(), day));
            CalendarPane.this.dateChooser.refresh();
            CalendarPane.this.dateChooser.setOkay(true);
            CalendarPane.this.dateChooser.dispose();
          }
        }
      });
  }

  /**
   * Paints the component.
   */
  public void paint(Graphics gc) {
    FontMetrics	fm = gc.getFontMetrics();
    Insets		ins = getInsets();
    int		h = fm.getMaxAscent();

    dp = ((dateChooser.getFirstDay() + 6) % 7) - 1;
    if (dp < 0) {
      dp += 7;
    }

    int	x = dp;
    int	y0 = ((getSize().height - getPreferredSize().height) / 2);
    int	yp = y0;
    int	x0 = ((getSize().width - getPreferredSize().width) / 2);
    // add SHIFT_FOR_KW_COLUMN just to have place to put the column KW
    int	xp = x0 + SHIFT_FOR_KW_COLUMN;

    paintBorder(gc);
    gc.setColor(Color.black);
    gc.clipRect(ins.left,
                ins.top,
                getSize().width - ins.left - ins.right,
                getSize().height - ins.top - ins.bottom);
    gc.translate(ins.left, ins.top);

    // print the name of the day
    for (int i = 0; i < 7; i++) {
      gc.drawString(WEEKDAY_NAMES[i], xp + 5 + i * (CELL_SIZE + 2), yp + h);
    }

    yp += 20;
    xp += dp * (CELL_SIZE + 2);

    // $$$ graf 010214 : simplify end of loop test
    for (int d = 1; d <= DateChooser.getDaysInMonth(dateChooser.getSelectedDate()); d++) {
      gc.setColor(d == dateChooser.getSelectedDate().getDay() ? highlightColor : Color.lightGray);
      gc.fill3DRect(xp, yp, CELL_SIZE, CELL_SIZE, true);

      gc.setColor((x > 4) ? WEEKEND_COLOR : Color.black);
      String ss = String.valueOf(d);
      int sw = fm.stringWidth(ss);

      gc.drawString(ss, xp - 3 + (CELL_SIZE - sw), yp + 3 + h);

      x = x + 1;

      // Print the number of the week just for the last line (value for the column KW)
      // if the last week of the month finish the the next month
      if (x == 7 || d == DateChooser.getDaysInMonth(dateChooser.getSelectedDate())) {
        Week		week = new Week(new Date(dateChooser.getSelectedDate().getYear(), dateChooser.getSelectedDate().getMonth(), d));

        gc.setColor(WEEK_NUMBER_COLOR);
        // x0 + 1 : to put the text a litle more on the right
        gc.drawString(String.valueOf(week.getWeek()), x0 + 1, yp + h + 3);
      }

      if (x == 7) {
        x = 0;
        // add SHIFT_FOR_KW_COLUMN just to have place to put the column KW
        xp = x0 + SHIFT_FOR_KW_COLUMN;
        yp += (CELL_SIZE + 2);
      } else {
        xp += (CELL_SIZE + 2);
      }
    }
  }

  /*
   * Gets the preferred size of the component.
   */
  public Dimension getPreferredSize() {
    Insets	ins = getInsets();

    // 7 days
    return new Dimension((((CELL_SIZE + 2) * 8) + ins.left + ins.right),
                         (((CELL_SIZE + 2) * 6) + 20) + ins.top + ins.bottom);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------


  private static final Color		WEEKEND_COLOR = UIManager.getColor("DateChooser.weekend");//Color.red.darker();
  private static final Color		WEEK_NUMBER_COLOR = UIManager.getColor("DateChooser.weeknumber");
  private static final int		SHIFT_FOR_KW_COLUMN = 21;
  private static final int		CELL_SIZE = 21;

  private static final String[]		WEEKDAY_NAMES = {
    VlibProperties.getString("Monday"),
    VlibProperties.getString("Tuesday"),
    VlibProperties.getString("Wednesday"),
    VlibProperties.getString("Thursday"),
    VlibProperties.getString("Friday"),
    VlibProperties.getString("Saturday"),
    VlibProperties.getString("Sunday")
  };

  private int				dp;
  private Color				highlightColor;
  private DateChooser			dateChooser;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8823525349311235417L;

}
