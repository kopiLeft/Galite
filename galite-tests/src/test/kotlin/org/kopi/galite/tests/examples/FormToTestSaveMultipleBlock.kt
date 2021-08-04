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
package org.kopi.galite.tests.examples

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import java.util.*

class FormToTestSaveMultipleBlock : DictionaryForm() {
  override val locale = Locale.UK

  override val title = "Training Form"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )
  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = "list"
  }
  val query = actor(
    ident = "query",
    menu = action,
    label = "query",
    help = "query",
  ) {
    key = Key.F3
    icon = "list"
  }
  val changeBlock = actor(
    ident = "change Block",
    menu = action,
    label = "change Block",
    help = "change Block",
  ) {
    key = Key.F4
    icon = "refresh"
  }
  val resetBlock = actor(
    ident = "reset",
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F5
    icon = "break"
  }
  val saveBlock = actor(
    ident = "saveBlock",
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F9
    icon = "save"
  }

  val block = insertBlock(Trainee()) {
    trigger(POSTQRY) {
      block2.trainingId[0] = trainingID.value
      block2.load()
    }

    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }
    command(item = query) {
      action = {
        queryMove()
      }
    }
    command(item = changeBlock) {
      action = {
        changeBlock()
      }
    }
  }

  val block2 = insertBlock(Centers()) {
    command(item = saveBlock) {
      action = {
        vBlock.setMode(VConstants.MOD_UPDATE)
        val rec: Int = vBlock.activeRecord

        transaction {
          vBlock.save()
        }

        vBlock.form.gotoBlock(vBlock)
        vBlock.gotoRecord(if (vBlock.isRecordFilled(rec)) rec + 1 else rec)
      }
    }
    command(item = resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  class Trainee: FormBlock(1, 10, "Training") {
    val t = table(Training)

    val trainingID = visit(domain = Domain<Int>(25), position = at(1, 1)) {
      label = "training ID"
      help = "training ID"
      columns(t.id) {
        priority = 1
      }
    }
  }

  class Centers : FormBlock(20, 20, "Centers") {
    val c = table(Center)

    val unique = index(message = "ID should be unique")

    val CenterId = visit(domain = Domain<Int>(20), position = at(1, 1)) {
      label = "center id"
      help = "The Center id"
      columns(c.id)
    }
    val trainingId = visit(domain = Domain<Int>(20), position = at(1, 1)) {
      label = "training id"
      help = "The training id"
      columns(c.refTraining)
    }
    val centerName = visit(domain = Domain<String>(20), position = at(1, 1)) {
      label = "center name"
      help = "center name"
      columns(c.centerName)
    }
    val address = visit(domain = Domain<String>(20), position = at(1, 2)) {
      label = "address"
      help = "address"
      columns(c.address)
    }
    val mail = visit(domain = Domain<String>(20), position = at(1, 3)) {
      label = "mail"
      help = "mail"
      columns(c.mail)
    }
    val country = visit(domain = Domain<String>(20), position = at(1, 4)) {
      label = "country"
      help = "country"
      columns(c.country)
    }
    val city = visit(domain = Domain<String>(20), position = at(1, 5)) {
      label = "city"
      help = "city"
      columns(c.city)
    }
    val zipCode = visit(domain = Domain<Int>(5), position = at(1, 6)) {
      label = "zipCode"
      help = "zipCode"
      columns(c.zipCode)
    }

    init {
      border = VConstants.BRD_LINE
    }
  }

  class SimpleBlock : FormBlock(1, 1, "Simple block") {
    val contact = visit(domain = Domain<String>(20), position = at(1, 1)) {
      label = "contact"
      help = "The contact"
    }
    val name = visit(domain = Domain<String>(20), position = follow(contact)) {

    }
  }
  init {
    transaction {
      SchemaUtils.create(Training, Center)
      addTrainings()
      addCenters()
    }
  }
}

fun main() {
  Application.runForm(formName = MutlipeBlockForm())
}
