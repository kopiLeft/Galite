/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.fullcalendar

import java.sql.SQLException

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.kopi.galite.visual.cross.VFullCalendarForm
import org.kopi.galite.visual.db.DBDeadLockException
import org.kopi.galite.visual.db.DBInterruptionException
import org.kopi.galite.visual.db.transaction
import org.kopi.galite.visual.form.FullCalendarBlockListener
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VDateField
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.VTimeField
import org.kopi.galite.visual.form.VTimestampField
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.type.Week
import org.kopi.galite.visual.visual.Message
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.VExecFailedException

abstract class VFullCalendarBlock(form: VForm) : VBlock(form) {

  private var fullCalendarForm: VFullCalendarForm? = null
  var dateField: VDateField? = null
  var fromTimeField: VTimeField? = null
  var toTimeField: VTimeField? = null
  var fromField: VTimestampField? = null
  var toField: VTimestampField? = null

  /**
   * Returns true if this block can display more than one record.
   */
  override fun isMulti(): Boolean = true

  abstract fun buildFullCalendarForm() : VFullCalendarForm

  fun addFullCalendarBlockListener(fbl: FullCalendarBlockListener?) {
    blockListener.add(FullCalendarBlockListener::class.java, fbl)
  }

  fun getEntries(date: Date): List<VFullCalendarEntry>? {
    var entries: List<VFullCalendarEntry>?

    try {
      while (true) {
        try {
          entries = form.transaction(Message.getMessage("searching_database")) {
            callProtectedTrigger(VConstants.TRG_PREQRY)
            queryEntries(Week(date))
          }
          break
        } catch (e: VException) {
          try {
            form.handleAborted(e)
          } catch (abortEx: VException) {
            throw abortEx
          }
        } catch (e: SQLException) {
          try {
            form.handleAborted(e)
          } catch (abortEx: DBDeadLockException) {
            throw VExecFailedException(MessageCode.getMessage("VIS-00058"))
          } catch (abortEx: DBInterruptionException) {
            throw VExecFailedException(MessageCode.getMessage("VIS-00058"))
          } catch (abortEx: SQLException) {
            throw VExecFailedException(abortEx)
          }
        } catch (e: Error) {
          try {
            form.handleAborted(e)
          } catch (abortEx: Error) {
            throw VExecFailedException(abortEx)
          }
        } catch (e: RuntimeException) {
          try {
            form.handleAborted(e)
          } catch (abortEx: RuntimeException) {
            throw VExecFailedException(abortEx)
          }
        }
      }
    } catch (e: Exception) {
      if (e.message != null) {
        form.error(e.message!!)
      }
      return null
    }
    return if (entries == null) {
      form.error(MessageCode.getMessage("VIS-00022"))
      null
    } else {
      entries
    }
  }

  /**
   * Builds the query entries that shows the list of data entries from database.
   *
   * Warning, you should use this method inside a transaction
   */
  private fun queryEntries(week: Week): MutableList<VFullCalendarEntry> {
    val query_tab = arrayOfNulls<VField>(fields.size)
    var query_cnt = 0

    /* get the fields to build query */
    for (field in fields) {

      /* skip fields not related to the database */
      if (field.getColumnCount() == 0) {
        continue
      }

      query_tab[query_cnt++] = field
    }

    /* build query: first rows to select ... */
    val columns = mutableListOf<Column<*>>()

    for (i in 0 until query_cnt) {
      columns.add(query_tab[i]!!.getColumn(0)!!.column)
    }

    /* add the DB column of the ID field. */
    columns.add(idColumn)

    /* ... and now their order */
    var orderSize = 0

    val orderBys = mutableListOf<Pair<Column<*>, SortOrder>>()

    for (i in 0 until query_cnt) {
      // control the size (nbr of columns and size of characters in an "order by" clause)
      val size = query_tab[i]!!.width * query_tab[i]!!.height

      orderSize += size

      if (query_tab[i]!!.getPriority() < 0) {
        orderBys.add(columns[i] to SortOrder.DESC)
      } else {
        orderBys.add(columns[i] to SortOrder.ASC)
      }
    }

    /* query from where ? */
    val tables = getSearchTables()
    val entries = mutableListOf<VFullCalendarEntry>()
    val ids = mutableListOf<Int>()

    val query = if(dateField != null) {
      val dateColumn = dateField!!.getColumn(0)!!.column
      val startDate = week.getDate(1).toSql()
      val endDate = week.getDate(7).toSql()

      tables!!.slice(columns)
        .select { (dateColumn greaterEq startDate) and (dateColumn lessEq  endDate) }
        .orderBy(*orderBys.toTypedArray())
    } else {
      val fromColumn = fromField!!.getColumn(0)!!.column
      val toColumn = toField!!.getColumn(0)!!.column
      val startDate = Timestamp(week.getDate(1).toCalendar()).toSql()
      val endDate = Timestamp(week.getDate(7).toCalendar()).toSql()

      tables!!.slice(columns)
        .select { (fromColumn greaterEq startDate) and (toColumn lessEq  endDate)  }
        .orderBy(*orderBys.toTypedArray())
    }

    for (result in query) {
      /* don't show record with ID = 0 */
      if (result[idColumn] == 0) {
        continue
      }

      ids.add(result[idColumn])

      val entry = if(dateField != null) {
        val values = mutableMapOf<VField, Any?>()
        lateinit var date: Date
        lateinit var start: Time
        lateinit var end: Time

        for (i in 0 until query_cnt) {
          val vField = query_tab[i]!!
          val value = vField.retrieveQuery(result, columns[i])

          when (vField) {
            dateField -> {
              date = value as Date
            }
            fromTimeField -> {
              start = value as Time
            }
            toTimeField -> {
              end = value as Time
            }
            else -> values[vField] = value
          }
        }

        VFullCalendarEntry(date, start, end, values)
      } else {
        val values = mutableMapOf<VField, Any?>()
        lateinit var start: Timestamp
        lateinit var end: Timestamp

        for (i in 0 until query_cnt) {
          val vField = query_tab[i]!!
          val value = vField.retrieveQuery(result, columns[i])

          when (vField) {
            fromField -> {
              start = value as Timestamp
            }
            toField -> {
              end = value as Timestamp
            }
            else -> values[vField] = value
          }
        }

        VFullCalendarEntry(start, end, values)
      }

      entries.add(entry)
    }

    return entries
  }

  fun doNotModalBlock(record: Int) {
    fullCalendarForm = buildFullCalendarForm()

    form.transaction(Message.getMessage("loading_record")) {
      fullCalendarForm!!.blocks[0].fetchRecord(record)
    }
    fullCalendarForm!!.doNotModal()
  }

  fun dateChanged(oldDate: Date, newDate: Date) {
    gotoDate(newDate)

    if(Week(oldDate) != Week(newDate)) {
      refreshEntries()
    }
  }

  private fun gotoDate(date: Date) {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == FullCalendarBlockListener::class.java) {
        (listeners[i + 1] as FullCalendarBlockListener).goToDate(date)
      }
      i -= 2
    }
  }

  private fun refreshEntries() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == FullCalendarBlockListener::class.java) {
        (listeners[i + 1] as FullCalendarBlockListener).refreshEntries()
      }
      i -= 2
    }
  }
}
