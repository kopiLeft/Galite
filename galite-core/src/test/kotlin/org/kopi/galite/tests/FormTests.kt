/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.tests

import com.helger.commons.mock.CommonsAssert.assertEquals
import org.junit.Test
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VIntegerField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormField
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VDefaultActor
import java.util.Date
import java.util.Locale


class FormTests : JApplicationTestBase() {

  val DateDebut = VDateField()
  val DateFin = VDateField()
  val France = VStringField(5, 1, 0, 0, false)
  val CEE = VStringField(50, 1, 0, 0, false)
  val Monde = VStringField(500, 1, 0, 0, false)
  val TestBool = VBooleanField()
  val TestVISIT: VIntegerField = VIntegerField(12, -0x80000000, 0x7fffffff)
  val TestSKIPPED = VStringField(50, 1, 0, 0, false)
  val TestHIDDEN = VStringField(50, 1, 0, 0, false)
  inner class VKT_BLOCK_Selection

  @Test
  fun formTest() {
    val testForm = TestForm.model
    val Selection = VKT_BLOCK_Selection()
    val S  = Selection

    assertEquals(testForm.actors, VDefaultActor(-5, "Edit", "common/FormDefault", "Autofill", "common/FormDefault", null, 113, 0))
    assertEquals(testForm.commands, arrayOfNulls<VCommand>(0))
    assertEquals(testForm.source, "FormTestCase1")
    assertEquals(testForm.pages, arrayOfNulls(0))
    assertEquals(testForm.blocks, arrayOf<VBlock>(S as VBlock))
    assertEquals(testForm.VKT_Triggers, Array(1) { IntArray(36) })
  }

  @Test
  fun selectionBlockTest() {
    val block = TestForm.model.getBlock("Selection")
    assertEquals(block!!.source, "FormTestCase1")
    assertEquals(block.name, "Selection")
    assertEquals(block.shortcut, "S")
    assertEquals(block.bufferSize, 1)
    assertEquals(block.displaySize, 1)
    assertEquals(block.pageNumber, 0)
    assertEquals(block.options, 0)
    assertEquals(block.border, 0)
    assertEquals(block.maxRowPos, 8)
    assertEquals(block.maxColumnPos, 2)
    assertEquals(block.displayedFields, 0)
    assertEquals(block.commands, arrayOfNulls<VCommand>(0))
    assertEquals(block.VKT_Triggers, Array(10) { IntArray(36) })
    assertEquals(block.access, intArrayOf(4, 4, 4))
    assertEquals(block.fields, arrayOf<VField>(
            DateDebut, DateFin, France, CEE, Monde, TestBool, TestVISIT, TestSKIPPED, TestHIDDEN
    ))
  }

  @Test
  fun fieldsTest() {
    val modelFieldList: List<VField> = TestForm.model.getBlock("Selection")!!.fields.toList()
    val dslFieldList: List<FormField<*>> = TestForm.formBlocks[0].blockFields.toList()

    with(modelFieldList) {
      val DateDebutField = find { it.name == "DateDebut" } as VDateField
      val DateFinField = find { it.name == "DateFin" } as VDateField
      val FranceField = find { it.name == "France" } as VStringField
      val CEEField = find { it.name == "Cee" } as VStringField
      val MondeField = find { it.name == "Monde" } as VStringField
      val TestBoolField = find { it.name == "TestBool" } as VBooleanField
      val TestVISITField = find { it.name == "TestVISIT" } as VIntegerField
      val TestSKIPPEDField = find { it.name == "TestSKIPPED" } as VStringField
      val TestHIDDENField = find { it.name == "TestHIDDEN" } as VStringField

      assertEquals(DateDebutField, DateDebut)
      assertEquals(DateFinField, DateFin)
      assertEquals(FranceField, France)
      assertEquals(CEEField, CEE)
      assertEquals(MondeField, Monde)
      assertEquals(TestBoolField, TestBool)
      assertEquals(TestVISITField, TestVISIT)
      assertEquals(TestSKIPPEDField, TestSKIPPED)
      assertEquals(TestHIDDENField, TestHIDDEN)
    }
  }
}

object TestForm : Form() {
  override val locale: Locale = Locale.FRANCE
  override val title = "Déclaration d'échange de biens"

  init {
    val testBlock = block(1, 1, "Selection", "Sélection", "S") {

      val DateDebut = visit(domain = Domain<Date>(),
                            position = at(1, 1)) {
        ident = "DateDebut"
        label = "Depuis le"
        help = "Date minimale de saisie des commandes."
      }

      val DateFin = visit(Domain<Date>(), at(1, 2)) {
        ident = "DateFin"
        label = "Jusqu'au"
        help = "Date maximale de saisie des commandes."
      }

      val France = mustFill(Domain<String>(5), at(3, 1)) {
        ident = "France"
        //Label should be initialized to "FRANCE"
        // label = "France"
        help = "Prendre en compte les commandes en France."
      }

      val Cee = mustFill(Domain<String>(50), at(4, 1)) {
        ident = "Cee"
        //label = "Cee"
        help = "Prendre en compte les commandes en CEE."
      }

      val Monde = mustFill(Domain<String>(500), at(5, 1)) {
        ident = "Monde"
        //label = "Monde"
        help = "Prendre en compte les commandes Monde."
      }

      val TestBool = mustFill(Domain<Boolean>(), at(6, 1)) {
        ident = "TestBool"
        //label = ""
        help = "Test pour BOOL type"
      }

      val TestVISIT = visit(Domain<Int>(12), at(7, 1)) {
        ident = "TestVISIT"
        //label = ""
        help = "Test pour Visit"
      }

      val TestSKIPPED = skipped(Domain<String>(50), at(8, 1)) {
        ident = "TestSKIPPED"
        //label = ""
        help = "Test pour hidden"
      }

      val TestHIDDEN = hidden(Domain<String>(50)){
        ident = "TestHIDDEN"
        // shouldn't exept labels
        help = "Test pour hidden"
      }
    }
  }
}
