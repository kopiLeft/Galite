/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.database.installed

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

class TransDB2 : TransDB("galite", 2) {
  /**
   * Exécuter les requêtes SQL
   */
  override fun execute() {
    SchemaUtils.create(ReportConfigurations)
  }

  override val SOURCE: String
    get() = TODO("Not yet implemented")
}

private object ReportConfigurations: Table("REPORTCONFIGURATIONS") {
  val id                = integer("ID").autoIncrement("REPORTCONFIGURATIONSID")
  val uc                = integer("UC")
  val ts                = integer("TS")
  val report            = varchar("REPORT", 200)
  val name              = varchar("KURZNAME", 30)
  val description       = varchar("BEZEICHNUNG", 100).nullable()
  val configuration     = blob("KONFIGURATION")
  val createdOn         = datetime("ERSTELLTAM")
  val createdBy         = integer("ERSTELLTVON")
  val changedOn         = datetime("GEAENDERTAM")
  val changedBy         = integer("GEAENDERTVON")

  override val primaryKey = PrimaryKey(id)

  init {
    uniqueIndex("REPORTCONFIGURATIONS0", report, name)
  }
}
