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

package org.kopi.galite.db

import org.jetbrains.exposed.sql.Table

class DBSchema {

  object Modules : Table() {
    val id = integer("id").autoIncrement()
    val uc = integer("uc")
    val ts = integer("ts")
    val shortName = varchar("shortName", 25).uniqueIndex()
    val father = integer("father")
    val sourceName = varchar("sourceName", 255)
    val priority = integer("priority")
    val objectName = varchar("objectName", 255)
    val symbol = integer("symbol").nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_Module_ID")
  }

  object   UserRights : Table() {
    val id = integer("id").autoIncrement()
    val ts = integer("ts")
    val user = integer("user")
    val module = integer("module")
    val access = bool("access")

    init {
      uniqueIndex("USER_RIGHTS0" ,user, module)
    }
    override val primaryKey = PrimaryKey(id, name = "PK_USER_RIGHTS_ID")
  }

  object   GroupRights : Table() {
    val id = integer("id").autoIncrement()
    val ts = integer("ts")
    val group = integer("group")
    val module = integer("module")
    val access = bool("access")

    init {
      uniqueIndex("GROUP_RIGHTS0" ,group, module)
    }
    override val primaryKey = PrimaryKey(id, name = "PK_GROUP_RIGHTS_ID")
  }

  object   GroupParties : Table() {
    val id = integer("id").autoIncrement()
    val ts = integer("ts")
    val user = integer("user")
    val group = integer("group")

    init {
      uniqueIndex("GROUP_PARTIES0" ,user, group)
    }
    override val primaryKey = PrimaryKey(id, name = "PK_GROUP_PARTIES_ID")
  }

  object   Symbols : Table() {
    val id = integer("id").autoIncrement()
    val ts = integer("ts")
    val shortName = varchar("shortName", 20).uniqueIndex("SYMBOLS0").nullable()
    val description = varchar("description", 50).uniqueIndex("SYMBOLS1").nullable()
    val objectName = varchar("objectName", 50)

    override val primaryKey = PrimaryKey(id, name = "PK_GROUP_PARTIES_ID")
  }

  object   Favorites : Table() {
    val id = integer("id").autoIncrement()
    val ts = integer("ts")
    val user = integer("user")
    val module = integer("module")

    override val primaryKey = PrimaryKey(id, name = "PK_Favorites_ID")
  }

  object   Groups : Table() {
    val id = integer("id").autoIncrement()
    val ts = integer("ts")
    val shortName = varchar("shortName", 10).uniqueIndex("Groups0")
    val description = varchar("description", 40).uniqueIndex("Groups1")
    override val primaryKey = PrimaryKey(id, name = "PK_Groups_ID")
  }
}
