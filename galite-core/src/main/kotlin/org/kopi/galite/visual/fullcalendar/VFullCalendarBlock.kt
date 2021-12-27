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
import java.util.Calendar

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.kopi.galite.visual.cross.VFullCalendarForm
import org.kopi.galite.visual.db.DBDeadLockException
import org.kopi.galite.visual.db.DBInterruptionException
import org.kopi.galite.visual.db.transaction
import org.kopi.galite.visual.form.BlockListener
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

  lateinit var fullCalendarForm: VForm
  var dateField: VDateField? = null
  var fromTimeField: VTimeField? = null
  var toTimeField: VTimeField? = null
  var fromField: VTimestampField? = null
  var toField: VTimestampField? = null
  var isAutoLoaded = true

  /**
   * Returns true if this block can display more than one record.
   */
  override fun isMulti(): Boolean = true

  override fun initIntern() { }

  /**
   * Fetch full calendar entries from database. This will select all entries between
   * the first day and the last day of a specific [date].
   *
   * @param date the date
   */
  fun fetchEntries(date: Date): List<VFullCalendarEntry> {
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
      return listOf()
    }
    return if (entries == null) {
      form.error(MessageCode.getMessage("VIS-00022"))
      listOf()
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
      val startDate = week.getFirstDay().toSql()
      val lastDay =  week.getLastDay().toCalendar()
      lastDay.add(Calendar.DAY_OF_MONTH, 1)
      val endDate = Date(lastDay).toSql()

      tables!!.slice(columns)
        .select { (dateColumn greaterEq startDate) and (dateColumn less endDate) }
        .orderBy(*orderBys.toTypedArray())
    } else {
      val fromColumn = fromField!!.getColumn(0)!!.column
      val toColumn = toField!!.getColumn(0)!!.column
      val firstDayOfWeek = week.getFirstDay().toCalendar().toInstant()
      val lastDay =  week.getLastDay().toCalendar()
      lastDay.add(Calendar.DAY_OF_MONTH, 1)
      val firstDayOfNextWeek = lastDay.toInstant()

      tables!!.slice(columns)
        .select {
          ((fromColumn greaterEq firstDayOfWeek) and (fromColumn less firstDayOfNextWeek)) or
                  ((toColumn greaterEq firstDayOfWeek) and (toColumn less firstDayOfNextWeek))
        }
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

  fun openForEdit(startDateTime: Timestamp, endDateTime: Timestamp) {
    set(startDateTime, endDateTime)
    insertMode()
  }

  internal fun openForEdit(record: Int, newStart: Timestamp, newEnd: Timestamp) {
    fetchRecordInBlock(record)
    set(newStart, newEnd)
    fullCalendarForm.doNotModal()
  }

  internal fun openForEdit(record: Int) {
    fetchRecordInBlock(record)
    fullCalendarForm.doNotModal()
  }

  private fun fetchRecordInBlock(record: Int) {
    form.transaction(Message.getMessage("loading_record")) {
      fullCalendarForm.getActiveBlock()?.fetchRecord(record)
    }
  }

  fun set(startDateTime: Timestamp, endDateTime: Timestamp) {
    if (dateField != null) {
      val start = startDateTime.toCalendar()
      val end = endDateTime.toCalendar()
      dateField!!.setDate(Date(start))
      fromTimeField!!.setTime(Time(start))
      toTimeField!!.setTime(Time(end))
    } else {
      fromField!!.setTimestamp(startDateTime)
      toField!!.setTimestamp(endDateTime)
    }
  }

  internal fun dateChanged(oldDate: Date, newDate: Date) {
    goToDate(newDate)

    if(Week(oldDate) != Week(newDate)) {
      refreshEntries()
    }
  }

  fun goToDate(date: Date) {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).goToDate(date)
      }
      i -= 2
    }
  }

  override fun insertMode() {
    fullCalendarForm.doNotModal()
    fullCalendarForm.getActiveBlock()?.insertMode()
  }

  /**
   * Goto first accessible field in current record
   * @exception VException      an exception may occur in field.leave()
   */
  override fun gotoFirstField() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    enterDateSelector()
  }

  fun enterDateSelector() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).enter()
      }
      i -= 2
    }
  }

  fun getSelectedDate(): Date? {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        return (listeners[i + 1] as BlockListener).getSelectedDate()
      }
      i -= 2
    }

    return Date.now()
  }

  /**
   * Refreshes the full calendar block data.
   */
  fun refreshEntries() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).refreshEntries()
      }
      i -= 2
    }
  }
}
