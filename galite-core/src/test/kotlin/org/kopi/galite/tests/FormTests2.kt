/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY: without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library: if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.kopi.galite.tests

import com.helger.commons.mock.CommonsAssert.assertEquals
import org.junit.Test
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormField
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VDefaultActor
import java.util.Date
import java.util.Locale


class FormTests2 : JApplicationTestBase() {

  val DateDebut = VDateField()
  val DateFin = VDateField()
  val France = VStringField(5, 1, 0, 0, false)
  val CEE = VStringField(50, 1, 0, 0, false)
  val Monde = VStringField(500, 1, 0, 0, false)
  val TestBool = VBooleanField()

  inner class VKT_BLOCK_Selection
  inner class VKT_BLOCK_Second
  inner class VKT_BLOCK_Third

  @Test
  fun formTest() {
    val testForm = TestForm.model
    val Selection = VKT_BLOCK_Selection()
    val Second = VKT_BLOCK_Second()
    val Third = VKT_BLOCK_Third()
    val S = Selection
    val D = Second
    val T = Third

    assertEquals(testForm.actors, VDefaultActor(-5, "Edit", "com/kopiright/apps/common/FormDefault", "Autofill", "com/kopiright/apps/common/FormDefault", null, 113, 0))
    assertEquals(testForm.commands, arrayOfNulls<VCommand>(0))
    assertEquals(testForm.source, "FormTestCase2")
    assertEquals(testForm.pages, arrayOfNulls(0))
    assertEquals(testForm.blocks, arrayOf<VBlock>(S as VBlock, D as VBlock, T as VBlock))
    assertEquals(testForm.VKT_Triggers, Array(1) { IntArray(36) })
  }

  @Test
  fun selectionBlockTest() {
    val block = TestForm.model.getBlock("Selection")

    assertEquals(block!!.source, "FormTestCase2")
    assertEquals(block.name, "Selection")
    assertEquals(block.shortcut, "S")
    assertEquals(block.bufferSize, 1)
    assertEquals(block.displaySize, 10)
    assertEquals(block.pageNumber, 0)
    assertEquals(block.options, 0)
    assertEquals(block.border, 0)
    assertEquals(block.maxRowPos, 3)
    assertEquals(block.maxColumnPos, 3)
    assertEquals(block.displayedFields, 0)
    assertEquals(block.commands, arrayOfNulls<VCommand>(0))
    assertEquals(block.VKT_Triggers, Array(2) { IntArray(36) })
    assertEquals(block.access, intArrayOf(4, 4, 4))
    assertEquals(block.fields, arrayOf<VField>(DateDebut))
  }

  @Test
  fun secondBlockTest() {
    val block = TestForm.model.getBlock("Second")

    assertEquals(block!!.source, "FormTestCase2")
    assertEquals(block.name, "Second")
    assertEquals(block.shortcut, "D")
    assertEquals(block.bufferSize, 10)
    assertEquals(block.displaySize, 1)
    assertEquals(block.pageNumber, 0)
    assertEquals(block.options, 0)
    assertEquals(block.border, 0)
    assertEquals(block.maxRowPos, 6)
    assertEquals(block.maxColumnPos, 2)
    assertEquals(block.displayedFields, 2)
    assertEquals(block.commands, arrayOfNulls<VCommand>(0))
    assertEquals(block.VKT_Triggers, Array(3) { IntArray(36) })
    assertEquals(block.access, intArrayOf(4, 4, 4))
    assertEquals(block.fields, arrayOf<VField>(DateFin, France))
  }

  @Test
  fun thirdBlockTest() {
    val block = TestForm.model.getBlock("Third")

    assertEquals(block!!.source, "FormTestCase2")
    assertEquals(block.name, "Third")
    assertEquals(block.shortcut, "T")
    assertEquals(block.bufferSize, 1)
    assertEquals(block.displaySize, 1)
    assertEquals(block.pageNumber, 0)
    assertEquals(block.options, 0)
    assertEquals(block.border, 0)
    assertEquals(block.maxRowPos, 9)
    assertEquals(block.maxColumnPos, 1)
    assertEquals(block.displayedFields, 0)
    assertEquals(block.commands, arrayOfNulls<VCommand>(0))
    assertEquals(block.VKT_Triggers, Array(4) { IntArray(36) })
    assertEquals(block.access, intArrayOf(4, 4, 4))
    assertEquals(block.fields, arrayOf<VField>(
            CEE, Monde, TestBool
    ))
  }

  @Test
  fun fieldsTest() {
    val selectionFieldList: List<VField> = TestForm.model.getBlock("Selection")!!.fields.toList()
    val secondFieldList: List<VField> = TestForm.model.getBlock("Selection")!!.fields.toList()
    val thirdFieldList: List<VField> = TestForm.model.getBlock("Selection")!!.fields.toList()

    val dslFieldList: List<FormField<*>> = TestForm.formBlocks[0].blockFields.toList()

    with(selectionFieldList) {
      val DateDebutField = find { it.name == "DateDebut" } as VDateField

      assertEquals(DateDebutField, DateDebut)
    }

    with(secondFieldList) {
      val DateFinField = find { it.name == "DateFin" } as VDateField
      val FranceField = find { it.name == "France" } as VStringField

      assertEquals(DateFinField, DateFin)
      assertEquals(FranceField, France)
    }

    with(thirdFieldList) {
      val CEEField = find { it.name == "Cee" } as VStringField
      val MondeField = find { it.name == "Monde" } as VStringField
      val TestBoolField = find { it.name == "TestBool" } as VBooleanField

      assertEquals(CEEField, CEE)
      assertEquals(MondeField, Monde)
      assertEquals(TestBoolField, TestBool)
    }
  }
}

object TestForm2 : Form() {
  override val locale: Locale = Locale.FRANCE
  override val title = "Déclaration d'échange de biens"

  init {
    val testBlock = block(1, 10, "Selection", "Sélection", "S") {
      val DateDebut = visit(domain = Domain<Date>(),
                            position = at(1..3, 1..3)) {
        ident = "DateDebut"
        label = "Depuis le"
        help = "Date minimale de saisie des commandes."
      }
    }

    val testBlock2 = block(10, 1, "Second", "Deuxieme", "D") {
      val DateFin = visit(Domain<Date>(), at(4..5, 1)) {
        ident = "DateFin"
        label = "Jusqu'au"
        TestForm2.help = "Date maximale de saisie des commandes."
      }

      val France = mustFill(Domain<String>(5), at(6, 1..2)) {
        ident = "France"
        //Label should be initialized to "FRANCE"
        // label = "France"
        TestForm2.help = "Prendre en compte les commandes en France."
      }
    }

    val testBlock3 = block(1, 1, "Third", "Troisieme", "T") {
      val Cee = mustFill(Domain<String>(50), at(7, 1)) {
        ident = "Cee"
        //label = "Cee"
        TestForm2.help = "Prendre en compte les commandes en CEE."
      }

      val Monde = mustFill(Domain<String>(500), at(8, 1)) {
        ident = "Monde"
        //label = "Monde"
        TestForm2.help = "Prendre en compte les commandes Monde."
      }

      val TestBool = mustFill(Domain<Boolean>(), at(9, 1)) {
        ident = "TestBool"
        //label = ""
        TestForm2.help = "Test pour BOOL type"
      }
    }
  }
}

