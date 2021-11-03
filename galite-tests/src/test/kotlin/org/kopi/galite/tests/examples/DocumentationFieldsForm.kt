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
package org.kopi.galite.tests.examples

import java.util.Locale
import java.io.File

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.db.connectToDatabase

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.Convert
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.Fixed
import org.kopi.galite.visual.domain.IMAGE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TEXT
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FieldAlignment
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.maxValue
import org.kopi.galite.visual.dsl.form.minValue
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.visual.FileHandler

/*** Field Access modifiers using Modes ***/
// **> FormToCheckFieldVisibility
// **> trigger ACTION does not exist in doc !


class DocumentationFieldsForm : DictionaryForm() {
  override val locale = Locale.UK

  override val title = "Commands Form"
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
  val saveBlock = actor(
    ident = "saveBlock",
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = "save"
  }

  val InsertMode = actor(
    ident = "Insert",
    menu = action,
    label = "Insert",
    help = " Insert",
  ) {
    key = Key.F7
    icon = "insert"
  }

  val deleteBlock = actor(
    ident = "deleteBlock",
    menu = action,
    label = "deleteBlock",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = "delete"
  }

  val block = insertBlock(FiledsBlock())
  val block2 = insertBlock(ColumnsBlock())
  val block3 = insertBlock(TriggersFieldBlock())

