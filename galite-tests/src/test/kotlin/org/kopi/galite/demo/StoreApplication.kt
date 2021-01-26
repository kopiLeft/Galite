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
package org.kopi.galite.demo

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.jodatime.datetime

import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.produit.ProduitForm
import org.kopi.galite.tests.db.DBSchemaTest
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

object Client : Table("CLIENTS") {
  val idClt = integer("ID CLIENT").autoIncrement()
  val nomClt = varchar("NOM", 25).uniqueIndex()
  val prenomClt = varchar("PRENOM", 25)
  val adresseClt = varchar("ADRESSE", 255)
  val ageClt = integer("AGE")
  val villeClt = varchar("VILLE", 255)
  val codePostalClt = integer("CODEPOSTAL")

  override val primaryKey = PrimaryKey(idClt, name = "PK_CLIENT_ID")
}

object Produit : Table("PRODUITS") {
  val idPdt = integer("ID PRODUIT").autoIncrement()
  val designation = varchar("DESIGNATION", 100)
  val categorie = varchar("CATEGORIE", 30)
  val nomTaxe = varchar("REGLETAXEPDT", 100).references(RegleTaxe.nomTaxe)
  val prixUHT = integer("PRIX UHT")
  val photo = blob("PHOTO").nullable()

  override val primaryKey = PrimaryKey(idPdt)
}

object Stock : Table("STOCK") {
  val idStckPdt = integer("IDSTXKPDT").references(Produit.idPdt)
  val idStckFourn = integer("IDSTXKFOURN").references(Fournisseur.idFourn)
  val minAlerte = integer("MINALERTE")

  override val primaryKey = PrimaryKey(idStckPdt, idStckFourn)
}

object Fournisseur : Table("FOURNISSEURS") {
  val idFourn = integer("IDFOURN").autoIncrement()
  val nomFourn = varchar("NOMFOURNISSEUR", 50)
  val tel = integer("TELEPHONE")
  val description = varchar("DESCRIPTION", 255).nullable()
  val adresse = varchar("ADRESSE", 70)
  val codePostal = integer("CODEPOSTAL")
  val logo = blob("LOGO").nullable()

  override val primaryKey = PrimaryKey(idFourn)
}

object FactureProduit : Table("FACTUREPRODUIT") {
  val idFPdt = integer("ID_FACTURE_PDT").references(Produit.idPdt)
  val quantite = integer("QUANTITE")

  override val primaryKey = PrimaryKey(idFPdt)
}

object Commande : Table("COMMANDES") {
  val numCmd = integer("NUM_CMD").autoIncrement()
  val idClt = integer("ID_CLT_CMD").references(Client.idClt)
  val dateCmd = varchar("DATE_COMMANDE", 25)
  val numFact = integer("NUM_FACT_CMD").references(Facture.numFact)
  val moyenPaiement = varchar("MOYEN_PAIEMENT", 50)
  val etatCmd = varchar("ETAT_CMD", 30)

  override val primaryKey = PrimaryKey(numCmd)

}

object Facture : Table("FACTURES") {
  val numFact = integer("NUM_FACT")
  val adresseFact = varchar("ADRESSE_FACTURATION", 30)
  val dateFact = date("DATE_FACTURATION")
  val montant = integer("MONTANT_TOTAL_A_PAYER")
  val refCmd = integer("REFERENCE_COMMANDE").references(Commande.numCmd)

  override val primaryKey = PrimaryKey(numFact)
}

object RegleTaxe : Table("REGLETAXE") {
  val idTaxe = integer("ID_TAXE").autoIncrement()
  val nomTaxe = varchar("REGLE_TAXE", 100)
  val taux = integer("TAUX_TAXE_EN_%")

  override val primaryKey = PrimaryKey(idTaxe)
}

object Paiement : Table("PAIEMENT") {
  val idTransaction = integer("ID_TRANSACTION")
  val moyenDePaiement = varchar("MOYEN_DE_PAIEMENT", 30)
  val montant = integer("MONTANT_A_PAYER")
  val datePaiement = date("DATE_PAIEMENT")
  val heurePaiement = datetime("HEURE_PAIEMENT").defaultExpression(CurrentDateTime())

  override val primaryKey = PrimaryKey(idTransaction)
}

