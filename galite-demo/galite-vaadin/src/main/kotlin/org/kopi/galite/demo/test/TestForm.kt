package org.kopi.galite.demo.test


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

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Command
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Key
import java.util.Locale
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.form.VBlockDefaultOuterJoin
import kotlin.test.assertEquals


class TestForm : DictionaryForm() {
    override val locale = Locale.UK
    override val title = "Test"
    val page = page("Initial page")
    val action = menu("Action")

    val list = actor(
        ident = "list",
        menu = action,
        label = "list",
        help = "Display List",
    ) {
        key = Key.F1   // key is optional here
        icon = "list"  // icon is optional here
    }

    val resetBlock = actor(
        ident = "reset",
        menu = action,
        label = "break",
        help = "Reset Block",
    ) {
        key = Key.F3   // key is optional here
        icon = "break"  // icon is optional here
    }


    val tb1: FormBlock = page.insertBlock(TestCommand()) {
        val that = this
        command(item = list) {
            action = {
                println("-----------Generating list-----------------")

                val table = VBlockDefaultOuterJoin.getSearchTables(that.vBlock)
                val columns = that.vBlock.getSearchColumns()
                val query = table!!.slice(columns).selectAll()
                transaction {


                println(query.prepareSQL(this))
                }
                recursiveQuery()
            }
        }

    }

}

class TestCommand : FormBlock(1, 10, "TestBlock") {
    val v = table(Client)
    val u = table(Command)


    val aux  = visit(domain = STRING(25) , at(1,1)){
        label = "testing"
        columns(v.firstNameClt){
            priority = 3
        }
    }
    val simpleInnerJoin = visit(domain = INT(25), at(2,1)) {
        label = "Simple inner join"
        columns(v.idClt, u.idClt) {
            priority = 1
        }
    }

}


fun main() {
    runForm(formName = TestForm())
}
