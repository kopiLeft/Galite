/*
 * Copyright (c) 2013-2025 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2025 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.plugins.generator

import java.io.File
import java.time.LocalDate

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.JavaLocalDateColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalDateTimeColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalTimeColumnType

import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder

object DBSchemaGenerator {
  @JvmStatic
  fun main(args: Array<String>) {
    if (args.size != 4) {
      return
    }
    val packageName = args[0]
    val schemaName = args[1]
    val subFolder = args[2]
    val directory = args[3]
    val generatedFile = File(createGeneratedPath(packageName, schemaName, subFolder, directory))

    // Initialize Reflections for the package where the objects are defined
    val reflections = Reflections(ConfigurationBuilder().forPackages(packageName).addScanners(Scanners.SubTypes))

    // Get all public classes of type [org.jetbrains.exposed.sql.Table] in the specified package
    val tableClasses = reflections.getSubTypesOf(Table::class.java).filter {
      try {
        val kClass = it.kotlin
        kClass.visibility == kotlin.reflect.KVisibility.PUBLIC
      } catch (_: Exception) {
        false
      }
    }

    val generatedCode = buildString {
      addHeader()
      appendLine()
      appendLine("package $packageName.$subFolder")
      appendLine()
      appendLine("import java.math.BigDecimal")
      appendLine()
      appendLine("import org.jetbrains.exposed.sql.Table")
      appendLine("import org.jetbrains.exposed.sql.javatime.date")
      appendLine("import org.jetbrains.exposed.sql.javatime.datetime")
      appendLine("import org.jetbrains.exposed.sql.javatime.time")
      appendLine()
      addClassComment(schemaName)
      appendLine()
      appendLine("val schéma = \"$schemaName\"") // Define the schema name

      // Add table classes declaration
      tableClasses.sortedBy { it.name }.forEach {
        it.kotlin.objectInstance?.let { table ->
          appendLine()
          // Table object definition
          appendLine("object ${it.simpleName} : Table(\"${'$'}schéma.${table.tableName}\") {")
          // Table columns definition
          table.columns.forEach { column ->
            appendLine("  val ${getColumnName(column)} = ${getColumnType(column, column.columnType)}")
          }
          // Table index definition
          if (table.indices.isNotEmpty()) {
            appendLine()
            appendLine("  init {")
            table.indices.forEach { index -> appendLine(getTableIndexDefinition(index)) }
            appendLine("  }")
          }
          // Primary key definition
          table.primaryKey?.let { primaryKey ->
            appendLine()
            appendLine("  override val primaryKey = PrimaryKey(${primaryKey.columns.joinToString(", ") { 
              key -> key.name.lowercase()
            }})")
          }
          appendLine("}")
        }
      }
    }

    generatedFile.parentFile?.mkdirs()
    generatedFile.writeText(generatedCode)
  }

  /**
   * Create the generated class path
   */
  private fun createGeneratedPath(packageName: String, schema: String, subFoler: String, directory: String): String {
    return "$directory${File.separator}" +
        "${packageName.replace(".", File.separator)}${File.separator}" +
        "$subFoler${File.separator}" +
        "DBSchema${schema.uppercase()}.kt"
  }

  /**
   * Get the column name of the [Table] object
   */
  private fun getColumnName(column: Column<*>): String {
    return if (column.name.lowercase() == "source") column.name.uppercase() else column.name.lowercase()
  }

  /**
   * Find the column's type and add the appropriate syntax
   */
  private fun getColumnType(column: Column<*>, type: IColumnType) : String {
    var columnDefinition = when (type) {
      is JavaLocalDateColumnType     -> "date(\"${column.name}\")"
      is JavaLocalTimeColumnType     -> "time(\"${column.name}\")"
      is JavaLocalDateTimeColumnType -> "datetime(\"${column.name}\")"
      is BooleanColumnType -> "bool(\"${column.name}\")"
      is IntegerColumnType -> "integer(\"${column.name}\")"
      is LongColumnType  -> "long(\"${column.name}\")"
      is ByteColumnType    -> "byte(\"${column.name}\")"
      is ShortColumnType   -> "short(\"${column.name}\")"
      is AutoIncColumnType     -> when(type.delegate) {
        is LongColumnType            -> "long(\"${column.name}\").autoIncrement()"
        else                         -> "integer(\"${column.name}\").autoIncrement()"
      }
      is DecimalColumnType     -> "decimal(\"${column.name}\", ${type.precision}, ${type.scale})"
      is BlobColumnType,
      is BinaryColumnType,
      is BasicBinaryColumnType -> "blob(\"${column.name}\")"
      is DoubleColumnType  -> "double(\"${column.name}\")"
      is UUIDColumnType    -> "uuid(\"${column.name}\")"
      is VarCharColumnType -> "varchar(\"${column.name}\", ${type.colLength})"
      is TextColumnType    -> "text(\"${column.name}\")"
      is CharColumnType    -> "char(\"${column.name}\", ${type.colLength})"
      else                 -> "!!! FIXME !!!"
    }
    // Check nullable
    if (type.nullable) {
      columnDefinition += ".nullable()"
    }
    // Check default value
    column.defaultValueFun?.let {
      columnDefinition += when (type) {
        is TextColumnType, is VarCharColumnType -> {
          ".default(\"${it.invoke()}\")"
        }
        is DecimalColumnType -> {
          ".default(${when (it.invoke().toString()) {
            "0" -> "BigDecimal.ZERO"
            "1" -> "BigDecimal.ONE"
            else -> "BigDecimal(\"${it.invoke()}\")"
          }})"
        }
        else               -> {
          ".default(${it.invoke()})"
        }
      }
    }
    return columnDefinition
  }

  /**
   * Get index definition
   */
  private fun getTableIndexDefinition(index: Index): String {
    return if (index.unique) {
      "    uniqueIndex(${getIndexName(index)}, ${index.columns.joinToString(separator = ", ") { col -> getColumnName(col) }})"
    } else {
      "    index(${getIndexName(index)}, false, ${index.columns.joinToString(separator = ", ") { col -> getColumnName(col) }})"
    }
  }

  /**
   * Get the index name, if [index.customName] not defined, set to null
   */
  private fun getIndexName(index: Index): String {
    return index.customName?.let { "\"$it\"" } ?: "null"
  }

  /**
   * Add header comment
   */
  private fun StringBuilder.addHeader() {
    this.append(
      """
/*
 * Copyright (c) 2013-${LocalDate.now().year} kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-${LocalDate.now().year} kopiRight Managed Solutions GmbH, Wien AT
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
      """.trimIndent()
    )
    this.appendLine()
  }

  /**
   * Add class comment
   */
  private fun StringBuilder.addClassComment(schema: String) {
    this.append(
      """
/*
 * Database structure generated for schema: $schema
 *
 * Automatically generated - do not modify.
 */
      """.trimIndent()
    )
    this.appendLine()
  }

  const val GENERATED_KOTLIN_SRC = "src/generated/kotlin"
  const val GENERATED_SRC = "src/generated"
}
