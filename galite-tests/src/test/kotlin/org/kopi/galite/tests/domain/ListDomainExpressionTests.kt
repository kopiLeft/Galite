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
package org.kopi.galite.tests.domain

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.mod
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.rem
import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.tests.form.User
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key

class ListDomainExpressionTest() : Form(title = "List domain test", locale = Locale.UK) {

  val action = menu("Action")

  val edit = menu("Edit")

  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val formActor = actor(
    menu =   action,
    label =  "form Command",
    help =   "actor to test form command",
  ) {
    key  =  Key.F2
    icon =  Icon.SAVE
  }

  val cmd = command(item = formActor) {
    println("----------- FORM COMMAND ----------------")
  }

 val blockListDomainExpressionTest = insertBlock(ListDomainExpression())
}

class ListDomainExpression : Block("Test block", 1, 1) {
  val u = table(User)
  val listNames = visit(domain = Names, position = at(1, 1)) {
    label = "list names"
    columns(u.name) {
      priority = 1
    }
  }

  val listAges = visit(domain = AgesUsers, position = at(2, 1)) {
    label = "list ages"
    columns(u.age) {
      priority = 2
    }
  }

  val job = visit(domain = Jobs, position = at(3, 1)) {
    label = "list jobs"
    columns(u.job) {
      priority = 3
    }
  }
}

object Names : ListDomain<String>(30) {
  override val table =  User

  init {
    "name"          keyOf User.name
    "uc/ts"         keyOf User.uc.plus(User.ts)
    "id"            keyOf User.id.rem(2)
  }
}

object AgesUsers : ListDomain<Int>(3) {
  override val table =  query(User.slice(User.age.minus(1).alias("age"),
                                         User.name,
                                         User.id.castTo<String>(VarCharColumnType()).alias("id"))
                                   .selectAll())

  init {
    "age"   keyOf User.age
    "name"  keyOf User.name
    "id"    keyOf User.id
  }
}

object Jobs : ListDomain<String>(20) {
  override val table =  User.alias("newUser")

  init {
    "job"   keyOf table[User.job]
    "cv"    keyOf table[User.cv].countDistinct()
    "id"    keyOf table[User.id].mod(5)
  }
}

fun main() {
  runForm(formName = ListDomainExpressionTest())
}
