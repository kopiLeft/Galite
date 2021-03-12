package org.kopi.galite.ui.vaadin.list

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.VWindow

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.KeyPressEvent
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.shared.Registration
import org.kopi.galite.form.VListDialog

/**
 * The list dialog widget composed of HTML sortable table
 * that allows single row selection.
 */
class ListDialog : Grid<VListDialog>(), KeyNotifier {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var table: ListTable? = null
  private var popup: Dialog? = null
  private var handlerRegistration: Registration? = null
  private var reference: Component? = null
  private var newForm: VInputButton? = null
  private var lastActiveWindow: VWindow? = null
  private var current: String? = null
  private var close: Icon? = null
  private var content: VerticalLayout? = null
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Shows the list dialog.
   * @param parent The parent where to attach the list.
   */
  fun show(parent: MainWindow) {
    if (popup != null && table != null) {
      parent.addWindow(popup!!, "")
      //table.render()
      //  popup.setWidget(this@ListDialog) // set the popup widget
      content!!.add(close)
      content!!.add(table) // put table inside the focus panel
      if (newForm != null) {
        content!!.add(newForm)
      }
    }
/*    object : Timer() {
      fun run() {
        if (popup != null && table != null) {
          parent.addWindow(popup!!, "")
          //table.render()
        //  popup.setWidget(this@ListDialog) // set the popup widget
          content!!.add(close)
          content!!.add(table) // put table inside the focus panel
          if (newForm != null) {
            content!!.add(newForm)
          }
        }
         *//*if (VInputTextField.getLastFocusedTextField() != null) {
          lastActiveWindow = VInputTextField.getLastFocusedTextField().getParentWindow()
        }*//*
        // it can be an editor widget
     *//*   if (lastActiveWindow == null && VEditorTextField.getLastFocusedEditor() != null) {
          lastActiveWindow = VEditorTextField.getLastFocusedEditor().getWindow()
        }*//*
      }
    }.schedule(50) //!!! delay it to ensure that it is shown after a field focus
 */
  }

  /**
   * Initializes the list dialog widget.
   * @param connection The application connection.
   */
  fun init() {
    close = Icon()
    close!!.style.set("name", "close")
    //  popup.addCloseHandler(this)
    //  popup.setAnimationEnabled(true)
    //setRollDownAnimation(popup)
    table!!.className = (Styles.LIST_DIALOG_TABLE)
    //      handlerRegistration = table!!.addItemClickListener(this)
    /*    close.addClickHandler(object : ClickHandler() {
          fun onClick(event: ClickEvent?) {
            handleRowSelection(-1, true, false)
          }
        })*/
    //  this.add(content)
  }

  /**
   * Sets the list dialog table model.
   * @param model The table model.
   */
/*  fun setModel(model: TableModel?) {
    if (table != null) {
      table.setModel(model)
    }
  }*/

  /**
   * Sets the new text widget.
   * @param newText The new text widget.
   */
  fun setNewText(newText: String?) {
    if (newText != null) {
      /*   newForm = VInputButton(newText, object : ClickHandler() {
           fun onClick(event: ClickEvent?) {
             handleRowSelection(-1, false, true)
           }
         })*/
      newForm!!.value= newText
      newForm!!.className = ("new-button")
      //newForm.setSize("100%", "100%") // occupy all available space.
    }
  }

  /**
   * Shows the list dialog relatively to a reference widget.
   * @param connection The application connection.
   * @param model The table model.
   * @param reference The reference widget.
   */
  fun showRelativeTo(reference: Component?) {
    this.reference = reference
  }

  /**
   * Shows the list dialog in the screen.
   */
  fun showCentred() {
    if (popup == null) {
      return
    }
    // popup.setGlassEnabled(true)
    popup!!.element.style.set("name", Styles.LIST_DIALOG.toString() + "-glass")
    // popup.center(VMainWindow.get().getCurrentWindow())
  }

  fun onBrowserEvent(event: Key) {
    /*  if (event.keys. == ARROW_DOWN) {
        if (table != null) {
          doKeyAction(event.hashCode())
        }
      }*/
    // super.onBrowserEvent(event)
  }

  fun onKeyPress(event: KeyPressEvent) {
    if (table != null) {
      val c = event.code
      var row = 0
      var col = 0
      if (current == null) {
        current = ""
        // table.unhighlightCell()
      }
      current += ("" + c).toLowerCase()[0]
      col = 0
      columnsLoop@ while (col < table!!.columns.size) {
        row = 0
        /*  while (row < table.getModelRowCount()) {
            if (table.getDisplayedValueAt(row, col).toLowerCase().startsWith(current)) {
              table.setCurrentRow(row)
              table.highlightCell(row, col)
              break@columnsLoop
            }
            row++
          }*/
        col++
      }
      /*if (row == table.getModelRowCount() && col == table.getColumnCount()) {
        current = ""
        table.unhighlightCell()
      }*/
    }
  }

