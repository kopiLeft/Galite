/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.report

import org.kopi.galite.cross.VDynamicReport
import org.kopi.galite.domain.Domain
import org.kopi.galite.field.Field
import org.kopi.galite.form.VConstants
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VCommand
import java.awt.event.KeyEvent

/**
 * Represents a report that contains fields [fields] and displays a table of [reportRows].
 */
open class Report: VReport() {
  /** Report's fields. */
  val fields = mutableListOf<Field<*>>()

  /** Report's data rows. */
  val reportRows = mutableListOf<ReportRow>()

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  fun <T : Comparable<T>> field(domain: Domain<T>, init: Field<T>.() -> Unit): Field<T> {
    val field = Field(domain)
    field.init()
    fields.add(field)
    return field
  }

  /**
   * Adds a row to the report.
   *
   * @param init initializes the row with values.
   */
  fun add(init: ReportRow.() -> Unit) {
    val row = ReportRow(fields)
    row.init()
    reportRows.add(row)
  }

  /**
   * Returns the row's data.
   *
   * @param rowNumber the index of the desired row.
   */
  fun getRow(rowNumber: Int): MutableMap<Field<*>, Any> = reportRows[rowNumber].data

  /**
   * Returns rows of data for a specific [field].
   *
   * @param field the field.
   */
  fun getRowsForField(field: Field<*>) = reportRows.map { it.data[field] }

  /**
   * Adds default report commands
   */
  fun reportCommands() {
    setActors(arrayOf(
            VActor("File", null,"Quit", null, VDynamicReport.QUIT_ICON, KeyEvent.VK_ESCAPE, 0),
            VActor("File", null, "Print", null, VDynamicReport.PRINT_ICON, KeyEvent.VK_F6, 0),
            VActor("File", null, "ExportCSV", null, VDynamicReport.EXPORT_ICON, KeyEvent.VK_F8, 0),
            VActor("File", null, "ExportXLSX", null, VDynamicReport.EXPORT_ICON, KeyEvent.VK_F9, KeyEvent.SHIFT_MASK),
            VActor("File", null, "ExportPDF", null, VDynamicReport.EXPORT_ICON, KeyEvent.VK_F9, 0),
            VActor("Action", null, "Fold", null, VDynamicReport.FOLD_ICON, KeyEvent.VK_F2, 0),
            VActor("Action", null, "Unfold", null, VDynamicReport.UNFOLD_ICON, KeyEvent.VK_F3, 0),
            VActor("Action", null, "FoldColumn", null, VDynamicReport.FOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED, 0),
            VActor("Action", null, "UnfoldColumn", null, VDynamicReport.UNFOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED, 0),
            VActor("Action", null, "Sort", null, VDynamicReport.SERIALQUERY_ICON, KeyEvent.VK_F4, 0),
            VActor("Help", null, "Help", null, VDynamicReport.HELP_ICON, KeyEvent.VK_F1, 0),
    ))
    super.commands = arrayOf(
            VCommand(VConstants.MOD_ANY, this, getActor(0), 1, "Quit"),
            VCommand(VConstants.MOD_ANY, this, getActor(1), 2, "Print"),
            VCommand(VConstants.MOD_ANY, this, getActor(2), 3, "PrintOptions"),
            VCommand(VConstants.MOD_ANY, this, getActor(3), 4, "ExportCSV"),
            VCommand(VConstants.MOD_ANY, this, getActor(4), 5, "ExportPDF"),
            VCommand(VConstants.MOD_ANY, this, getActor(5), 6, "ExportXLSX"),
            VCommand(VConstants.MOD_ANY, this, getActor(6), 7, "Fold"),
            VCommand(VConstants.MOD_ANY, this, getActor(7), 8, "Unfold"),
            VCommand(VConstants.MOD_ANY, this, getActor(8), 9, "Sort"),
            VCommand(VConstants.MOD_ANY, this, getActor(9), 10, "Help"),
    )
  }

  override fun init() {
    super.model.columns = arrayOf()
    source = "Test" // TODO
  }

  override fun add() {
    // TODO
  }
}
