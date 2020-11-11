package org.kopi.galite.tests.form

import org.junit.Test
import org.kopi.galite.form.*
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VDefaultActor
import kotlin.test.Ignore
import kotlin.test.assertEquals

class VFormTests: JApplicationTestBase() {

  /**
   * This form test has 3 actors
   * 3 default commands(QuitForm,ResetForm,HelpForm)
   * 3 other commands (saisie, descendre, monter)
   * 5 fields
   */
  @Test
  @Ignore
  fun testForm() {
    val vFormTests = formTest()
    assertEquals("Selection",vFormTests.VKT_BLOCK_Selection().name,"name is okay")
    assertEquals("Déclaration d'échange de biens",vFormTests.getTitle())
    assertArraysEquals(arrayOf("Commandes"),vFormTests.pages)

    assertEquals(4,vFormTests.VKT_Triggers.size)
   // assertEquals(false,vFormTests.retryProtected())

    assertArraysEquals(vFormTests.actors, arrayOf(VActor("File",
            "com.FormDefault",
            "Quit",
            "com.FormDefault",
            "quit",
            27,
            0),
            VActor("File",
                    "com.FormDefault",
                    "Break",
                    "com.FormDefault",
                    "break",
                    114,
                    0),
            VActor("Help",
                    "com.FormDefault",
                    "Help",
                    "com.FormDefault",
                    null,
                    112,
                    0),
            VActor("Action",
                    "com.FormDefault",
                    "Saisie",
                    "formTest",
                    "edit",
                    115,
                    1),
            VActor("Edit",
                    "com.FormDefault",
                    "Descendre",
                    "formTest",
                    "refresh",
                    115,
                    0),
            VActor("Edit",
                    "com.FormDefault",
                    "Monter",
                    "formTest",
                    "refresh",
                    116,
                    0),
            VDefaultActor(-5,
                    "Edit",
                    "com.FormDefault",
                    "Autofill",
                    "com.FormDefault",
                    null,
                    113,
                    0)))
    assertEquals(10,vFormTests.VKT_BLOCK_Selection().DateDebut.width)
    assertEquals(1,vFormTests.VKT_BLOCK_Selection().DateDebut.height)
  }
}

class formTest : VForm() {
  inner class VKT_BLOCK_Selection : VBlock(this@formTest) {
    public override fun setInfo() {
      DateDebut.setInfo("DateDebut",
              0,
              -1,
              0,
              intArrayOf(2, 2, 2),
              null,
              null,
              0,
              0,
              null,
              VPosition(1,
                      1,
                      1,
                      1,
                      -1),
              2,
              null)
      DateFin.setInfo("DateFin",
              1,
              -1,
              0,
              intArrayOf(2, 2, 2),
              null,
              null,
              0,
              0,
              null,
              VPosition(1,
                      1,
                      2,
                      2,
                      -1),
              2,
              null)
      France.setInfo("France",
              2,
              -1,
              0,
              intArrayOf(4, 4, 4),
              null,
              null,
              0,
              0,
              null,
              VPosition(3,
                      3,
                      1,
                      1,
                      -1),
              2,
              null)
      CEE.setInfo("CEE",
              3,
              -1,
              0,
              intArrayOf(4, 4, 4),
              null,
              null,
              0,
              0,
              null,
              VPosition(4,
                      4,
                      1,
                      1,
                      -1),
              2,
              null)
      Monde.setInfo("Monde",
              4,
              -1,
              0,
              intArrayOf(4, 4, 4),
              null,
              null,
              0,
              0,
              null,
              VPosition(5,
                      5,
                      1,
                      1,
                      -1),
              2,
              null)
    }


    override fun executeVoidTrigger(i: Int) {
      when (i) {
        1 -> {
          println("command is working!")
          return
        }
        2 -> {
          val vkt_block_selection = this
          val ai = vkt_block_selection.sortedRecords
          val j = vkt_block_selection.activeRecord
          var l = -1
          var j1 = 0
          while (j1 < ai.size) {
            if (ai[j1] != j) {
              j1++
              continue
            }
            l = j1
            break
            j1++
          }
          if (l == -1) return
          if (!vkt_block_selection.isRecordFilled(ai[l + 1])) {
            return
          } else {
            ai[l] = ai[l + 1]
            ai[l + 1] = j
            fireOrderChanged()
            return
          }
        }
        3 -> {
          val vkt_block_selection1 = this
          val ai1 = vkt_block_selection1.sortedRecords
          val k = vkt_block_selection1.activeRecord
          var i1 = -1
          var k1 = 0
          while (k1 < ai1.size) {
            if (ai1[k1] != k) {
              k1++
              continue
            }
            i1 = k1
            break
            k1++
          }
          if (i1 <= 0) {
            return
          } else {
            ai1[i1] = ai1[i1 - 1]
            ai1[i1 - 1] = k
            fireOrderChanged()
            return
          }
        }
      }
      super.executeVoidTrigger(i)
    }
    val DateDebut = VDateField()
    val DateFin = VDateField()
    val France = VBooleanField()
    val CEE = VBooleanField()
    val Monde = VBooleanField()


    private  val `$assertionsDisabled` /* synthetic field */ = false
    val `class$VKT_BLOCK_Selection` /* synthetic field */: Class<*>? = null


    init {
      if (`class$Test` == null) `class$Test` = `_mthclass$`("org.kopi.galite.tests.form.formTest")
    }


    init {
      super.source = "org.kopi.galite.tests.form.formTest"
      super.name = "Selection"
      super.shortcut = "S"
      super.bufferSize = 1
      super.displaySize = 1
      super.pageNumber = 0
      super.options = 0
      super.border = 0
      super.maxRowPos = 5
      super.maxColumnPos = 2
      super.displayedFields = 0
      super.commands = arrayOf(VCommand(7,
              this,
              getActor(3),
              1,
              "Saisie"),
              VCommand(7,
                      this,
                      getActor(4),
                      2,
                      "Descendre"),
              VCommand(7,
                      this,
                      getActor(5),
                      3,
                      "Monter")
      )
      super.VKT_Triggers = Array(9) { IntArray(36) }
      super.access = intArrayOf(
              4, 4, 4
      )
      super.fields = arrayOf(
              DateDebut, DateFin, France, CEE, Monde
      )
    }
  }

