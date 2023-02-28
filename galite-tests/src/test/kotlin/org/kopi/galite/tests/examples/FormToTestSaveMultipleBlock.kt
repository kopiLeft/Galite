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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.VExecFailedException

class FormToTestSaveMultipleBlock : DictionaryForm(title = "Training Form", locale = Locale.UK) {
  val action = menu("Action")
  val autoFill = actor(
    menu = action,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )
  val saveBlock = actor(
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F9
    icon = Icon.SAVE
  }
  val list = actor(
    menu = action,
    label = "list",
    help = "List data",
  ) {
    key = Key.F5
    icon = Icon.LIST
  }
  val block = insertBlock(Trainee())
  val multipleBlock = insertBlock(Centers())

  inner class Trainee: Block("Training", 1, 1) {
    val t = table(Training)

    val trainingID = visit(domain = INT(25), position = at(1, 1)) {
      label = "training ID"
      help = "training ID"
      columns(t.id) {
        priority = 1
      }
    }

    val uc = hidden(domain = INT(20)) { columns(t.uc) }
    val ts = hidden(domain = INT(20)) { columns(t.ts) }

    val name = skipped(domain = STRING(25), position = at(2, 1)) {
      label = "Name"
    }

    init {
      trigger(POSTQRY) {
        multipleBlock.load()
      }

      command(item = list) {
        transaction {
          recursiveQuery()
        }
      }
    }
  }

  inner class Centers : Block("Centers", 20, 20) {
    val c = table(Center)
    val index0 = index(message = "Index 0")
    val index1 = index(message = "Index 1")

    val centerId = hidden(domain = INT(20)) {
      label = "center id"
      help = "The Center id"
      columns(c.id)
    }
    val ts = hidden(domain = INT(20)) {
      label = "ts"
      value = 0
      columns(c.ts)
    }
    val uc = hidden(domain = INT(20)) {
      label = "uc"
      value = 0
      columns(c.uc)
    }
    val trainingId = visit(domain = INT(20), position = at(1, 1)) {
      label = "training id"
      help = "The training id"
      columns(c.refTraining)
      onInsertHidden()
    }
    val centerName = visit(domain = STRING(20), position = at(1, 1)) {
      label = "center name"
      help = "center name"
      columns(c.centerName) {
        index = index0
      }
      onInsertHidden()
    }
    val address = visit(domain = STRING(20), position = at(1, 2)) {
      label = "address"
      help = "address"
      columns(c.address) {
        index = index0 + index1
      }
      onInsertHidden()
    }
    val mail = visit(domain = STRING(20), position = at(1, 3)) {
      label = "mail"
      help = "mail"
      columns(c.mail) {
        index = index1
      }
      onInsertHidden()
    }

    init {
      border = Border.LINE

      command(item = saveBlock) {
        val b = this
        val rec: Int = b.activeRecord

        b.validate()

        if (!b.isFilled()) {
          b.currentRecord = 0
          throw VExecFailedException()
        }

        transaction {
          b.save()
        }

        b.form.gotoBlock(b)
        b.gotoRecord(if (b.isRecordFilled(rec)) rec + 1 else rec)
      }
    }
  }
}

fun main() {
  runForm(formName = FormToTestSaveMultipleBlock())
}
