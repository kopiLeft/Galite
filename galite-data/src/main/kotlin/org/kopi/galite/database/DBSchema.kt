/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.database

import org.jetbrains.exposed.sql.Sequence
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

// ----------------------------------------------------------------------
// VERSIONEN
// ----------------------------------------------------------------------
object Versions : Table("VERSIONEN") {
  val packageName       = varchar("PAKET", 32)
  val number            = integer("NUMMER")
  val date              = datetime("DATUM")

  override val primaryKey = PrimaryKey(packageName, number)
}

object Modules : Table("MODULE") {
  val id                = integer("ID").autoIncrement("MODULEID")
  val uc                = integer("UC")
  val ts                = integer("TS")
  val shortName         = varchar("KURZNAME", 25).uniqueIndex()
  val parent            = integer("VATER")
  val sourceName        = varchar("QUELLE", 255)
  val priority          = integer("PRIORITAET")
  val objectName        = varchar("OBJEKT", 255).nullable()
  val symbol            = integer("SYMBOL").nullable()

  override val primaryKey = PrimaryKey(id, name = "PK_MODULE_ID")
}

object UserRights : Table("BENUTZERRECHTE") {
  val id                = integer("ID").autoIncrement("BENUTZERRECHTEID")
  val ts                = integer("TS")
  val user              = integer("BENUTZER")
  val module            = integer("MODUL")
  val access            = bool("ZUGRIFF")

  init {
    uniqueIndex("BENUTZERRECHTE0", user, module)
  }

  override val primaryKey = PrimaryKey(id, name = "PK_BENUTZERRECHTE_ID")
}

object GroupRights : Table("GRUPPENRECHTE") {
  val id                = integer("ID").autoIncrement("GRUPPENRECHTEID")
  val ts                = integer("TS")
  val group             = integer("GRUPPE")
  val module            = integer("MODUL")
  val access            = bool("ZUGRIFF")

  init {
    uniqueIndex("GRUPPENRECHTE0", group, module)
  }

  override val primaryKey = PrimaryKey(id, name = "PK_GRUPPENRECHTE_ID")
}

object GroupParties : Table("GRUPPENZUGEHOERIGKEITEN") {
  val id                = integer("ID").autoIncrement("GRUPPENZUGEHOERIGKEITENID")
  val ts                = integer("TS")
  val user              = integer("BENUTZER")
  val group             = integer("GRUPPE")

  init {
    uniqueIndex("GRUPPENZUGEHOERIGKEITEN0", user, group)
  }

  override val primaryKey = PrimaryKey(id, name = "PK_GRUPPENZUGEHOERIGKEITEN_ID")
}

object Symbols : Table("SYMBOLE") {
  val id                = integer("ID").autoIncrement("SYMBOLEID")
  val ts                = integer("TS")
  val shortName         = varchar("KURZNAME", 20).uniqueIndex("SYMBOLE0").nullable()
  val description       = varchar("BEZEICHNUNG", 50).uniqueIndex("SYMBOLE1").nullable()
  val objectName        = varchar("OBJEKT", 50)

  override val primaryKey = PrimaryKey(id, name = "PK_SYMBOLE_ID")
}

object Favorites : Table("FAVORITEN") {
  val id                = integer("ID").autoIncrement("FAVORITENID")
  val ts                = integer("TS")
  val user              = integer("BENUTZER")
  val module            = integer("MODUL")

  override val primaryKey = PrimaryKey(id, name = "PK_FAVORITEN_ID")
}

object Users : Table("KOPI_USERS") {
  val id                = integer("ID").autoIncrement("KOPI_USERSID")
  val uc                = integer("UC")
  val ts                = integer("TS")
  val shortName         = varchar("KURZNAME", 10).uniqueIndex("KOPI_USERS0")
  val name              = varchar("NAME", 50).uniqueIndex("KOPI_USERS1")
  val character         = varchar("ZEICHEN", 10).uniqueIndex("KOPI_USERS2")
  val phone             = varchar("TELEFON", 20).nullable()
  val email             = varchar("EMAIL", 40).nullable()
  val active            = bool("AKTIV")
  val createdOn         = datetime("ERSTELLTAM")
  val createdBy         = integer("ERSTELLTVON")
  val changedOn         = datetime("GEAENDERTAM")
  val changedBy         = integer("GEAENDERTVON")

  override val primaryKey = PrimaryKey(id, name = "PK_KOPI_USERS_ID")
}

object Groups : Table("GRUPPEN") {
  val id                = integer("ID").autoIncrement("GRUPPENID")
  val ts                = integer("TS")
  val shortName         = varchar("KURZNAME", 10).uniqueIndex("GRUPPEN0")
  val description       = varchar("BEZEICHNUNG", 40).uniqueIndex("GRUPPEN1")

  override val primaryKey = PrimaryKey(id, name = "PK_GRUPPEN_ID")
}

object References : Table("REFERENZEN") {
  val table             = varchar("TABELLE", 255)
  val column            = varchar("SPALTE", 255)
  val reference         = varchar("REFERENZ", 255)
  val action            = char("AKTION", 1)

  override val primaryKey = PrimaryKey(table, column)
}

object Dummy : Table("DUMMY") {
  val table             = char("dummy", 1)
}

object Dual : Table("DUAL") {
  val table             = char("dummy", 1)
}

object ReportConfigurations: Table("REPORTCONFIGURATIONS") {
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

// Sequences
val ModulesId                   = Sequence("MODULEID")
val UserRightsId                = Sequence("BENUTZERRECHTEID")
val GroupRightsId               = Sequence("GRUPPENRECHTEID")
val GroupPartiesId              = Sequence("GRUPPENZUGEHOERIGKEITENID")
val SymbolsId                   = Sequence("SYMBOLEID")
val FavoritesId                 = Sequence("FAVORITENID")
val UsersId                     = Sequence("KOPI_USERSID")
val GroupsId                    = Sequence("GRUPPENID")
val ReportConfigurationsId      = Sequence("REPORTCONFIGURATIONSID")

val list_Of_Tables = listOf(
  Versions, Modules, UserRights, GroupRights, GroupParties, Symbols,
  Favorites, Users, Groups, References, Dummy, ReportConfigurations
)

val sequencesList = listOf(ModulesId, UserRightsId, GroupRightsId, GroupPartiesId, SymbolsId,
                           FavoritesId, UsersId, GroupsId, ReportConfigurationsId)