  inner class FiledsBlock : FormBlock(1, 10, "Training") {

    /*** STRING ***/
    // test Convert Upper + style true --> don't work in swing
    /*val string1 = visit(domain = STRING(10, Convert.UPPER, true), position = at(1, 1)) {
      label = "string1"
    }*/

    // test Convert Upper + style false
    val string2 = visit(domain = STRING(10, Convert.UPPER, false), position = at(2, 1)) {
      label = "string2"
    }
    // test Convert Lower + Fixed ON + specify width, height, visibleHeight --> More doc on option fixed ON OFF !!!
    val string3 = visit(domain = STRING(40, 10, 4, Fixed.ON, Convert.LOWER), position = at(3, 1)) {
      label = "string3"
    }
    // test Convert Lower + Fixed OFF + specify width, height, visibleHeight
   val string4 = visit(domain = STRING(40, 10, 4, Fixed.OFF, Convert.NAME), position = at(3, 2)) {
      label = "string4"
    }

    /*** TEXT ***/ //--> set Documentation example!!!
    // test Fixed ON + styled = true
    val text1 = visit(domain = TEXT(80, 50, 2, Fixed.ON, styled = true), position = at(6, 1)) {
      label = "text1"
    }
    // test Fixed OFF + styled = true
    val text2 = visit(domain = TEXT(80, 50, 2, Fixed.OFF, styled = true), position = at(6, 2)) {
      label = "text2"
    }
    // test Fixed ON + styled = false
    val text3 = visit(domain = TEXT(80, 50, 2, Fixed.ON, styled = false), position = at(6, 3)) {
      label = "text3"
    }
    // test Fixed OFF + styled = false
    val text = visit(domain = TEXT(80, 50, 2, Fixed.OFF, styled = false), position = at(6, 4)) {
      label = "text"
    }

    /*** IMAGE ***/
    val image = visit(domain = IMAGE(width = 20, height = 10), position = at(8, 1)) {
      label = "image"
    }

    /*** DECIMAL ***/
    //test decimal field + min and max value
    val decimal = visit(domain = DECIMAL(width = 10, scale = 5), position = at(9, 1)) {
      label = "decimal"
      minValue = Decimal.valueOf("1.9")
      maxValue = Decimal.valueOf("5.9")
    }

    /*** INT ***/
    //test int field + min and max value
    val int = visit(domain = INT(3), position = at(10, 1)) {
      label = "int"
      minValue = 1
      maxValue = 100
    }

    /*** CODE ***/
    //test boolean
    val booleanCodeField = visit(domain = boolCode, position = at(11, 1)) {
      label = "booleanCodeField"
    }
    //test int
    val intCodeField = visit(domain = intCode, position = at(11, 2)) {
      label = "intCodeField"
    }
    //test decimal
    val decimalCodeField = visit(domain = decimalCode, position = at(11, 3)) {
      label = "decimalCodeField"
    }
    //test string
    val stringCodeField = visit(domain = stringCode, position = at(11, 4)) {
      label = "stringCodeField"
    }

    /*** LIST ***/
    val ListField = visit(domain = listDomain, position = at(12, 1)) {
      label = "ListField"
    }

    /*** Creating Form Fields Field Access Modifiers ***/
    //mustfill
    val mustfillField = mustFill(domain = INT(5), position = at(13, 1)) {
      label = "mustfillField"
    }
    //visit
    val visitField = visit(domain = INT(5), position = at(13, 2)) {
      label = "visitField"
    }
    //skipped + test follow
    val skippedField = skipped(domain = INT(5), follow(visitField)) {
      label = "skippedField"
    }
    //hidden
    val hiddenField = hidden(domain = INT(5)) {
      label = "hiddenField"
    }
    //test filed position --> at(row, column..multifield)
    val multifield = visit(domain = STRING(20), position = at(14, 1..9)) {
      label = "multifield"
    }

    /*** Field Alignment ***/
    //test alignment Left
    val alignmentLeft = visit(domain = STRING(20), position = at(15, 1)) {
      label = "alignmentLeft"
      align = FieldAlignment.LEFT
    }
    //test alignment Right
    val alignmentRight= visit(domain = STRING(20), position = at(15, 2)) {
      label = "alignmentRight"
      align = FieldAlignment.RIGHT
    }
    //test alignment Center
    val alignmentCenter = visit(domain = STRING(20), position = at(15, 3)) {
      label = "alignmentCenter"
      align = FieldAlignment.CENTER
    }


    /*** Field Drop files ***/
    val dropFile = visit(domain = STRING(20), position = at(16, 1)) {
      label = "Cv"
      droppable("pdf")
      trigger(ACTION) {
        FileHandler.fileHandler!!.openFile(form.model.getDisplay()!!, object : FileHandler.FileFilter {
          override fun accept(pathname: File?): Boolean {
            return (pathname!!.isDirectory
                    || pathname.name.toLowerCase().endsWith(".pdf"))
          }

          override val description: String
            get() = "PDF"
        })
      }
    }

    /*** Field Options ***/ // ---> Fix doc choise to write NOECHO or NO ECHO same in FieldOption.kt
    //test NOECHO
    val NOECHO = visit(domain = STRING(20), position = at(17, 1)) {
      label = "NOECHO"
      options(FieldOption.NOECHO)
    }
    //test NOECHO
    val NOEDIT = visit(domain = STRING(20), position = at(17, 2)) {
      label = "NOEDIT"
      options(FieldOption.NOEDIT)
    }
    //test SORTABLE
    val SORTABLE = visit(domain = STRING(20), position = at(17, 3)) {
      label = "SORTABLE"
      options(FieldOption.SORTABLE)
    }
    //test TRANSIENT
    val TRANSIENT = visit(domain = STRING(20), position = at(17, 4)) {
      label = "TRANSIENT"
      options(FieldOption.TRANSIENT)
    }
    //test NO DELETE ON UPDATE
    val NODELETEONUPDATE = visit(domain = STRING(20), position = at(17, 5)) {
      label = "NO DELETE ON UPDATE"
      options(FieldOption.NO_DELETE_ON_UPDATE)
    }
    //test NO DETAIL
    val NODETAIL = visit(domain = STRING(20), position = at(17, 6)) {
      label = "NO DETAIL"
      options(FieldOption.NO_DETAIL)
    }
    //test NO CHART
    val NOCHART = visit(domain = STRING(20), position = at(17, 7)) {
      label = "NO CHART"
      options(FieldOption.NO_CHART)
    }
    //test QUERY UPPER
    val QUERYUPPER = visit(domain = STRING(20), position = at(17, 8)) {
      label = "QUERY UPPER"
      options(FieldOption.QUERY_UPPER)
    }
    //test QUERY UPPER
    val QUERYLOWER = visit(domain = STRING(20), position = at(17, 9)) {
      label = "QUERY LOWER"
      options(FieldOption.QUERY_LOWER)
    }
  }