  /**
   * Allows access to the key events.
   * @param keyCode The key code.
   */
  protected fun doKeyAction(keyCode: Int) {
    /*       when (keyCode) {
             KeyCodes.KEY_HOME -> {
               table.setCurrentRow(0)
               current = ""
               table.unhighlightCell()
             }
             KeyCodes.KEY_END -> {
               table.setCurrentRow(table.getRowCount() - 1)
               current = ""
               table.unhighlightCell()
             }
             KeyCodes.KEY_UP -> {
               table.shiftUp(1)
               current = ""
               table.unhighlightCell()
             }
             KeyCodes.KEY_DOWN -> {
               table.shiftDown(1)
               current = ""
               table.unhighlightCell()
             }
             KeyCodes.KEY_PAGEUP -> {
               table.shiftUp(20)
               current = ""
               table.unhighlightCell()
             }
             KeyCodes.KEY_PAGEDOWN -> {
               table.shiftDown(20)
               current = ""
               table.unhighlightCell()
             }
             KeyCodes.KEY_SPACE -> handleRowSelection(-1, false, true)
             KeyCodes.KEY_ENTER -> handleRowSelection(table.getSelectedRow(), false, false)
             KeyCodes.KEY_ESCAPE -> handleRowSelection(-1, true, false) // when escape, we return -1 as selected row
             else -> {
             }*/
  }

  fun clear() {
    close = null
    //table.clear()
   // table.release()
    table = null
    popup = null
    handlerRegistration!!.remove()
    handlerRegistration = null
    reference = null
    newForm = null
    lastActiveWindow = null
  }



//---------------------------------------------------
// CONSTRUCTOR
//---------------------------------------------------

  /**
   * Creates a new `VListDialog` instance.
   */
  init {
    //makeFocusable()
    element.setAttribute("hideFocus", "true")
    element.style["outline"] = "0px"
    className = Styles.LIST_DIALOG
    //sinkEvents(Event.ONKEYDOWN or Event.ONKEYPRESS)
    //addKeyPressHandler(this)
  }

/*
fun onClick(event: ClickEvent?) {
   if (table != null) {
       val clicked: Int
       clicked = table.getClickedRow(event)
       if (clicked != -1) {
         handleRowSelection(clicked, false, false)
       }
     }
}
*/
  /**
   * Handles a row selection in the list dialog table.
   * @param selectedRow The selected row.
   * @param escaped Should we escape ?
   * @param newForm Should we do a new form ?
   */
  fun handleRowSelection(selectedRow: Int, escaped: Boolean, newForm: Boolean) {
/*  if (popup != null) {
   val connector: ListDialogConnector?
   connector = connector
   if (connector != null && table != null) {
     // we return the model row to used directly in server side.
     connector.fireOnSelection(if (selectedRow == -1) -1 else table.getModelRow(selectedRow), escaped, newForm)
   }
   popup.hide()
   if (handlerRegistration != null) {
     handlerRegistration.removeHandler() // remove click handler
   }
 }*/
  }

/*   fun onClose(event: CloseEvent<PopupPanel?>) {
      event.getTarget().clear()
      event.getTarget().removeFromParent()
      if (lastActiveWindow != null) {
        lastActiveWindow.goBackToLastFocusedTextBox()
      }
    }*/

  fun onLoad() {
/*        super.onLoad()
       Scheduler.get().scheduleFinally(object : ScheduledCommand() {
         fun execute() {
           if (newForm != null) {
             if (newForm.getElement().getClientWidth() > table.getTHeadElement().getClientWidth()) {
               table.getElement().getStyle().setWidth(newForm.getElement().getClientWidth(), Unit.PX)
               table.setTableWidth(newForm.getElement().getClientWidth())
             }
           }
           if (reference != null) {
             popup.showRelativeTo(reference)
             popup.getElement().getStyle().setMarginTop(-16, Unit.PX)
           } else {
             showCentred()
           }
           focus() // get focus to activate keyboard events.
         }
       })*/
  }

  override fun focus() {
/*        Scheduler.get().scheduleEntry(object : ScheduledCommand() {
         fun execute() {
           super@VListDialog.focus()
           if (table != null) {
             table.selectRow(0)
           }
         }
       })*/
  }

}
