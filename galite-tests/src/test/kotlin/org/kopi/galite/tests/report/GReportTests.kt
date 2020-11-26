package org.kopi.galite.tests.report

import org.kopi.galite.domain.Domain
import org.kopi.galite.report.dsl.FieldAlignment
import org.kopi.galite.report.dsl.Report
import java.util.*


object GReportTests : Report() {
    override val locale = Locale.FRANCE
    override val title = "GenratedReport"
    override val reportCommands = true

    val name = field(Domain<String>(20)) {
        label = "name"
        help = "The user name"
        align = FieldAlignment.CENTER
    }

    val age = field(Domain<Int>(3)) {
        label = "age"
        help = "The user age"
        align = FieldAlignment.CENTER
    }

    init {
        add {
            this[name] = "User1"
            this[age] = 22
        }
        add {
            this[name] = "User2"
            this[age] = 23
        }
    }
}