@SpringBootApplication
open class StoreApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
  connectToDatabase()
  createStoreTables()
  DBSchemaTest.reset()
  initDatabase()
  initModules()
  addClients()
  addProducts()
  addFourns()
  addRegleTaxe()
  addStock()
  addCmds()
  addFactures()
  addFacturePrdt()
  runApplication<StoreApplication>(*args)
}

/**
 * Initialises the database with creating the necessary tables and creates users.
 */
fun initDatabase(user: String = DBSchemaTest.connectedUser) {
  transaction {
    createStoreTables()
  }
}

fun connectToDatabase(url: String = DBSchemaTest.testURL,
                      driver: String = DBSchemaTest.testDriver,
                      user: String = DBSchemaTest.testUser,
                      password: String = DBSchemaTest.testPassword) {
  Database.connect(url, driver = driver, user = user, password = password)
  DBSchemaTest.connectedUser = user
}

/**
 * Creates DBSchema tables
 */
fun createStoreTables() {
  list_Of_StoreTables.forEach { table ->
    SchemaUtils.create(table)
  }
}

val list_Of_StoreTables = listOf(Client, Produit, Stock, Fournisseur, Paiement,
                                 Facture, RegleTaxe, Commande, FactureProduit)

fun initModules() {
  transaction {
    DBSchemaTest.insertIntoModule("2000", "org/kopi/galite/test/Menu", 10)
    DBSchemaTest.insertIntoModule("1000", "org/kopi/galite/test/Menu", 10, "2000")
    DBSchemaTest.insertIntoModule("2009", "org/kopi/galite/test/Menu", 90, "1000", ClientForm::class)
    DBSchemaTest.insertIntoModule("2010", "org/kopi/galite/test/Menu", 90, "1000", ProduitForm::class)
  }
}
fun initUserRights(user: String = DBSchemaTest.connectedUser) {
  transaction {
    DBSchemaTest.insertIntoUserRights(user, "2000", true)
    DBSchemaTest.insertIntoUserRights(user, "1000", true)
    DBSchemaTest.insertIntoUserRights(user, "2009", true)
    DBSchemaTest.insertIntoUserRights(user, "2010", true)
  }
}

fun addClients() {
  transaction {
    Client.insert {
      it[idClt] = 0
      it[nomClt] = "Salah"
      it[prenomClt] = "Mohamed"
      it[adresseClt] = "10,Rue du Lac"
      it[villeClt] = "Megrine"
      it[codePostalClt] = 2001
      it[ageClt] = 40
    }
    Client.insert {
      it[idClt] = 1
      it[nomClt] = "Guesmi"
      it[prenomClt] = "Khaled"
      it[adresseClt] = "14,Rue Mongi Slim"
      it[villeClt] = "Tunis"
      it[codePostalClt] = 6000
      it[ageClt] = 35
    }
    Client.insert {
      it[idClt] = 2
      it[nomClt] = "Bouaroua"
      it[prenomClt] = "Ahmed"
      it[adresseClt] = "10,Rue du Lac"
      it[villeClt] = "Mourouj"
      it[codePostalClt] = 5003
      it[ageClt] = 35
    }
  }
}

fun addProducts() {
  transaction {
    Produit.insert {
      it[idPdt] = 0
      it[designation] = "designation Produit 0"
      it[categorie] = "categorie 0"
      it[nomTaxe] = "Regle Taxe 0"
      it[prixUHT] = 263
    }
    Produit.insert {
      it[idPdt] = 1
      it[designation] = "designation Produit 1"
      it[categorie] = "categorie 2"
      it[nomTaxe] = "Regle Taxe 1"
      it[prixUHT] = 314
    }
    Produit.insert {
      it[idPdt] = 2
      it[designation] = "designation Produit 2"
      it[categorie] = "categorie 2"
      it[nomTaxe] = "Regle Taxe 0"
      it[prixUHT] = 180
    }
    Produit.insert {
      it[idPdt] = 3
      it[designation] = "designation Produit 3"
      it[categorie] = "categorie 3"
      it[nomTaxe] = "Regle Taxe 2"
      it[prixUHT] = 65
    }
  }
}