  /*** BLOCK to test Field Columns + Field Commands
   * put columns priority to 1 and indexTest to 9 and click on list
   * then put columns priority to 9 and indexTest to 1 and click on list to see the different
   * put value in indexTest field and click save then put same value and save to test the index
   * add twoColumns column and list to test inner join
   *
   * commandField to test Field Commands
   * ***/
  inner class ColumnsBlock : FormBlock(1, 10, "ColumnsBlock") {
    val t = table(TestTable)
   // val t2 = table(TestTable2)
    val i = index(message = "this should be unique")

    /*** Field Columns ***/
    //test columns()
    val columns = visit(domain = INT(20), position = at(1, 1)) {
      label = "columns"
      columns(t.id) {
        priority= 1
      }
    }
    //test index + property
    val indexTest = visit(domain = STRING(20), position = at(1, 2)) {
      label = "indexTest"
      columns(t.name) {
        index = i
        priority = 9
      }
      trigger(PREVAL) {
        println("PREVAL Trigger !!")
      }
    }

    val commandField = visit(domain = STRING(20), position = at(2, 1)) {
      label = "indexTest"

      command(item = autoFill) {
        mode(Mode.UPDATE, Mode.INSERT, Mode.QUERY) // --> to set in Doc !!!
        action = {}
      }
    }

    //test join two columns()
  /*  val twoColumns = visit(domain = INT(20), position = at(1, 3)) {
      label = "two columns"
      columns(t.id, t2.refTable1)
    }*/


    /* --> dont work index = number !!!
        mustFill(domain = LONG(20)) {
          label = "Lesson"
          help = "The lesson you have to attend to"
          column(LEC.Lesson) {
            index = 0
          }
        }

        visit(domain = LONG(10)) {
          label = "Lecturer"
          column(T.Lecturer) {
            index = 1
          }
        }

        mustFill(domain = STRING(20)) {
          label = "Time"
          column(LEC.Time) {
            index = 1
          }
        }
     */

    init  {
      command(item = list) {
        action = {
          recursiveQuery()
        }
      }
      command(item = saveBlock) {
        action = {
          insertMode()
          saveBlock()
        }
      }
    }
  }

  /*** Field Triggers ***/
  inner class TriggersFieldBlock : FormBlock(1, 10, "Training") {
    init {
      options(BlockOption.NODELETE)
    }
    val t = table(TestTriggers)
    //test PREFLD
    val PREFLD = visit(domain = INT(20), position = at(1, 1)) {
      label = "PREFLD"
      columns(t.id)
      trigger(PREFLD) {
        println("PREFLD Trigger !!")
      }
    }
    //test POSTFLD
    val POSTFLD = visit(domain = INT(20), position = at(1, 2)) {
      label = "POSTFLD"
      trigger(POSTFLD) {
        println("POSTFLD Trigger !!")
      }
    }

    //test POSTCHG
    val POSTCHG = visit(domain = INT(20), position = at(1, 3)) {
      label = "POSTCHG"
      trigger(POSTCHG) {
        println("POSTCHG Trigger !!")
      }
    }

    //test PREVAL put value in this field then click on save actor
    val PREVAL = visit(domain = STRING(20), position = at(1, 4)) {
      label = "PREVAL"
      trigger(PREVAL) {
        println("PREVAL Trigger !!")
      }
    }

    //test VALFLD put value in this field then leave the field
    val VALFLD = visit(domain = STRING(20), position = at(1, 5)) {
      label = "VALFLD"
      trigger(VALFLD) {
        println("VALFLD Trigger !!")
      }
    }

    //test VALIDATE put value in this field then leave the field **> problem same value as VALFLD !!!
    val VALIDATE = visit(domain = STRING(20), position = at(1, 6)) {
      label = "VALIDATE"
      trigger(VALIDATE) {
        println("VALIDATE Trigger !!")
      }
    }

    //test DEFAULT you need to click insert button first
    val DEFAULTTrigger = visit(domain = STRING(20), position = at(1, 7)) {
      label = "DEFAULTTrigger"
      trigger(DEFAULT) {
        this.value = "DEFAULT VALUE"
      }
    }
    //FORMAT : Not defined actually

    //test ACCESS change the visibility of the filed to skipped
    val ACCESSTrriger = visit(domain = STRING(20), position = at(2, 1)) {
      label = "ACCESSTrriger"
      trigger(ACCESS) { Access.SKIPPED }
    }

    //test VALUE
    val VALUETrriger = visit(domain = STRING(20), position = at(2, 2)) {
      label = "VALUE"
      trigger(VALUE) {
        "VALUE"
      }
    }

    //test AUTOLEAVE **> to check in kopi !!!
    val AUTOLEAVE = visit(domain = STRING(20), position = at(2, 3)) {
      label = "AUTOLEAVE"
      trigger(AUTOLEAVE) {
        true
      }
    }

    //test PREINS & POSTINS---> put value click on insert command then save command
    val PREINSPOSTINSTrigger = visit(domain = STRING(20), position = at(2, 4)) {
      label = "PREINS/POSTINS"
      columns(t.INS)
      trigger(PREINS) {
        println("PREINS trigger !!!")
      }
      trigger(POSTINS) {
        println("POSTINS trigger !!!")
      }
    }

    //test PREUPD & POSTUPD--->  click on list command then update this field and click on save
    val PrePostUpd = visit(domain = STRING(20), position = at(2, 5)) {
      label = "PREUPD/POSTUPD"
      columns(t.UPD)
      trigger(PREUPD) {
        println("PREUPD trigger !!!")
      }
      trigger(POSTUPD) {
        println("POSTUPD trigger !!!")
      }
    }

    //test PREDEL--->  click on list command then delete
    val PREDELTrigger = visit(domain = STRING(20), position = at(2, 6)) {
      label = "PREDEL"
      columns(t.UPD)
      trigger(PREDEL) {
        println("PREDEL trigger !!!")
      }
    }

    init {
      command(item = list) {
        action = {
          recursiveQuery()
        }
      }
      command(item = InsertMode) {
        action = {
          insertMode()
        }
      }
      command(item = saveBlock) {
        action = {
          saveBlock()
        }
      }

      command(item = deleteBlock) {
        action = {
          deleteBlock()
        }
      }
    }
  }
}

