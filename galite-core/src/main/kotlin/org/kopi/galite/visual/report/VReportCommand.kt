/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.kopi.galite.visual.report

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.print.DefaultPrintManager
import org.kopi.galite.visual.print.PrintManager
import org.kopi.galite.visual.visual.Action
import org.kopi.galite.visual.visual.ActionHandler
import org.kopi.galite.visual.visual.PrinterManager
import org.kopi.galite.visual.visual.VActor
import org.kopi.galite.visual.visual.VCommand
import org.kopi.galite.visual.visual.VHelpGenerator

class VReportCommand(
  val report: VReport,
  actor: VActor
) : VCommand(0xFFFF, null, actor, actor.number, actor.actorIdent), ActionHandler {
  /**
   * Returns the actor
   */
  override fun setEnabled(enabled: Boolean) {
    if (actor != null) {
      actor!!.isEnabled = enabled
      actor!!.number = trigger
      actor!!.handler = this
    }
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param    action        the action to perform.
   * @param    block        This action should block the UI thread ?
   */
  @Deprecated("use method performAsyncAction", ReplaceWith("performAsyncAction(action, block)"))
  override fun performAction(action: Action, block: Boolean) {
    report.performAction(action, block)
    /*try {
      executeVoidTrigger(getTrigger());
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param    action        the action to perform.
   */
  override fun performAsyncAction(action: Action) {
    report.performAsyncAction(action)
    /*try {
      executeVoidTrigger(getTrigger());
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  /**
   * Performs a void trigger
   *
   * @param    VKT_Type    the number of the trigger
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    when (VKT_Type) {
      Constants.CMD_QUIT -> report.close()
      Constants.CMD_PRINT -> {
        val pm: PrintManager = DefaultPrintManager.getPrintManager()
        pm.print(report,
                 report,
                 1,
                 PrinterManager.getPrinterManager().getCurrentPrinter(),
                 null,
                 null)
      }
      Constants.CMD_EXPORT_CSV -> report.export(VReport.TYP_CSV)
      Constants.CMD_EXPORT_XLS -> report.export(VReport.TYP_XLS)
      Constants.CMD_EXPORT_XLSX -> report.export(VReport.TYP_XLSX)
      Constants.CMD_EXPORT_PDF -> report.export(VReport.TYP_PDF)
      Constants.CMD_FOLD -> report.foldSelection()
      Constants.CMD_UNFOLD -> report.unfoldSelection()
      Constants.CMD_SORT -> report.sortSelectedColumn()
      Constants.CMD_FOLD_COLUMN -> report.foldSelectedColumn()
      Constants.CMD_UNFOLD_COLUMN -> report.unfoldSelectedColumn()
      Constants.CMD_HELP -> report.showHelp()
    }
  }

  /**
   * Performs a void trigger
   *
   * @param    trigger    the  trigger
   */
  override fun executeVoidTrigger(trigger: Trigger?) {
    // DO NOTHING !
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  override fun helpOnCommand(help: VHelpGenerator) {
    if (actor == null) {
      return
    }
    actor!!.helpOnCommand(help)
  }

}