fun addFourns() {
  transaction {
    Fournisseur.insert {
      it[idFourn] = 0
      it[nomFourn] = "Radhia Jouini"
      it[tel] = 21203506
      it[description] = " description du fournisseur ayant l'id 0 "
      it[codePostal] = 2000
    }
    Fournisseur.insert {
      it[idFourn] = 1
      it[nomFourn] = "Sarra Boubaker"
      it[tel] = 99806234
      it[description] = " description du fournisseur ayant l'id 1 "
      it[codePostal] = 3005
    }
    Fournisseur.insert {
      it[idFourn] = 2
      it[nomFourn] = "Hamida Zaoueche"
      it[tel] = 55896321
      it[description] = " description du fournisseur ayant l'id 2 "
      it[codePostal] = 6008
    }

  }
}

fun addRegleTaxe() {
  transaction {
    RegleTaxe.insert {
      it[idTaxe] = 0
      it[nomTaxe] = "Regle Taxe 0"
      it[taux] = 19
    }
    RegleTaxe.insert {
      it[idTaxe] = 1
      it[nomTaxe] = "Regle Taxe 1"
      it[taux] = 22
    }
    RegleTaxe.insert {
      it[idTaxe] = 2
      it[nomTaxe] = "Regle Taxe 2"
      it[taux] = 13
    }
    RegleTaxe.insert {
      it[idTaxe] = 3
      it[nomTaxe] = "Regle Taxe 3"
      it[taux] = 9
    }
    RegleTaxe.insert {
      it[idTaxe] = 4
      it[nomTaxe] = "Regle Taxe 4"
      it[taux] = 20
    }
  }
}

fun addFactures() {
  transaction {
    Facture.insert {
      it[numFact] = 0
      it[adresseFact] = "adresse facture 0"
      it[dateFact] = DateTime()
      it[montant] = 453
      it[refCmd] = 0
    }
    Facture.insert {
      it[numFact] = 1
      it[adresseFact] = "adresse facture 1"
      it[dateFact] = DateTime()
      it[montant] = 600
      it[refCmd] = 1
    }
    Facture.insert {
      it[numFact] = 2
      it[adresseFact] = "adresse facture 2"
      it[dateFact] = DateTime()
      it[montant] = 870
      it[refCmd] = 2
    }
    Facture.insert {
      it[numFact] = 3
      it[adresseFact] = "adresse facture 3"
      it[dateFact] = DateTime()
      it[montant] = 999
      it[refCmd] = 3
    }
  }
}

fun addStock() {
  transaction {
    Stock.insert {
      it[idStckPdt] = 0
      it[idStckFourn] = 0
      it[minAlerte] = 50
    }
    Stock.insert {
      it[idStckPdt] = 1
      it[idStckFourn] = 1
      it[minAlerte] = 100
    }
    Stock.insert {
      it[idStckPdt] = 2
      it[idStckFourn] = 2
      it[minAlerte] = 50
    }
    Stock.insert {
      it[idStckPdt] = 3
      it[idStckFourn] = 3
      it[minAlerte] = 20
    }
  }
}

fun addCmds() {
  transaction {
    Commande.insert {
      it[numCmd] = 0
      it[idClt] = 0
      it[dateCmd] = "01/01/2020"
      it[numFact] = 0
      it[moyenPaiement] = "cheque"
      it[etatCmd] = "en_cours"
    }
    Commande.insert {
      it[numCmd] = 1
      it[idClt] = 0
      it[dateCmd] = "01/01/2020"
      it[numFact] = 1
      it[moyenPaiement] = "cheque"
      it[etatCmd] = "en_cours"
    }
    Commande.insert {
      it[numCmd] = 2
      it[idClt] = 1
      it[dateCmd] = "20/01/2021"
      it[numFact] = 1
      it[moyenPaiement] = "carte"
      it[etatCmd] = "en_cours"
    }
    Commande.insert {
      it[numCmd] = 3
      it[idClt] = 2
      it[dateCmd] = "13/05/2020"
      it[numFact] = 2
      it[moyenPaiement] = "espece"
      it[etatCmd] = "en_cours"
    }
  }
}


fun addFacturePrdt() {
  transaction {
    FactureProduit.insert {
      it[idFPdt] = 0
      it[quantite] = 10
    }
    FactureProduit.insert {
      it[idFPdt] = 1
      it[quantite] = 3
    }
    FactureProduit.insert {
      it[idFPdt] = 2
      it[quantite] = 1
    }
    FactureProduit.insert {
      it[idFPdt] = 3
      it[quantite] = 2
    }
  }
}
