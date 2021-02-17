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
package org.kopi.galite.tests.form

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.form.VBlockDefaultOuterJoin
import org.kopi.galite.tests.JApplicationTestBase

class FormWithNullableColumnsTest : JApplicationTestBase() {

  @Test
  fun innerJoinTwoTablesTest() {
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockWithTwoTablesInnerJoin

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT CLIENTS.ID, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                           " \"ORDER\".QUANTITY FROM CLIENTS INNER JOIN \"ORDER\" ON CLIENTS.ID = \"ORDER\".CLIENT_ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun leftJoinTwoTablesTest() {
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockWithTwoTablesLeftJoin

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".CLIENT_ID, CLIENTS.\"NAME\", CLIENTS.MAIL, \"ORDER\".QUANTITY FROM CLIENTS" +
                           " LEFT JOIN \"ORDER\" ON CLIENTS.ID = \"ORDER\".CLIENT_ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun innerJoinThreeTablesTest() {
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockWithThreeTablesInnerJoin

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, PRODUCTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\"," +
                           " CLIENTS.MAIL, \"ORDER\".QUANTITY, PRODUCTS.DESIGNATION FROM \"ORDER\" INNER JOIN " +
                           "CLIENTS ON \"ORDER\".CLIENT_ID = CLIENTS.ID INNER JOIN" +
                           " PRODUCTS ON \"ORDER\".PRODUCT_ID = PRODUCTS.ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun leftJoinThreeTablesTest() {
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockWithThreeTablesLeftJoin

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    transaction {
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, PRODUCTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\"," +
                           " CLIENTS.MAIL, \"ORDER\".QUANTITY, PRODUCTS.DESIGNATION FROM \"ORDER\" LEFT JOIN " +
                           "CLIENTS ON \"ORDER\".CLIENT_ID = CLIENTS.ID LEFT JOIN" +
                           " PRODUCTS ON \"ORDER\".PRODUCT_ID = PRODUCTS.ID",
                   query.prepareSQL(this))
    }
  }

  @Test
  fun innerJoinThreeTablesInOneFieldTest() {
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockWithThreeTablesInnerJoinInOneField

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
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
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockWithThreeTablesLeftJoinInOneField

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
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
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockThreeTablesMiddleLeftJoinInOneField

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    //BLOCK31
    /*SELECT T0.ID, T2.ID, T2.Name, T2.mail, T0.quantity, T1.Country, T1.ZIP
 FROM TEST_ORDER T0 LEFT OUTER JOIN TEST_CLIENT T2 ON T0.CLIENT_ID = T2.ID A
 ND T2.ID = T0.CLIENT_ID LEFT OUTER JOIN TEST_ADRESS T1 ON T0.CLIENT_ID = T1.CLIENT_ID
 AND T1.CLIENT_ID = T0.CLIENT_ID WHERE T0.ID = 1 AND T1.CLIENT_ID = T2.ID*/
    transaction {
      println("--------------−−> " +query.prepareSQL(this))
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                           " \"ORDER\".QUANTITY, ADRESS.COUNTRY, ADRESS.ZIP FROM \"ORDER\" LEFT JOIN CLIENTS " +
                           "ON \"ORDER\".CLIENT_ID = CLIENTS.ID LEFT JOIN ADRESS " +
                           "ON \"ORDER\".CLIENT_ID = ADRESS.CLIENT_ID",
                   query.prepareSQL(this))

    }
  }

  @Test
  fun leftJoiInEndThreeTablesInOneFieldTest() {
    FormWithNullableColumn.model
    val block =  FormWithNullableColumn.blockThreeTablesEndLeftJoinInOneField

    block.form
    block.form.model
    block.name.value = "client1"

    val table = VBlockDefaultOuterJoin.getSearchTables(block.vBlock)
    val columns = block.vBlock.getSearchColumns()
    val query = table!!.slice(columns).selectAll()

    //BLOCK41
    /*  SELECT T0.ID, T1.ID, T1.Name, T1.mail, T0.quantity, T2.Country, T2.ZIP
    FROM TEST_ORDER T0 LEFT OUTER JOIN TEST_CLIENT T1 ON T0.CLIENT_ID = T1.ID
    AND T1.ID = T0.CLIENT_ID LEFT OUTER JOIN TEST_ADRESS T2 ON T0.CLIENT_ID = T2.CLIENT_ID
    AND T2.CLIENT_ID = T0.CLIENT_ID WHERE T0.ID = 1 AND T2.CLIENT_ID = T1.ID*/

    transaction {
      println("--------------−−> " +query.prepareSQL(this))
      assertEquals("SELECT \"ORDER\".ID, CLIENTS.ID, CLIENTS.TS, CLIENTS.UC, CLIENTS.\"NAME\", CLIENTS.MAIL," +
                           " \"ORDER\".QUANTITY, ADRESS.COUNTRY, ADRESS.ZIP FROM \"ORDER\" LEFT JOIN CLIENTS " +
                           "ON \"ORDER\".CLIENT_ID = CLIENTS.ID LEFT JOIN ADRESS " +
                           "ON \"ORDER\".CLIENT_ID = ADRESS.CLIENT_ID",
                   query.prepareSQL(this))

    }
  }
}
