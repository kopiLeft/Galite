/*
 * This is a test file to check my learning progress and knowledge
 * of Galite and the implementation of its modules and classes.
 *
 * Written by: Mohamed Aziz Tousli.
 */

package org.kopi.galite.demo.test

import org.kopi.galite.demo.bill.BillForm
import java.util.Locale
import org.kopi.galite.demo.database.Bill
import org.kopi.galite.demo.database.Command
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.*

class TestForm : DictionaryForm (title = "Test", locale = Locale.UK) {

    // Insert default menu items and their correspondent commands
    init {
        insertMenus()
        insertCommands()
    }

    // Create first tab called "Tab 1"
    val tab1 = page(title = "Tab 1")

    // Insert the first Block "TestBlock1" into "Tab 1", while adding the Report menu item
    val dummy = tab1.insertBlock(TestBlock1()) {
        command (item = report) {
            createReport {
                TestReport()
            }
        }
    }

    // Define our first Block class "TestBlock1"
//    val nbOfRows : Int = 10
//    val nbOfVisibleRows : Int = 5
    // Why we cannot setup variables?
    class TestBlock1 : Block(title = "Block 1", buffer = 10, visible = 5) {

        init {
            border = Border.ETCHED
        }

        val address = mustFill(domain = STRING(50), position = at(line = 1, column = 1)) {
            label = "Address"
            help = "The address of the user"
            align = FieldAlignment.LEFT
        }

        val age = visit(domain = INT(3), position = follow(address)) {
            label = "Age"
            help = "The age of the user"
            align = FieldAlignment.RIGHT
        }

        val firstName = skipped(domain = STRING(10), position = at(3..5,3..5)) {
            label = "First Name"
            help = "The first name of the user"
            align = FieldAlignment.CENTER
            // Change options one by one later when I can run the code
            options(FieldOption.NOECHO)
        }

        // hidden() does not have a position
        val lastName = hidden(domain = STRING(10)) {
            label = "Last Name"
            help = "The last name of the user"
        }

        val cv = visit(domain = STRING(20), position = follow(firstName)) {
            label = "CV"
            help = "The Curriculum Vitae of the user"
            droppable("pdf")
            // Action to fill later
        }








    }
}

fun main() {
    runForm(form = TestForm::class)
}