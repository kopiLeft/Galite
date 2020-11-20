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

class FormTests {

  val DateDebut = VDateField()
  val DateFin = VDateField()
  val France = VStringField(5, 1, 0, 0, false)
  val CEE = VStringField(50, 1, 0, 0, false)
  val Monde = VStringField(500, 1, 0, 0, false)
  val TestBool = VBooleanField()
  val TestVISIT = VStringField(50, 1, 0, 0, false)
  val TestSKIPPED = VStringField(50, 1, 0, 0, false)
  val TestHIDDEN = VStringField(50, 1, 0, 0, false)

  @Test
  fun formTest(){
    val testForm = TestForm.model
    val `$assertionsDisabled` /* synthetic field */ = false
    val VKT_BLOCK_Selection /* synthetic field */: Class<*>? = null

    assertEquals(testForm.actors, VDefaultActor(-5, "Edit", "com/kopiright/apps/common/FormDefault", "Autofill", "com/kopiright/apps/common/FormDefault", null, 113, 0))
    assertEquals(testForm.command, arrayOfNulls<VCommand>(0))
    assertEquals(testForm.source, "FormTestCase1")
    assertEquals(testForm.pages, arrayOfNulls(0))
    assertEquals(testForm.blocks, arrayOf<VBlock>(VKT_BLOCK_Selection)
            assertEquals (testForm.VKT_Triggers, Array(10) { IntArray(36) })
    assertEquals(testForm.access, intArrayOf(4, 4, 4))
    assertEquals(testForm.fields, arrayOf<VField>(
            DateDebut, DateFin, France, CEE, Monde, TestBool, TestVISIT, TestSKIPPED, TestHIDDEN
    ))



  }

  @Test
  fun selectionBlockTest(){
    val block = TestForm.model.getBlock("Selection")
    assertEquals(block!!.source, "FormTestCase1")
    assertEquals(block.name, "Selection")
    assertEquals(block.shortcut, "S")
    assertEquals(block.bufferSize, 1)
    assertEquals(block.displaySize, 1)
    assertEquals(block.pageNumber, 0)
    assertEquals(block.options, 0)
    assertEquals(block.border, 0)
    assertEquals(block.maxRowPos, 9)
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
  fun fieldsTest(){
    val modelFieldList : List<VField> = TestForm.model.getBlock("Selection")!!.fields.toList()
    val dslFieldList : List<FormField<*>> = TestForm.formBlocks[0].blockFields.toList()


  }


}

object TestForm : Form() {
  override val locale = Locale.FRANCE
  override val title = "Déclaration d'échange de biens"

  init {
    val testBlock = block(1, 1, "Selection", "Sélection", "S") {

      val DateDebut = visit(domain = Domain<Date>(),
                            position = at(1, 1)) {
        label = "Depuis le"
        help = "Date minimale de saisie des commandes."
      }

      val DateFin = visit(Domain<Date>(), at(1, 2)) {

        label = "Jusqu'au"
        help = "Date maximale de saisie des commandes."
      }

      val France = mustFill(Domain<String>(5), at(3, 1)) {
        //Label should be initialized to "FRANCE"
        // label = "France"
        help = "Prendre en compte les commandes en France."
      }

      val Cee = mustFill(Domain<String>(50), at(4, 1)) {
        //label = "Cee"
        help = "Prendre en compte les commandes en CEE."
      }

      val Monde = mustFill(Domain<String>(500), at(5, 1)) {
        //label = "Monde"
        help = "Prendre en compte les commandes Monde."
      }

      val TestBool = mustFill(Domain<Boolean>(), at(6, 1)) {
        //label = ""
        help = "Test pour BOOL type"
      }

      val TestVISIT = mustFill(Domain<String>(50), at(7, 1)) {
        //label = ""
        help = "Test pour Visit"
      }

      val TestSKIPPED = mustFill(Domain<String>(50), at(8, 1)) {
        //label = ""
        help = "Test pour hidden"
      }
    }
  }
}
