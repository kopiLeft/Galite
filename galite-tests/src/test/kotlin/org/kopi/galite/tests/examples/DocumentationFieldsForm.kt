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
import java.io.File
import java.math.BigDecimal

import org.jetbrains.exposed.sql.Table
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
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FieldAlignment
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.FileHandler

/*** Field Access modifiers using Modes ***/
// See [FormToCheckFieldVisibility]

class DocumentationFieldsForm : DictionaryForm(title = "Form to test fields", locale = Locale.UK) {

  val autoFill = actor(
    menu = actionMenu,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val list = actor(
    menu = actionMenu,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = Icon.LIST
  }

  val saveBlock = actor(
    menu = actionMenu,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = Icon.SAVE
  }

  val deleteBlock = actor(
    menu = actionMenu,
    label = "deleteBlock",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = Icon.DELETE
  }

  val fieldsTypesBlock = insertBlock(FieldsTypesBlock())
  val fieldsAccessBlock = insertBlock(FieldsAccessBlock())
  val fieldsAlignmentBlock = insertBlock(FieldsAlignmentBlock())
  val fieldsOptionsBlock = insertBlock(FieldsOptionsBlock())
  val queryBlock = insertBlock(QueryBlock()) {
    command(item = serialQuery) {
      serialQuery()
    }
  }
  val sortableMultiBlock = insertBlock(SortableMultiBlock())
  val priorityAndIndexBlock = insertBlock(ColumnsBlock())
  val innerJoinBlock = insertBlock(InnerJoinBlock())
  val triggersFieldsBlock = insertBlock(TriggersFieldBlock())
  val lastBlock = insertBlock(LastBlock())

  inner class FieldsTypesBlock : Block("Block to test fields types", 1, 10) {
    init {
      border = Border.LINE
    }

    /*** STRING ***/
    // test Convert Upper + style true --> don't work in swing
    /*val string1 = visit(domain = STRING(10, Convert.UPPER, true), position = at(1, 1)) {
      label = "string 1"
    }*/

    // test Convert Upper + style false
    val string2 = visit(domain = STRING(10, Convert.UPPER, false), position = at(2, 1)) {
      label = "string 2"
    }

    // test Convert Lower + Fixed ON + specify width, height, visibleHeight
    val string3 = visit(domain = STRING(40, 10, 4, Fixed.ON, Convert.LOWER), position = at(3, 1)) {
      label = "string 3"
    }

    // test Convert Lower + Fixed OFF + specify width, height, visibleHeight
    val string4 = visit(domain = STRING(40, 10, 4, Fixed.OFF, Convert.NAME), position = at(3, 2)) {
      label = "string 4"
    }

    /*** TEXT ***/
    // test Fixed ON + styled = true
    val text1 = visit(domain = TEXT(80, 50, 2, Fixed.ON, styled = true), position = at(6, 1)) {
      label = "text 1"
    }

    // test Fixed OFF + styled = true
    val text2 = visit(domain = TEXT(80, 50, 2, Fixed.OFF, styled = true), position = at(6, 2)) {
      label = "text 2"
    }

    // test Fixed ON + styled = false
    val text3 = visit(domain = TEXT(80, 50, 2, Fixed.ON, styled = false), position = at(6, 3)) {
      label = "text 3"
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
    // test decimal field + min and max value
    val decimal = visit(domain = DECIMAL(width = 10, scale = 5) { min = BigDecimal("1.9"); max = BigDecimal("5.9") },
                        position = at(9, 1)) {
      label = "decimal"
    }

    /*** INT ***/
    // test int field + min and max value
    val int = visit(domain = INT(3) { min = 1; max = 100 }, position = at(10, 1)) {
      label = "int"
    }

    /*** CODE ***/
    // test boolean
    val booleanCodeField = visit(domain = BoolCode, position = at(11, 1)) {
      label = "booleanCode Field"
    }

    // test int
    val intCodeField = visit(domain = IntCode, position = at(11, 2)) {
      label = "intCode Field"
    }

    // test decimal
    val decimalCodeField = visit(domain = DecimalCode, position = at(11, 3)) {
      label = "decimalCode Field"
    }

    // test string
    val stringCodeField = visit(domain = StringCode, position = at(11, 4)) {
      label = "stringCode Field"
    }

    /*** LIST ***/
    val listField = visit(domain = ListDomain, position = at(12, 1)) {
      label = "List Field"
    }

    /*** Field Drop files ***/
    val dropFile = visit(domain = STRING(20), position = at(13, 1)) {
      label = "drop File"
      droppable("pdf")
      trigger(ACTION) {
        FileHandler.fileHandler!!.openFile(form.model.getDisplay()!!, object : FileHandler.FileFilter {
          override fun accept(pathname: File?): Boolean {
            return (pathname!!.isDirectory
                    || pathname.name.lowercase().endsWith(".pdf"))
          }

          override val description: String
            get() = "PDF"
        })
      }
    }
  }

  /*** Block to test fields Access and multiField ***/
  inner class FieldsAccessBlock : Block("Block to test fields Access and multiField", 1, 10) {
    init {
      border = Border.LINE
    }

    /*** Creating Form Fields Field Access Modifiers ***/
    // mustfill
    val mustfillField = visit(domain = INT(5), position = at(1, 1)) {
      label = "mustfill Field"
      trigger(VALUE) { 10 }
    }

    // visit
    val visitField = visit(domain = INT(5), position = at(1, 2)) {
      label = "visit Field"
    }

    // skipped + test follow
    val skippedField = skipped(domain = INT(5), follow(visitField)) {
      label = "skipped Field"
    }

    // hidden
    val hiddenField = hidden(domain = INT(5)) {
      label = "hidden Field"
    }

    /*** multiField ***/
    // test filed position --> at(row, column..multiField)
    val multiField = visit(domain = STRING(20), position = at(2, 1..3)) {
      label = "multi Field"
    }
  }

  /*** Block to test fields Alignment ***/
  inner class FieldsAlignmentBlock : Block("Block to test fields Alignment", 1, 10) {
    init {
      border = Border.LINE
    }

    /*** Field Alignment ***/
    // test alignment Left
    val alignmentLeft = visit(domain = STRING(20), position = at(1, 1)) {
      label = "alignment Left"
      align = FieldAlignment.LEFT
    }

    // test alignment Right
    val alignmentRight = visit(domain = STRING(20), position = at(1, 2)) {
      label = "alignment Right"
      align = FieldAlignment.RIGHT
    }

    // test alignment Center
    val alignmentCenter = visit(domain = STRING(20), position = at(1, 3)) {
      label = "alignment Center"
      align = FieldAlignment.CENTER
    }
  }

  /*** Block to test fields Options ***/
  inner class FieldsOptionsBlock : Block("Block to test fields Options", 1, 10) {
    init {
      border = Border.LINE
    }
    /*** Field Options ***/
    // test NOECHO
    val noEcho = visit(domain = STRING(20), position = at(1, 1)) {
      label = "no Echo"
      options(FieldOption.NOECHO)
    }

    // test NOEDIT
    val noEdit = visit(domain = STRING(20), position = at(1, 2)) {
      label = "no Edit"
      options(FieldOption.NOEDIT)
    }

    // test TRANSIENT
    val transient = visit(domain = STRING(20), position = at(1, 3)) {
      label = "Transient"
      options(FieldOption.TRANSIENT)
    }

    // test NO DELETE ON UPDATE
    val noDeleteOnUpdate = visit(domain = STRING(20), position = at(1,4)) {
      label = "No Delete On Update"
      options(FieldOption.NO_DELETE_ON_UPDATE)
    }

    // test NO DETAIL
    val noDetail = visit(domain = STRING(20), position = at(1, 5)) {
      label = "no Detail"
      options(FieldOption.NO_DETAIL)
    }

    // test NO CHART
    val noChart = visit(domain = STRING(20), position = at(1, 6)) {
      label = "no Chart"
      options(FieldOption.NO_CHART)
    }
  }

  inner class SortableMultiBlock : Block("Block to test : SORTABLE field options", 10, 10) {
    init {
      border = Border.LINE
    }

    // test SORTABLE
    val sortable = visit(domain = STRING(20), position = at(1, 1)) {
      label = "Sortable"
      options(FieldOption.SORTABLE)
    }
  }

  /*** BLOCK to test Field Options :
   * QUERY UPPER
   * QUERY LOWER
   */
  inner class QueryBlock : Block("Block to test QUERY UPPER and QUERY LOWER field options", 1, 10) {
    init {
      border = Border.LINE
    }
    val t = table(TestTable)

    val id = hidden(domain = INT(25)) {
      label = "ID"
      columns(t.id)
    }

    // test QUERY UPPER
    val queryUpper = visit(domain = STRING(20), position = at(1, 1)) {
      label = "query Upper"
      columns(t.name)
      options(FieldOption.QUERY_UPPER)
    }

    // test QUERY LOWER
    val queryLower = visit(domain = STRING(20), position = at(2, 1)) {
      label = "query Lower"
      columns(t.lastName)
      options(FieldOption.QUERY_LOWER)
    }
  }

  /*** BLOCK to test Field Columns + Field Commands
   * put id field priority to 1 and name field to 9 and click on list
   * then put id field priority to 9 and name field to 1 and click on list to see the different
   * put value in name field and click save then put same value and save to test the index
   * add twoColumns column and list to test inner join
   *
   * commandField to test Field Commands
   * ***/
  inner class ColumnsBlock : Block("Block to test: columns, priority, index and command Field", 1, 10) {
    val t = table(TestTable)

    val i = index(message = "this should be unique")

    /*** Field Columns ***/
    // test columns() et priority
    val id = visit(domain = INT(20), position = at(1, 1)) {
      label = "id"
      help = "field to test priority"
      columns(t.id) {
        priority = 1
      }
    }

    // test index et priority
    val name = visit(domain = STRING(20), position = at(1, 2)) {
      label = "name"
      help = "field to test index"
      columns(t.name) {
        index = i
        priority = 9
      }
    }

    val commandField = visit(domain = STRING(20), position = at(2, 1)) {
      label = "commandField"

      command(item = autoFill, Mode.UPDATE, Mode.INSERT, Mode.QUERY) { }
    }

    init  {
      border = Border.LINE

      command(item = list) {
        recursiveQuery()
      }
      command(item = saveBlock) {
        insertMode()
        saveBlock()
      }
    }
  }

  /*** BLOCK to test Inner join
   * add twoColumns column and list to test inner join
   * ***/
  inner class InnerJoinBlock : Block("Block to test: Inner join", 1, 10) {
    init {
      border = Border.LINE

      command(item = list) {
        recursiveQuery()
      }
    }

    val t = table(TestTable)
    val t2 = table(TestTable2)

    // test join two columns()
    val innerJoinColumns = visit(domain = INT(20), position = at(1, 3)) {
      label = "inner join columns"
      columns(t.id, t2.refTable1)
    }
  }

  /*** Field Triggers ***/
  inner class TriggersFieldBlock : Block("Block to test: Field Triggers", 1, 10) {
    val t = table(TestTriggers)

    val id = hidden(domain = INT(20)) {
      label = "ID"
      columns(t.id)
    }

    // test PREFLD : enter field to call PREFLD trigger
    val preFldTriggerField = visit(domain = STRING(20), position = at(1, 1)) {
      label = "PREFLD Trigger Field"
      trigger(PREFLD) {
        this.value = "PREFLD Trigger"
      }
    }

    // test POSTFLD : enter field then leave it to call POSTFLD trigger
    val postFldTriggerField = visit(domain = STRING(20), position = at(1, 2)) {
      label = "POSTFLD Trigger Field"
      trigger(POSTFLD) {
        this.value = "POSTFLD Trigger"
      }
    }

    // test POSTCHG : enter field put value then leave it to call POSTCHG trigger (executed after changes in field)
    val postChgTriggerField = visit(domain = STRING(20), position = at(1, 3)) {
      label = "POSTCHG Trigger Field"
      trigger(POSTCHG) {
        this.value = "POSTCHG Trigger"
      }
    }

    // test PREVAL : put value in this field then leave it to call PREVAL trigger (executed before validate field)
    val preValTriggerField = visit(domain = STRING(20), position = at(1, 4)) {
      label = "PREVAL Trigger Field"
      trigger(PREVAL) {
        this.value = "PREVAL Trigger"
      }
    }

    // test VALFLD : put value in this field then leave it to call PREVAL trigger (executed when validates the field)
    val valFldTriggerField = visit(domain = STRING(20), position = at(2, 1)) {
      label = "VALFLD Trigger Field"
      trigger(VALFLD) {
        this.value = "VALFLD Trigger"
      }
    }

    // test VALIDATE : put value in this field then leave it to call VALIDATE trigger (executed when validates the field)
    val validateTriggerField = visit(domain = STRING(20), position = at(2, 2)) {
      label = "VALIDATE Trigger Field"
      trigger(VALIDATE) {
        this.value = "VALIDATE Trigger"
      }
    }

    // test DEFAULT : click on insert button first to call DEFAULT trigger
    val defaultTriggerField  = visit(domain = STRING(20), position = at(2, 3)) {
      label = "DEFAULT Trigger Field"
      trigger(DEFAULT) {
        this.value = "DEFAULT Trigger"
      }
    }

    // FORMAT trigger : Not defined actually

    // test ACCESS : the visibility of the filed is changed to skipped
    val accessTriggerField  = visit(domain = STRING(20), position = at(3, 1)) {
      label = "ACCESS Trigger Field"
      access { Access.SKIPPED }
    }

    // test VALUE : the field contains a default value
    val valueTriggerField = visit(domain = STRING(20), position = at(3, 2)) {
      label = "VALUE Trigger Field"
      trigger(VALUE) {
        "VALUE Trigger"
      }
    }

    // test AUTOLEAVE
    val autoleaveTriggerField = visit(domain = STRING(20), position = at(3, 3)) {
      label = "AUTOLEAVE Trigger Field"
      trigger(AUTOLEAVE) {
        true
      }
    }

    // test PREINS : click on insertMode command then save command and check value in database
    // test POSTINS : click on insertMode command then save command and check lastBlock
    val preInsTriggerField = visit(domain = STRING(20), position = at(4, 1)) {
      label = "PREINS Trigger Field"
      columns(t.INS)
      trigger(PREINS) {
        this.value = "PREINS Trigger"
      }
      trigger(POSTINS) {
        lastBlock.postInsTriggerField.value = "POSTINS Trigger"
      }
    }

    // test PREUPD : click on list command then save command and assert that PREUPD trigger change the field value
    // test POSTUPD : click on list command then save command and assert that POSTUPD trigger change the field value of the lastBlock
    val preUpdTriggerField = visit(domain = STRING(20), position = at(4, 2)) {
      label = "PREUPD Trigger Field"
      columns(t.UPD)
      trigger(PREUPD) {
        this.value = "PREUPD Trigger"
      }
      trigger(POSTUPD) {
        block.form.notice("POSTUPD Trigger")
      }
    }

    // test PREDEL : click on list command then delete
    val preDelTriggerField = visit(domain = STRING(20), position = at(4, 3)) {
      label = "PREDEL Trigger Field"
      trigger(PREDEL) {
        block.form.notice("PREDEL Trigger")
      }
    }

    val uc = hidden(domain = INT(20)) { columns(t.uc) }
    val ts = hidden(domain = INT(20)) { columns(t.ts) }

    init {
      border = Border.LINE

      command(item = list) {
        recursiveQuery()
      }
      command(item = insertMode) {
        insertMode()
      }
      command(item = saveBlock) {
        saveBlock()
      }

      command(item = deleteBlock) {
        deleteBlock()
      }
    }
  }

  inner class LastBlock : Block("LastBlock", 1, 10) {
    val postInsTriggerField = visit(domain = STRING(20), position = at(1, 1)) {
      label = "POSTINS Trigger Field"
    }
    val postUpdTriggerField = visit(domain = STRING(20), position = at(1, 2)) {
      label = "POSTUPD Trigger Field"
    }
  }
}

object BoolCode: CodeDomain<Boolean>() {
  init {
    "married" keyOf true
    "single" keyOf false
  }
}

object IntCode: CodeDomain<Int>() {
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

object DecimalCode: CodeDomain<BigDecimal>() {
  init {
    "piece" keyOf BigDecimal("1.00")
    "per cent" keyOf BigDecimal("0.01")
  }
}
object StringCode: CodeDomain<String>() {
  init {
    "JDK" keyOf "Java Development Kit"
    "JRE" keyOf "Java Runtime Environment"
  }
}

object ListDomain : ListDomain<String>(20) {
  override val table = TestTable

  init {
    "id" keyOf table.id
    "name" keyOf table.name
  }
}

object TestTable : Table("TESTTABLE1") {
  val id = integer("ID")
  val name = varchar("NAME", 20)//.nullable()
  val lastName = varchar("LASTNAME", 20).nullable()
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
  val uc = integer("UC").default(0)
  val ts = integer("TS").default(0)
  val INS = varchar("INS", 20).nullable()
  val UPD = varchar("UPD", 20).nullable()

  override val primaryKey = PrimaryKey(TestTable.id, name = "TRIGGERS_ID")
}

fun main() {
  initDocumentationData()
  runForm(formName = DocumentationFieldsForm())
}
