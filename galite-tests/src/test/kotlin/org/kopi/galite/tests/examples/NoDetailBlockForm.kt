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

import junit.framework.TestCase.assertEquals
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test

import java.time.LocalDateTime
import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.domain.*

import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DATETIME
import org.kopi.galite.visual.domain.Fixed
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.BlockOption

class NoDetailBlockForm : DictionaryForm(title = "Training", locale = Locale.UK) {
  val autoFill = actor(
    menu = actionMenu,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
                      )

  val block = insertBlock(TrainingNoDetailBlock())


  inner class TrainingNoDetailBlock : Block("Training", 100, 10) {
    init {
      options(BlockOption.NODETAIL)
      // Définition des commandes de block
      breakCommand
      command(item = serialQuery, Mode.QUERY) { serialQuery() }
    }
    val t = table(Training)

    val trainingID = visit(domain = INT(25), position = at(1, 1)) {
      label = "training ID"
      help = "training ID"
      columns(t.id)
    }
    val trainingName = visit(domain = STRING(50), position = at(2, 1)) {
      label = "training Name"
      help = "training Name"
      columns(t.trainingName) {
        priority = 1
      }
    }
    val trainingType = visit(domain = Type, position = follow(trainingName)) {
      label = "training Type"
      help = "training Type"
      columns(t.type) {
        priority = 1
      }
    }
    val active = visit(domain = BOOL, position = at(4, 1)) {
      label = "active?"
      help = "active"
      columns(t.active) {
        priority = 1
      }
    }
    val informations = visit(domain = STRING(80, 50, 10, Fixed.ON), position = at(10, 1)) {
      label = "training informations"
      help = "The training informations"
      columns(t.informations) {
        priority = 1
      }
    }
    val trainDateLocalDateTime = visit(domain = DATETIME, at(12,1)) {
      label = "training DateTime"
      columns(t.trainDateTime)
    }
  }
}

class testLocalDateTimeInNoDetailBlock: GaliteVUITestBase() {
  val nodetailBlockForm = NoDetailBlockForm()

  @Test
  fun `test noDetail record of type LocalDateTime`() {
    transaction {
      initData()
      nodetailBlockForm.block.load()
    }
    for ( rec in 0 until nodetailBlockForm.block.buffer) {
      if (nodetailBlockForm.block.isCurrentRecordFilled() && nodetailBlockForm.block.trainDateLocalDateTime[rec] != null) {
        assertEquals(LocalDateTime::class.simpleName, nodetailBlockForm.block.trainDateLocalDateTime[rec]!!::class.simpleName)
      }
    }
  }
}

fun main() {
  runForm(formName = NoDetailBlockForm())
}
