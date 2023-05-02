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

package org.kopi.galite.visual.list

import org.jetbrains.exposed.sql.*
import kotlin.reflect.KClass

import org.kopi.galite.visual.domain.TableInitializer
import org.kopi.galite.visual.l10n.ListLocalizer

abstract class VListColumn(
  var title: String,
  private val internalColumn: ExpressionWithColumnType<*>?,
  private val table: TableInitializer?,
  private val align: Int,
  val width: Int,
  val isSortAscending: Boolean,
) : VConstants, ObjectFormatter {

  /**
   * Returns the column alignment
   */
  override fun getAlign(): Int {
    return align
  }

  /**
   * Returns a representation of value
   */
  override fun formatObject(value: Any?): Any? {
    return value?.toString() ?: VConstants.EMPTY_TEXT
  }

  /**
   * Returns the data type provided by this list.
   * @return The data type provided by this list.
   */
  abstract fun getDataType(): KClass<*>

  val column: ExpressionWithColumnType<*>? get() = internalColumn?.let { table?.let { tableInit -> tableInit().resolveColumn(it) } as ExpressionWithColumnType } ?: internalColumn

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localize this object.
   *
   * @param     loc
   */
  fun localize(loc: ListLocalizer) {
    title = loc.getColumnTitle(title)
  }

  /**
   * Finds and returns the column in this [ColumnSet] corresponding to the [column] from the original table.
   *
   * @param column The column in the original table.
   */
  fun ColumnSet.resolveColumn(column: ExpressionWithColumnType<*>): Expression<*> {
    return when (this) {
      is Table -> {
        println("is Table")
//        println()
//        println("===============${column.toString()}")
//      if (column is Column<*>)
        column
//      else Alias.QueryAlias.get(column)
      }
      is QueryAlias -> {
        println("is QueryAlias")
//      println("========" +fields.size)
//      println("========" +fields.toString())
////      println("========" +fields.single { it.name == column.name })
//println("****"+(if (column is Column<*>) get(column as Column<*>)
//else fields.single{ (it as Column<*>).name == "ID"}))
        //    println("===============${column.toString()}")

        //  println ("=======111::"+get(column).toString())

            if (column is Column<*>)  {
//          println("================:::"+get(column as Expression<*>).toString())
          get(column as Column<*>)
           }
//        else fields.single { (it as Column<*>).name.uppercase() == title.uppercase() }
//        else columns.single { it.name.uppercase() == title.uppercase() }
        //get(column)
        //   else column

//        println()
//        println("==================="+(query.set.fields.filterIsInstance<Expression<*>>().toString()))
//        println("==================="+(query.set.source.fields.toString()))
//        println("==================="+(query.set.source.columns.toString()))
//        println("==================="+(query.set.source.columns.filterIsInstance<Expression<*>>().toString()))
//        println("==================="+column.toString())
//        println("==================="+(query.set.fields.filterIsInstance<ExpressionWithColumnType<*>>().find { it == column }.toString()))
//
//        println()
//        println("==================="+(query.set.fields.filterIsInstance<ExpressionAlias<*>>().find { it == column as ExpressionWithColumnType<*>}?.let {
//          it.delegate.alias("$alias.${it.alias}").aliasOnlyExpression() ?: column as ExpressionWithColumnType<*>
//        }.toString()))

       else query.set.source.columns.find { it == column }?.clone() as Expression<*> //column


//        query.set.fields.filterIsInstance<ExpressionAlias<*>>().find { it == column }?.let {
//          it.delegate.alias("$alias.${it.alias}").aliasOnlyExpression() ?: column
//        }
      }




      is Alias<*> -> {
         println("is Alias")
        get(column as Column<*>)
      }
      else -> fields.single { (it as Column<*>).name.uppercase() == title.uppercase() }
    }
  }

  private fun <T : Any?> Column<T>.clone() = Column<T>(table.alias("syn__0__"), name, columnType)

}