fun main() {
  connectToDatabase()
  transaction {
    SchemaUtils.create(TestTable, TestTable2, TestTriggers)
    SchemaUtils.createSequence(org.jetbrains.exposed.sql.Sequence("TESTTABLE1ID"))
    SchemaUtils.createSequence(org.jetbrains.exposed.sql.Sequence("TRIGGERSID"))
    TestTable.insert {
      it[id] = 1
      it[name] = "TEST-1"
    }
    TestTable.insert {
      it[id] = 2
      it[name] = "TEST-2"
    }
    TestTable2.insert {
      it[id] = 1
      it[name] = "T"
      it[refTable1] = 1
    }
    TestTriggers.insert {
      it[id] = 1
      it[INS] = "INS-1"
      it[UPD] = "UPD-1"
    }
  }

  runForm(formName = DocumentationFieldsForm())
}

object boolCode: CodeDomain<Boolean>() {
  init {
    "married" keyOf true
    "single" keyOf false
  }
}

object intCode: CodeDomain<Int>() {
  init {
    "Sunday" keyOf 1
    "Monday" keyOf 2
    "Tuesday" keyOf 3
    "Wednesday" keyOf 4
    "Thursday" keyOf 5
    "Friday" keyOf 6
    "Saturday" keyOf 7
  }
}

object decimalCode: CodeDomain<Decimal>() {
  init {
    "piece" keyOf Decimal.valueOf("1.00")
    "per cent" keyOf Decimal.valueOf("0.01")
  }
}
object stringCode: CodeDomain<String>() {
  init {
    "JDK" keyOf "Java Development Kit"
    "JRE" keyOf "Java Runtime Environment"
  }
}

object listDomain : ListDomain<String>(20) {
  override val table = TestTable

  init {
    "id" keyOf table.id
    "name" keyOf table.name
  }
}

object TestTable : Table("TESTTABLE1") {
  val id = integer("ID")
  val name = varchar("NAME", 20)//.nullable()
  val age = integer("AGE").nullable()

  override val primaryKey = PrimaryKey(id, name = "TESTTABLE1_ID")
}

object TestTable2 : Table("TESTTABLE2") {
  val id = integer("ID")
  val name = varchar("NAME", 20).nullable()
  val refTable1 = integer("REFERENCE").references(TestTable.id)
}

object TestTriggers : Table("TRIGGERS") {
  val id = integer("ID")
  val INS = varchar("INS", 20).nullable()
  val UPD = varchar("UPD", 20).nullable()

  override val primaryKey = PrimaryKey(TestTable.id, name = "TRIGGERS_ID")
}