  override fun init() {
    addActors( arrayOf(VActor("File",
            "com.FormDefault",
            "Quit",
            "com.FormDefault",
            "quit",
            27,
            0),
            VActor("File",
                    "com.FormDefault",
                    "Break",
                    "com.FormDefault",
                    "break",
                    114,
                    0),
            VActor("Help",
                    "com.FormDefault",
                    "Help",
                    "com.FormDefault",
                    null,
                    112,
                    0),
            VActor("Action",
                    "com.FormDefault",
                    "Saisie",
                    "formTest",
                    "edit",
                    115,
                    1),
            VActor("Edit",
                    "com.FormDefault",
                    "Descendre",
                    "formTest",
                    "refresh",
                    115,
                    0),
            VActor("Edit",
                    "com.FormDefault",
                    "Monter",
                    "formTest",
                    "refresh",
                    116,
                    0),
            VDefaultActor(-5,
                    "Edit",
                    "com.FormDefault",
                    "Autofill",
                    "com.FormDefault",
                    null,
                    113,
                    0)
    ))
    super.commands = arrayOf(VCommand(7,
            this,
            getActor(0),
            1,
            "Quit"),
            VCommand(7,
                    this,
                    getActor(1),
                    2,
                    "Break"),
            VCommand(7,
                    this,
                    getActor(2),
                    3,
                    "Help")
    )
    super.source = "org.kopi.galite.tests.form.formTest"
    super.pages = arrayOf("Id$0")
    super.blocks = arrayOf(
            VKT_BLOCK_Selection().also { Selection = it }.also {
              S = it
            }
    )
    super.VKT_Triggers = Array(4) { IntArray(36) }
    super.VKT_Triggers[0][16] = 4
    Selection!!.setInfo(0)
  }

  override fun executeVoidTrigger(i: Int) {
    when (i) {
      1 -> {
        Commands.quitForm(this)
        return
      }
      2 -> {
        Commands.resetForm(this)
        return
      }
      3 -> {
        showHelp(this)
        return
      }
      4 -> {
        println("Everything is fine!")
        return
      }
    }
    super.executeVoidTrigger(i)
  }

  protected var Selection: VKT_BLOCK_Selection? = null
  protected var S: VKT_BLOCK_Selection? = null

  companion object {
    fun `_mthclass$`(s: String?): Class<*> {
      return try {
        Class.forName(s)
      } catch (classnotfoundexception: ClassNotFoundException) {
        throw NoClassDefFoundError(classnotfoundexception.message)
      }
    }

    private var `$assertionsDisabled` /* synthetic field */ = false
    var `class$Test` /* synthetic field */: Class<*>? = null
    val timestamp = System.currentTimeMillis()

    init {
      if (`class$Test` == null) `class$Test` = `_mthclass$`("org.kopi.galite.tests.form.formTest")
      `$assertionsDisabled` = `class$Test`!!.desiredAssertionStatus() xor true
    }
  }
}


