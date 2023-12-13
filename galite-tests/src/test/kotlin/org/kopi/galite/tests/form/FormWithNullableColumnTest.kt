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
package org.kopi.galite.tests.form

import kotlin.test.assertEquals

import org.junit.Test

import org.jetbrains.exposed.sql.selectAll

import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.form.VBlockDefaultOuterJoin

class FormWithNullableColumnsTest : JApplicationTestBase() {

  @Test
  fun innerJoinTwoTablesTest() {
    val block =  FormWithNullableColumn.blockWithTwoTablesInnerJoin

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT CLIENTS.ID, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                           " \"ORDER\".QUANTITY FROM CLIENTS INNER JOIN \"ORDER\" ON CLIENTS.ID = \"ORDER\".CLIENT_ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun leftJoinTwoTablesTest() {
    val block =  FormWithNullableColumn.blockWithTwoTablesLeftJoin

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".CLIENT_ID, CLIENTS.\"NAME\", CLIENTS.MAIL, \"ORDER\".QUANTITY FROM CLIENTS" +
                           " LEFT JOIN \"ORDER\" ON CLIENTS.ID = \"ORDER\".CLIENT_ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun innerJoinThreeTablesTest() {
    val block =  FormWithNullableColumn.blockWithThreeTablesInnerJoin

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, PRODUCTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\"," +
                           " CLIENTS.MAIL, \"ORDER\".QUANTITY, PRODUCTS.DESCRIPTION FROM \"ORDER\" INNER JOIN " +
                           "CLIENTS ON \"ORDER\".CLIENT_ID = CLIENTS.ID INNER JOIN" +
                           " PRODUCTS ON \"ORDER\".PRODUCT_ID = PRODUCTS.ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun leftJoinThreeTablesTest() {
    val block =  FormWithNullableColumn.blockWithThreeTablesLeftJoin

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, PRODUCTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\"," +
                           " CLIENTS.MAIL, \"ORDER\".QUANTITY, PRODUCTS.DESCRIPTION FROM \"ORDER\" LEFT JOIN " +
                           "CLIENTS ON \"ORDER\".CLIENT_ID = CLIENTS.ID LEFT JOIN" +
                           " PRODUCTS ON \"ORDER\".PRODUCT_ID = PRODUCTS.ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun innerJoinThreeTablesInOneFieldTest() {
    val block =  FormWithNullableColumn.blockWithThreeTablesInnerJoinInOneField

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
     assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                          " \"ORDER\".QUANTITY, ADRESS.COUNTRY, ADRESS.ZIP FROM \"ORDER\" INNER JOIN CLIENTS ON" +
                          " \"ORDER\".CLIENT_ID = CLIENTS.ID INNER JOIN ADRESS ON " +
                          "\"ORDER\".CLIENT_ID = ADRESS.CLIENT_ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun leftJoiThreeTablesInOneFieldTest() {
    val block =  FormWithNullableColumn.blockWithThreeTablesLeftJoinInOneField

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
     assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                          " \"ORDER\".QUANTITY, ADRESS.COUNTRY, ADRESS.ZIP FROM \"ORDER\" LEFT JOIN CLIENTS ON " +
                          "\"ORDER\".CLIENT_ID = CLIENTS.ID LEFT JOIN ADRESS ON \"ORDER\".CLIENT_ID = ADRESS.CLIENT_ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun leftJoiInMiddleThreeTablesInOneFieldTest() {
    val block =  FormWithNullableColumn.blockThreeTablesMiddleLeftJoinInOneField

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                           " \"ORDER\".QUANTITY, ADRESS.COUNTRY, ADRESS.ZIP FROM \"ORDER\" LEFT JOIN CLIENTS " +
                           "ON \"ORDER\".CLIENT_ID = CLIENTS.ID LEFT JOIN ADRESS " +
                           "ON \"ORDER\".CLIENT_ID = ADRESS.CLIENT_ID",
                   query.prepareSQL(this))

    }
  }

  @Test
  fun leftJoiInEndThreeTablesInOneFieldTest() {
    val block =  FormWithNullableColumn.blockThreeTablesEndLeftJoinInOneField

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                           " \"ORDER\".QUANTITY, ADRESS.COUNTRY, ADRESS.ZIP FROM \"ORDER\" LEFT JOIN CLIENTS " +
                           "ON \"ORDER\".CLIENT_ID = CLIENTS.ID LEFT JOIN ADRESS " +
                           "ON \"ORDER\".CLIENT_ID = ADRESS.CLIENT_ID",
                   query.prepareSQL(this))

    }
  }

  @Test
  fun leftInnerLeftJoinFourTables() {
    val block =  FormWithNullableColumn.blockFourTablesLeftInnerLeftJoin

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT CLIENTS.ID, ADRESS.ID, PRODUCTS.ID, CLIENTS.\"NAME\", CLIENTS.MAIL," +
              " \"ORDER\".QUANTITY FROM \"ORDER\" LEFT JOIN CLIENTS " +
              "ON \"ORDER\".CLIENT_ID = CLIENTS.ID INNER JOIN ADRESS " +
              "ON \"ORDER\".ADDRESS_ID = ADRESS.ID LEFT JOIN PRODUCTS " +
              "ON \"ORDER\".PRODUCT_ID = PRODUCTS.ID", query.prepareSQL(this) )
    }
  }

  @Test
  fun innerLeftInnerJoinFourTables() {
    val block =  FormWithNullableColumn.blockFourTablesInnerLeftInnerJoin

    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT ADRESS.ID, CLIENTS.ID, \"ORDER\".PRODUCT_ID, CLIENTS.\"NAME\", CLIENTS.MAIL," +
              " \"ORDER\".QUANTITY FROM ADRESS INNER JOIN \"ORDER\" " +
              "ON ADRESS.ID = \"ORDER\".ADDRESS_ID LEFT JOIN CLIENTS " +
              "ON CLIENTS.ID = \"ORDER\".CLIENT_ID INNER JOIN PRODUCTS " +
              "ON PRODUCTS.ID = \"ORDER\".PRODUCT_ID" , query.prepareSQL(this) )
    }
  }

  @Test
  fun innerJoinNoJoinTwoTables() {
    val block =  FormWithNullableColumn.blockTwoTablesInnerJoinNoJoin

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT ADRESS.ID, \"ORDER\".CLIENT_ID " +
              "FROM ADRESS INNER JOIN \"ORDER\" " +
              "ON ADRESS.ID = \"ORDER\".ADDRESS_ID", query.prepareSQL(this) )
    }
  }

  @Test
  fun noJoinInnerJoinTwoTables() {
    val block =  FormWithNullableColumn.blockTwoTablesNoJoinInnerJoin

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".CLIENT_ID, ADRESS.ID "  +
              "FROM ADRESS INNER JOIN \"ORDER\" " +
              "ON ADRESS.ID = \"ORDER\".ADDRESS_ID", query.prepareSQL(this) )
    }
  }

  @Test
  fun leftJoinNoJoinTwoTables() {
    val block =  FormWithNullableColumn.blockTwoTablesLeftJoinNoJoin

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT ADRESS.CLIENT_ID, \"ORDER\".QUANTITY "  +
              "FROM ADRESS INNER JOIN \"ORDER\" " +
              "ON ADRESS.CLIENT_ID = \"ORDER\".CLIENT_ID", query.prepareSQL(this) )
    }
  }

  @Test
  fun noJoinLeftJoinTwoTables() {
    val block =  FormWithNullableColumn.blockTwoTablesNoJoinLeftJoin

    val table = VBlockDefaultOuterJoin.getSearchTables(block.block)
    val columns = block.block.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".QUANTITY, ADRESS.CLIENT_ID "  +
              "FROM ADRESS INNER JOIN \"ORDER\" " +
              "ON ADRESS.CLIENT_ID = \"ORDER\".CLIENT_ID", query.prepareSQL(this) )
    }
  }
}
